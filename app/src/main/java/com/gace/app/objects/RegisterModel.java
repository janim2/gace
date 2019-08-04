package com.gace.app.objects;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class RegisterModel {

    private String id,username,email,phone,location,gender;

    public RegisterModel(){

    }


    public RegisterModel(String username, String email, String phone, String location, String gender) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.location = location;
        this.gender = gender;

    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getLocation() {
        return location;
    }

    public String getGender() {
        return gender;
    }


}
