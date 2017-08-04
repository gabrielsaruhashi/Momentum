package shag.com.shag.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import shag.com.shag.Clients.FacebookClient;
import shag.com.shag.Models.Event;
import shag.com.shag.Models.Message;
import shag.com.shag.Models.Poll;
import shag.com.shag.Other.MyAlarm;
import shag.com.shag.Other.ParseApplication;
import shag.com.shag.R;

import static com.raizlabs.android.dbflow.config.FlowManager.getContext;

public class SelectEventDeadlineActivity extends AppCompatActivity {

    public final static int MILLISECONDS_IN_MINUTE = 60000;
    Button makeEventButton;
    Date newDate = new Date(new Date().getTime()+MILLISECONDS_IN_MINUTE*60);
    EditText etDescription;
    String category;
    String eventType;
    String foodDescription;
    ParseUser currentUser;
    double eventLat;
    double eventLng;
    String placeName;
    TextView tvCategory;
    PlaceAutocompleteFragment autocompleteFragment;
    int color;

    private Button[] btn = new Button[4];
    private Button btn_unfocus;
    private int[] btn_id = {R.id.bt30, R.id.bt1, R.id.bt2, R.id.bt3, R.id.bt6,R.id.bt12};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ParseObject.registerSubclass(Poll.class);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_event_deadline);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        myToolbar.setTitleTextColor(0xFFFFFFFF);
        tvCategory = (TextView) findViewById(R.id.tvCategory);

        currentUser = ParseUser.getCurrentUser();
        category = getIntent().getStringExtra("Category");
        //public or private
        eventType = getIntent().getStringExtra("Event Type");
        //if public get the name, and LatLng of place
        if (eventType.equals("Public")){
            placeName=getIntent().getStringExtra("Place Name");
            eventLat=getIntent().getDoubleExtra("Lat",0.0);
            eventLng=getIntent().getDoubleExtra("Lng",0.0);
            //if food category, find the restaurant details
            if(category.equals("Food")) {
                foodDescription = getIntent().getStringExtra("Food Details");
            }
        }
        Button tv30 = (Button) findViewById(R.id.bt30);
        setListenerForTime(tv30, 30);

        Button tv1h = (Button) findViewById(R.id.bt1);
        setListenerForTime(tv1h, 60);

        Button tv2h = (Button) findViewById(R.id.bt2);
        setListenerForTime(tv2h, 120);

        Button tv3h = (Button) findViewById(R.id.bt3);
        setListenerForTime(tv3h, 180);

        Button tv6h = (Button) findViewById(R.id.bt6);
        setListenerForTime(tv6h, 360);

        Button tv12h = (Button) findViewById(R.id.bt12);
        setListenerForTime(tv12h, 720);
        etDescription = (EditText) findViewById(R.id.tvDescriptionInput);

        tvCategory.setText(category);
        tvCategory.setTextColor(Color.WHITE);

        btn_unfocus = tv30;


        switch (category){
            case "Food":
                color=R.color.food_color;
                tvCategory.setBackgroundResource(R.color.food_color);
                tv30.setBackgroundColor(getResources().getColor(R.color.food_color));
                tv1h.setBackgroundColor(getResources().getColor(R.color.food_color));
                tv2h.setBackgroundColor(getResources().getColor(R.color.food_color));
                tv3h.setBackgroundColor(getResources().getColor(R.color.food_color));
                tv6h.setBackgroundColor(getResources().getColor(R.color.food_color));
                tv12h.setBackgroundColor(getResources().getColor(R.color.food_color));
                break;
            case "Party":
                color=R.color.party_color;
                tvCategory.setBackgroundResource(R.color.party_color);
                tv30.setBackgroundColor(getResources().getColor(R.color.party_color));
                tv1h.setBackgroundColor(getResources().getColor(R.color.party_color));
                tv2h.setBackgroundColor(getResources().getColor(R.color.party_color));
                tv3h.setBackgroundColor(getResources().getColor(R.color.party_color));
                tv6h.setBackgroundColor(getResources().getColor(R.color.party_color));
                tv12h.setBackgroundColor(getResources().getColor(R.color.party_color));
                break;
            case "Explore":
                color=R.color.explore_color;
                tvCategory.setBackgroundResource(R.color.explore_color);
                tv30.setBackgroundColor(getResources().getColor(R.color.explore_color));
                tv1h.setBackgroundColor(getResources().getColor(R.color.explore_color));
                tv2h.setBackgroundColor(getResources().getColor(R.color.explore_color));
                tv3h.setBackgroundColor(getResources().getColor(R.color.explore_color));
                tv6h.setBackgroundColor(getResources().getColor(R.color.explore_color));
                tv12h.setBackgroundColor(getResources().getColor(R.color.explore_color));
                break;
            case "Music":
                color=R.color.explore_color;
                tvCategory.setBackgroundResource(R.color.explore_color);
                tv30.setBackgroundColor(getResources().getColor(R.color.explore_color));
                tv1h.setBackgroundColor(getResources().getColor(R.color.explore_color));
                tv2h.setBackgroundColor(getResources().getColor(R.color.explore_color));
                tv3h.setBackgroundColor(getResources().getColor(R.color.explore_color));
                tv6h.setBackgroundColor(getResources().getColor(R.color.explore_color));
                tv12h.setBackgroundColor(getResources().getColor(R.color.explore_color));
                break;
            case "Sports":
                color=R.color.sports_color;
                tvCategory.setBackgroundResource(R.color.sports_color);
                tv30.setBackgroundColor(getResources().getColor(R.color.sports_color));
                tv1h.setBackgroundColor(getResources().getColor(R.color.sports_color));
                tv2h.setBackgroundColor(getResources().getColor(R.color.sports_color));
                tv3h.setBackgroundColor(getResources().getColor(R.color.sports_color));
                tv6h.setBackgroundColor(getResources().getColor(R.color.sports_color));
                tv12h.setBackgroundColor(getResources().getColor(R.color.sports_color));
                break;
            case "Chill":
                color=R.color.chill_color;
                tvCategory.setBackgroundResource(R.color.chill_color);
                tv30.setBackgroundColor(getResources().getColor(R.color.chill_color));
                tv1h.setBackgroundColor(getResources().getColor(R.color.chill_color));
                tv2h.setBackgroundColor(getResources().getColor(R.color.chill_color));
                tv3h.setBackgroundColor(getResources().getColor(R.color.chill_color));
                tv6h.setBackgroundColor(getResources().getColor(R.color.chill_color));
                tv12h.setBackgroundColor(getResources().getColor(R.color.chill_color));
                break;
            case "Misc":
                color=R.color.misc_color;
                tvCategory.setBackgroundResource(R.color.misc_color);
                tv30.setBackgroundColor(getResources().getColor(R.color.misc_color));
                tv1h.setBackgroundColor(getResources().getColor(R.color.misc_color));
                tv2h.setBackgroundColor(getResources().getColor(R.color.misc_color));
                tv3h.setBackgroundColor(getResources().getColor(R.color.misc_color));
                tv6h.setBackgroundColor(getResources().getColor(R.color.misc_color));
                tv12h.setBackgroundColor(getResources().getColor(R.color.misc_color));
                break;

        }


        makeEventButton = (Button) findViewById(R.id.btMakeEvent);
        makeEventButton.setBackgroundColor(getResources().getColor(color));
        makeEventButton.setTextColor(Color.WHITE);
        makeEventButton.setEnabled(false);

        etDescription.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(s.toString().trim().length()==0){
                    makeEventButton.setEnabled(false);
                } else {
                    makeEventButton.setEnabled(true);
                }


            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

            makeEventButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (makeEventButton.isEnabled()) {

                        createEvent();
                        Intent i = new Intent(SelectEventDeadlineActivity.this, MainActivity.class);
                        startActivity(i);
                    }


                }
            });


    }

    public void setListenerForTime(final Button tv, final int minToDeadline) {
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case R.id.bt30 :
                        setFocus(btn_unfocus, tv);
                        break;

                    case R.id.bt1 :
                        setFocus(btn_unfocus, tv);
                        break;

                    case R.id.bt2 :
                        setFocus(btn_unfocus, tv);
                        break;

                    case R.id.bt3 :
                        setFocus(btn_unfocus, tv);
                        break;
                    case R.id.bt6 :
                        setFocus(btn_unfocus, tv);
                        break;

                    case R.id.bt12 :
                        setFocus(btn_unfocus, tv);
                        break;


                }
                newDate.setTime((new Date()).getTime() + minToDeadline*MILLISECONDS_IN_MINUTE);
                Toast.makeText(SelectEventDeadlineActivity.this, "Date: " + newDate.toString(), Toast.LENGTH_LONG).show();

            }
        });



    }

    private void setFocus(Button btn_unfocus, Button btn_focus) {
        btn_unfocus.setTextColor(Color.WHITE);
        btn_unfocus.setBackgroundColor(getResources().getColor(color));
        btn_focus.setTextColor(color);
        btn_focus.setBackgroundColor(Color.WHITE);
        this.btn_unfocus = btn_focus;
    }

    private void createEvent() {
        final Event newEvent = new Event();
        // populate newEvent
        newEvent.setEventOwnerName(currentUser.getString("name"));
        newEvent.setDescription(etDescription.getText().toString());

        //TODO what is this friends at event for

        newEvent.setFriendsAtEvent(new ArrayList<Long>());

        //if public, set the location and lat long
        if (eventType.equals("Public")){
            newEvent.setLocation(placeName);
            newEvent.setLatitude(eventLat);
            newEvent.setLongitude(eventLng);
            newEvent.setIsEventPrivate(false);
        }
        else{
            newEvent.setIsEventPrivate(true);
        }


        //  upon creating, save event owner's id to participant list
        ArrayList<String> initialParticipantsIds = new ArrayList<String>(Arrays.asList(currentUser.getObjectId()));
        newEvent.setParticipantsIds(initialParticipantsIds);
        //  upon creating, save event owner's facebook id to participant list
        HashMap data = (HashMap) currentUser.getMap("authData");
        HashMap facebookData = (HashMap) data.get("facebook");
        String userFacebookId = (String) facebookData.get("id");
        ArrayList<String> initialParticipantsFbIds = new ArrayList<String>();
        initialParticipantsFbIds.add(userFacebookId);
        newEvent.setParticipantsFacebookIds(initialParticipantsFbIds);


        // newEvent.setEventOwnerId(Long.parseLong(getCurrentUser().getObjectId(), 36));
        newEvent.setEventOwnerId(currentUser.getObjectId());


        //if they didn't pick a deadline, auto-set 1 hour deadline
        newEvent.setDeadline(newDate);
        if (newEvent.deadline == null) {
            newEvent.deadline = new Date();
            newEvent.deadline.setTime(new Date().getTime() + MILLISECONDS_IN_MINUTE*60);
            newEvent.setDeadline(newEvent.deadline);
        }

        newEvent.setIsFirstCreated(true);

        newEvent.setRecommendationMade(false);

        newEvent.setCategory(category);

        HashMap<String,List<Object>> hm = (HashMap) currentUser.getMap("categories_tracker");

        // update user's category counter
        // update category counter
        String x = category;
        int oldCounter = (int) hm.get(category).get(0);
        hm.get(category).set(0,oldCounter+1);

        if (foodDescription!=null){
            //get list of sub categories
            List<String> foodApiSubcategories = Arrays.asList(foodDescription.split(", "));
            //get food data object
            List<Object> foodData = hm.get("Food");
            //get hashmap of scores
            HashMap<String, Integer> foodSubCategoryMap = (HashMap<String, Integer>) foodData.get(1);

            //for each of the found food types
            for (String foodType : foodApiSubcategories){
                //if that food type is not already in the map
                if (foodSubCategoryMap.get(foodType)!=null){
                    //get points and increment by one
                    int foodSubCategoryPoints = foodSubCategoryMap.get(foodType);
                    foodSubCategoryPoints +=1;
                    //reset key to include incremented value
                    foodSubCategoryMap.put(foodType,foodSubCategoryPoints);
                    foodData.set(1,foodSubCategoryMap);
                    hm.put("Food", foodData);

                }
                //if food type is not in map
                else{
                    foodSubCategoryMap.put(foodType,1);
                    hm.get("Food").set(1,foodSubCategoryMap);
                }
            }
        }
        currentUser.put("categories_tracker", hm);


        newEvent.setLastMessageSent(new Message());
        newEvent.setTimeOfEvent(new Date());

        //newEvent.setTimeOfEvent(new Date((new Date()).getTime() + 24 * 60 * 60 * 1000)); //TODO: PUT REAL INFO IN HERE (after polls)
        //newEvent.setParseGeoPoint(new ParseGeoPoint(47.6101, -122.2015)); //TODO: PUT REAL INFO HERE TOO
        //newEvent.setLatitude(47.6101);
        //newEvent.setLongitude(-122.2015);

        newEvent.setParticipantsLocations(new HashMap<String, ParseGeoPoint>());
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
                                query.whereEqualTo("event_owner_id", currentUser.getObjectId());
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
}
