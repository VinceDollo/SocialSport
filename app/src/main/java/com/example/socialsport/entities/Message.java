package com.example.socialsport.entities;

import androidx.annotation.NonNull;

import java.util.Date;

public class Message {
    private String message;
    private String date;
    private String idSender;

    public Message(String message, String date, String idSender) {
        this.message = message;
        this.date = date;
        this.idSender = idSender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getIdSender() {
        return idSender;
    }

    public void setIdSender(String idSender) {
        this.idSender = idSender;
    }
}
