package com.gace.app.objects;

public class Chat_group {

    public String key;
    public String isagroup;
    public String image;
    public String group_name;
    public String group_description;


    public Chat_group(){

    }

    public Chat_group(String group_key, String isgroup,String g_image, String g_name, String group_description) {

        this.key = group_key;
        this.isagroup = isgroup;
        this.image = g_image;
        this.group_name = g_name;
        this.group_description = group_description;

    }


    public String getIsagroup() {
        return isagroup;
    }

    public String getKey() {
        return key;
    }

    public String getImage() {
        return image;
    }

    public String getGroup_name() {
        return group_name;
    }

    public String getGroup_description() {
        return group_description;
    }
}
