package shag.com.shag.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by samrabelachew on 7/20/17.
 */

public class Poll implements Parcelable {
    private String chatTitle;
    private String question;

    private ArrayList<String> choices;
    private HashMap<String,Integer> scores;

    public ArrayList<String> peopleVoted;

    //chat name
    public String getChatTitle() {
        return chatTitle;
    }
    public void setChatTitle(String chatTitle) {
        this.chatTitle = chatTitle;
    }


    //question
    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;}

    //choices
    public ArrayList<String> getChoices() {
        return choices;
    }

    public void setChoices(ArrayList<String> choices) {
        this.choices = choices;
    }


    //scores
    public void setScores(HashMap<String, Integer> scores) { this.scores = scores;
    }
    public HashMap<String, Integer> getScores() { return scores; }

    public void updateScores(HashMap<String, Integer> scores, String choice) {
        int currentScore = scores.get(choice);
        scores.put(choice, currentScore+1);
    }

    //people who have voted
    public ArrayList<String> getPeopleVoted() {
        return peopleVoted;
    }

    public void setPeopleVoted(ArrayList<String> peopleVoted) {
        this.peopleVoted = peopleVoted;
    }

    public void updatePeopleVoted(ArrayList<String> peopleVoted, String user) {
        peopleVoted.add(user);

    }


    public Poll() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.chatTitle);
        dest.writeString(this.question);
        dest.writeStringList(this.choices);
        dest.writeSerializable(this.scores);
        dest.writeStringList(this.peopleVoted);
    }

    protected Poll(Parcel in) {
        this.chatTitle = in.readString();
        this.question = in.readString();
        this.choices = in.createStringArrayList();
        this.scores = (HashMap<String, Integer>) in.readSerializable();
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
