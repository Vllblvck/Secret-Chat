package com.mypackage.messages;

import java.io.Serializable;

public class Message implements Serializable {
    private static final long serialVersionUID = 1L;
    private String msg; //TODO encrypt messages
    private String recipient;
    private String sender;
    private MessageType type;

    public Message(MessageType type, String msg) {
        this.type = type;
        this.msg = msg;
    }

    public Message(MessageType type, String recipient, String msg) {
        this.type = type;
        this.recipient = recipient;
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public MessageType getType() {
        return type;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
}
