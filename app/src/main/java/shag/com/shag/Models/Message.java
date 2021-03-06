package shag.com.shag.Models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by gabesaruhashi on 7/14/17.
 */

@ParseClassName("Message")
public class Message extends ParseObject {
    private String messageId;
    // database variables
    private User userSender;
    private String body;
    private String senderProfileImageUrl;
    //TODO delete them
    // local variables
    private String senderId;
    private String eventId;
    private String senderName;
    private ArrayList<String> likedUsers;


    public List<String> getLikedUsers() {
        return getList("liked_users");
    }

    public void setLikedUsers(ArrayList<String> likedUsers) {
        this.likedUsers = likedUsers;
        put("liked_users", likedUsers);

    }

    public void updateLikedUsers(ArrayList<String> likedUsers, String newUser) {
        likedUsers.add(newUser);
        put("liked_users", likedUsers);

    }



    public String getMessageId() {
        return getObjectId();
    }

    public String getEventId() {
        return getString("event_id");
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
        put("event_id", eventId);
    }


    public String getBody() {
        return getString("message_body");
    }

    public Date getCreatedDate() {
        return getCreatedAt();
    }

    public void setBody(String body) {
        this.body = body;
        put("message_body", body);
    }

    public String getSenderId() {
        return getString("sender_id");
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
        put("sender_id", senderId);
    }

    public String getSenderProfileImageUrl() {
        return getString("sender_profile_image_url");
    }

    public void setSenderProfileImageUrl(String senderProfileImageUrl) {
        this.senderProfileImageUrl = senderProfileImageUrl;
        put("sender_profile_image_url", senderProfileImageUrl);
    }

    public void setUserSender(User userSender) {
        this.userSender = userSender;
        put("User_sender", userSender);
    }

    public ParseObject getUserSender() {
        return getParseObject("User_sender");
    }

    public String getSenderName() {
        return getString("sender_name");
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
        put("sender_name", senderName);

    }
}