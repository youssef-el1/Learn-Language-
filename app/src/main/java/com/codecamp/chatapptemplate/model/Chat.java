package com.codecamp.chatapptemplate.model;

public class Chat {
    private String chatImage;
    private String chatName;
    private String chatMessage;
    private String id;

    public Chat(String chatImage, String chatName, String chatMessage, String id) {
        this.chatImage = chatImage;
        this.chatName = chatName;
        this.chatMessage = chatMessage;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getChatImage() {
        return chatImage;
    }

    public void setChatImage(String chatImage) {
        this.chatImage = chatImage;
    }

    public String getChatName() {
        return chatName;
    }

    public void setChatName(String chatName) {
        this.chatName = chatName;
    }

    public String getChatMessage() {
        return chatMessage;
    }

    public void setChatMessage(String chatMessage) {
        this.chatMessage = chatMessage;
    }
}
