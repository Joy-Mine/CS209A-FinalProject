package com.example.springproject.crawl;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;


public class OpenIssueInfo {
    static int count = 0;
    static List<String> created_at = new ArrayList<>();
    static List<String> updated_at = new ArrayList<>();
    static List<String> state = new ArrayList<>();
    static List<Long> number = new ArrayList<>();
    static List<String> title = new ArrayList<>();
    public OpenIssueInfo() throws IOException, ParseException {
        String[] temp=new String[1];
        OpenIssueInfo.main(temp);
    }
    public static void main(String[] args) throws IOException, ParseException {
        readByURL("https://api.github.com/repos/CMU-Perceptual-Computing-Lab/openpose/issues?state=open&page=1&per_page=100");
        readByURL("https://api.github.com/repos/CMU-Perceptual-Computing-Lab/openpose/issues?state=open&page=2&per_page=100");
        readByURL("https://api.github.com/repos/CMU-Perceptual-Computing-Lab/openpose/issues?state=open&page=3&per_page=100");
        System.out.println("Total Open Issue Amount is: " + count);
        System.out.println("Created At List is: " + created_at);
        System.out.println("Updated At List is: " + updated_at);
        System.out.println("State List is: " + state);
        System.out.println("Number List is: " + number);
        System.out.println("Title List is: " + title);
    }

    public static void readByURL(String url) throws IOException, ParseException {
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

//        Scanner in = new Scanner(inputStream);
//        String line = in.nextLine();
//        JSONArray jsonArray = (JSONArray) new JSONParser().parse(line);

        for (int i = 0; i  < jsonArray.size(); i++) {
            JSONObject jsonObject = (JSONObject) jsonArray.get(i);
            int order = i + 1;
            System.out.println("=================" + order + "th ? ==============");
            System.out.println(jsonObject.get("created_at"));
            created_at.add((String) jsonObject.get("created_at"));
            System.out.println(jsonObject.get("updated_at"));
            updated_at.add((String) jsonObject.get("updated_at"));
            System.out.println(jsonObject.get("state"));
            state.add((String) jsonObject.get("state"));
            System.out.println(jsonObject.get("number"));
            number.add((Long) jsonObject.get("number"));
            System.out.println(jsonObject.get("title"));
            title.add((String) jsonObject.get("title"));
            count++;
        }
    }

    public int getCount() {
        return count;
    }

    public List<String> getCreated_at() {
        return created_at;
    }

    public List<String> getUpdated_at() {
        return updated_at;
    }

    public List<String> getState() {
        return state;
    }

    public List<Long> getNumber() {
        return number;
    }

    public List<String> getTitle() {
        return title;
    }
}
