package shag.com.shag.Models;

import java.util.ArrayList;

/**
 * Created by gabesaruhashi on 7/14/17.
 */

//TODO figure out what should be stored here
public class Chat {
    private String chatId;
    //TODO decide the properties of the chat class. Right now it is kind of redundant
    // TODO might be interesting to later invite ppl even if event expired in feed
    private ArrayList<String> chatParticipantsIds;

    // trivial variables
    private String chatTitle;
    private String chatIconUrl;
    private String description;


    // GETTERS & SETTERS
    public String getChatId() { return chatId; }

    public void setChatId(String chatId) { this.chatId = chatId; }

    public String getChatTitle() {
        return chatTitle;
    }

    public void setChatTitle(String chatTitle) {
        this.chatTitle = chatTitle;
    }

    public String getChatIconUrl() {
        return chatIconUrl;
    }

    public void setChatIconUrl(String chatIconUrl) {
        this.chatIconUrl = chatIconUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
