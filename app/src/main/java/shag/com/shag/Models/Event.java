package shag.com.shag.Models;

import java.util.ArrayList;

/**
 * Created by samrabelachew on 7/10/17.
 */

public class Event {

    //fields
    public String eventName;
    public String location;
    public String genre;
    public String time;
    public ArrayList<String> friendsAtEvent;

    //getters
    public String getEventName() {
        return eventName;
    }

    public String getLocation() {
        return location;
    }

    public String getGenre() {
        return genre;
    }

    public ArrayList<String> getFriendsAtEvent() {
        return friendsAtEvent;
    }


    //setters
    public void setFriendsAtEvent(ArrayList<String> friendsAtEvent) {
        this.friendsAtEvent = friendsAtEvent;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }
}
