package shag.com.shag.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by samrabelachew on 7/10/17.
 */
@ParseClassName("Event")
public class Event extends ParseObject implements Parcelable {

    // fields
    public String eventOwnerId;
    public String eventId;
    public long eventOwnerFbId;
    public String eventOwnerName;
    public String eventName;
    public LatLng latLng;
    public String description;
    public String location;
    public String category;
    public ArrayList<Long> friendsAtEvent;
    public ArrayList<String> participantsIds;
    public Date deadline;
    public Date timeOfEvent;
    private User eventOwner;
    public ParseGeoPoint parseGeoPoint;
    public boolean isFirstCreated;

    //CONSTRUCTORS
    public Event(){
        friendsAtEvent = new ArrayList<>();

    }
    // GETTERS

    public String getEventId() {
        return eventId;
    }

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

    public ArrayList<String> getParticipantsIds() { return participantsIds;}

    public long getEventOwnerFbId() { return eventOwnerFbId; }

    public User getEventOwner() {
        return eventOwner;
    }

    public String getEventOwnerId() {
        return eventOwnerId;
    }

    public Date getTimeOfEvent() {return timeOfEvent; }

    public boolean getIsFirstCreated() {
        return getBoolean("is_first_created");
    }

    // SETTERS


    public void setIsFirstCreated(boolean isFirstCreated) {
        this.isFirstCreated = isFirstCreated;
        put("is_first_created", isFirstCreated);
    }
    public void setEventOwner(User eventOwner) {
        this.eventOwner = eventOwner;
        // TODO check if I need the line below
        // put("event_owner", eventOwner);
    }

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

    public void setParticipantsIds(ArrayList<String> participantsIds) {
        this.participantsIds=participantsIds;
        put("participants_id", participantsIds);
      }

    public void setEventOwnerId(String eventOwnerId) {
        this.eventOwnerId = eventOwnerId;
        put("event_owner_id", eventOwnerId);
    }

    //TODO: when this is called, update alarm to 24 hours after timeOfEvent
    public void setTimeOfEvent(Date timeOfEvent) {
        this.timeOfEvent = timeOfEvent;
        put("event_time", timeOfEvent);
    }

    public static Creator<Event> getCREATOR() {
        return CREATOR;
    }


    /*//TODO: make sure timeOfEvent isn't causing any errors*/

    public static Event fromParseObject(ParseObject object) {
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
        //todo: include time of event?
        // Retrieve location using objectId

        // fetch event owner
        //TODO this runs slowly, try to figure out how to make it faster (this is also why chats take so long)
        try {
            ParseObject user = object.getParseObject("User_event_owner").fetch();
            // user found! Convert it to a user model
            User eventOwner = User.fromParseObject(user);
            event.setEventOwner(eventOwner); // setting event owner
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return event;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.eventOwnerId);
        dest.writeString(this.eventId);
        dest.writeLong(this.eventOwnerFbId);
        dest.writeString(this.eventOwnerName);
        dest.writeString(this.eventName);
        dest.writeParcelable(this.latLng, flags);
        dest.writeString(this.description);
        dest.writeString(this.location);
        dest.writeString(this.category);
        dest.writeList(this.friendsAtEvent);
        dest.writeStringList(this.participantsIds);
        dest.writeLong(this.deadline != null ? this.deadline.getTime() : -1);
        dest.writeLong(this.timeOfEvent != null ? this.timeOfEvent.getTime() : -1);
        dest.writeParcelable(this.eventOwner, flags);
        dest.writeByte(this.isFirstCreated ? (byte) 1 : (byte) 0);
    }

    protected Event(Parcel in) {
        this.eventOwnerId = in.readString();
        this.eventId = in.readString();
        this.eventOwnerFbId = in.readLong();
        this.eventOwnerName = in.readString();
        this.eventName = in.readString();
        this.latLng = in.readParcelable(LatLng.class.getClassLoader());
        this.description = in.readString();
        this.location = in.readString();
        this.category = in.readString();
        this.friendsAtEvent = new ArrayList<Long>();
        in.readList(this.friendsAtEvent, Long.class.getClassLoader());
        this.participantsIds = in.createStringArrayList();
        long tmpDeadline = in.readLong();
        this.deadline = tmpDeadline == -1 ? null : new Date(tmpDeadline);
        long tmpTimeOfEvent = in.readLong();
        this.timeOfEvent = tmpTimeOfEvent == -1 ? null : new Date(tmpTimeOfEvent);
        this.eventOwner = in.readParcelable(User.class.getClassLoader());
        this.parseGeoPoint = in.readParcelable(ParseGeoPoint.class.getClassLoader());
        this.isFirstCreated = in.readByte() != 0;
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel source) {
            return new Event(source);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };
}
