package shag.com.shag.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import shag.com.shag.Adapters.FriendsAdapter;
import shag.com.shag.Clients.FacebookClient;
import shag.com.shag.Models.Event;
import shag.com.shag.Models.User;
import shag.com.shag.Other.DividerItemDecorator;
import shag.com.shag.Other.MyAlarm;
import shag.com.shag.Other.ParseApplication;
import shag.com.shag.R;

import static com.raizlabs.android.dbflow.config.FlowManager.getContext;

public class SelectEventFriendsActivity extends AppCompatActivity {
    Button btnSubmit;
    ArrayList<User> friends;
    // recycler view
    RecyclerView rvFriends;
    // the adapter wired to the new view
    FriendsAdapter adapter;
    FacebookClient client;
    String category;
    String description;
    Long deadline;
    ParseUser currentUser;
    public final static int MILLISECONDS_IN_MINUTE = 60000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_event_friends);
        currentUser = ParseUser.getCurrentUser();

        // instantiate friends client
        client = ParseApplication.getFacebookRestClient();
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        // initialize the list of tweets
        friends = new ArrayList<>();
        // construct the adater from the data source
        adapter = new FriendsAdapter(friends);
        // initialize recycler view
        rvFriends = (RecyclerView) findViewById(R.id.rvFriends);

        // attach the adapter to the RecyclerView
        rvFriends.setAdapter(adapter);
        // Set layout manager to position the items
        rvFriends.setLayoutManager(new LinearLayoutManager(getContext()));

        // add line divider decorator
        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecorator(rvFriends.getContext(), DividerItemDecorator.VERTICAL_LIST);
        rvFriends.addItemDecoration(itemDecoration);

        category=getIntent().getStringExtra("Category");
        description=getIntent().getStringExtra("Description");
        deadline=getIntent().getLongExtra("Deadline",1);


        populateFriendsList();

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createEvent();
            }
        });

    }

    private void createEvent() {
        final Event newEvent = new Event();
        // populate newEvent
        newEvent.setEventOwnerName(ParseUser.getCurrentUser().getString("name"));
        newEvent.setDescription(description);
                /*newEvent.setLatLng(
                        new LatLng(47.628883, -122.342606)
                ); */
        //TODO what is this friends at event for
        newEvent.setFriendsAtEvent(new ArrayList<Long>());
        newEvent.setLocation("Facebook Seattle");

        //  upon creating, save event owner's id to participant list
        ArrayList<String> initialParticipantsIds = new ArrayList<String>(Arrays.asList(currentUser.getObjectId()));
        newEvent.setParticipantsIds(initialParticipantsIds);
        newEvent.setEventOwnerId(currentUser.getObjectId());
        newEvent.setCategory(category);
        newEvent.setTimeOfEvent(new Date()); //TODO: PUT REAL INFO IN HERE AT SOME POINT

        if (newEvent.deadline == null) {
            newEvent.deadline = new Date();
            newEvent.deadline.setTime(new Date().getTime() + MILLISECONDS_IN_MINUTE*60);
            newEvent.setDeadline(newEvent.deadline);
        }

        // get hashmap & category
        Map hm = (HashMap) currentUser.getMap("categories_tracker");

        // update category counter
        int oldCounter = (int) hm.get(category);
        hm.put(category, oldCounter + 1);
        currentUser.put("categories_tracker", hm);

        newEvent.put("User_event_owner", currentUser);
        Log.i("DEBUG_CREATE", currentUser.getObjectId());

                /* delete later - decided not to use chatId
                // create chat and set 'unique' id
                String chatId = UUID.randomUUID().toString();
                Chat eventChat = new Chat();
                eventChat.setChatId(chatId);
                //TODO make chat be a nested object. it is not working rn
                // newEvent.setEventChat(eventChat); */






        FacebookClient client = ParseApplication.getFacebookRestClient();
        client.getMyInfo(new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse response) {
                try {
                    String id = response.getJSONObject().getString("id");
                    newEvent.setEventOwnerFbId(Long.parseLong(id));
                    Log.i("CreateDetails", newEvent.toString());

                    newEvent.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e == null) {
                                Log.d("DEBUG", "EI");
                                //Toast.makeText(getContext(), "Successfully created event on Parse",
                                //Toast.LENGTH_SHORT).show();

                                // send back to pick category dialog after being saved
                                ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
                                    query.whereEqualTo("event_owner_id",ParseUser.getCurrentUser().getObjectId());
                                    query.orderByDescending("createdAt");
                                    query.setLimit(1);
                                    query.getFirstInBackground(new GetCallback<ParseObject>() {
                                        @Override
                                        public void done(ParseObject object, ParseException e) {
                                            if (object != null) {
                                                ParsePush.subscribeInBackground(object.getObjectId());

                                                //do not set a reminder if there is only 1 person at the event
                                                //TODO: add back in this check for >1
                                                //TODO: move this to another place
                                                //if (participantsIds.size() > 1) {
                                                //Set alarm reminder
                                                Bundle bundle = new Bundle();
                                                bundle.putString("eventId", object.getObjectId());
                                                bundle.putString("eventDescription", newEvent.description);
                                                new MyAlarm(getContext(), bundle, newEvent.getTimeOfEvent().getTime());
                                                // }
                                            }
                                        }
                                    });

                                //sendBackResult(newEvent);
                                Intent intent = new Intent(SelectEventFriendsActivity.this, MainActivity.class);
                                startActivity(intent);
                            } else {
                                Log.e("Creating Event", "Failed to save message", e);
                            }
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });



    }

    // get friends
    public void populateFriendsList() {
        client.getFriendsUsingApp(new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse response) {
                try {
                    JSONArray users = response.getJSONObject().getJSONArray("data");
                    for (int i = 0; i < users.length(); i++) {
                        User friend = User.fromJson(users.getJSONObject(i));
                        friends.add(friend);
                        adapter.notifyItemInserted(friends.size() - 1);
                        rvFriends.smoothScrollToPosition(0);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
