package shag.com.shag.Models;

import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;
import java.util.Map;

/**
 * Created by gabesaruhashi on 7/12/17.
 */
public class CustomUser {
    private ParseUser user;


    public CustomUser(ParseUser obj) {
        user = obj;

    }

    public void setSomeString(String stringName, String value) {
        user.put(stringName, value);
        user.saveInBackground();
    }

    public void setSomeLong(String longName, Long value) {
        user.put(longName, value);
        user.saveInBackground();
    }

    public void setSomeStringArray(String listName, List<String> list) {
        user.put(listName, list);
        user.saveInBackground();
    }

    public void setSomeEmptyList(String listName, List<ParseObject> parseObjectList) {
        user.put(listName, parseObjectList);
        user.saveInBackground();
    }

    public void setMap(String objectName, Map map) {
        user.put(objectName, map);
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("CustomUser", "Categories Tracker Made");
                    // Hooray! Let them use the app now.
                } else {
                    // Sign up didn't succeed. Look at the ParseException
                    // to figure out what went wrong
                    e.printStackTrace();
                }
            }
        });
    }

}
