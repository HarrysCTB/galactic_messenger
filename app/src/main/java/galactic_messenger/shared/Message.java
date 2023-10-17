package galactic_messenger.shared;

import java.io.Serializable;

public class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    private String sender;
    private String receiver;
    private String content;
    private MessageType type;

    public Message(String sender, String receiver, String content, MessageType type) {
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.type = type;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getContent() {
        return content;
    }

    public MessageType getType() {
        return type;
    }

    public enum MessageType {
        TEXT,
        FILE
    }
}