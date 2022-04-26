package com.example.socialsport.entities;

import androidx.annotation.NonNull;

public class Message {
    private final String messageContent;
    private String date;
    private final String idSender;

    public Message(String message, String date, String idSender) {
        this.messageContent = message;
        this.date = date;
        this.idSender = idSender;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


    @NonNull
    @Override
    public String toString() {
        return "Message{" +
                "message='" + messageContent + '\'' +
                ", date='" + date + '\'' +
                ", idSender='" + idSender + '\'' +
                '}';
    }
}
