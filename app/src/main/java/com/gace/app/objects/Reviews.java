package com.gace.app.objects;

public class Reviews {

    public String message;
    public String title;
    public String name;

    public Reviews(){

    }

    public Reviews(String message, String title,String name) {

        this.message = message;
        this.title = title;
        this.name = name;
    }


    public String getMessage(){
        return message;
    }

    public String getTitle(){
        return title;
    }

    public String getName(){
        return name;
    }

}
