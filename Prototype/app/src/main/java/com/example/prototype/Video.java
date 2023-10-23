package com.example.prototype;

public class Video {
    private String URL;
    private String date;
    private String time;

    public Video() {
        URL = "";
        date = "";
        time = "";
    }

    public Video(String URL, String date, String time) {
        this.URL = URL;
        this.date = date;
        this.time = time;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
