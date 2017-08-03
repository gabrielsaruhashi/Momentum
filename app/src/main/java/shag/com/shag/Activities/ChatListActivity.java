package shag.com.shag.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

        chatListFragment = (ChatListFragment) getSupportFragmentManager().findFragmentById(R.id.chatListFragment);
        toolbarTextView = (TextView) findViewById(R.id.tvToolbarText);
        toolbarTextView.setText("Your Chats");

    }
    // add number of chats to toolbar
    @Override
    public void OnChatLoad(int chatSize) {
        if (chatListFragment != null && chatListFragment.isInLayout()) {
            toolbarTextView.setText("(" + chatSize + ")");
        }
    }
}
