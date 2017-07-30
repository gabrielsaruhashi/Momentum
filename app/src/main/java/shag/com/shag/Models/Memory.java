package shag.com.shag.Models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.util.ArrayList;

import static com.loopj.android.http.AsyncHttpClient.log;

/**
 * Created by gabesaruhashi on 7/18/17.
 */


@ParseClassName("Memory")
public class Memory extends ParseObject implements Parcelable {
    private String memoryId;
    private String memoryName;
    private ArrayList<ParseFile> picturesParseFiles;
    private ArrayList<String> participantsIds;
    private ArrayList<String> participantsFacebookIds;
    private String eventId; //id of the corresponding event
    private long facebookAlbumId;
    private int indexOfLastPictureShared;

    //needs to implement empty constructor to be a Parse Object
    public Memory() {

    }

    //
    public static Memory fromParseObject(ParseObject object) {
        Memory memory = new Memory();
        memory.setMemoryId(object.getObjectId());
        memory.setMemoryName(object.getString("memory_name"));
        // memory.picturesParseFiles = getList("pictures_parse_files");
        memory.setEventId(object.getString("event_id"));
        memory.setParticipantsIds((ArrayList) object.getList("participants_ids"));
        memory.setPicturesParseFiles((ArrayList) object.getList("pictures_parse_files"));

        return memory;
    }

    public Memory(String eventDescription, final ArrayList<String> participantsIds, final String eventId, final ArrayList<String> facebookIds) {
        // set values
        setMemoryName(eventDescription);
        setParticipantsIds(participantsIds);
        setEventId(eventId);
        setParticipantsFacebookIds(facebookIds);
        picturesParseFiles = new ArrayList<ParseFile>();
        setPicturesParseFiles(picturesParseFiles);


        // save to memories database
        this.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.i("success", "memory created");
                    /*
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
                    });*/
                } else {
                    log.e("MyAlarm", "Error creating memory: " + e.toString());
                }
            }
        });
    }

    // GETTERS & SETTERS


    public int getIndexOfLastPictureShared() {
        return getInt("index_of_last_picture_shared");
    }

    public void setIndexOfLastPictureShared(int indexOfLastPictureShared) {
        this.indexOfLastPictureShared = indexOfLastPictureShared;
        put("index_of_last_picture_shared", indexOfLastPictureShared);
    }

    public ArrayList<String> getParticipantsFacebookIds() {
        return (ArrayList) get("participants_facebook_ids");
    }

    public void setParticipantsFacebookIds(ArrayList<String> participantsFacebookIds) {
        this.participantsFacebookIds = participantsFacebookIds;
        put("participants_facebook_ids", participantsFacebookIds);
    }

    public long getFacebookAlbumId() {
        return getLong("facebook_album_id");
    }

    public void setFacebookAlbumId(long facebookAlbumId) {
        this.facebookAlbumId = facebookAlbumId;
        put("facebook_album_id", facebookAlbumId);
    }


    public String getMemoryName() {
        return getString("memory_name");
    }

    public void setMemoryName(String memoryName) {
        this.memoryName = memoryName;
        put("memory_name", memoryName);
    }

    public ArrayList<ParseFile> getPicturesParseFiles() {
        return (ArrayList) get("pictures_parse_files");
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
        return getString("event_id");
    }

    public ArrayList<String> getParticipantsIds() {
        return (ArrayList) getList("participants_ids");
    }

    public void setParticipantsIds(ArrayList<String> participantsIds) {
        this.participantsIds = participantsIds;
        put("participants_ids", participantsIds);
    }


    public void setMemoryId(String memoryId) {
        this.memoryId = memoryId;
    }

    public String getMemoryId() {
        return getObjectId();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.memoryName);
        dest.writeList(this.picturesParseFiles);
        dest.writeStringList(this.participantsIds);
        dest.writeString(this.eventId);
        dest.writeString(this.memoryId);

    }

    protected Memory(Parcel in) {
        this.memoryName = in.readString();
        this.picturesParseFiles = new ArrayList<ParseFile>();
        in.readList(this.picturesParseFiles, ParseFile.class.getClassLoader());
        this.participantsIds = in.createStringArrayList();
        in.readList(this.participantsIds, String.class.getClassLoader());
        this.eventId = in.readString();
        this.memoryId = in.readString();
    }

    public static final Creator<Memory> CREATOR = new Creator<Memory>() {
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
