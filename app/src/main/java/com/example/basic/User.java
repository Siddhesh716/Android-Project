package com.example.basic;

public class User {
    private String name, mobile, age, email, weight;

    public User(String name, String mobile, String age, String email, String weight) {
        this.name = name;
        this.mobile = mobile;
        this.age = age;
        this.email = email;
        this.weight = weight;
    }

    public String getName() { return name; }
    public String getMobile() { return mobile; }
    public String getAge() { return age; }
    public String getEmail() { return email; }
    public String getWeight() { return weight; }
}
