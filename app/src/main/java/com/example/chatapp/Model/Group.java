package com.example.chatapp.Model;

public class Group {
    private String message;
    private String sender;
    private boolean isseen;

    public Group(String message, String sender, boolean isseen) {
        this.message = message;
        this.sender = sender;
        this.isseen = isseen;
    }

    public Group(){}

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

    public boolean isIsseen() {
        return isseen;
    }

    public void setIsseen(boolean isseen) {
        this.isseen = isseen;
    }
}
