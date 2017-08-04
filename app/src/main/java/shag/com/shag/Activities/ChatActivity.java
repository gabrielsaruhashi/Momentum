package shag.com.shag.Activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.PorterDuff;
import android.location.Location;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;
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
import shag.com.shag.Fragments.DialogFragments.ConflictingEventsDialogFragment;
import shag.com.shag.Fragments.DialogFragments.CreatePollDialogFragment;
import shag.com.shag.Fragments.DialogFragments.DatePickerFragment;
import shag.com.shag.Fragments.DialogFragments.TimePickerFragment;
import shag.com.shag.Models.CalendarEvent;
import shag.com.shag.Models.Event;
import shag.com.shag.Models.Message;
import shag.com.shag.Models.Poll;
import shag.com.shag.Other.DividerItemDecorator;
import shag.com.shag.Other.ParseApplication;
import shag.com.shag.R;

import static com.raizlabs.android.dbflow.config.FlowManager.getContext;


public class ChatActivity extends AppCompatActivity implements CreatePollDialogFragment.CreatePollFragmentListener,
        TimePickerFragment.TimePickerFragmentListener, DatePickerFragment.DatePickerFragmentListener,
        PollsAdapter.TimeButtonsInterface, PollsAdapter.LocationButtonsInterface, PollsAdapter.ConflictTextViewInterface,
        PollsAdapter.EventReadyCheckInterface {

    static final String TAG = "DEBUG_CHAT";
    static final int MAX_CHAT_MESSAGES_TO_SHOW = 50;
    static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    Context context;
    EditText etMessage;
    ImageButton btSend;
    ArrayList<View> timeButtons;
    ArrayList<View> locationButtons;
    int viewPosition;
    private FusedLocationProviderClient mFusedLocationClient;
    // constants
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private static final int MY_PERMISSIONS_REQUEST_READ_CALENDAR = 23;
    private static final int INVITE_FRIENDS_ACTIVITY = 27;
    // thread
    Runnable taskToGetCalendarEvents;
    // for the adapter logic
    RecyclerView rvChat;
    ArrayList<Message> mMessages;
    MessagesAdapter mAdapter;
    // Keep track of initial load to scroll to the bottom of the ListView
    boolean mFirstLoad;
    /* polls */
    // list of tweets
    ArrayList<Poll> polls;
    // recycler view
    RecyclerView rvPolls;
    // the adapter wired to the new view
    PollsAdapter pollAdapter;

    // for the chat views
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
    private Event eventFromQuery;
    private String favFood;

    // ONLY use these variables when opening push notification
    boolean openedPush;
    // for calendar integration
    private ArrayList<CalendarEvent> calendarEvents;
    private ArrayList<CalendarEvent> conflictingCalendarEvents;
    Menu menu;
    ParseQuery<Poll> parseQueryPoll;
    SubscriptionHandling<Poll> pollSubscriptionHandling;

    TextView tvConflict;

    ParseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        //TODO check if this is working
        tvConflict = (TextView) findViewById(R.id.tvConflict);
      currentUser = ParseApplication.getCurrentUser();

//        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close) {
//            public void onDrawerOpened(View drawerView) {
//                super.onDrawerOpened(drawerView);
//
//            }
//        };
        //drawerLayout.addDrawerListener(actionBarDrawerToggle);
        //actionBarDrawerToggle.syncState();

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        myToolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));


        currentUser = ParseApplication.getCurrentUser();

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
        pollAdapter = new PollsAdapter(getContext(), this, this, this, this, polls);

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

        //set location client
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(ChatActivity.this);

        // unwrap intent and get current user id, and participants
        Intent intent = getIntent();
        eventId = intent.getStringExtra("event_id");
        chatParticipantsIds = intent.getStringArrayListExtra("participants_ids");

        //finding out if this is the first time the event has been creating
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
        query.include("User_event_owner");
        query.include("last_message_sent");

        try {
            eventFromQuery = (Event) query.get(eventId);
            //if so, user has just opened a push notification, need to query for more info
            if (chatParticipantsIds == null) {
                openedPush = true;
                chatParticipantsIds = (ArrayList) eventFromQuery.getList("participants_id");
                chatParticipantsIds.add("InuSHuTqkn");  //adding shaggy
            }

            isEventNew = eventFromQuery.getBoolean("is_first_created");
            isEventPrivate = eventFromQuery.getBoolean("is_event_private");
            isRecommendationMade = eventFromQuery.getBoolean("is_recommendation_made");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // get current user information
        currentUserId = currentUser.getObjectId();

        //set up initial polls
        //if event is new, make time and location polls
        if (isEventNew) {
            try {
                createTimeAndLocationPolls();

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
        } else {
            refreshPolls();

        }

        // populate views, setup listeners and populate views
        //refresh messages and polls both here
        setupMessagePosting();

        // instantiate conflicting calendar events
        conflictingCalendarEvents = new ArrayList<CalendarEvent>();

        // check for permissions and set up thread to get events
        taskToGetCalendarEvents = new Runnable() {
            @Override
            public void run() {
                String[] projection = new String[]{CalendarContract.Events.CALENDAR_ID, CalendarContract.Events.TITLE, CalendarContract.Events.DESCRIPTION,
                        CalendarContract.Events.DTSTART, CalendarContract.Events.DTEND, CalendarContract.Events.ALL_DAY, CalendarContract.Events.EVENT_LOCATION};
                //TODO change start time
                // 0 = January, 1 = February, ...
                Calendar startTime = Calendar.getInstance();
                startTime.set(2017, 00, 01, 00, 00);
                Calendar endTime = Calendar.getInstance();
                endTime.set(2018, 00, 01, 00, 00);

                // the range is all data from 2014
                String selection = "(( " + CalendarContract.Events.DTSTART + " >= " + startTime.getTimeInMillis() + " ) AND ( " + CalendarContract.Events.DTSTART + " <= " + endTime.getTimeInMillis() + " ))";

                // instantiate array of calendar events
                calendarEvents = new ArrayList<CalendarEvent>();

                // ensure user actually gave permission to read events
                if (ContextCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
                    final Cursor cursor = getBaseContext().getContentResolver().query(CalendarContract.Events.CONTENT_URI, projection, selection, null, null);
                    Log.i("DEBUG_CURSOR", DatabaseUtils.dumpCursorToString(cursor));

                    // output the events
                    if (cursor.moveToFirst()) {
                        do {
                            // update the calendarEvents array on the UI Thread
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //Toast.makeText(getApplicationContext(), "Title: " + cursor.getString(1) + " Start-Time: " + (new Date(cursor.getLong(3))).toString(), Toast.LENGTH_SHORT).show();
                                    CalendarEvent freshCalendarEvent = CalendarEvent.fromCalendarCursor(cursor);
                                    calendarEvents.add(freshCalendarEvent);
                                }
                            });
                        }
                        while (cursor.moveToNext() && cursor.getPosition() < cursor.getCount() - 1);
                    }
                    //cursor.close();
                    // cursor.close
                }
            }
        };
        setupCalendars();


        if (currentUserId.equals(eventFromQuery.getString("event_owner_id")) && isRecommendationMade == false
                && isEventPrivate == true) {
            //if no recommendation has been made yet, if event i==food/private, and if everyone has joined
            if (new Date().after(eventFromQuery.getDate("deadline")) && isRecommendationMade == false
                    && isEventPrivate == true && eventFromQuery.getString("category").equals("Food")) {
                isRecommendationMade = true;
                ParseQuery<ParseObject> eventQuery = ParseQuery.getQuery("Event");
                eventQuery.getInBackground(eventId, new GetCallback<ParseObject>() {
                    public void done(ParseObject eventDb, ParseException e) {
                        if (e == null) {
                            eventDb.put("is_recommendation_made", isRecommendationMade);
                            eventDb.saveInBackground();
                        }
                    }
                });
                recommendRestaurant();
            }

            setupLiveQueires();
        }
    }

    private void setupLiveQueires() {
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
                    public void onEvent(ParseQuery<Message> query, final Message object) {
                        String senderId = object.getSenderId();
                        String newEventId = object.getEventId();

                        if (!senderId.equals(currentUserId) && newEventId.equals(eventId)) {
                            // RecyclerView updates need to be run on the UI thread
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mMessages.add(0, object);
                                    mAdapter.notifyDataSetChanged();
                                    rvChat.scrollToPosition(0);

                                }
                            });
                        }
                    }


                });


        parseQueryPoll = ParseQuery.getQuery(Poll.class);
        pollSubscriptionHandling = parseLiveQueryClient.subscribe(parseQueryPoll);
        pollSubscriptionHandling.handleEvent(SubscriptionHandling.Event.CREATE, new SubscriptionHandling.HandleEventCallback<Poll>() {
            @Override
            public void onEvent(ParseQuery<Poll> query, Poll object) {
                String senderId = (String) object.get("poll_creator_id");
                if (senderId != null) {
                    String newEventId = object.getEventId();
                    polls.add(object);

//                    if (!senderId.equals(currentUserId) && eventId.equals(newEventId)) {
//                        polls.add(object);
//                    }

                    // RecyclerView updates need to be run on the UI thread
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pollAdapter.notifyDataSetChanged();
                            rvPolls.scrollToPosition(0);

                        }
                    });
                }
                String newEventId = object.getEventId();
                polls.add(object);

