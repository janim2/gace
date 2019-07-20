package com.gace.app.objects;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

public class Post {

    public  String eventid;
    public String image;
    public String description;
    public String location;
    public String title;
    public String user;
    public String rate;
    public String prize;
    public String date;
    public String time;



    public Post(String eventid, String image,String description, String location,String title,
                String user,String rate,String prize,String date, String time) {

        this.eventid = eventid;
        this.image = image;
        this.description = description;
        this.location = location;
        this.title = title;
        this.user = user;
        this.rate = rate;
        this.prize = prize;
        this.date = date;
        this.time = time;


//        this.image = image;

    }

    public String getEventid(){return eventid; }

    public String getImage() { return image; }

    public String getDescription() { return description; }
//    public void setDescription(String description) {this.description = description; }

    public String getLocation() { return location; }
//    public void setLocation(String location) { this.location= location; }

    public String getTitle() { return title; }
//    public void setTitle(String title) { this.title = title; }

     public String getUser() { return user; }

     public String getRate() { return rate; }

     public String getPrize() { return prize; }

     public String getDate() { return date; }

     public String getTime() { return time; }
//    public void setUser(String user) { this.user= user; }
//
//     public String getImage() { return image; }
//    public void setImage(String image) { this.image= image; }


//    // [START post_to_map]
//    @Exclude
//    public Map<String, Object> toMap() {
//        HashMap<String, Object> result = new HashMap<>();
//
//
//        result.put("user",user);
//        result.put("title", title);
//        result.put("description", description);
//        result.put("location", location);
//        result.put("image",image);
//        return result;
//
//
//
//    }
}
