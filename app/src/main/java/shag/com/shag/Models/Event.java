package shag.com.shag.Models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by samrabelachew on 7/10/17.
 */

@ParseClassName("Event")
public class Event extends ParseObject implements Parcelable, Comparable {


    // fields
    public String eventOwnerName;
    public String eventName;
    public LatLng latLng;
    public String description;
    public String location;
    public String category;
    public ArrayList<Long> friendsAtEvent;
    public long eventOwnerId;
    public long eventOwnerFbId;
    public ArrayList<Long> participantsIds;
    public Date deadline;
    public ParseGeoPoint parseGeoPoint;

    // GETTERS

    public String getLocation() { return location; }

    public String getCategory() {
        return category;
    }

    public ArrayList<Long> getFriendsAtEvent() {
        return friendsAtEvent;
    }

    public String getDescription() { return description; }

    public Date getDeadline() {
        return deadline;
    }

    public String getEventOwnerName() { return eventOwnerName; }

    public LatLng getLatLng() { return latLng; }
    public ParseGeoPoint getParseGeoPoint() { return parseGeoPoint; }

    public ArrayList<Long> getParticipantsIds() { return participantsIds;}
    public long getEventOwnerId() { return eventOwnerId; }

    // SETTERS
    public void setFriendsAtEvent(ArrayList<Long> friendsAtEvent) {
        this.friendsAtEvent = friendsAtEvent;
        put("friendsAtEvent", friendsAtEvent);
    }

    public void setLocation(String location) {
        this.location=location;
        put("location", location); }

    public void setDescription(String description) {
        this.description=description;
        put("description", description);
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
        put("deadline", deadline);
    }

    public void setEventOwnerName(String eventOwnerName) {
        this.eventOwnerName = eventOwnerName;
        put("event_owner_name", eventOwnerName); }

    public void setEventOwnerFbId(long eventOwnerFbId) {
        this.eventOwnerFbId = eventOwnerFbId;
        put("event_owner_fb_id", eventOwnerFbId);
    }

    /*public void setLatLng(LatLng latLng) { put("lat_lng", latLng); } */
    public void  setParseGeoPoint(ParseGeoPoint parseGeoPoint){
        this.parseGeoPoint=parseGeoPoint;
        put("parse_geo_point", parseGeoPoint);
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
        put("lat_lng", latLng); }

    public void setCategory(String category) {
        this.category=category;
        put("category", category); }


    public void setEventOwnerId(long eventOwnerId) {
        this.eventOwnerId=eventOwnerId;
        put("event_owner_id", eventOwnerId); }

    public void setParticipantsIds(ArrayList<Long> participantsIds) {
        this.participantsIds=participantsIds;
        this.participantsIds = participantsIds; }

    public static Creator<Event> getCREATOR() {
        return CREATOR;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeParcelable(this.latLng, flags);
        dest.writeString(this.eventName);
        dest.writeString(this.description);
        dest.writeString(this.location);
        dest.writeString(this.category);
        dest.writeList(this.friendsAtEvent);
        dest.writeLong(this.eventOwnerId);
        dest.writeList(this.participantsIds);
    }

    public Event() {
    }

    protected Event(Parcel in) {

        this.latLng = in.readParcelable(LatLng.class.getClassLoader());
        this.eventName = in.readString();
        this.description = in.readString();
        this.location = in.readString();
        this.category = in.readString();
        this.friendsAtEvent = new ArrayList<Long>();
        in.readList(this.participantsIds, Long.class.getClassLoader());
        this.eventOwnerId = in.readLong();
        this.participantsIds = new ArrayList<Long>();
        in.readList(this.participantsIds, Long.class.getClassLoader());
    }

    public static final Parcelable.Creator<Event> CREATOR = new Parcelable.Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel source) {
            return new Event(source);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    //TODO: change time to Date object or add one so we can compare events
    @Override
    public int compareTo(@NonNull Object o) {
        return this.deadline.compareTo(((Event) o).deadline);
    }
    /*
    @Override
    public String toString() {
        return "Event details: " +
                "\ndescription: " + description +
                "\ndeadline: " + deadline.toString() +
                "\nevent owner id: " + eventOwnerId +
                "\nowner name : " + eventOwnerName;
    }*/

    public static Event fromParseObject(ParseObject object) {
        Event event = new Event();
        Object obj = object.get("state");
        event.deadline = object.getDate("deadline");
        event.eventOwnerName = object.getString("event_owner_name");
        event.eventName = object.getString("event_name");
        event.description = object.getString("description");
        event.location = object.getString("location");
        event.category = object.getString("category");
        event.eventOwnerId = object.getLong("event_owner_id");
        event.eventOwnerFbId = object.getLong("event_owner_fb_id");
        return event;
    }
}
