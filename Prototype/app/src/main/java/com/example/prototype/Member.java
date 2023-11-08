package com.example.prototype;

public class Member {
    private String userID;
    private String accessLevel;

    public Member() {
        userID = "";
        accessLevel = "";
    }

    public Member(String userID, String accessLevel) {
        this.userID = userID;
        this.accessLevel = accessLevel;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(String accessLevel) {
        this.accessLevel = accessLevel;
    }
}
