package shag.com.shag.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by samrabelachew on 7/10/17.
 */

public class Event implements Parcelable {

    // fields
    public LatLng latLng;
    public String eventName;
    public long eventId;
    public String description;
    public String location;
    public String genre;
    public String time;
    public ArrayList<String> friendsAtEvent;
    public User eventOwner;
    public ArrayList<Long> participantsIds;

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.latLng, flags);
        dest.writeString(this.eventName);
        dest.writeLong(this.eventId);
        dest.writeString(this.description);
        dest.writeString(this.location);
        dest.writeString(this.genre);
        dest.writeString(this.time);
        dest.writeStringList(this.friendsAtEvent);
        dest.writeParcelable(this.eventOwner, flags);
        dest.writeList(this.participantsIds);
    }

    public Event() {
    }

    protected Event(Parcel in) {
        this.latLng = in.readParcelable(LatLng.class.getClassLoader());
        this.eventName = in.readString();
        this.eventId = in.readLong();
        this.description = in.readString();
        this.location = in.readString();
        this.genre = in.readString();
        this.time = in.readString();
        this.friendsAtEvent = in.createStringArrayList();
        this.eventOwner = in.readParcelable(User.class.getClassLoader());
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
}
