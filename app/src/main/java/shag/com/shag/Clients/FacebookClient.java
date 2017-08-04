package shag.com.shag.Clients;

import android.content.Context;
import android.os.Bundle;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.HttpMethod;
import com.parse.ParseUser;

import java.util.HashMap;

import shag.com.shag.Other.ParseApplication;


/**
 * Created by hanapearlman on 7/10/17.
 */

public class FacebookClient {
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
    public void getFriendsUsingAppAsynch(GraphRequest.Callback callback) {
        Bundle params = new Bundle();
        params.putString("fields", "id,name,picture.type(large)");
        new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/friends", params, HttpMethod.GET,
                callback).executeAsync();
    }

    public void postFacebookAlbum(int[] contributors, String albumName, GraphRequest.Callback callback) {
        ParseUser user = ParseApplication.getCurrentUser();
        HashMap data = (HashMap) user.getMap("authData");
        HashMap facebookData = (HashMap) data.get("facebook");
        String userFacebookId = (String) facebookData.get("id");
        Bundle params = new Bundle();
        params.putString("name", albumName);
        params.putIntArray("contributors", contributors);
        new GraphRequest(AccessToken.getCurrentAccessToken(), "/"+ userFacebookId + "/albums", params, HttpMethod.POST,
                callback).executeAsync();
    }

    public void getAlbumPhotos(long albumId, GraphRequest.Callback callback) {
        Bundle params = new Bundle();
        params.putString("fields", "likes, images");
        new GraphRequest(AccessToken.getCurrentAccessToken(), "/"+ albumId + "/photos", params, HttpMethod.GET,
                callback).executeAsync();
    }

    public void postPictureToAlbum(byte[] imageFormData, long albumId, GraphRequest.Callback callback) {
        Bundle params = new Bundle();
        params.putByteArray("source", imageFormData);
        /* make the API call */
        new GraphRequest(AccessToken.getCurrentAccessToken(),
                "/" + albumId + "/photos", params, HttpMethod.POST,
                callback).executeAsync();
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

    public void getMyPermissions(GraphRequest.Callback callback) {
        Bundle params = new Bundle();
        new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions", params, HttpMethod.GET,
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
