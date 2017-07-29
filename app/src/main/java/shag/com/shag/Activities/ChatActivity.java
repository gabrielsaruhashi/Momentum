package shag.com.shag.Activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseLiveQueryClient;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SubscriptionHandling;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import shag.com.shag.Adapters.MessagesAdapter;
import shag.com.shag.Adapters.PollsAdapter;
import shag.com.shag.Clients.VolleyRequest;
import shag.com.shag.Fragments.DialogFragments.CreatePollDialogFragment;
import shag.com.shag.Fragments.DialogFragments.DatePickerFragment;
import shag.com.shag.Fragments.DialogFragments.TimePickerFragment;
import shag.com.shag.Models.Event;
import shag.com.shag.Models.Message;
import shag.com.shag.Models.Poll;
import shag.com.shag.Other.DividerItemDecorator;
import shag.com.shag.R;

import static com.raizlabs.android.dbflow.config.FlowManager.getContext;

public class ChatActivity extends AppCompatActivity implements CreatePollDialogFragment.CreatePollFragmentListener,
        TimePickerFragment.TimePickerFragmentListener, DatePickerFragment.DatePickerFragmentListener,
        PollsAdapter.TimeButtonsInterface, PollsAdapter.LocationButtonsInterface {

    static final String TAG = "DEBUG_CHAT";
    static final int MAX_CHAT_MESSAGES_TO_SHOW = 50;
    static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    Context context;
    EditText etMessage;
    Button btSend;
    ArrayList<View> timeButtons;
    ArrayList<View> locationButtons;
    int viewPosition;
    private FusedLocationProviderClient mFusedLocationClient;


    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;


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
    boolean openedPush;


    // chat id
    private Event event;
    private String eventId;
    private ArrayList<String> chatParticipantsIds;
    private String currentUserId;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private Button submitPoll;
    private boolean isEventNew;
    private boolean isEventPrivate;
    private boolean isRecommendationMade;


    private String timeWinner;
    private ParseGeoPoint locationWinner;
    private ParseObject eventFromQuery;

    private String favFood;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        context = this;

        //creating custom polls
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
        pollAdapter = new PollsAdapter(getContext(), this, this, polls);

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

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(ChatActivity.this);

        // unwrap intent and get current user id
        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        eventId = intent.getStringExtra("event_id");
        chatParticipantsIds = intent.getStringArrayListExtra("participants_ids");
        int s = chatParticipantsIds.size();
        event = intent.getParcelableExtra("event");

        if (chatParticipantsIds == null) {
            openedPush = true;
            try {
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
                eventFromQuery = query.get(eventId);
                chatParticipantsIds = (ArrayList) eventFromQuery.getList("participants_id");
            } catch (ParseException e) {

                e.printStackTrace();
            }
        }



        //finding out if this is the first time the event has been creating
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
        try {
            eventFromQuery = query.get(eventId);
            isEventNew = eventFromQuery.getBoolean("is_first_created");
            isEventPrivate = eventFromQuery.getBoolean("is_event_private");
            isRecommendationMade=eventFromQuery.getBoolean("is_recommendation_made");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        currentUserId = ParseUser.getCurrentUser().getObjectId();

        setupMessagePosting();

        //if event is new, make time and lcoation polls
        if (isEventNew) {
            try {
                createTimeAndLocationPolls("Time");
                createTimeAndLocationPolls("Location");
            } catch (ParseException e) {
                e.printStackTrace();
            }
            isEventNew = false;
            //find the event in db and make it now new
            ParseQuery<ParseObject> eventQuery = ParseQuery.getQuery("Event");
            eventQuery.getInBackground(eventId, new GetCallback<ParseObject>() {
                public void done(ParseObject eventDb, ParseException e) {
                    if (e == null) {
                        eventDb.put("is_first_created", isEventNew);
                        eventDb.saveInBackground();
                    }
                }
            });
        }
        String id = (String) eventFromQuery.getString("event_owner_id");

        if (currentUserId.equals(eventFromQuery.getString("event_owner_id")) && isRecommendationMade==false
                && isEventPrivate==true) {
            isRecommendationMade=true;
            ParseQuery<ParseObject> eventQuery = ParseQuery.getQuery("Event");
            eventQuery.getInBackground(eventId, new GetCallback<ParseObject>() {
                public void done(ParseObject eventDb, ParseException e) {
                    if (e == null) {
                        eventDb.put("is_first_created", isEventNew);
                        eventDb.saveInBackground();
                    }
                }
            });
            recommendRestaurant(chatParticipantsIds);
        }


        refreshPolls();


        // Make sure the Parse server is setup to configured for live queries
        // URL for server is determined by Parse.initialize() call.
        ParseLiveQueryClient parseLiveQueryClient = ParseLiveQueryClient.Factory.getClient();

        ParseQuery<Message> parseQuery = ParseQuery.getQuery(Message.class);
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

        //setup on server side
        ParseQuery<Poll> parseQuery2 = ParseQuery.getQuery(Poll.class);
        SubscriptionHandling<Poll> subscriptionHandling2 = parseLiveQueryClient.subscribe(parseQuery2);
        subscriptionHandling2.handleEvents(new SubscriptionHandling.HandleEventsCallback<Poll>() {
            @Override
            public void onEvents(ParseQuery<Poll> query, SubscriptionHandling.Event event, Poll object) {
                String senderId = object.getPollCreator();
                String newEventId = object.getEventId();

                if (!senderId.equals(currentUserId) && newEventId.equals(eventId)) {
                    polls.add(object);
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
//        subscriptionHandling2.handleEvent(SubscriptionHandling.Event.CREATE, new SubscriptionHandling.HandleEventCallback<Poll>() {
//            @Override
//            public void onEvent(ParseQuery<Poll> query, Poll object) {
//                String senderId = object.getPollCreator();
//                String newEventId = object.getEventId();
//
//                if (!senderId.equals(currentUserId) && newEventId.equals(eventId)) {
//                    polls.add(object);
//                }
//
//                // RecyclerView updates need to be run on the UI thread
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        pollAdapter.notifyDataSetChanged();
//                        rvPolls.scrollToPosition(0);
//
//                    }
//                });
//            }
//        });


    }

    private void recommendRestaurant(ArrayList<String> userIds) {
        // construct OR query to userQuery

        // for each id in the participants list, create a query and add to the OR arraylist
        ArrayList<String> eventUsers = new ArrayList<>(chatParticipantsIds);
        eventUsers.remove("InuSHuTqkn");
        final String cuisineInterest = findMostPopularFood(eventUsers);
        if (ActivityCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        }
        else{
            ActivityCompat.requestPermissions(ChatActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION );
        }


        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(ChatActivity.this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        favFood=cuisineInterest;
                        Double lat = location.getLatitude();
                        Double lng = location.getLongitude();
                        VolleyRequest.getInstance(getApplicationContext()).addToRequestQueue(getFoodRequest(cuisineInterest, lat.toString(), lng.toString()));


                    }
                });


    }

    private String findMostPopularFood(ArrayList<String> people) {

        //list x = list of userids
        //for each elemment in x
        //make a query for that user
        //specify id
        //person.get
        ArrayList<String> x = people;

        final HashMap<String, Integer> foodCounts = new HashMap<>();
        //TODO use an AsyncTask
        for (String userId : people) {
            ParseQuery<ParseUser> person = ParseUser.getQuery();
            try {
                ParseUser userX = person.get(userId);
                String id = userX.getObjectId();
                HashMap<String, Object> categoryMap = (HashMap<String, Object>) userX.getMap("categories_tracker");
                List<Object> foodData = (List<Object>) categoryMap.get("Food");
                HashMap<String, Integer> subCategoryMap = (HashMap<String, Integer>) foodData.get(1);
                for (String foodType : subCategoryMap.keySet()) {
                    //if subcategory already found, add to sum hash map
                    if (foodCounts.get(foodType) != null) {
                        foodCounts.put(foodType, foodCounts.get(foodType) + subCategoryMap.get(foodType));
                    } else {
                        foodCounts.put(foodType, subCategoryMap.get(foodType));
                    }

                }


            } catch (ParseException e) {
                e.getMessage();
            }

//            person.getInBackground(userId, new GetCallback<ParseUser>() {
//                @Override
//                public void done(ParseUser object, ParseException e) {
//                    String id =object.getObjectId();
//                    HashMap<String,Object> categoryMap = (HashMap<String, Object>) object.getMap("categories_tracker");
//                    List<Object> foodData = (List<Object>) categoryMap.get("Food");
//                    HashMap<String,Integer> subCategoryMap= (HashMap<String, Integer>) foodData.get(1);
//                    for (String foodType: subCategoryMap.keySet()){
//                        //if subcategory already found, add to sum hash map
//                        if (foodCounts.get(foodType)!=null){
//                            foodCounts.put(foodType,foodCounts.get(foodType)+subCategoryMap.get(foodType));
//                        }
//                        else{
//                            foodCounts.put(foodType,subCategoryMap.get(foodType));
//                        }
//
//                    }
//                }
//            });
        }
        String maxKey = null;
        int maxValue = -1;
        for (String food : foodCounts.keySet()) {
            if (foodCounts.get(food) > maxValue) {
                maxKey = food;
                maxValue = foodCounts.get(food);
            }
        }

        return maxKey;

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
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


                if (data.equals("hi Shaggy")) {
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

    public void createTimeAndLocationPolls(final String type) throws ParseException {
        final Poll poll = new Poll();
        //poll.setEventId(eventId);
        //poll.put("Event", chatEvent);
        poll.setEventId(eventId);
        poll.put("Poll_creator", ParseUser.getCurrentUser());
        poll.setPollType(type);
        poll.setQuestion(type + " of Event");
        ArrayList<String> customs = new ArrayList<>();
        customs.add("Custom");
        customs.add("Custom");
        customs.add("Custom");
        customs.add("Custom");

        poll.setChoices(customs);
        poll.setScores(new HashMap<String, Integer>());
        poll.setPeopleVoted(new ArrayList<String>());

        if (poll.getPollType().equals("Location")) {
            poll.setLocationOptions(new HashMap<String, ParseGeoPoint>());
        }

        poll.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(ChatActivity.this, "yay you put " + type + " poll on parse", Toast.LENGTH_LONG).show();
                }

            }
        });
        polls.add(poll);
        pollAdapter.notifyItemInserted(polls.size() - 1);
        rvPolls.scrollToPosition(0);
        //poll.save();

    }

    public void createTimePoll() {
        Poll poll = new Poll();
        //poll.setEventId(eventId);
        //poll.put("Event", chatEvent);
        poll.setEventId(eventId);
        poll.put("Poll_creator", ParseUser.getCurrentUser());
        poll.setPollType("Time");
        poll.setQuestion("Time of Event");
        ArrayList<String> customs = new ArrayList<>();
        customs.add("Custom");
        customs.add("Custom");
        customs.add("Custom");
        customs.add("Custom");

        poll.setChoices(customs);
        poll.setScores(new HashMap<String, Integer>());
        poll.setPeopleVoted(new ArrayList<String>());

        polls.add(poll);
        pollAdapter.notifyItemInserted(polls.size() - 1);
        rvPolls.scrollToPosition(0);
        poll.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(ChatActivity.this, "yay you put time poll on parse", Toast.LENGTH_LONG).show();
                }

            }
        });

    }


    @Override
    public void onFinishCreatePollFragment(Poll poll) {
        //poll.put("Event",eventId);
        poll.setEventId(eventId);
        //poll.put("Event", chatEvent);
        poll.put("Poll_creator", ParseUser.getCurrentUser());
        poll.setPollType("Custom");
        poll.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(ChatActivity.this, "yay you put poll on parse", Toast.LENGTH_LONG).show();
                }

            }
        });
        polls.add(poll);
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


        query.orderByAscending("createdAt");

        // Execute query to fetch all POLLS from Parse asynchronously
        // this is equivalent to a SELECT query with SQL
        query.findInBackground(new FindCallback<Poll>() {
            public void done(List<Poll> poll, ParseException e) {
                if (e == null) {
                    polls.clear();
                    //polls.addAll(poll);
                    for (int i = 0; i < poll.size(); i++) {
                        Poll tempPoll = poll.get(i);
                        polls.add(poll.get(i));
                    }
                    pollAdapter.notifyDataSetChanged(); // update adapter

                    // Scroll to the bottom of the list on initial load
                    if (mFirstLoad) {
                        rvPolls.smoothScrollToPosition(0);
                        mFirstLoad = false;
                    }


                } else {
                    Log.e("poll", "Error Loading Polls" + e);
                }

                findPollWinners(polls);


            }
        });

    }

    public Date convertStringToDate(String time) {
//        int monthEnd = time.indexOf('/');
//        int month = Integer.parseInt(time.substring(0, monthEnd));
//        int date = Integer.parseInt(time.substring(monthEnd + 1, monthEnd + 3));
//        int year = Integer.parseInt(time.substring(monthEnd + 4, monthEnd + 8));
//        int hour = Integer.parseInt(time.substring(clockStart + 2, time.indexOf(':')));
//        int min = Integer.parseInt(time.substring(time.indexOf(':') + 1, time.indexOf(':') + 3));
//        String amPm = time.substring(time.length() - 2, time.length());
//        if (amPm.equals("AM")) {
//            if (hour == 12) {
//                hour = 0;
//            }
//        } else {
//            hour = hour + 12;
//        }

        int clockStart = time.indexOf('@');
        String dateTime = time.substring(0, clockStart);
        String clockTime = time.substring(clockStart + 1);

//        Calendar calendar = Calendar.getInstance();
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("M/d/yyyy h:mm a", Locale.US);
        try {
            c.setTime(sdf.parse(dateTime + " " + clockTime));
        } catch (java.text.ParseException e) {
            e.printStackTrace();
            Log.e("Chat Activity", "can't parse date");

        }
//        calendar.clear();
//        calendar.set(Calendar.MONTH, month);
//        calendar.set(Calendar.DAY_OF_WEEK_IN_MONTH, date);
//        calendar.set(Calendar.YEAR, year);
//        calendar.set(Calendar.HOUR_OF_DAY, hour);
//        calendar.set(Calendar.MINUTE, min);
//        calendar.set(Calendar.SECOND, 0);
//        calendar.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    public void findPollWinners(List<Poll> polls) {
        String placeName = null;
        for (Poll eachPoll : polls) {
            if (eachPoll.getPollType().equals("Time")) {
                //if everyone has voted (minus one is Shaggy)
                if (eachPoll.getPeopleVoted().size() == chatParticipantsIds.size() - 1) {
                    Collection<Integer> score = eachPoll.getScores().values();
                    int max = Collections.max(score);
                    for (String key : eachPoll.getScores().keySet()) {
                        if (eachPoll.getScores().get(key) == max) {
                            timeWinner = key;
                            break;
                        }
                    }

                }
            } else if (eachPoll.getPollType().equals("Location")) {
                if (eachPoll.getPeopleVoted().size() == chatParticipantsIds.size() - 1) {
                    Collection<Integer> score = eachPoll.getScores().values();
                    int max = Collections.max(score);
                    for (String key : eachPoll.getScores().keySet()) {
                        if (eachPoll.getScores().get(key) == max) {
                            Map<String, ParseGeoPoint> places = eachPoll.getLocationOptions();
                            placeName=key;
                            locationWinner = places.get(key);
                            break;
                        }
                    }

                }

            }
        }
        if (timeWinner != null || locationWinner != null) {
            ParseQuery<ParseObject> eventQuery = ParseQuery.getQuery("Event");
            final String finalPlaceName = placeName;
            eventQuery.getInBackground(eventId, new GetCallback<ParseObject>() {
                public void done(ParseObject eventDb, ParseException e) {
                    if (e == null) {
                        if (timeWinner != null) {
                            Date date = convertStringToDate(timeWinner);
                            eventDb.put("event_time", date);
                        }
                        if (locationWinner != null) {
                            Double lat = locationWinner.getLatitude();
                            Double lng = locationWinner.getLongitude();
                            eventDb.put("location", finalPlaceName);
                            eventDb.put("latitude", lat);
                            eventDb.put("longitude", lng);
                        }
                        eventDb.saveInBackground();
                    }
                }
            });
        }

    }

    // function creates the main OR query to search for all user ids
    public ParseQuery<Message> createOrQueries(ArrayList<String> userIds) {
        // OR queries array
        List<ParseQuery<Message>> queries = new ArrayList<ParseQuery<Message>>();

        // for each id in the participants list, create a query and add to the OR arraylist
        for (int i = 0; i < userIds.size(); i++) {
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

    @Override
    public void onBackPressed() {
        if (openedPush) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("viewpager_position", 2);
            context.startActivity(intent);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public void onFinishTimePickerFragment(String time, int btn, int post) {
        Poll poll = polls.get(post);
        final List<String> choices = poll.getChoices();
        final Map<String, Integer> scores = poll.getScores();


        ParseQuery<ParseObject> pollQuery = ParseQuery.getQuery("Poll");

        if (btn == 0) {
            RadioButton rButton0 = (RadioButton) timeButtons.get(0);
            rButton0.setText(rButton0.getText() + " @ " + time);
            choices.set(0, rButton0.getText().toString());
            scores.put(rButton0.getText().toString(), 0);

        } else if (btn == 1) {
            RadioButton rButton1 = (RadioButton) timeButtons.get(1);
            rButton1.setText(rButton1.getText() + " @ " + time);
            choices.set(1, rButton1.getText().toString());
            scores.put(rButton1.getText().toString(), 0);

        } else if (btn == 2) {
            RadioButton rButton2 = (RadioButton) timeButtons.get(2);
            rButton2.setText(rButton2.getText() + " @ " + time);
            choices.set(2, rButton2.getText().toString());
            scores.put(rButton2.getText().toString(), 0);


        } else {
            RadioButton rButton3 = (RadioButton) timeButtons.get(3);
            rButton3.setText(rButton3.getText() + " @ " + time);
            choices.set(3, rButton3.getText().toString());
            scores.put(rButton3.getText().toString(), 0);

        }

        pollQuery.getInBackground(poll.getPollId(), new GetCallback<ParseObject>() {
            public void done(ParseObject pollDb, ParseException e) {
                if (e == null) {
                    pollDb.put("choices", choices);
                    pollDb.put("scores", scores);
                    pollDb.saveInBackground();
                }
            }
        });


    }


    @Override
    public void onFinishDatePickerFragment(String day, int btn, int post) {
        if (btn == 0) {
            RadioButton rButton0 = (RadioButton) timeButtons.get(0);
            rButton0.setText(day);
        } else if (btn == 1) {
            RadioButton rButton1 = (RadioButton) timeButtons.get(1);
            rButton1.setText(day);
        } else if (btn == 2) {
            RadioButton rButton2 = (RadioButton) timeButtons.get(2);
            rButton2.setText(day);
        } else {
            RadioButton rButton3 = (RadioButton) timeButtons.get(3);
            rButton3.setText(day);
        }


    }


    @Override
    public void setTimeValues(ArrayList<View> al) {
        timeButtons = al;

    }

    @Override
    public void setLocationValues(ArrayList<View> al, int position) {
        locationButtons = al;
        viewPosition = position;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        int btn;
        Poll poll = polls.get(viewPosition);
        final List<String> choices = poll.getChoices();
        final Map<String, Integer> scores = poll.getScores();
        final Map<String, ParseGeoPoint> locOptions = poll.getLocationOptions();

        //button values are hidden in code
        if (requestCode - 0 == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            btn = 0;
        } else if (requestCode - 1 == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            btn = 1;
            requestCode = requestCode - 1;
        } else if (requestCode - 2 == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            btn = 2;
            requestCode = requestCode - 2;
        } else {
            btn = 3;
            requestCode = requestCode - 3;
        }

        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.i(TAG, "Place: " + place.getName());
                LatLng loc = place.getLatLng();
                ParseGeoPoint newLoc = new ParseGeoPoint(loc.latitude, loc.longitude);
                locOptions.put(place.getName().toString(), newLoc);

                if (btn == 0) {
                    RadioButton rButton0 = (RadioButton) locationButtons.get(0);
                    rButton0.setText(place.getName());
                    choices.set(0, rButton0.getText().toString());
                    scores.put(rButton0.getText().toString(), 0);

                } else if (btn == 1) {
                    RadioButton rButton1 = (RadioButton) locationButtons.get(1);
                    rButton1.setText(place.getName());
                    choices.set(1, rButton1.getText().toString());
                    scores.put(rButton1.getText().toString(), 0);
                } else if (btn == 2) {
                    RadioButton rButton2 = (RadioButton) locationButtons.get(2);
                    rButton2.setText(place.getName());
                    choices.set(2, rButton2.getText().toString());
                    scores.put(rButton2.getText().toString(), 0);
                } else {
                    RadioButton rButton3 = (RadioButton) locationButtons.get(3);
                    rButton3.setText(place.getName());
                    choices.set(3, rButton3.getText().toString());
                    scores.put(rButton3.getText().toString(), 0);
                }

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
        ParseQuery<ParseObject> pollQuery = ParseQuery.getQuery("Poll");

        pollQuery.getInBackground(poll.getPollId(), new GetCallback<ParseObject>() {
            public void done(ParseObject pollDb, ParseException e) {
                if (e == null) {
                    pollDb.put("choices", choices);
                    pollDb.put("scores", scores);
                    pollDb.put("location_options", locOptions);
                    pollDb.saveInBackground();
                }
            }
        });


    }

    public void onEventReady(MenuItem menuItem) {
        Intent i = new Intent(context, EventReadyActivity.class);
        i.putExtra("timeOfEvent", event.timeOfEvent);
        i.putExtra("latitude", event.latitude);
        i.putExtra("longitude", event.longitude);
        context.startActivity(i);
    }

    public JsonObjectRequest getFoodRequest(String foodType, String lat, String lng) {
        // Pass second argument as "null" for GET requests
        String url = "https://developers.zomato.com/api/v2.1/search?q=";
        String foodSearch = foodType;
        String params = "&lat=" + lat + "&lon=" + lng + "&radius=18000";


        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url + foodSearch + params, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("response", "aww yeah");
                        try {
                            JSONArray restaurantArray = response.getJSONArray("restaurants");
                            JSONObject restaurant = restaurantArray.getJSONObject(0).getJSONObject("restaurant");
                            String restaurantName = restaurant.getString("name");
                            String address = restaurant.getJSONObject("location").getString("address");
                            callShaggyForResponse("Recommendation", restaurantName + "@ " + address);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //error occur
                    }
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Accept", "application/json");
                params.put("user-key", "09e67b4492d86e45473c0af26442ab3d");
                return params;
            }
        };
        return req;
    }

    private void callShaggyForResponse(String type, String body) {

        if (type.equals("Recommendation")) {
            Message m = new Message();
            m.setSenderId("InuSHuTqkn");
            m.setBody("Hey! There seems to be a lot of interest in " + favFood + ". Why not try out " + body);
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
        refreshMessages();
        refreshPolls();
    }
}

