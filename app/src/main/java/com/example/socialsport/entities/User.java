package com.example.socialsport.entities;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class User implements Serializable {

    private String email;
    private String name;
    private String age;
    private String image;

    public User() {
        email = "";
        name = "";
        age = "";
        image = null;
    }

    public User(String email, String name, String age, String image) {
        this.email = email;
        this.name = name;
        this.age = age;
        this.image = image;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @NonNull
    public String toString() {
        StringBuilder result = new StringBuilder("name : ");
        result.append(getName()).append(" - email : ").append(getEmail())
                .append(" - age : ").append(getAge())
                .append(" - image : ");
        if (getImage() != null) {
            result.append(getImage());
        } else {
            result.append("null");
        }
        return result.toString();
    }
}