package shag.com.shag.Models;


import android.os.Parcel;
import android.os.Parcelable;

import com.parse.ParseObject;

import org.json.JSONObject;

/**
 * Created by samrabelachew on 7/10/17.
 */

public class User implements Parcelable {

    //fields
    public String name;
    public String imageUrl;
    public String userID;
    public long fbUserID;
    public String phoneNumber; //TODO get phone number

    // for retrieving facebook data
    public static User fromJson(JSONObject json) {
        User user = new User();
//        try {
//            user.setFbUserID(json.getLong("id"));
//            user.setName(json.getString("name"));
//            JSONObject pictureData = json.getJSONObject("picture").getJSONObject("data");
//            user.imageUrl = pictureData.getString("url"); //this might be it's own endpoint??
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
        return user;
    }

    // for creating user when retrieving data from database
    public static User fromParseObject(ParseObject object) {
        User user = new User();
        // populate user info
        user.setUserID(object.getObjectId());
        //TODO decide if we need this and find out if it works (haven't tested)
        /*
        try {
            user.setFbUserID(object.getJSONObject("auth_data_facebook").getLong("id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
        user.setName(object.getString("name"));
        user.setImageUrl(object.getString("profile_image_url"));

        return user;
    }

    //getters
    public String getName() {
        return name;
    }

    public String getUserID() {
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

    public void setUserID(String userID) {
        this.userID = userID;
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
        dest.writeString(this.imageUrl);
        dest.writeString(this.userID);
        dest.writeLong(this.fbUserID);
        dest.writeString(this.phoneNumber);
    }

    public User() {
    }

    protected User(Parcel in) {
        this.name = in.readString();
        this.imageUrl = in.readString();
        this.userID = in.readString();
        this.fbUserID = in.readLong();
        this.phoneNumber = in.readString();

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
