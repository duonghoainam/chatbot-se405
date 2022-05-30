package com.example.chatapp.Model;

import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners;

import java.util.List;

public class Group {
    private String admin;
    private String name;
    private List<GroupMessage> groupMessages;
    private List<String> users;

    public List<GroupMessage> getGroupMessages() {
        return groupMessages;
    }

    public void setGroupMessages(List<GroupMessage> groupMessages) {
        this.groupMessages = groupMessages;
    }

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Group(String admin, String name, List<GroupMessage> groupMessages, List<String> users) {
        this.admin = admin;
        this.name = name;
        this.groupMessages = groupMessages;
        this.users = users;
    }

    public Group(){}
}
