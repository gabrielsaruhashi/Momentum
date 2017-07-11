package shag.com.shag.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import shag.com.shag.Models.User;
import shag.com.shag.R;

public class UserProfileActivity extends AppCompatActivity {
    //instance fields
    TextView name;
    ImageView profileImage;
    User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        //resolve view objects
        name = (TextView) findViewById(R.id.tvName);
        //Glide.with(this).load().into(profileImage);


    }
}
