package com.gace.app.objects;

public class ChatMessage {

    private String messageText;
    private String messageUser;
    private String documentName;
    private long messageTime;

    public ChatMessage(String messageText, long messageTime, String documentName, String messageUser) {
        this.messageText = messageText;
        this.messageTime = messageTime;
        this.documentName = documentName;
        this.messageUser = messageUser;
    }

    public ChatMessage(String s){

    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageUser() {
        return messageUser;
    }

    public void setMessageUser(String messageUser) {
        this.messageUser = messageUser;
    }

    public String getDocumentName() {
        return documentName;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }
}