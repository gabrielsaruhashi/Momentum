package shag.com.shag.Models;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by samrabelachew on 7/20/17.
 */

public class Poll {
    private String chatTitle;
    private ArrayList<String> choices;
    private HashMap<String,Double> scores;

    public String getChatTitle() {
        return chatTitle;
    }

    public void setChatTitle(String chatTitle) {
        this.chatTitle = chatTitle;
    }

    public ArrayList<String> getChoices() {
        return choices;
    }

    public void setChoices(ArrayList<String> choices) {
        this.choices = choices;
    }

    public HashMap<String, Double> getScores() {
        return scores;
    }

    public void setScores(HashMap<String, Double> scores) {
        this.scores = scores;
    }
}
