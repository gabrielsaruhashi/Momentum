package shag.com.shag.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import shag.com.shag.Other.ParseApplication;

/**
 * Created by samrabelachew on 7/20/17.
 */

@ParseClassName("Poll")
public class Poll extends ParseObject implements Parcelable {
    private String eventId;
    private String chatTitle;
    private String question;
    private List<String> choices;
    //private ArrayList<String> choices;
    private Map<String,Integer> scores;
    private String pollCreator;
    private List<String> peopleVoted;
    private Event event;
    private String pollWinner;
    private String pollType;
    private Map<String,ParseGeoPoint> locationOptions;
    private ParseUser pollOwner;
    private String pollOwnerId;

    public Poll() {

    }


    public String getPollId() {
        return getObjectId();
    }

    //event ID
    public void setEventId(String eventId) {
        this.eventId = eventId;
        put("event_id", eventId);
    }

    public String getEventId() {
        return getString("event_id");
    }



    //event
    public void setEvent(Event event) {
        this.event = event;
        put("Event", event);
    }




    public void setPollCreator() {
        put("Poll_creator", ParseApplication.getCurrentUser());
    }


    //chat name
    public String getChatTitle() {
        return chatTitle;
    }
    public void setChatTitle(String chatTitle) {
        this.chatTitle = chatTitle;
        put("chat_Title", chatTitle);
    }


    //question
    public String getQuestion() {
        return getString("question");
    }

    public void setQuestion(String question) {
        this.question = question;
        put("question",question);
    }

    //choices
    public List<String> getChoices() {
        return getList("choices");
    }

    public void setChoices(List<String> choices) {
        this.choices = choices;
        put("choices", choices);
    }

    public void addChoices (List<String> choices, String newChoice) {

        choices.add(newChoice);
        put("choices", choices);
    }


    //scores
    public void setScores(Map<String, Integer> scores) {
        this.scores = scores;
        put("scores", scores);
    }
    public Map<String, Integer> getScores() { return getMap("scores"); }

    public void updateScores(Map<String, Integer> scores, String choice) {
        int currentScore = scores.get(choice);
        scores.put(choice, currentScore+1);
        put("scores",scores);
    }

    //location options (if a location poll)
    public Map<String, ParseGeoPoint> getLocationOptions() {
        return getMap("location_options");

    }

    public void setLocationOptions(Map<String, ParseGeoPoint> locationOptions) {
        this.locationOptions = locationOptions;
        put("location_options", locationOptions);
    }

    //people who have voted
    public List<String> getPeopleVoted() {
        return getList("people_voted");
    }

    public void setPeopleVoted(List<String> peopleVoted) {
        this.peopleVoted = peopleVoted;
        put("people_voted",peopleVoted);
    }

    public void updatePeopleVoted(List<String> peopleVoted, String user) {
        peopleVoted.add(user);
        put("people_voted",peopleVoted);

    }

    //poll type
    public String getPollType() {
        return getString("poll_type");
    }

    public void setPollType(String pollType) {
        this.pollType = pollType;
        put("poll_type", pollType);
    }

    //poll winner
    public String getPollWinner() {
        return getString("poll_winner");
    }

    public void setPollWinner(String pollWinner) {
        this.pollWinner = pollWinner;
        put("poll_winner", pollWinner);
    }
    //poll owner
    public ParseUser getPollCreator() {
        return getParseUser("Poll_creator");
    }

    public void setPollCreator(ParseUser pollOwner) {
        this.pollOwner = pollOwner;
        put("Poll_creator", pollWinner);

    }

    //poll owner Id
    public String getPollOwnerId() {
        return getString("poll_creator_id");
    }

    public void setPollOwnerId(String pollOwnerId) {
        this.pollOwnerId = pollOwnerId;
        put("poll_creator_id", pollWinner);

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.eventId);
        dest.writeString(this.chatTitle);
        dest.writeString(this.question);
        dest.writeStringList(this.choices);
        dest.writeInt(this.scores.size());
        for (Map.Entry<String, Integer> entry : this.scores.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeValue(entry.getValue());
        }
        dest.writeString(this.pollCreator);
        dest.writeStringList(this.peopleVoted);
    }

    protected Poll(Parcel in) {
        this.eventId = in.readString();
        this.chatTitle = in.readString();
        this.question = in.readString();
        this.choices = in.createStringArrayList();
        int scoresSize = in.readInt();
        this.scores = new HashMap<String, Integer>(scoresSize);
        for (int i = 0; i < scoresSize; i++) {
            String key = in.readString();
            Integer value = (Integer) in.readValue(Integer.class.getClassLoader());
            this.scores.put(key, value);
        }
        this.pollCreator = in.readString();
        this.peopleVoted = in.createStringArrayList();
    }

    public static final Creator<Poll> CREATOR = new Creator<Poll>() {
        @Override
        public Poll createFromParcel(Parcel source) {
            return new Poll(source);
        }

        @Override
        public Poll[] newArray(int size) {
            return new Poll[size];
        }
    };
}
