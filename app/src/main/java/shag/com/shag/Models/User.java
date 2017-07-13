package shag.com.shag.Models;


import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by samrabelachew on 7/10/17.
 */

public class User implements Parcelable {


    //fields
    public String name;
    public String username;
    public String imageUrl;
    public long userID;
    public long fbUserID;
    public String phoneNumber;
    public ArrayList<Long> currentInterestsIds;
    //TODO: make sure to set this value when a user creates an event
    public ArrayList<Event> events;

    public static User fromJson(JSONObject json) {
        User u = new User();
        //i think you could only get here if you had your user id already, so you wouldn't be getting it from the JSON?
        try {
            u.setFbUserID(json.getLong("id"));
            u.setName(json.getString("name"));
            JSONObject pictureData = json.getJSONObject("picture").getJSONObject("data");
            u.imageUrl = pictureData.getString("url"); //this might be it's own endpoint??
            u.events = new ArrayList<Event>();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return u;
    }

    //getters
    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public long getUserID() {
        return userID;
    }

    public long getFbUserID() {
        return fbUserID;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    public String getPhoneNumber() { return phoneNumber; }

    //setters
    public void setFbUserID(long fbUserID) {
        this.fbUserID = fbUserID;
    }

    public void setUserID(long userID) {
        this.userID = userID;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.username);
        dest.writeString(this.imageUrl);
        dest.writeLong(this.userID);
        dest.writeLong(this.fbUserID);
        dest.writeString(this.phoneNumber);
        dest.writeList(this.currentInterestsIds);
    }

    public User() {
    }

    protected User(Parcel in) {
        this.name = in.readString();
        this.username = in.readString();
        this.imageUrl = in.readString();
        this.userID = in.readLong();
        this.fbUserID = in.readLong();
        this.phoneNumber = in.readString();
        this.currentInterestsIds = new ArrayList<Long>();
        in.readList(this.currentInterestsIds, Long.class.getClassLoader());
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
