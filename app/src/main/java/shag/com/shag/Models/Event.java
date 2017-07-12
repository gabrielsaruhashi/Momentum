package shag.com.shag.Models;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by samrabelachew on 7/10/17.
 */

public class Event implements Comparable {

    // fields
    public String eventName;
    public long eventId;
    public String description;
    public String location;
    public String genre;
    public String time;
    public ArrayList<String> friendsAtEvent;
    public User eventOwner;
    public ArrayList<Long> participantsIds;
    public Date deadline;
    public Date createdAt;

    // getters
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
    public String getDescription() { return description; }


    // setters
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

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    //TODO: change time to Date object or add one so we can compare events
    @Override
    public int compareTo(@NonNull Object o) {
        return this.deadline.compareTo(((Event) o).deadline);
    }
}
