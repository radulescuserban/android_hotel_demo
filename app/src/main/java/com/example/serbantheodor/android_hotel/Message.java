package com.example.serbantheodor.android_hotel;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

/**
 * Created by Serban Theodor on 08-Mar-16.
 */

public class Message implements Serializable {
    String id, message, user_id, createdAt;
    public Message() {
    }

    public Message(String id, String message, String user_id, String createdAt) {
        this.id = id;
        this.user_id = user_id;
        this.message = message;
        this.createdAt = createdAt;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }


}