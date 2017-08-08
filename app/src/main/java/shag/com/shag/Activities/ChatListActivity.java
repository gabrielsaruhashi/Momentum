package shag.com.shag.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import shag.com.shag.Fragments.ChatListFragment;
import shag.com.shag.R;

public class ChatListActivity extends AppCompatActivity implements ChatListFragment.OnChatLoadListener {

    ChatListFragment chatListFragment;
    Toolbar myToolbar;
    TextView toolbarTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        myToolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));
        chatListFragment = (ChatListFragment) getSupportFragmentManager().findFragmentById(R.id.chatListFragment);
        toolbarTextView = (TextView) findViewById(R.id.tvToolbarText);
        toolbarTextView.setTextColor(ContextCompat.getColor(this, R.color.white));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat_list, menu);
        return true;
    }

    // add number of chats to toolbar
    @Override
    public void OnChatLoad(int chatSize) {
        if (chatListFragment != null && chatListFragment.isInLayout()) {
            toolbarTextView.setText("(" + chatSize + ")");
        }
    }

    public void onFeedView(MenuItem menuItem) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
