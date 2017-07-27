package shag.com.shag.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import shag.com.shag.Clients.FacebookClient;
import shag.com.shag.Models.CustomUser;
import shag.com.shag.Other.ParseApplication;
import shag.com.shag.R;

public class LoginActivity extends AppCompatActivity {
    Context context;
    List<String> permissions;
    FacebookClient client;

    // properties to be added to ParseUser
    String profileImageUrl;
    String fbUsername;
    long fbUid;
    String name;
    String email;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        context = this;
        intent = getIntent();

        // create permi ssions
        permissions = Arrays.asList("user_friends");

        // initiate client
        client = ParseApplication.getFacebookRestClient();

        //LoginManager.getInstance().logOut();
        //ParseUser.logOut();
        Button bLogin = (Button) findViewById(R.id.bLogin);
        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

        if (ParseUser.getCurrentUser() != null) {
            //ParseUser.logOut();
            onLoginSuccess();

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

    public void onLoginSuccess() {
        //currently this if statement is never used
        //if we override onPushOpen  we will eventually need this

        if (intent.getAction() == null && intent.getExtras() != null) {
            //Intent i = new Intent(context, MainActivity.class);
            //context.startActivity(i);
            Bundle extras = intent.getExtras();
            String jsonData = extras.getString("com.parse.Data");
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(jsonData);
                String eventID = jsonObject.getString("event_id");
                if (eventID != null) {
                    Intent i = new Intent(context, ChatActivity.class);
                    //TODO ad
                    i.putExtra("event_id", eventID);
                    context.startActivity(i);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Intent i = new Intent(context, MainActivity.class);
            // initialize recent friends and facebook friends 'global/app' variable
            ParseUser currentUser = ParseUser.getCurrentUser();
            ParseApplication.getRecentFriends();
            ParseApplication.getFacebookFriends();
            context.startActivity(i);
        }
    }

    @Override
    public void onBackPressed() {
        //do nothing
    }


    public Map createCategoriesMap() {
        Map hm = new HashMap();

        // Put elements to the map
        hm.put("Chill", 0);
        hm.put("Party", 0);
        hm.put("Sports", 0);
        hm.put("Misc", 0);
        hm.put("Food", 0);
        hm.put("Music", 0);
        return hm;
    }

    public void login() {
        // login
        ParseFacebookUtils.logInWithReadPermissionsInBackground(this, permissions, new LogInCallback() {
            // upon success, return user object
            @Override
            public void done(final ParseUser user, ParseException err) {

                if (user == null) {
                    Log.d("MyApp", "Uh oh. The user cancelled the Facebook login.");
                }
                // if user is registering
                else if (user.isNew()) {
                    user.getCurrentUser();
                    // user.saveInBackground();

                    // get user info
                    client.getMyInfo(new GraphRequest.Callback() {
                        @Override
                        public void onCompleted(GraphResponse response) {
                            JSONObject userJSON = response.getJSONObject();
                            // initialize properties for new user
                            try {
                                name = userJSON.getString("name");
                                // fbUid = userJSON.getLong("id");
                                profileImageUrl = userJSON.getJSONObject("picture")
                                        .getJSONObject("data")
                                        .getString("url");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            CustomUser newCustomUser = new CustomUser(user);
                            newCustomUser.setSomeString("name", name);
                            newCustomUser.setSomeString("profile_image_url", profileImageUrl);
                            newCustomUser.setSomeStringArray("memories_ids", new ArrayList<String>());
                            //TOD add memories list not working
                            newCustomUser.setSomeEmptyList("Memories_list", new ArrayList<ParseObject>());

                            newCustomUser.setMap("categories_tracker", createCategoriesMap());
                            newCustomUser.setMap("recent_friends_map", new HashMap<String, Integer>());
                        }
                    });
                    Log.d("MyApp", "User signed up and logged in through Facebook!");

                    onLoginSuccess();
                } else {
                    user.getCurrentUser();
                    //TODO what is this line below for?
                    user.saveInBackground();
                    Log.d("MyApp", "User logged in through Facebook!");
                    Log.d("MyApp", user.getUsername());
                    Log.d("MyApp", user.getCreatedAt().toString());
                    Log.d("MyApp", user.getObjectId());
                    onLoginSuccess();
                }
            }
        });
    }
}