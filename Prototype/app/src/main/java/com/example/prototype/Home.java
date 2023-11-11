package com.example.prototype;

import java.util.ArrayList;
import java.util.List;

public class Home {
    private String name;
    private List<Member> members;

    public Home() {
        name = "";
        members = new ArrayList<>();
    }

    public Home(String name) {
        this.name = name;
        members = new ArrayList<>();
    }

    public Home(String name, List<Member> members) {
        this.name = name;
        this.members = members;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Member> getMembers() {
        return members;
    }

    public void setMembers(List<Member> members) {
        this.members = members;
    }

    public void addMember(Member member) {
        members.add(member);
    }
}
