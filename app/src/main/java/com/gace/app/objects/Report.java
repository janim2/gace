package com.gace.app.objects;

public class Report {

    String id,email,reason,message;

    public Report(){

    }

    public Report(String id, String email, String reason, String message) {
        this.id = id;
        this.email = email;
        this.reason = reason;
        this.message = message;
    }

    public String getId() {
        return id;
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
