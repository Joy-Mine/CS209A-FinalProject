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

public class ClosedIssueInfo {
    static int count = 0;
    static List<Long> number = new ArrayList<>();
    static List<String> title = new ArrayList<>();
    static List<String> created_at = new ArrayList<>();
    static List<String> updated_at = new ArrayList<>();
    static List<String> state = new ArrayList<>();
    public static void main(String[] args) throws IOException, ParseException {
        readByURL("https://api.github.com/repos/CMU-Perceptual-Computing-Lab/openpose/issues?state=closed&page=1&per_page=100");
        readByURL("https://api.github.com/repos/CMU-Perceptual-Computing-Lab/openpose/issues?state=closed&page=2&per_page=100");
        readByURL("https://api.github.com/repos/CMU-Perceptual-Computing-Lab/openpose/issues?state=closed&page=3&per_page=100");
        readByURL("https://api.github.com/repos/CMU-Perceptual-Computing-Lab/openpose/issues?state=closed&page=4&per_page=100");
        readByURL("https://api.github.com/repos/CMU-Perceptual-Computing-Lab/openpose/issues?state=closed&page=5&per_page=100");
        readByURL("https://api.github.com/repos/CMU-Perceptual-Computing-Lab/openpose/issues?state=closed&page=6&per_page=100");
        readByURL("https://api.github.com/repos/CMU-Perceptual-Computing-Lab/openpose/issues?state=closed&page=7&per_page=100");
        readByURL("https://api.github.com/repos/CMU-Perceptual-Computing-Lab/openpose/issues?state=closed&page=8&per_page=100");
        readByURL("https://api.github.com/repos/CMU-Perceptual-Computing-Lab/openpose/issues?state=closed&page=9&per_page=100");
        readByURL("https://api.github.com/repos/CMU-Perceptual-Computing-Lab/openpose/issues?state=closed&page=10&per_page=100");
        readByURL("https://api.github.com/repos/CMU-Perceptual-Computing-Lab/openpose/issues?state=closed&page=11&per_page=100");
        readByURL("https://api.github.com/repos/CMU-Perceptual-Computing-Lab/openpose/issues?state=closed&page=12&per_page=100");
        readByURL("https://api.github.com/repos/CMU-Perceptual-Computing-Lab/openpose/issues?state=closed&page=13&per_page=100");
        readByURL("https://api.github.com/repos/CMU-Perceptual-Computing-Lab/openpose/issues?state=closed&page=14&per_page=100");
        readByURL("https://api.github.com/repos/CMU-Perceptual-Computing-Lab/openpose/issues?state=closed&page=15&per_page=100");
        readByURL("https://api.github.com/repos/CMU-Perceptual-Computing-Lab/openpose/issues?state=closed&page=16&per_page=100");
        readByURL("https://api.github.com/repos/CMU-Perceptual-Computing-Lab/openpose/issues?state=closed&page=17&per_page=100");
        readByURL("https://api.github.com/repos/CMU-Perceptual-Computing-Lab/openpose/issues?state=closed&page=18&per_page=100");
        readByURL("https://api.github.com/repos/CMU-Perceptual-Computing-Lab/openpose/issues?state=closed&page=19&per_page=100");
        System.out.println("Total Open Issue Amount is: " + count);
        System.out.println("Total Number list is: " + number);
        System.out.println("Total title list is: " + title);
        System.out.println("Total created at list is: " + created_at);
        System.out.println("Total updated at list is: " + updated_at);
        System.out.println("Total state list is: " + state);
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

        for (int i = 0; i  < jsonArray.size(); i++) {
            JSONObject jsonObject = (JSONObject) jsonArray.get(i);
            int order = i + 1;
            System.out.println("=================" + order + "th ? ==============");
            System.out.println(jsonObject.get("number"));
            number.add((Long) jsonObject.get("number"));
            System.out.println(jsonObject.get("title"));
            title.add((String) jsonObject.get("title"));
            System.out.println(jsonObject.get("created_at"));
            created_at.add((String) jsonObject.get("created_at"));
            System.out.println(jsonObject.get("updated_at"));
            updated_at.add((String) jsonObject.get("updated_at"));
            System.out.println(jsonObject.get("state"));
            state.add((String) jsonObject.get("state"));
            count++;
        }
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

    public List<String> getState() {
        return state;
    }

    public List<Long> getNumber() {
        return number;
    }

}
