package shag.com.shag.Activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import shag.com.shag.Clients.FacebookClient;
import shag.com.shag.Models.User;
import shag.com.shag.R;

public class UserProfileActivity extends AppCompatActivity {
    //instance fields
    TextView name;
    TextView friends;
    ImageView profileImage;
    Context context;
    User u;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        context = this;

        //resolve view objects
        name = (TextView) findViewById(R.id.tvName);
        profileImage = (ImageView) findViewById(R.id.ivProfileImage);
        friends = (TextView) findViewById(R.id.tvFriends);


        FacebookClient client = new FacebookClient(this);
        client.getMyInfo(new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse response) {
                JSONObject userJSON = response.getJSONObject();
                u = User.fromJson(userJSON);
                name.setText(u.getName());
                Glide.with(context).load(u.imageUrl).into(profileImage);
            }
        });

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
        });
    }
}
