package shag.com.shag.Models;

import com.parse.ParseUser;

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

}
