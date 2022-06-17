package com.example.chatapp.Model;

public class BotReply {
    private String message;

    public BotReply(String message) {
        this.message = message;
    }

    public BotReply(){}

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
