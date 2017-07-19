package shag.com.shag.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.parse.ParseClassName;
import com.parse.ParseFile;

import java.util.ArrayList;

/**
 * Created by gabesaruhashi on 7/18/17.
 */

@ParseClassName("Memory")
public class Memory implements Parcelable {
    private String memoryName;
    private ArrayList<ParseFile> picturesParseFiles;
    private ArrayList<String> participantsIds;

    // GETTERS & SETTERS
    public String getMemoryName() {
        return memoryName;
    }

    public void setMemoryName(String memoryName) {
        this.memoryName = memoryName;
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

    public Memory() {
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
