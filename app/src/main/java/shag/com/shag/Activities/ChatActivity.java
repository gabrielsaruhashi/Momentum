package shag.com.shag.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.parse.FindCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseLiveQueryClient;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SubscriptionHandling;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import shag.com.shag.Adapters.MessagesAdapter;
import shag.com.shag.Adapters.PollsAdapter;
import shag.com.shag.Fragments.DialogFragments.CreatePollDialogFragment;
import shag.com.shag.Models.Event;
import shag.com.shag.Models.Message;
import shag.com.shag.Models.Poll;
import shag.com.shag.Other.DividerItemDecorator;
import shag.com.shag.R;

import static com.raizlabs.android.dbflow.config.FlowManager.getContext;

public class ChatActivity extends AppCompatActivity implements CreatePollDialogFragment.CreatePollFragmentListener{
    static final String TAG = "DEBUG_CHAT";
    static final int MAX_CHAT_MESSAGES_TO_SHOW = 50;
    Context context;
    EditText etMessage;
    Button btSend;

    RecyclerView rvChat;
    ArrayList<Message> mMessages;
    MessagesAdapter mAdapter;
    // Keep track of initial load to scroll to the bottom of the ListView
    boolean mFirstLoad;

    //polls
    // list of tweets
    ArrayList<Poll> polls;
    // recycler view
    RecyclerView rvPolls;
    // the adapter wired to the new view
    PollsAdapter pollAdapter;


