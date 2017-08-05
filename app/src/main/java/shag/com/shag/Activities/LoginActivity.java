package shag.com.shag.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.widget.LoginButton;
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
    boolean isNew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        context = this;
        intent = getIntent();

        // create permissions
        permissions = Arrays.asList("user_friends", "user_friends");

        // initiate client
        client = ParseApplication.getFacebookRestClient();

        //LoginManager.getInstance().logOut();
        //ParseUser.logOut();
        LoginButton bLogin = (LoginButton) findViewById(R.id.bLogin);
        bLogin.setReadPermissions(Arrays.asList("user_photos", "user_friends"));
        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

        ParseUser me = ParseApplication.getCurrentUser();
        if (me != null && me.isAuthenticated()) {
            //ParseUser.logOut();
            onLoginSuccess();

        }
        //TODO delete this
        Button btContacts = (Button) findViewById(R.id.btContacts);
        btContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, InviteFriendsActivity.class);
                startActivity(i);

            }
        });
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
                String title = jsonObject.getString("title");
                if (eventID != null) {
                    Intent i = new Intent(context, ChatActivity.class);
                    i.putExtra("event_id", eventID);
                    context.startActivity(i);
                } else if (title.equals("Event Reminder")) {
                    Intent i = new Intent(context, MainActivity.class);
                    i.putExtra("viewpager_position", 1);
                    context.startActivity(i);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Intent i = new Intent(context, MainActivity.class);
            i.putExtra("isNew", isNew);
            // initialize recent friends and facebook friends 'global/app' variable
            //TODO refactor this
            ParseApplication.getRecentFriends();
            // get additional publishing permission
            ParseApplication.getFacebookPermissionsSet();
            // get facebook friends should be last call for it sets the first load to false
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

        //sub category Object
        ArrayList<Object> chillValues = new ArrayList<Object>();
        ArrayList<Object> partyValues = new ArrayList<Object>();
        ArrayList<Object> sportsValues = new ArrayList<Object>();
        ArrayList<Object> miscValues = new ArrayList<Object>();
        ArrayList<Object> foodValues = new ArrayList<Object>();
        ArrayList<Object> musicValues = new ArrayList<Object>();

        //set values object
        chillValues.add(0);
        chillValues.add(new HashMap<String, Integer>());

        partyValues.add(0);
        partyValues.add(new HashMap<String, Integer>());

        sportsValues.add(0);
        sportsValues.add(new HashMap<String, Integer>());

        miscValues.add(0);
        miscValues.add(new HashMap<String, Integer>());

        foodValues.add(0);
        foodValues.add(new HashMap<String, Integer>());

        musicValues.add(0);
        musicValues.add(new HashMap<String, Integer>());

        // Put elements to the map
        hm.put("Chill", chillValues);
        hm.put("Party", partyValues);
        hm.put("Sports", sportsValues);
        hm.put("Misc", miscValues);
        hm.put("Food", foodValues);
        hm.put("Music", musicValues);
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
                    // boolean to take user to onboarding
                    isNew = true;

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
                    isNew = false;
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