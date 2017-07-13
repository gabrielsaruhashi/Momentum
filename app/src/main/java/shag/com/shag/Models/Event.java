package shag.com.shag.Models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by samrabelachew on 7/10/17.
 */

@ParseClassName("Event")
public class Event extends ParseObject implements Parcelable, Comparable {


    // fields
    public LatLng latLng;
    public String eventName;
    public String description;
    public String location;
    public String category;
    public String time;
    public ArrayList<String> friendsAtEvent;
    public long eventOwnerId;
    public String eventOwnerName;
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

    public String getCategory() {
        return category;
    }

    public ArrayList<String> getFriendsAtEvent() {
        return friendsAtEvent;
    }

    public String getDescription() { return description; }

    public Date getDeadline() {
        return deadline;
    }

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

    public void setGenre(String category) {
        this.category = category;
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
        dest.writeString(this.description);
        dest.writeString(this.location);
        dest.writeString(this.category);
        dest.writeString(this.time);
        dest.writeStringList(this.friendsAtEvent);
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
        this.time = in.readString();
        this.friendsAtEvent = in.createStringArrayList();
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

    @Override
    public String toString() {
        return "Event details: name: " + eventName +
                "\ndescription: " + description +
                "\ndeadline: " + deadline.toString() +
                "\nevent owner id: " + eventOwnerId +
                "\nowner name : " + eventOwnerName;
    }
}
