package shag.com.shag.Activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import org.json.JSONObject;

import shag.com.shag.Clients.FacebookClient;
import shag.com.shag.Models.User;
import shag.com.shag.R;

public class UserProfileActivity extends AppCompatActivity {
    //instance fields
    TextView name;
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

            }
        });
    }
}
