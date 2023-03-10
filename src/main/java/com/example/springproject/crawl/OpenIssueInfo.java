package com.example.springproject.crawl;
import com.example.springproject.domain.Contributor;
import org.apache.ibatis.jdbc.SQL;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class OpenIssueInfo {
    static int count = 0;
    public  static Connection connection = null;
    public static Lock lock = new ReentrantLock();
    public static PreparedStatement openIssue_statement = null;

    static List<Long> number = new ArrayList<>();
    static List<String> title = new ArrayList<>();
    static List<String> created_at = new ArrayList<>();
    static List<String> updated_at = new ArrayList<>();
    static List<String> body = new ArrayList<>();

    public static void openDB(String database) throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (Exception e) {
            System.err.println("Can't find Mysql Driver, please check the path");
            System.exit(1);
        }
        try {
            connection = Druid.getConnection();
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            System.err.println("Database connection failed");
            System.err.println(e.getMessage());
            System.exit(1);
        }

        try {
            openIssue_statement = connection.prepareStatement("insert ignore into openissue values(?,?,?,?,?)");
        } catch (SQLException e) {
            System.err.println("Insert Statement Fail");
            System.err.println(e.getMessage());
            closeDB();
            System.exit(1);
        }
    }

    public static void closeDB() throws SQLException{
        if (connection != null) {
            if (openIssue_statement != null)
                openIssue_statement.close();
            Druid.closeAll(connection);
        }
    }

    public static void main(String[] args) throws IOException, ParseException, SQLException, java.text.ParseException {
        lock.lock();
        try {
            openDB("repo");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        readByURL("https://api.github.com/repos/CMU-Perceptual-Computing-Lab/openpose/issues?state=open&page=1&per_page=100");
        readByURL("https://api.github.com/repos/CMU-Perceptual-Computing-Lab/openpose/issues?state=open&page=2&per_page=100");
        readByURL("https://api.github.com/repos/CMU-Perceptual-Computing-Lab/openpose/issues?state=open&page=3&per_page=100");
        System.out.println("Total Open Issue Amount is: " + count);
        System.out.println("Total Number list is: " + number);
        System.out.println("Total title list is: " + title);
        System.out.println("Total created at list is: " + created_at);
        System.out.println("Total updated at list is: " + updated_at);
        System.out.println("Total body list is: " + body);

        commit();
        closeDB();
        lock.unlock();

    }



    public static void readByURL(String url) throws IOException, ParseException, SQLException, java.text.ParseException {
        URL u = new URL(url);
        URLConnection connection = u.openConnection();
        HttpURLConnection httpURLConnection = (HttpURLConnection) connection;


        int code = httpURLConnection.getResponseCode();
        if (code != 200)
            return;

        InputStream inputStream = httpURLConnection.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = null;
        StringBuilder stringBuilder = new StringBuilder();
        while ((line = bufferedReader.readLine()) != null)
            stringBuilder.append(line);

        bufferedReader.close();
        JSONArray jsonArray = (JSONArray) new JSONParser().parse(stringBuilder.toString());

        for (int i = 0; i  < jsonArray.size(); i++) {
            JSONObject jsonObject = (JSONObject) jsonArray.get(i);
            int order = i + 1;
            System.out.println("=================" + order + "th ? ==============");
            System.out.println(jsonObject.get("number"));
            number.add((Long) jsonObject.get("number"));
            Long number = (Long) jsonObject.get("number");
            System.out.println(jsonObject.get("title"));
            title.add((String) jsonObject.get("title"));
            String title = (String) jsonObject.get("title");
            System.out.println(jsonObject.get("created_at"));
            created_at.add((String) jsonObject.get("created_at"));
            Date created_at = new Date(com.example.springproject.common.DateUtil.StrConvertDate((String) jsonObject.get("created_at")).getTime());
            System.out.println(jsonObject.get("updated_at"));
            updated_at.add((String) jsonObject.get("updated_at"));
            Date updated_at = new Date(com.example.springproject.common.DateUtil.StrConvertDate((String) jsonObject.get("updated_at")).getTime());
            //System.out.println(jsonObject.get("body"));
            body.add((String) jsonObject.get("body"));
            String body = (String) jsonObject.get("body");
            if (connection != null) {
                openIssue_statement.setLong(1, number);
                openIssue_statement.setString(2, title);
                openIssue_statement.setDate(3, created_at);
                openIssue_statement.setDate(4, updated_at);
                openIssue_statement.setString(5, body);
                openIssue_statement.addBatch();
            }
            openIssue_statement.executeBatch();


            count++;
        }
    }

    public static void commit() throws SQLException {
        try {
            connection.commit();
        } catch (SQLException e) {
            System.err.println("SQL error: " + e.getMessage());
        } try {
            connection.rollback();
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        closeDB();
        System.exit(1);
    }

    public int getCount() {
        return count;
    }

    public List<String> getTitle() {
        return title;
    }

    public List<String> getUpdated_at() {
        return updated_at;
    }

    public List<String> getCreated_at() {
        return created_at;
    }

    public List<String> getBody() {
        return body;
    }

    public static List<Long> getNumber() {
        return number;
    }

}
