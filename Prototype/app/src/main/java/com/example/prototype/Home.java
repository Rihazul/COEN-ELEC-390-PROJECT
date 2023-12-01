package com.example.prototype;

import java.util.HashMap;
import java.util.Map;

public class Home {
    private String name;
    private Map<String, String> members;

    public Home() {
        name = "";
        members = new HashMap<>();
    }

    public Home(String name) {
        this.name = name;
        members = new HashMap<>();
    }

    public Home(String name, Map<String, String> members) {
        this.name = name;
        this.members = members;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getMembers() {
        return members;
    }

    public void setMembers(Map<String, String> members) {
        this.members = members;
    }

    public void addMember(String userID, String member) {
        members.put(userID, member);
    }
}
