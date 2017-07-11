package shag.com.shag.Clients;

import android.content.Context;
import android.os.Bundle;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.HttpMethod;
import com.loopj.android.http.AsyncHttpClient;

/**
 * Created by hanapearlman on 7/10/17.
 */

public class FacebookClient {
    public static final String REST_URL = "https://graph.facebook.com/v2.9"; // might need to add v2.9
    AsyncHttpClient client;

    public FacebookClient(Context context) {
        client = new AsyncHttpClient();
    }

    //get user JSON: REST_URL + "/me" or REST_URL + "/id"
    //http://graph.facebook.com/" + facebookId + "/picture?type=square for profile pic

    public void getFriendsUsingApp(GraphRequest.Callback callback) {
        Bundle params = new Bundle();
        params.putString("fields", "id,name,picture");
        new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/friends", params, HttpMethod.GET,
                callback).executeAsync();
    }

    public void getMyInfo(GraphRequest.Callback callback) {
        Bundle params = new Bundle();
        params.putString("fields", "id,name,picture");
        new GraphRequest(AccessToken.getCurrentAccessToken(), "/me", params, HttpMethod.GET,
                callback).executeAsync();
    }
}
