package com.appetesg.estusolucionTranscarga.modelos;


public class ChatModel {
    private String messageText;
    private String messageUser;
    private String messageUserId;
    private long messageTime;


    public ChatModel(String messageText, String messageUser, String messageUserId, long messageTime) {
        this.messageText = messageText;
        this.messageUser = messageUser;
        this.messageUserId = messageUserId;
        this.messageTime = messageTime;
    }

    public ChatModel(String messageText, String messageUser, String messageUserId) {
        this.messageText = messageText;
        this.messageUser = messageUser;
        this.messageUserId = messageUserId;
    }

    public ChatModel() {
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

    public String getMessageUserId() {
        return messageUserId;
    }

    public void setMessageUserId(String messageUserId) {
        this.messageUserId = messageUserId;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }
}
