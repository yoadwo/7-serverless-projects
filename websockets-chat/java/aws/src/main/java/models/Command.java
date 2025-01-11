package models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Command {
    private String roomId;
    private String message;
    private String action;

    public Command() { }

    public Command(String message, String action, String roomId) {
        this.message = message;
        this.action = action;
        this.roomId = roomId;
    }

    public String getMessage() {
        return message;
    }

    public String getAction() {
        return action;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }
}
