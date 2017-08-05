package shag.com.shag.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class SelectEventDetailsActivity extends AppCompatActivity {
    public final static int MILLISECONDS_IN_MINUTE = 60000;
    Button nextButton;
    Date newDate = new Date(new Date().getTime()+MILLISECONDS_IN_MINUTE*60);
    EditText etDescription;
    String category;
    String eventType;
    String foodDescription;
    ParseUser currentUser;
    double eventLat;
    double eventLng;
    String placeName;
    PlaceAutocompleteFragment autocompleteFragment;
    CardView cd1;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ParseObject.registerSubclass(Poll.class);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_event_details);

        currentUser = ParseApplication.getCurrentUser();
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

        etDescription = (EditText) findViewById(R.id.tvDescriptionInput);

        CardView tv30 = (CardView) findViewById(R.id.cv30m);
        setListenerForTime(tv30, 30);

        CardView tv1h = (CardView) findViewById(R.id.cv1h);
        setListenerForTime(tv1h, 60);

        CardView tv2h = (CardView) findViewById(R.id.cv2h);
        setListenerForTime(tv2h, 120);

        CardView tv3h = (CardView) findViewById(R.id.cv3h);
        setListenerForTime(tv3h, 180);

        CardView tv6h = (CardView) findViewById(R.id.cv6h);
        setListenerForTime(tv6h, 360);

        CardView tv12h = (CardView) findViewById(R.id.cv12h);
        setListenerForTime(tv12h, 720);

        nextButton = (Button) findViewById(R.id.btNext);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                createEvent();
                Intent i = new Intent(SelectEventDetailsActivity.this,MainActivity.class);
                startActivity(i);


            }
        });

    }

    public void setListenerForTime(CardView tv, final int minToDeadline) {
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newDate.setTime((new Date()).getTime() + minToDeadline*MILLISECONDS_IN_MINUTE);
                Toast.makeText(SelectEventDetailsActivity.this, "Date: " + newDate.toString(), Toast.LENGTH_LONG).show();

            }
        });

    }

    private void createEvent() {
        final Event newEvent = new Event();
        // populate newEvent
        newEvent.setEventOwnerName(ParseApplication.getCurrentUser().getString("name"));
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
        ArrayList<String> initialParticipantsIds = new ArrayList<String>(Arrays.asList(ParseApplication.getCurrentUser().getObjectId()));
        newEvent.setParticipantsIds(initialParticipantsIds);
        //  upon creating, save event owner's facebook id to participant list
        HashMap data = (HashMap) ParseApplication.getCurrentUser().getMap("authData");

        HashMap facebookData = (HashMap) data.get("facebook");
        String userFacebookId = (String) facebookData.get("id");
        ArrayList<String> initialParticipantsFbIds = new ArrayList<String>();
        initialParticipantsFbIds.add(userFacebookId);
        newEvent.setParticipantsFacebookIds(initialParticipantsFbIds);


        // newEvent.setEventOwnerId(Long.parseLong(getCurrentUser().getObjectId(), 36));
        newEvent.setEventOwnerId(ParseApplication.getCurrentUser().getObjectId());


        //if they didn't pick a deadline, auto-set 1 hour deadline
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
        ParseObject currentUser = ParseApplication.getCurrentUser();

        //newEvent.put("User_event_owner", currentUser);
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
                                query.whereEqualTo("event_owner_id",ParseApplication.getCurrentUser().getObjectId());
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
