package com.example.socialsport.entities;

public class User {

    private String email;
    private String name;
    private String age;

    public User(){
        email = "";
        name = "";
        age = "";
    }

    public User(String email, String name, String age){
        this.email = email;
        this.name = name;
        this.age = age;

        new User(); //Code smell
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

}
