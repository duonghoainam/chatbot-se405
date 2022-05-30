package com.example.chatapp.Model;

public class GroupMessage {
    private String message;
    private String sender;

    public GroupMessage(String message, String sender) {
        this.message = message;
        this.sender = sender;
    }

    public GroupMessage(){}

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }


}
