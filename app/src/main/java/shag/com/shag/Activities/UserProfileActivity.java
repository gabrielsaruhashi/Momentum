package shag.com.shag.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.CallbackManager;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.LoginButton;
import com.parse.ParseUser;

import org.json.JSONObject;

import shag.com.shag.Clients.FacebookClient;
import shag.com.shag.Models.User;
import shag.com.shag.Other.ParseApplication;
import shag.com.shag.R;

public class UserProfileActivity extends AppCompatActivity {
    //instance fields
    TextView name;
    //TextView friends;
    ImageView profileImage;
    Context context;
    User u;
    CallbackManager callbackManager;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        context = this;

        //resolve view objects
        name = (TextView) findViewById(R.id.tvName);
        profileImage = (ImageView) findViewById(R.id.ivProfileImage);
        //friends = (TextView) findViewById(R.id.tvFriends);



        FacebookClient client = ParseApplication.getFacebookRestClient();
        client.getMyInfo(new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse response) {
                JSONObject userJSON = response.getJSONObject();
                u = User.fromJson(userJSON);

                name.setText(u.getName());
                Glide.with(context).load(u.imageUrl).into(profileImage);
            }
        });

        /*
        client.getFriendsUsingApp(new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse response) {
                try {
                    JSONArray users = response.getJSONObject().getJSONArray("data");
                    for (int i = 0; i < users.length(); i++) {
                        User u = User.fromJson(users.getJSONObject(i));
                        friends.setText(friends.getText().toString() + u.getName() + "\n");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });*/

        callbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logOut();
                ParseUser.logOut();
                Intent intent = new Intent(UserProfileActivity.this, LoginActivity.class);
                context.startActivity(intent);
            }
        });

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        myToolbar.setTitleTextColor(0xFFFFFFFF);

    }

    public void onFeedView(MenuItem menuItem) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }
}
