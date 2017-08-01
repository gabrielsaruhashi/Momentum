package shag.com.shag.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

/**
 * Created by samrabelachew on 7/10/17.
 */
@ParseClassName("Event")
public class Event extends ParseObject implements Parcelable {

    // fields
    public String eventOwnerId;
    public String eventId;
    public long eventOwnerFbId;
    public double latitude;
    public double longitude;
    public String eventOwnerName;
    public String eventName;
    public LatLng latLng;
    public String description;
    public String location;
    public String category;
    public ArrayList<Long> friendsAtEvent;
    public ArrayList<String> participantsFacebookIds;
    public ArrayList<String> participantsIds;
    public Date deadline;
    public Date timeOfEvent;
    public ParseGeoPoint parseGeoPoint;
    public boolean isFirstCreated;
    public Map participantsLocations;
    private ParseObject eventOwner;
    private Double relevance;
    public boolean isEventPrivate;
    public boolean isRecommendationMade;
    public String time;
    public ParseObject lastMessageSent;


    //CONSTRUCTORS
    public Event() {
        friendsAtEvent = new ArrayList<>();
        participantsFacebookIds = new ArrayList<>();

    }
    // GETTERS

    public ParseObject getLastMessageSent() {
        return getParseObject("last_message_sent");
    }

    public void setLastMessageSent(ParseObject message) {
        this.lastMessageSent = message;
        put("last_message_sent", message);
    }

    public ArrayList<String> getParticipantsFacebookIds() {
        return (ArrayList) get("participants_facebook_ids");
    }

    public String getEventId() {
        return getObjectId();
    }

    public String getLocation() {
        return getString("location");
    }

    public String getCategory() {
        return getString("category");
    }

    public String getDescription() {
        return getString("description");
    }

    public Date getDeadline() {
        return getDate("deadline");
    }

    public String getEventOwnerName() {
        return getString("event_owner_name");
    }

    public LatLng getLatLng() {
        return this.latLng;
    }

    public double getLatitude() {
        return getDouble("latitude");
    }

    public double getLongitude() {
        return getDouble("longitude");
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
        put("latitude", latitude);
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
        put("longitude", longitude);
    }

    public ParseGeoPoint getParseGeoPoint() {
        ParseGeoPoint toReturn = getParseGeoPoint("parse_geo_point");
        return toReturn;
    }

    public ArrayList<String> getParticipantsIds() {
        return (ArrayList) get("participants_id");
    }

    // TODO Fix this
    public ParseUser getEventOwner() {
        //return User.fromParseObject(getParseObject("event_owner_name"));
        return getParseUser("User_event_owner");
    }

    public Date getTimeOfEvent() {
        return getDate("event_time");
    }

    //TODO improve this method
    public Double getRelevance() {
        return relevance;
    }

    public void setRelevance(Double relevance) {
        this.relevance = relevance;
    }

    public boolean getIsFirstCreated() {
        return getBoolean("is_first_created");

    }

    public boolean getIsEventPrivate() {
        return getBoolean("is_event_private");

    }

    public boolean getIsRecommendationMade() {
        return isRecommendationMade;
    }

    public String getTime() {
        return getString("time");
    }
    // SETTERS

    public void setTime(String time) {
        this.time = time;
        put("time", time);
    }

    public void setRecommendationMade(boolean recommendationMade) {
        this.isRecommendationMade = recommendationMade;
        put("is_recommendation_made", isRecommendationMade);
    }

    public void setParticipantsFacebookIds(ArrayList<String> participantsFacebookIds) {
        this.participantsFacebookIds = participantsFacebookIds;
        put("participants_facebook_ids", participantsFacebookIds);
    }

    public void setIsFirstCreated(boolean isFirstCreated) {
        this.isFirstCreated = isFirstCreated;
        put("is_first_created", isFirstCreated);
    }

    public void setIsEventPrivate(boolean isEventPrivate) {
        this.isEventPrivate = isEventPrivate;
        put("is_event_private", isEventPrivate);
    }

    public void setEventOwner(ParseObject eventOwner) {
        this.eventOwner = eventOwner;
        put("User_event_owner", eventOwner);
    }

    public void setFriendsAtEvent(ArrayList<Long> friendsAtEvent) {
        this.friendsAtEvent = friendsAtEvent;
        put("friendsAtEvent", friendsAtEvent);
    }

    public void setLocation(String location) {
        this.location = location;
        put("location", location);
    }

    public void setDescription(String description) {
        this.description = description;
        put("description", description);
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
        put("deadline", deadline);
    }

    public void setEventOwnerName(String eventOwnerName) {
        this.eventOwnerName = eventOwnerName;
        put("event_owner_name", eventOwnerName);
    }

    public void setEventOwnerFbId(long eventOwnerFbId) {
        this.eventOwnerFbId = eventOwnerFbId;
        put("event_owner_fb_id", eventOwnerFbId);
    }

    /*public void setLatLng(LatLng latLng) { put("lat_lng", latLng); } */
    public void setParseGeoPoint(ParseGeoPoint parseGeoPoint) {
        this.parseGeoPoint = parseGeoPoint;
        put("parse_geo_point", parseGeoPoint);
        setLatLng(new LatLng(parseGeoPoint.getLatitude(), parseGeoPoint.getLongitude()));
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public void setCategory(String category) {
        this.category = category;
        put("category", category);
    }

    public void setParticipantsIds(ArrayList<String> participantsIds) {
        this.participantsIds = participantsIds;
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

    public void setParticipantsLocations(Map locations) {
        this.participantsLocations = locations;
        put("participants_locations", participantsLocations);
    }

    public Map getParticipantsLocations() {
        return getMap("participants_locations");
    }


    /*//TODO: make sure timeOfEvent isn't causing any errors*/


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.eventId);
        dest.writeParcelable(this.latLng, flags);
        dest.writeString(this.eventName);
        dest.writeString(this.description);
        dest.writeString(this.location);
        dest.writeString(this.category);
        dest.writeList(this.friendsAtEvent);
        dest.writeList(this.participantsIds);
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
        dest.writeSerializable(this.timeOfEvent);
        dest.writeByte(this.isFirstCreated ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isEventPrivate ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isRecommendationMade ? (byte) 1 : (byte) 0);

    }

    protected Event(Parcel in) {
        this.eventId = in.readString();
        this.latLng = in.readParcelable(LatLng.class.getClassLoader());
        this.eventName = in.readString();
        this.description = in.readString();
        this.location = in.readString();
        this.category = in.readString();
        this.friendsAtEvent = new ArrayList<Long>();
        in.readList(this.friendsAtEvent, Long.class.getClassLoader());  //TODO: make sure no bugs
        this.participantsIds = new ArrayList<String>();
        in.readList(this.participantsIds, Long.class.getClassLoader());
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.timeOfEvent = (Date) in.readSerializable();
        this.isFirstCreated = in.readByte() != 0;
        this.isEventPrivate = in.readByte() != 0;
        this.isRecommendationMade = in.readByte() != 0;
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
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return event;
    }
}
