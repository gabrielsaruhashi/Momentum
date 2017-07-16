package shag.com.shag.Models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by gabesaruhashi on 7/14/17.
 */

@ParseClassName("Message")
public class Message extends ParseObject {
    private String messageId;
    private String eventId;
    private User userSender;
    private String body;

    public String getMessageId() {
        return messageId;
    }

    public String getEventId() { return eventId; }

    public void setEventId(String eventId) {
        this.eventId = eventId;
        put("event_id", eventId);
    }


    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
        put("message_body", body);
    }

    public User getUserSender() {
        return userSender;
    }

    public void setUserSender(User userSender) {
        this.userSender = userSender;
        put("user_sender", userSender);
    }
}