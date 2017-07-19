package shag.com.shag.Models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.parse.GetCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
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
public class Memory extends ParseObject implements Parcelable {

    private String memoryName;
    private ArrayList<ParseFile> picturesParseFiles;
    private ArrayList<String> participantsIds;
    private String eventId; //id of the corresponding event

    //needs to implement empty constructor to be a Parse Object
    public Memory() {

    }

    //
    public static Memory fromParseObject(ParseObject object) {
        Memory memory = new Memory();
        memory.setMemoryName(object.getString("memory_name"));
        // memory.picturesParseFiles = getList("pictures_parse_files");
        memory.setEventId(object.getString("event_id"));
        memory.setParticipantsIds((ArrayList) object.getList("participants_ids"));
        memory.setPicturesParseFiles((ArrayList) object.getList("pictures_parse_files"));

        return memory;
    }

    public Memory(String eventDescription, final ArrayList<String> participantsIds, final String eventId) {
        // set values
        setMemoryName(eventDescription);
        setParticipantsIds(participantsIds);

        setEventId(eventId);

        picturesParseFiles = new ArrayList<ParseFile>();
        setPicturesParseFiles(picturesParseFiles);


        // save to memories database
        this.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Memory");
                    query.whereEqualTo("event_id", eventId); //get the event that just finished
                    query.getFirstInBackground(new GetCallback<ParseObject>() {
                        @Override
                        public void done(final ParseObject memoryObject, ParseException e) {
                            if (memoryObject != null) {
                                // create memory using event's info
                                final String id = memoryObject.getObjectId();
                                // for every participant, add memory to their memories
                                for (String participantId: participantsIds) {
                                    ParseQuery<ParseUser> query = ParseUser.getQuery();
                                    query.getInBackground(participantId, new GetCallback<ParseUser>() {
                                        @Override
                                        public void done(ParseUser user, ParseException e) {
                                            if (e == null) {
                                                List<ParseObject> memories;
                                                // TODO clean this once we fix the database
                                                if (user.getList("Memories_list") == null) {
                                                    memories = new ArrayList<ParseObject>();
                                                } else {
                                                    memories = user.getList("Memories_list");
                                                }

                                                memories.add(memoryObject);
                                                user.put("Memories_list", memories);
                                                user.saveInBackground(new SaveCallback() {
                                                    @Override
                                                    public void done(ParseException e) {
                                                        if (e == null) {
                                                            Log.i("SUCCESS", "SUCCESS");
                                                        } else {
                                                            Log.i("Nope", e.getMessage());
                                                        }
                                                    }
                                                });
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

    // GETTERS & SETTERS
    public String getMemoryName() {
        return memoryName;
    }

    public void setMemoryName(String memoryName) {
        this.memoryName = memoryName;
        put("memory_name", memoryName);
    }

    public ArrayList<ParseFile> getPicturesParseFiles() {
        return picturesParseFiles;
    }

    public void setPicturesParseFiles(ArrayList<ParseFile> picturesParseFiles) {
        this.picturesParseFiles = picturesParseFiles;
        put("pictures_parse_files", picturesParseFiles);

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
        put("participants_ids", participantsIds);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.memoryName);
        dest.writeStringList(this.participantsIds);
    }

    protected Memory(Parcel in) {
        this.memoryName = in.readString();
        this.participantsIds = in.createStringArrayList();
    }

    public static final Parcelable.Creator<Memory> CREATOR = new Parcelable.Creator<Memory>() {
        @Override
        public Memory createFromParcel(Parcel source) {
            return new Memory(source);
        }

        @Override
        public Memory[] newArray(int size) {
            return new Memory[size];
        }
    };
}
