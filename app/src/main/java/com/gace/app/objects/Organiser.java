package com.gace.app.objects;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Organiser {

    private String id, name,email,reason,message;

    public Organiser(){

    }

    public Organiser(String id, String name, String email, String reason, String message) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.reason = reason;
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getReason() {
        return reason;
    }

    public String getMessage() {
        return message;
    }
}
