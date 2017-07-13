package shag.com.shag.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

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
    long fbUid;
    String name;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = this;


        // create permissions
        permissions = Arrays.asList("user_friends");

        // initiate client
        client = ParseApplication.getFacebookRestClient();

        if (ParseUser.getCurrentUser() != null) {
            onLoginSuccess();
        } else {
            // login
            ParseFacebookUtils.logInWithReadPermissionsInBackground(this, permissions, new LogInCallback() {
                @Override
                public void done(final ParseUser user, ParseException err) {

                    if (user == null) {
                        Log.d("MyApp", "Uh oh. The user cancelled the Facebook login.");
                    }
                    // if user is registering
                    else if (user.isNew()) {

                        // get user info
                        client.getMyInfo(new GraphRequest.Callback() {
                            @Override
                            public void onCompleted(GraphResponse response) {
                                JSONObject userJSON = response.getJSONObject();
                                // initialize properties for new user
                                try {

                                    name = userJSON.getString("name");
                                    fbUid = userJSON.getLong("id");
                                    profileImageUrl = userJSON.getJSONObject("picture")
                                            .getJSONObject("data")
                                            .getString("url");

                                    CustomUser newCustomUser = new CustomUser(user);
                                    newCustomUser.setSomeString("name", name);
                                    newCustomUser.setSomeString("profile_image_url", profileImageUrl);


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                             /*
                             user.put("name", name);
                             user.put("profile_image_url", profileImageUrl);
                             user.put("fbuid", fbUid);
                             user.saveInBackground(new SaveCallback() {
                                 @Override
                                 public void done(ParseException e) {
                                     if (e == null) {
                                         Toast.makeText(LoginActivity.this, "Successfully created message on Parse",
                                                 Toast.LENGTH_SHORT).show();
                                     } else {
                                         Log.e("Sad", "Failed to save message", e);
                                     }
                                 }
                             }); */
                            }
                        });
                        Log.d("MyApp", "User signed up and logged in through Facebook!");

                        onLoginSuccess();
                    } else {

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

    public void onLoginSuccess() {
        Intent i = new Intent(context, MainActivity.class);
        context.startActivity(i);
    }

    @Override
    public void onBackPressed() {
        //do nothing
    }
}