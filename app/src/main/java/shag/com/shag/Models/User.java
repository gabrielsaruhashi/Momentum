package shag.com.shag.Models;


import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

/**
 * Created by samrabelachew on 7/10/17.
 */
public class User {

    //fields
    public String name;
    public String username;
    public String imageUrl;
    public long userID;
    public long fbUserID;
    public String phoneNumber;
    public ArrayList<Long> currentInterestsIds;

    public static User fromJson(JSONObject json) {
        User u = new User();
        //i think you could only get here if you had your user id already, so you wouldn't be getting it from the JSON?
        try {
            u.setFbUserID(json.getLong("id"));
            u.setName(json.getString("name"));
            //JSONObject picture = json.getObject("picture");
            //u.setProfilePicture(picture.getString("url"); //this might be it's own endpoint??

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
}
