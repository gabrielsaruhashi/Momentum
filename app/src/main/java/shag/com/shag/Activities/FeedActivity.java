package shag.com.shag.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

import butterknife.ButterKnife;
import shag.com.shag.Adapters.FeedAdapter;
import shag.com.shag.Models.Event;
import shag.com.shag.Models.User;
import shag.com.shag.Other.DividerItemDecorator;
import shag.com.shag.R;

public class FeedActivity extends AppCompatActivity {

    // list of tweets
    ArrayList<Event> events;
    // recycler view
    RecyclerView rvEvents;
    // the adapter wired to the new view
    FeedAdapter adapter;
    private MenuItem miActionProgressItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        setContentView(R.layout.activity_feed);

        // initialize the list of tweets
        events = new ArrayList<>();
        // construct the adater from the data source
        adapter = new FeedAdapter(events);
        // initialize recycler view
        rvEvents = (RecyclerView) findViewById(R.id.rvEvents);

        // attach the adapter to the RecyclerView
        rvEvents.setAdapter(adapter);
        // Set layout manager to position the items
        rvEvents.setLayoutManager(new LinearLayoutManager(this));

        // add line divider decorator
        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecorator(this, DividerItemDecorator.VERTICAL_LIST);
        rvEvents.addItemDecoration(itemDecoration);

        populateFeed();
    }

    public void populateFeed() {
        User gabriel = new User();
        gabriel.username = "gabesaruhashi";
        gabriel.name="Gabriel S.";
        gabriel.phoneNumber="6505757751";
        gabriel.currentInterestsIds = new ArrayList<Long>(0);

        Event fakeEvent = new Event();
        fakeEvent.eventId = new Long(123);
        fakeEvent.eventName = "Party at Zuck's";
        fakeEvent.location = "Facebook Seattle";
        fakeEvent.genre = "Partay";
        fakeEvent.time = "4pm";
        fakeEvent.eventOwner=gabriel;
        fakeEvent.participantsIds= new ArrayList<Long>(0);
        ArrayList<String> friends = new ArrayList<String>(3);
        friends.add("Gabriel");
        friends.add("Samra");
        friends.add("Hana");
        fakeEvent.friendsAtEvent = friends;

        events.add(0, fakeEvent);
        adapter.notifyItemInserted(events.size() - 1);
        rvEvents.smoothScrollToPosition(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }


    public void onProfileView(MenuItem item){
        //launch profile view
        Intent i = new Intent(this, UserProfileActivity.class);
        startActivity(i);
    }

    public void onSettingsView(MenuItem item){
        //launch profile view
        Intent i = new Intent(this, SettingsActivity.class);
        startActivity(i);
    }
}
