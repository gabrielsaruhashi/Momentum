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
    AsyncHttpClient client;
    Context context;

    public FacebookClient(Context context) {
        this.context = context;
    }

    //get user JSON: REST_URL + "/me" or REST_URL + "/id"
    //http://graph.facebook.com/" + facebookId + "/picture?type=square for profile pic

    public void getFriendsUsingApp(GraphRequest.Callback callback) {
        Bundle params = new Bundle();
        params.putString("fields", "id,name,picture.type(large)");
        new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/friends", params, HttpMethod.GET,
                callback).executeAndWait();
    }


    public void getFriendsInfo(long friendFbId, GraphRequest.Callback callback) {
        Bundle params = new Bundle();
        params.putString("fields", "id,name,picture.type(large)");
        new GraphRequest(AccessToken.getCurrentAccessToken(), "/" + friendFbId, params, HttpMethod.GET,
                callback).executeAsync();
    }

    public void getMyInfo(GraphRequest.Callback callback) {
        Bundle params = new Bundle();
        params.putString("fields", "id,name,picture.type(large)");
        new GraphRequest(AccessToken.getCurrentAccessToken(), "/me", params, HttpMethod.GET,
                callback).executeAsync();
    }



    //returns event node (need to call getJSONObject() before parsing)
    public void getEventInfo(long eventID, GraphRequest.Callback callback) {
        new GraphRequest(AccessToken.getCurrentAccessToken(), "/" + eventID, null, HttpMethod.GET,
                callback).executeAsync();
    }

    //response must be: "attending", "maybe", or "declined"
    public void sendEventResponse(long eventID, String response, GraphRequest.Callback callback) {
        new GraphRequest(AccessToken.getCurrentAccessToken(), "/" + eventID + "/" + response, null, HttpMethod.POST,
                callback).executeAsync();
    }

    //response must be: "attending", "maybe", or "declined"
    public void getLinkingAccessToken(GraphRequest.Callback callback, String page_id) {
        /* make the API call */
        new GraphRequest(AccessToken.getCurrentAccessToken(), "/" +page_id, null, HttpMethod.GET,
                callback).executeAsync();
    }
}
