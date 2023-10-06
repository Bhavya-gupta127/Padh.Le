package com.example.project;

// stores the details of the user
public class Detail_Card {
    String Name; // name of the User
    String Email;
    String Uid; // Unique ID of the user (helps to uniquely recoganize a user among all) {its auto generated}
    String Category;

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public Detail_Card(){}
    Detail_Card(String name, String email, String uid) {
        this.Name = name;
        this.Email = email;
        this.Uid=uid;
    }
    Detail_Card(String name, String email, String uid, String category) {
        this.Name = name;
        this.Email = email;
        this.Uid=uid;
        this.Category=category;
    }
}