//                    if (!senderId.equals(currentUserId) && eventId.equals(newEventId)) {
//                        polls.add(object);
//                    }

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

        // Connect to Parse server
        pollSubscriptionHandling.handleEvent(SubscriptionHandling.Event.UPDATE, new SubscriptionHandling.HandleEventCallback<Poll>() {
            @Override
            public void onEvent(ParseQuery<Poll> query, Poll object) {
                int pos = -1;
                if (object.getPollType().equals("Time")) {
                    pos = 0;
                } else if (object.getPollType().equals("Location")) {
                    pos = 1;
                }
                String newEventId = object.getEventId();
                if (eventId.equals(newEventId)) {
                    //reconstruct new list
                    polls.remove(pos);

                    ArrayList<Poll> newPollList = new ArrayList<Poll>();
                    newPollList.addAll(polls);
                    newPollList.add(pos,object);

                    polls.clear();
                    polls.addAll(newPollList);
                    //polls.set(pos, object);

                }
                // RecyclerView updates need to be run on the UI thread
                final int finalPos = pos;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //rvPolls.invalidate();
                        pollAdapter.notifyDataSetChanged();
                        //pollAdapter.notifyItemChanged(finalPos);
                        rvPolls.scrollToPosition(0);
                        //pollAdapter.notifyItemChanged(1);

                    }
                });
            }

        });

    }

    private void recommendRestaurant() {
        // construct OR query to userQuery

        ArrayList<String> eventUsers = new ArrayList<>(chatParticipantsIds);
        eventUsers.remove("InuSHuTqkn");
        final String cuisineInterest = findMostPopularFood(eventUsers);

        //get location permissions
        if (ActivityCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        } else {
            ActivityCompat.requestPermissions(ChatActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
        }

        //find last location
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(ChatActivity.this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        favFood = cuisineInterest;
                        Double lat = location.getLatitude();
                        Double lng = location.getLongitude();
                        VolleyRequest.getInstance(getApplicationContext()).addToRequestQueue(getFoodRequest(cuisineInterest, lat.toString(), lng.toString()));


                    }
                });


    }

    private String findMostPopularFood(ArrayList<String> people) {

        final HashMap<String, Integer> foodCounts = new HashMap<>();
        //TODO use an AsyncTask
        for (String userId : people) {
            ParseQuery<ParseUser> person = ParseUser.getQuery();
            try {
                ParseUser userX = person.get(userId);
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
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }


//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }


    // setup button event handler which posts the entered message to Parse
    void setupMessagePosting() {
        // find the text field and button
        etMessage = (EditText) findViewById(R.id.etMessage);
        btSend = (ImageButton) findViewById(R.id.btSend);
        rvChat = (RecyclerView) findViewById(R.id.rvChat);
        mMessages = new ArrayList<>();
        mFirstLoad = true;

        // set adapter
        mAdapter = new MessagesAdapter(ChatActivity.this, currentUserId, mMessages);
        rvChat.setAdapter(mAdapter);

        btSend.getBackground().setColorFilter(ContextCompat.getColor(context, R.color.deselected_gray), PorterDuff.Mode.MULTIPLY);

        btSend.setEnabled(false);

        etMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                    btSend.setEnabled(true);
                    btSend.getBackground().setColorFilter(ContextCompat.getColor(context, R.color.medium_gray), PorterDuff.Mode.MULTIPLY);
                } else {
                    btSend.setEnabled(false);
                    btSend.getBackground().setColorFilter(ContextCompat.getColor(context, R.color.deselected_gray), PorterDuff.Mode.MULTIPLY);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        // associate the LayoutManager with the RecylcerView
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ChatActivity.this);
        // display the newest posts ordered from oldest to newest
        linearLayoutManager.setReverseLayout(true);
        rvChat.setLayoutManager(linearLayoutManager);

        btSend.setEnabled(false);
        etMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                    btSend.setEnabled(true);
                } else {
                    btSend.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        // when send button is clicked, create message object on Parse
        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String data = etMessage.getText().toString();

                // using new `Message` Parse-backed model now
                final Message message = new Message();
                // populate message
                //TODO pass the entire event object
                message.setBody(data);
                // save User pointer
                message.put("User_sender", currentUser);
                message.setSenderId(currentUserId);
                message.setEventId(eventId);
                message.setSenderProfileImageUrl(currentUser.getString("profile_image_url"));
                message.setSenderName(currentUser.getString("name"));

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

                            ParseCloud.callFunctionInBackground("pushChannelTest", payload);
                            Toast.makeText(ChatActivity.this, "Successfully created message on Parse",
                                    Toast.LENGTH_SHORT).show();

                            etMessage.setText(null);
                            // add message to arraylist

                            eventFromQuery.setLastMessageSent(message);
                            eventFromQuery.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        Log.i("CHATACTIVITY", "All good");
                                        mMessages.add(0, message);
                                        mAdapter.notifyItemInserted(0);
                                        rvChat.scrollToPosition(0);

                                        if (data.equalsIgnoreCase("hi Shaggy")) {
                                            final Message m = new Message();
                                            m.setSenderId("InuSHuTqkn");
                                            m.setBody("Hi! My name is Shaggy");
                                            m.setEventId(eventId);
                                            m.setSenderName("Shaggy");
                                            try {
                                                m.save();

                                                eventFromQuery.setLastMessageSent(m);
                                                eventFromQuery.saveInBackground(new SaveCallback() {
                                                    @Override
                                                    public void done(ParseException e) {
                                                        if (e == null) {
                                                            Log.i("CHATACTIVITY", "All good");
                                                            mMessages.add(0, m);
                                                            mAdapter.notifyItemInserted(0);
                                                            rvChat.scrollToPosition(0);
                                                        } else {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                });
                                            } catch (ParseException exception) {
                                                exception.printStackTrace();
                                            }

                                        }
                                    } else {
                                        e.printStackTrace();
                                    }
                                }
                            });

                        } else {
                            Log.e(TAG, "Failed to save message", e);
                        }
                    }
                });
            }
        });

        // populate messages
        refreshMessages();

    }

    public void setupCalendars() {

        // Assume thisActivity is the current activity
        int permissionCheck = ContextCompat.checkSelfPermission(ChatActivity.this,
                android.Manifest.permission.READ_CALENDAR);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CALENDAR},
                    MY_PERMISSIONS_REQUEST_READ_CALENDAR);

        } else { // if permissions is granted, just start the thread
            Thread calendarThread = new Thread(taskToGetCalendarEvents);
            calendarThread.start();
        }
    }

    // when user grants access to the read events class
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Log.d("TAG", "dialog onRequestPermissionsResult");
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CALENDAR:
                // Check Permissions Granted or not
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Thread calendarThread = new Thread(taskToGetCalendarEvents);
                    calendarThread.start();
                } else {
                    // Permission Denied
                    Toast.makeText(getContext(), "Read contact permission is denied", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    public void createTimeAndLocationPolls() throws ParseException {
        final Poll timePoll = new Poll();
        final Poll locPoll = new Poll();

        //poll.setEventId(eventId);
        //poll.put("Event", chatEvent);

        timePoll.setEventId(eventId);
        timePoll.put("Poll_creator", currentUser);
        timePoll.put("poll_creator_id", currentUserId);
        timePoll.setPollType("Time");
        timePoll.setQuestion("Time of Event");
        ArrayList<String> customs = new ArrayList<>();
        customs.add("Custom");
        customs.add("Custom");
        customs.add("Custom");
        customs.add("Custom");

        timePoll.setChoices(customs);
        timePoll.setScores(new HashMap<String, Integer>());
        timePoll.setPeopleVoted(new ArrayList<String>());

//        if (poll.getPollType().equals("Location")) {
//            poll.setLocationOptions(new HashMap<String, ParseGeoPoint>());
//        }

        timePoll.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(ChatActivity.this, "yay you put a Time poll on parse", Toast.LENGTH_LONG).show();
                    if (isEventPrivate) {

                        //make a location poll
                        locPoll.setEventId(eventId);
                        locPoll.put("Poll_creator", currentUser);
                        locPoll.put("poll_creator_id", currentUserId);
                        locPoll.setPollType("Location");
                        locPoll.setQuestion("Location of Event");
                        ArrayList<String> customs = new ArrayList<>();
                        customs.add("Custom");
                        customs.add("Custom");
                        customs.add("Custom");
                        customs.add("Custom");

                        locPoll.setChoices(customs);
                        locPoll.setScores(new HashMap<String, Integer>());
                        locPoll.setPeopleVoted(new ArrayList<String>());
                        locPoll.setLocationOptions(new HashMap<String, ParseGeoPoint>());
                        polls.add(locPoll);
                        pollAdapter.notifyDataSetChanged();
                        rvPolls.scrollToPosition(0);
                        locPoll.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                Toast.makeText(ChatActivity.this, "yay you put a loc poll on parse", Toast.LENGTH_LONG).show();

                            }
                        });


                    }
                }

            }
        });
        polls.add(timePoll);
        pollAdapter.notifyDataSetChanged();
        rvPolls.scrollToPosition(0);
        //poll.save();

    }

    public void createTimePoll() {
        Poll poll = new Poll();
        //poll.setEventId(eventId);
        //poll.put("Event", chatEvent);
        poll.setEventId(eventId);
        poll.put("Poll_creator", currentUser);
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
        poll.put("Poll_creator", currentUser);
        poll.put("poll_creator_id", currentUserId);
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
            public void done(List<Poll> pollList, ParseException e) {
                if (e == null) {
                    polls.clear();
                    //polls.addAll(poll);
                    for (int i = 0; i < pollList.size(); i++) {
                        polls.add(pollList.get(i));
                    }
                    pollAdapter.notifyDataSetChanged(); // update adapter

                    // Scroll to the bottom of the list on initial load
                    if (mFirstLoad) {
                        rvPolls.smoothScrollToPosition(0);
                        mFirstLoad = false;
                    }

                    if (pollList != null && pollList.size() != 0) {
                        findPollWinners(polls);
                    }


                } else {
                    Log.e("poll", "Error Loading Polls" + e);
                }


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
                            placeName = key;
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
                            eventDb.put("event_time_string", timeWinner);
                        }
                        if (locationWinner != null) {
                            Double lat = locationWinner.getLatitude();
                            Double lng = locationWinner.getLongitude();
                            eventDb.put("location", finalPlaceName);
                            eventDb.put("latitude", lat);
                            eventDb.put("longitude", lng);
                        }

                        if (timeWinner != null && locationWinner != null) {
                            MenuItem mi = menu.findItem(R.id.miEventReady);
                            mi.setVisible(true);
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
        //need to re-load chats if user opened a push
        if (openedPush) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("viewpager_position", 2);
            context.startActivity(intent);
        } else {
            super.onBackPressed(); //otherwise follow regular life cycle
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
            //TODO check this, put in different thread
            if (conflictsWithCalendarEvent((String) rButton0.getText())) {
                rButton0.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
            }

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

            if (conflictsWithCalendarEvent((String) rButton3.getText())) {
                rButton3.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
            }

        }

        pollQuery.getInBackground(poll.getPollId(), new GetCallback<ParseObject>() {
            public void done(ParseObject pollDb, ParseException e) {
                if (e == null) {
                    pollDb.put("choices", choices);
                    pollDb.put("scores", scores);
                    try {
                        pollDb.save();
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
    }

    //TODO this might break if calendar events hasnt returned yet
    public boolean conflictsWithCalendarEvent(String timeOption) {
        Date dateTimeOption = convertStringToDate(timeOption);
        boolean hasConflict = false;

        // gets list of all calendar events that conflict with this time option
        for (CalendarEvent calendarEvent : calendarEvents) {
            if (!calendarEvent.getdStart().after(dateTimeOption) && !calendarEvent.getdEnd().before(dateTimeOption)) {
                /* calendar event start time <= poll option <= calendar event end time */
                conflictingCalendarEvents.add(calendarEvent);
                // return true to add * to poll option UI
                hasConflict = true;
            }
        }
        return hasConflict;
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
        final ParseQuery<ParseObject> pollQuery = ParseQuery.getQuery("Poll");

        pollQuery.getInBackground(poll.getPollId(), new GetCallback<ParseObject>() {
            public void done(ParseObject pollDb, ParseException e) {
                if (e == null) {
                    pollDb.put("choices", choices);
                    pollDb.put("scores", scores);
                    pollDb.put("location_options", locOptions);
                    try {
                        pollDb.save();
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
    }

    public void onEventReady(MenuItem menuItem) {
        Intent i = new Intent(context, EventReadyActivity.class);
        i.putExtra("eventId", eventFromQuery.getEventId());
        context.startActivity(i);
    }

    public void onInviteFriends(MenuItem menuItem) {
        Intent i = new Intent(context, InviteFriendsActivity.class);
        i.putExtra("eventId", eventFromQuery.getEventId());
        i.putExtra("eventOwnerName", eventFromQuery.getEventOwnerName());
        i.putExtra("eventParticipants", eventFromQuery.getParticipantsIds());
        i.putExtra("eventDescription", eventFromQuery.getDescription());
        this.startActivityForResult(i, INVITE_FRIENDS_ACTIVITY);
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
                            callShaggyForResponse("Recommendation", restaurantName + " @ " + address);
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
            final Message m = new Message();
            m.setSenderId("InuSHuTqkn");
            if (favFood == null) {
                m.setBody("Hey! Feel free to start looking for restaurants. Why not try out " + body);
            } else {
                m.setBody("Hey! There seems to be a lot of interest in " + favFood + ". Why not try out " + body);
            }
            m.setEventId(eventId);
            m.setSenderName("Shaggy");
            m.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
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
                    payload.put("customData", m.getBody());
                    payload.put("title", "New message in channel");
                    payload.put("channelID", eventId);
                    payload.put("senderID", currentUserId);
                    payload.put("token", token);

                    eventFromQuery.setLastMessageSent(m);
                    eventFromQuery.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Log.i("CHATACTIVITY", "All good");
                            } else {
                                e.printStackTrace();
                            }
                        }
                    });

                    mAdapter.notifyItemInserted(0);
                    rvChat.smoothScrollToPosition(0);
                }
            });

        }
        refreshMessages();
        refreshPolls();
    }

    private void showConflictingEventsDialog() {
        FragmentManager fm = getSupportFragmentManager();
        ConflictingEventsDialogFragment conflictingEventsDialogFragment = ConflictingEventsDialogFragment.newInstance(conflictingCalendarEvents);
        conflictingEventsDialogFragment.show(fm, "fragment_conflicting_calendar_events");
    }

    @Override
    public void setTvConflictVisibility(TextView tvConflict, ArrayList<String> timeOptions) {
        for (String timeOption : timeOptions) {
            if (conflictsWithCalendarEvent(timeOption)) {
                tvConflict.setVisibility(View.VISIBLE);
                // set click listener for conflicts
                tvConflict.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showConflictingEventsDialog();
                    }
                });
            }
        }
    }

    public void checkIfEventReady() {
        findPollWinners(polls);
        if (timeWinner != null && locationWinner != null) {
            MenuItem mi = menu.findItem(R.id.miEventReady);
            mi.setVisible(true);
        }
    }
}

