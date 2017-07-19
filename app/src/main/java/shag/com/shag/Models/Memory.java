package shag.com.shag.Models;

import android.util.Log;

import com.parse.GetCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import static com.loopj.android.http.AsyncHttpClient.log;

/**
 * Created by gabesaruhashi on 7/18/17.
 */
@ParseClassName("Memory")
public class Memory extends ParseObject {
    private String memoryName;
    private ArrayList<String> picturesUrls;
    private ArrayList<String> participantsIds;
    private String eventId; //id of the corresponding event

    //needs to implement empty constructor to be a Parse Object
    public Memory() {

    }

    // GETTERS & SETTERS
    public String getMemoryName() {
        return memoryName;
    }

    public void setMemoryName(String memoryName) {
        this.memoryName = memoryName;
        put("memoryName", memoryName);
    }

    public ArrayList<String> getPicturesUrls() {
        return picturesUrls;
    }

    public void setPicturesUrls(ArrayList<String> picturesUrls) {
        this.picturesUrls = picturesUrls;
        //TODO: put these in db?
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
        put("event_id", eventId);
    }

    public String getEventId() {
        return this.eventId;
    }

    public ArrayList<String> getParticipantsIds() {
        return participantsIds;
    }

    public void setParticipantsIds(ArrayList<String> participantsIds) {
        this.participantsIds = participantsIds;
        put("participantsIds", participantsIds);
    }

    public Memory(String eventName, final ArrayList<String> participantsIds, final String eventId) {
        this.memoryName = "";
        if (eventName != null) {
            setMemoryName(eventName);
        }

        this.participantsIds = new ArrayList<String>();
        if (participantsIds != null) {
            setParticipantsIds(participantsIds);
        }

        this.eventId = "";
        if (eventId != null) {
            setEventId(eventId);
        }

        picturesUrls = new ArrayList<String>();

        saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    log.i("Memory", "MEMORY CREATED");

                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Memory");
                    query.whereEqualTo("event_id", eventId); //get the event we just created
                    query.getFirstInBackground(new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject object, ParseException e) {
                            if (object != null) {
                                final String id = object.getObjectId();
                                for (String participantId: participantsIds) {
                                    ParseQuery<ParseUser> query = ParseUser.getQuery();
                                    query.getInBackground(participantId, new GetCallback<ParseUser>() {
                                        @Override
                                        public void done(ParseUser user, ParseException e) {
                                            if (e == null) {
                                                List<String> memoriesIds = user.getList("memories_ids");
                                                memoriesIds.add(id);
                                                user.put("memories_ids", memoriesIds);
                                                user.saveInBackground();
                                            } else {
                                                Log.e("Memory error", "Error saving memory" + e);
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    });
                } else {
                    log.e("MyAlarm", "Error creating memory: " + e.toString());
                }
            }
        });
    }
}
