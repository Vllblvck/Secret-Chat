package com.mypackage.messages;

import java.io.Serializable;

public class Message implements Serializable {
    private static final long serialVersionUID = 1L;
    private String msg;
    private MessageType type;
    private String recipient;

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
}
