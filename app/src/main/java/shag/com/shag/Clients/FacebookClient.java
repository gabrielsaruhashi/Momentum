package shag.com.shag.Clients;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * Created by hanapearlman on 7/10/17.
 */

public class FacebookClient {
    public static final String REST_URL = "https://graph.facebook.com/v2.9"; // might need to add v2.9
    AsyncHttpClient client;

    public FacebookClient(Context context) {
        client = new AsyncHttpClient();
    }

    //TODO: figure out whether this should return a boolean & whether this endpoint actually exists
    //TODO: figure put if need to pass user token in the request url
    //TODO: need to add api key?
    public void friends(long userBId, AsyncHttpResponseHandler handler) {
        String apiUrl = REST_URL + "/me/friends/" + userBId;
        RequestParams params = new RequestParams();
        client.get(apiUrl, params, handler);
    }

    //get user JSON: REST_URL + "/me" or REST_URL + "/id"
    //http://graph.facebook.com/" + facebookId + "/picture?type=square for profile pic
}
