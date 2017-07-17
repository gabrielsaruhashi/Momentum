package shag.com.shag.Models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

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

    // local variables
    private String senderId;
    private String eventId;

    public String getMessageId() {
        return getObjectId();
    }

    public String getEventId() { return getString("event_id"); }

    public void setEventId(String eventId) {
        this.eventId = eventId;
        put("event_id", eventId);
    }


    public String getBody() {
        return getString("message_body");
    }

    public void setBody(String body) {
        this.body = body;
        put("message_body", body);
    }

    public User getUserSender() {
        return userSender;
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
}