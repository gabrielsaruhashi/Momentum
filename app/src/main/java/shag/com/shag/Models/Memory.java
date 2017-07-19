package shag.com.shag.Models;

import com.parse.ParseClassName;<<<<<<< HEAD
import android.os.Parcel;
import android.os.Parcelable;

import com.parse.ParseClassName;
import com.parse.ParseFile;
=======
import android.util.Log;

import com.parse.GetCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
>>>>>>> master

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

    public ArrayList<ParseFile> getPicturesParseFiles() {
        return picturesParseFiles;
    }

    public void setPicturesParseFiles(ArrayList<ParseFile> picturesParseFiles) {
        this.picturesParseFiles = picturesParseFiles;

    }

    public ArrayList<String> getParticipantsIds() {
        return participantsIds;
    }

    public void setParticipantsIds(ArrayList<String> participantsIds) {
        this.participantsIds = participantsIds;
        put("participantsIds", participantsIds);
    }

    public Memory(String eventName, final ArrayList<String> participantsIds, String id) {
        this.memoryName = "";
        if (eventName != null) {
            setMemoryName(eventName);
        }

        this.participantsIds = new ArrayList<String>();
        if (participantsIds != null) {
            setParticipantsIds(participantsIds);
        }


        saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    log.i("Memory", "MEMORY CREATED");

                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Memory");
                    query.orderByDescending("createdAt");
                    query.setLimit(1);
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
