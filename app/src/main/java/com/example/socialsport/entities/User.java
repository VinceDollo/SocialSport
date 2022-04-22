package com.example.socialsport.entities;

import android.graphics.Bitmap;

import java.io.Serializable;

public class User implements Serializable {

    private String email;
    private String name;
    private String age;
    private String profileImage;

    public User(){
        email = "";
        name = "";
        age = "";
        profileImage = null;
    }

    public User(String email, String name, String age, String bitmap){
        this.email = email;
        this.name = name;
        this.age = age;
        this.profileImage = bitmap;
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

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String toString(){
        String fin = "name : " + getName() + " - email : "+ getEmail() + " - age : "+ getAge();
        if(getProfileImage()!=null){
            fin+= " - image : "+getProfileImage();
        }
        return fin;
    }
}
