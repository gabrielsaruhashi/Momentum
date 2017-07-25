package shag.com.shag.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    //poll creator
    public String getPollCreator() {
        return getString("poll_creator");

    }

    public void setPollCreator(String pollCreator) {
        this.pollCreator = pollCreator;
        put("poll_creator", pollCreator);
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
