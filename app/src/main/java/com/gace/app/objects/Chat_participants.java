package com.gace.app.objects;

public class Chat_participants {

    private String participant_number;
    private String participant_id;
    private String participant_image;
    private String participant_name;

    public Chat_participants(String p_number, String p_id, String p_image, String p_name) {
        this.participant_number = p_number;
        this.participant_id = p_id;
        this.participant_image = p_image;
        this.participant_name = p_name;
    }

    public Chat_participants(String s){

    }

    public String getParticipant_number() {
        return participant_number;
    }

    public String getParticipant_id() {
        return participant_id;
    }

    public String getParticipant_image() {
        return participant_image;
    }

    public String getParticipant_name() {
        return participant_name;
    }


}