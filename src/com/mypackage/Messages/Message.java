package com.mypackage.Messages;

import java.io.Serializable;

public class Message implements Serializable {
    private static final long serialVersionUID = 1L;
    private String msg;
    private MessageType type;

    public Message(MessageType type, String msg) {
        this.type = type;
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public MessageType getType() {
        return type;
    }
}
