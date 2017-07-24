package shag.com.shag.Models;

import com.parse.ParseObject;

import java.util.ArrayList;

/**
 * Created by gabesaruhashi on 7/14/17.
 */

//TODO figure out what should be stored here
public class Chat {
    //TODO decide the properties of the chat class. Right now it is kind of redundant
    // TODO might be interesting to later invite ppl even if event expired in feed
    private String eventId;
    private ArrayList<String> chatParticipantsIds;
    private ParseObject event;

    // trivial variables
    private String chatTitle;
    private String chatIconUrl;
    private String description;

    // GETTERS & SETTERS


    public ParseObject getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public ArrayList<String> getChatParticipantsIds() { return chatParticipantsIds; }

    public void setChatParticipantsIds(ArrayList<String> chatParticipantsIds) { this.chatParticipantsIds = chatParticipantsIds; }

    public void addChatParticipantsIds(String newChatParticipant) {
        getChatParticipantsIds().add(newChatParticipant);
       }

    public String getChatTitle() {
        return chatTitle;
    }

    public void setChatTitle(String chatTitle) {
        this.chatTitle = chatTitle;
    }

    public String getChatIconUrl() {
        return chatIconUrl;
    }

    public void setChatIconUrl(String chatIconUrl) {
        this.chatIconUrl = chatIconUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
