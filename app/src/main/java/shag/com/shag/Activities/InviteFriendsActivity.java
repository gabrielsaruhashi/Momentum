package shag.com.shag.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import shag.com.shag.Fragments.ContactsFragment;
import shag.com.shag.R;

public class InviteFriendsActivity extends AppCompatActivity {
    ContactsFragment contactsFragment;
    Toolbar myToolbar;
    TextView toolbarTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_friends);

        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        toolbarTextView = (TextView) findViewById(R.id.tvToolbarText);
    }
}
