package shag.com.shag.Models;

import java.util.ArrayList;

/**
 * Created by gabesaruhashi on 7/18/17.
 */

public class Memory {
    private String memoryName;
    private ArrayList<String> picturesUrls;
    private ArrayList<String> participantsIds;

    // GETTERS & SETTERS
    public String getMemoryName() {
        return memoryName;
    }

    public void setMemoryName(String memoryName) {
        this.memoryName = memoryName;
    }

    public ArrayList<String> getPicturesUrls() {
        return picturesUrls;
    }

    public void setPicturesUrls(ArrayList<String> picturesUrls) {
        this.picturesUrls = picturesUrls;
    }

    public ArrayList<String> getParticipantsIds() {
        return participantsIds;
    }

    public void setParticipantsIds(ArrayList<String> participantsIds) {
        this.participantsIds = participantsIds;
    }
}
