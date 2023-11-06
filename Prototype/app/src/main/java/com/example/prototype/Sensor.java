package com.example.prototype;

public class Sensor {

    String distance;
    String date;
    String time;

    public Sensor() {
        distance = "";
        date = "";
        time = "";
    }

    public Sensor(String distance, String date, String time) {
        this.distance = distance;
        this.date = date;
        this.time = time;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
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
