package shag.com.shag.Models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import org.json.JSONObject;

/**
 * Created by gabesaruhashi on 7/14/17.
 */

@ParseClassName("Message")
public class Message extends ParseObject {
    private String messageId;
    private String eventId;
    private User userSender;
    //TODO temporary variable
    private String senderId;
    private String body;

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
        JSONObject json = getJSONObject("user_sender");
        User userSender = User.fromJson(json);
        return userSender;
    }

    public void setUserSender(User userSender) {
        this.userSender = userSender;
        put("user_sender", userSender);
    }

    public String getSenderId() {
        return getString("sender_id");
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
        put("sender_id", senderId);
    }
}