    // chat id
    private String eventId;
    private ArrayList<String> chatParticipantsIds;
    private String currentUserId;
    private Event chatEvent;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private Button submitPoll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        ParseObject.registerSubclass(Poll.class);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close);

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        context = this;

        submitPoll = (Button) findViewById(R.id.btMakePoll);
        submitPoll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
        // initialize the list of polls
        polls = new ArrayList<>();
        // construct the adater from the data source
        pollAdapter = new PollsAdapter(polls);
        // initialize recycler view
        rvPolls = (RecyclerView) findViewById(R.id.rvPolls);

        // attach the adapter to the RecyclerView
        rvPolls.setAdapter(pollAdapter);
        // Set layout manager to position the items
        rvPolls.setLayoutManager(new LinearLayoutManager(getContext()));

        // add line divider decorator
        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecorator(rvPolls.getContext(), DividerItemDecorator.VERTICAL_LIST);
        rvPolls.addItemDecoration(itemDecoration);



        // unwrap intent and get current user id
        Intent intent = getIntent();
        eventId = intent.getStringExtra("event_id");
        chatParticipantsIds = intent.getStringArrayListExtra("participants_ids");
        currentUserId = ParseUser.getCurrentUser().getObjectId();
        chatEvent = getIntent().getParcelableExtra("event");
        setupMessagePosting();
        populatePolls();


        // Make sure the Parse server is setup to configured for live queries
        // URL for server is determined by Parse.initialize() call.
        ParseLiveQueryClient parseLiveQueryClient = ParseLiveQueryClient.Factory.getClient();

        ParseQuery<Message> parseQuery = ParseQuery.getQuery(Message.class);
        ParseQuery<Poll> parseQuery2 = ParseQuery.getQuery(Poll.class);
        // This query can even be more granular (i.e. only refresh if the entry was added by some other user)
        // parseQuery.whereNotEqualTo(USER_ID_KEY, ParseUser.getCurrentUser().getObjectId());

        // Connect to Parse server
        SubscriptionHandling<Message> subscriptionHandling = parseLiveQueryClient.subscribe(parseQuery);

        // Listen for CREATE events
        subscriptionHandling.handleEvent(SubscriptionHandling.Event.CREATE, new
            SubscriptionHandling.HandleEventCallback<Message>() {
                @Override
                public void onEvent(ParseQuery<Message> query, Message object) {
                    String senderId = object.getSenderId();
                    String newEventId = object.getEventId();

                    if (!senderId.equals(currentUserId) && newEventId.equals(eventId)) {
                        mMessages.add(0, object);
                    }

                    // RecyclerView updates need to be run on the UI thread
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.notifyDataSetChanged();
                            rvChat.scrollToPosition(0);

                        }
                    });
                }



            });

        SubscriptionHandling<Poll> subscriptionHandling2 = parseLiveQueryClient.subscribe(parseQuery2);
        subscriptionHandling2.handleEvent(SubscriptionHandling.Event.CREATE, new SubscriptionHandling.HandleEventCallback<Poll>() {
            @Override
            public void onEvent(ParseQuery<Poll> query, Poll object) {
                String senderId = object.getPollCreator();
                String newEventId = object.getEventId();

                if (!senderId.equals(currentUserId) && newEventId.equals(eventId)) {
                    polls.add(0, object);
                }

                // RecyclerView updates need to be run on the UI thread
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pollAdapter.notifyDataSetChanged();
                        rvPolls.scrollToPosition(0);

                    }
                });
            }
        });


    }

    private void populatePolls() {
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(actionBarDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    // setup button event handler which posts the entered message to Parse
    void setupMessagePosting() {
        // find the text field and button
        etMessage = (EditText) findViewById(R.id.etMessage);
        btSend = (Button) findViewById(R.id.btSend);
        rvChat = (RecyclerView) findViewById(R.id.rvChat);
        mMessages = new ArrayList<>();
        mFirstLoad = true;

        // set adapter
        mAdapter = new MessagesAdapter(ChatActivity.this, currentUserId, mMessages);
        rvChat.setAdapter(mAdapter);

        // associate the LayoutManager with the RecylcerView
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ChatActivity.this);
        // display the newest posts ordered from oldest to newest
        linearLayoutManager.setReverseLayout(true);
        rvChat.setLayoutManager(linearLayoutManager);

        // when send button is clicked, create message object on Parse
        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String data = etMessage.getText().toString();

                // using new `Message` Parse-backed model now
                Message message = new Message();
                // populate message
                //TODO pass the entire event object
                message.setBody(data);
                // save User pointer
                message.put("User_sender", ParseUser.getCurrentUser());
                message.setSenderId(currentUserId);
                message.setEventId(eventId);
                message.setSenderProfileImageUrl(ParseUser.getCurrentUser().getString("profile_image_url"));
                message.setSenderName(ParseUser.getCurrentUser().getString("name"));

                message.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            String token = "";
                            try {
                                //TODO: find a way to get the instance ID and filter out poster from receivers, io exception
                                token = InstanceID.getInstance(context)
                                        .getToken(getString(R.string.gcm_defaultSenderId), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                                Log.d("DEBUG_CHAT_ACTIVITY", "token = " + token);
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                            HashMap<String, String> payload = new HashMap<>();
                            payload.put("customData", data);
                            payload.put("title", "New message in channel");
                            payload.put("channelID", eventId);
                            payload.put("senderID", currentUserId);
                            payload.put("token", token);

                            //TODO: this would probably be a better way to notify if there's time later
                            /*InstanceID instanceID = InstanceID.getInstance(this);
                            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);*/

                            ParseCloud.callFunctionInBackground("pushChannelTest", payload);

                            Toast.makeText(ChatActivity.this, "Successfully created message on Parse",
                                    Toast.LENGTH_SHORT).show();


                        } else {
                            Log.e(TAG, "Failed to save message", e);
                        }
                    }
                });
                etMessage.setText(null);
                // add message to arraylist
                mMessages.add(0, message);
                mAdapter.notifyItemInserted(0);
                rvChat.smoothScrollToPosition(0);


                if (data.equals("hi Shaggy")){
                    Message m = new Message();
                    m.setSenderId("InuSHuTqkn");
                    m.setBody("Hi! My name is Shaggy");
                    m.setEventId(eventId);
                    m.setSenderName("Shaggy");
                    try {
                        m.save();
                        mAdapter.notifyItemInserted(0);
                        rvChat.smoothScrollToPosition(0);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }

            }
        });

        // call refresh messages
        refreshMessages();
        refreshPolls();

    }


    @Override
    public void onFinishCreatePollFragment(Poll poll) {
        poll.setEventId(eventId);
        poll.setPollCreator(ParseUser.getCurrentUser().getObjectId());
        poll.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {

            }
        });
        polls.add(0,poll);
        pollAdapter.notifyDataSetChanged();
        rvPolls.scrollToPosition(0);
        //Log.d("onReturnValue", "Got value " + poll.getQuestion() + " back from Dialog!");
    }

    public void showDialog() {
        CreatePollDialogFragment newFragment = CreatePollDialogFragment.newInstance();
        newFragment.show(getSupportFragmentManager(), "dialog");
    }

    // Query messages from Parse so we can load them into the chat adapter
    void refreshMessages() {

        // construct OR query to execute
        ParseQuery<Message> query = createOrQueries(chatParticipantsIds);

        // AND query for messages that are from this event
        query.whereEqualTo("event_id", eventId);

        // Configure limit and sort order
        query.setLimit(MAX_CHAT_MESSAGES_TO_SHOW);

        // get the latest 50 messages, order will show up newest to oldest of this group
        query.orderByDescending("createdAt");

        // Execute query to fetch all messages from Parse asynchronously
        // this is equivalent to a SELECT query with SQL
        query.findInBackground(new FindCallback<Message>() {
            public void done(List<Message> messages, ParseException e) {
                if (e == null) {
                    mMessages.clear();
                    mMessages.addAll(messages);
                    mAdapter.notifyDataSetChanged(); // update adapter

                    // Scroll to the bottom of the list on initial load
                    if (mFirstLoad) {
                        rvChat.smoothScrollToPosition(0);
                        mFirstLoad = false;
                    }
                } else {
                    Log.e("message", "Error Loading Messages" + e);
                }
            }
        });

    }
    void refreshPolls() {

        // construct OR query to execute
        ParseQuery query = new ParseQuery("Poll");

        // AND query for messages that are from this event
        query.whereEqualTo("event_id", eventId);



        // get the latest 50 messages, order will show up newest to oldest of this group
        query.orderByDescending("createdAt");

        // Execute query to fetch all messages from Parse asynchronously
        // this is equivalent to a SELECT query with SQL
        query.findInBackground(new FindCallback<Poll>() {
            public void done(List<Poll> poll, ParseException e) {
                if (e == null) {
                    polls.clear();
                    polls.addAll(poll);
                    pollAdapter.notifyDataSetChanged(); // update adapter

                    // Scroll to the bottom of the list on initial load
                    if (mFirstLoad) {
                        rvPolls.smoothScrollToPosition(0);
                        mFirstLoad = false;
                    }
                } else {
                    Log.e("message", "Error Loading Messages" + e);
                }
            }
        });

    }


    // function creates the main OR query to search for all user ids
    public ParseQuery<Message> createOrQueries(ArrayList<String> userIds) {
        // OR queries array
        List<ParseQuery<Message>> queries = new ArrayList<ParseQuery<Message>>();

        // for each id in the participants list, create a query and add to the OR arraylist
        for (int i = 0; i < userIds.size(); i ++) {
            ParseQuery myQuery = new ParseQuery("Message");
            // auxiliary list
            List list = new ArrayList();
            list.add(userIds.get(i));

            // create the query condition
            myQuery.whereContainedIn("sender_id", list);

            // add query to the array of OR queries
            queries.add(myQuery);
        }

        // return or query
        return ParseQuery.or(queries);
    }

}

