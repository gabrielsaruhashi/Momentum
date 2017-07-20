package shag.com.shag.Models;

import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.List;

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

    public void setSomeArray(String listName, List<Long> list) {
        user.put(listName, list);
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

}
