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
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import shag.com.shag.Clients.FacebookClient;
import shag.com.shag.Models.Event;
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
    PlaceAutocompleteFragment autocompleteFragment;
    CardView cd1;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ParseObject.registerSubclass(Poll.class);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_event_details);

        category = getIntent().getStringExtra("Category");

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
                //newEvent.deadline = newDate;

            }
        });

    }

    private void createEvent() {
        final Event newEvent = new Event();
        // populate newEvent
        newEvent.setEventOwnerName(ParseUser.getCurrentUser().getString("name"));
        newEvent.setDescription(etDescription.getText().toString());
                /*newEvent.setLatLng(
                        new LatLng(47.628883, -122.342606)
                ); */
        //TODO what is this friends at event for
        newEvent.setFriendsAtEvent(new ArrayList<Long>());
        newEvent.setLocation("Facebook Seattle");

        //  upon creating, save event owner's id to participant list
        ArrayList<String> initialParticipantsIds = new ArrayList<String>(Arrays.asList(ParseUser.getCurrentUser().getObjectId()));
        Log.i("DEBUG_CREATE_EVENT", initialParticipantsIds.toString());
        newEvent.setParticipantsIds(initialParticipantsIds);

        // newEvent.setEventOwnerId(Long.parseLong(getCurrentUser().getObjectId(), 36));
        newEvent.setEventOwnerId(ParseUser.getCurrentUser().getObjectId());

        if (newEvent.deadline == null) {
            newEvent.deadline = new Date();
            newEvent.deadline.setTime(new Date().getTime() + MILLISECONDS_IN_MINUTE*60);
            newEvent.setDeadline(newEvent.deadline);
        }

        newEvent.setIsFirstCreated(true);
        newEvent.setCategory(category);
        newEvent.setTimeOfEvent(new Date()); //TODO: PUT REAL INFO IN HERE AT SOME POINT
        ParseObject currentUser = ParseUser.getCurrentUser();
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
