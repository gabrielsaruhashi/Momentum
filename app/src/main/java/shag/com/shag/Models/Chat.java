package shag.com.shag.Models;

import com.parse.ParseException;
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

    public void setEvent(Event proposedEvent) {
        this.event = proposedEvent;
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

    public Event getParcelableEvent() {
        ParseObject object = getEvent();

        final Event event = new Event();
        event.eventId = object.getObjectId();
        event.deadline = object.getDate("deadline");
        event.eventOwnerName = object.getString("event_owner_name");
        event.eventName = object.getString("event_name");
        event.description = object.getString("description");
        event.location = object.getString("location");
        event.category = object.getString("category");
        event.eventOwnerFbId = object.getLong("event_owner_fb_id");
        event.participantsIds = (ArrayList) object.getList("participants_id");
        //event.parseGeoPoint = object.getParseGeoPoint("parse_geo_point");
        event.timeOfEvent = object.getDate("event_time");
        event.latitude = object.getDouble("latitude");
        event.longitude = object.getDouble("longitude");
        event.participantsLocations = object.getMap("participants_locations");

        // fetch event owner
        //TODO this runs slowly, try to figure out how to make it faster (this is also why chats take so long)
        try {
            ParseObject user = object.getParseObject("User_event_owner").fetch();
            // user found! Convert it to a user model
            event.setEventOwner(user); // setting event owner
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return event;
    }

}
