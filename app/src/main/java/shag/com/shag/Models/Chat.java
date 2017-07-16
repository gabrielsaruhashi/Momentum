package shag.com.shag.Models;

import java.util.ArrayList;

/**
 * Created by gabesaruhashi on 7/14/17.
 */

public class Chat {
    private String chatId;
    private String eventId;
    private String eventOwner;
    private ArrayList<String> participantsIds;
    private ArrayList<Message> messagesIds;
    private String description;

    /*
    public Chat(String eventId, ArrayList<String> participantsIds) {
        this.eventId = eventId;
        this.participantsIds = participantsIds;
    } */

    // GETTERS & SETTERS

    public String getChatId() {
        return chatId;
    }

    public String getEventId() {
        return eventId;
    }

    public ArrayList<String> getParticipantsIds() {
        return participantsIds;
    }

    public ArrayList<Message> getMessagesIds() {
        return messagesIds;
    }

    public void setParticipantsIds(ArrayList<String> participantsIds) {
        this.participantsIds = participantsIds;
    }

    public void setMessagesIds(ArrayList<Message> messagesIds) {
        this.messagesIds = messagesIds;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
