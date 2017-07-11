package shag.com.shag.Models;

import java.util.ArrayList;

/**
 * Created by samrabelachew on 7/10/17.
 */

public class User {

    //fields
    public String name;
    public String username;
    public long userID;
    public long fbUserID;
    public String phoneNumber;
    public ArrayList<Long> currentInterestsIds;


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
