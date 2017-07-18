package shag.com.shag.Fragments.DialogFragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import shag.com.shag.Activities.MainActivity;
import shag.com.shag.Clients.FacebookClient;
import shag.com.shag.Fragments.FriendsFragment;
import shag.com.shag.Models.Event;
import shag.com.shag.Other.ParseApplication;
import shag.com.shag.R;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;

/**
 * Created by gabesaruhashi on 7/11/17.
 */

public class CreateDetailsDialogFragment extends DialogFragment  {
    private EditText etDescription;
    private EditText etLocation;
    private Button btSend;
    private Button btCancel;
    private ImageButton btInvite;
    private ImageButton btLocation;
    private ImageButton btTime;
    private Event newEvent;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private LinearLayout llExpireOptions;
    public final static int MILLISECONDS_IN_MINUTE = 60000;
    private String category;
    FacebookClient facebookClient;
    private String currentUserId;


    public CreateDetailsDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    // gets the category name from pick category fragment
    public static CreateDetailsDialogFragment newInstance(String category) {
        CreateDetailsDialogFragment frag = new CreateDetailsDialogFragment();
        Bundle args = new Bundle();
        args.putString("category", category);
        frag.setArguments(args);
        return frag;
    }

    // inflate the view
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // initialize client
        facebookClient = ParseApplication.getFacebookRestClient();
        // get current user id
        currentUserId = ParseUser.getCurrentUser().getObjectId();

        return inflater.inflate(R.layout.fragment_create_details, container, false);
    }

    // 1) defines the listener interface with a method passing back data result.
    public interface CreateDetailsDialogListener {
        void onFinishCreateDetailsDialog(Event newEvent);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // instantiate new event
        newEvent = new Event();

        // get views
        etDescription = (EditText) view.findViewById(R.id.etDescription);
        etLocation = (EditText) view.findViewById(R.id.locationInput);

        btSend = (Button) view.findViewById(R.id.btSend);
        btLocation = (ImageButton) view.findViewById(R.id.btLocation);
        llExpireOptions = (LinearLayout) view.findViewById(R.id.llExpireOptions);
        llExpireOptions.setVisibility(View.GONE);
        btTime = (ImageButton) view.findViewById(R.id.btTime);
        btInvite = (ImageButton) view.findViewById(R.id.btInvite);

        // Fetch arguments from bundle and set title
        category = getArguments().getString("category");
        //getDialog().setTitle(category);
        // Show soft keyboard automatically and request focus to field
        etDescription.requestFocus();
        //getDialog().getWindow().setSoftInputMode(
                //WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);


        // Setup a callback when the "submit" button is pressed
        btSend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
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
                ArrayList<String> initialParticipantsIds = new ArrayList<String>(Arrays.asList(currentUserId));
                Log.i("DEBUG_CREATE_EVENT", initialParticipantsIds.toString());
                newEvent.setParticipantsIds(initialParticipantsIds);

                //TODO remove owner id
                // newEvent.setEventOwnerId(Long.parseLong(getCurrentUser().getObjectId(), 36));
                newEvent.setEventOwnerId(currentUserId);

                if (newEvent.deadline == null) {
                    newEvent.deadline = new Date();
                    newEvent.deadline.setTime(new Date().getTime() + MILLISECONDS_IN_MINUTE*60);
                    newEvent.setDeadline(newEvent.deadline);
                }

                newEvent.setCategory(category);
                // save current user as the event owner
                ParseObject currentUser = ParseUser.getCurrentUser();
                newEvent.put("User_event_owner", currentUser);
                Log.i("DEBUG_CREATE", currentUser.getObjectId());

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
                                        Toast.makeText(getContext(), "Successfully created event!", Toast.LENGTH_SHORT).show();

                                        // send back to pick category dialog after being saved
                                        sendBackResult(newEvent);
                                    } else {
                                        Log.e(TAG, "Failed to save message", e);
                                    }
                                }
                            });

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

            }

        });

        btLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOpenSearch();
            }
        });

        btTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (llExpireOptions.getVisibility() == View.GONE) {
                    llExpireOptions.setVisibility(View.VISIBLE);
                } else {
                    llExpireOptions.setVisibility(View.GONE);
                }
            }
        });

        TextView tv30 = (TextView) view.findViewById(R.id.tv30);
        setListenerForTime(tv30, 30);

        TextView tv1h = (TextView) view.findViewById(R.id.tv1h);
        setListenerForTime(tv1h, 60);

        TextView tv2h = (TextView) view.findViewById(R.id.tv2h);
        setListenerForTime(tv2h, 120);

        TextView tv3h = (TextView) view.findViewById(R.id.tv3h);
        setListenerForTime(tv3h, 180);

        TextView tv6h = (TextView) view.findViewById(R.id.tv6h);
        setListenerForTime(tv6h, 360);

        TextView tv12h = (TextView) view.findViewById(R.id.tv12h);
        setListenerForTime(tv12h, 720);

        // invite friends listener
        btInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Begin the transaction
                FragmentTransaction ft = getChildFragmentManager().beginTransaction();
                // Replace the contents of the container with the new fragment
                ft.replace(R.id.friends_list_container, new FriendsFragment());
                // or ft.add(R.id.your_placeholder, new FooFragment());

                // Complete the changes added above
                ft.commit();

            }
        });

    }

    private void onOpenSearch() {
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                            .build(getActivity());
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(getActivity(), data);
                newEvent.setLocation(place.getName().toString());
                newEvent.setParseGeoPoint(new ParseGeoPoint(place.getLatLng().latitude, place.getLatLng().longitude));
                //newEvent.setLatLng(place.getLatLng());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(getActivity(), data);
                // TODO: Handle the error.

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }


    // Call this method to send the data back to the parent fragment
    public void sendBackResult(Event event) {
        // Notice the use of `getTargetFragment` which will be set when the dialog is displayed
        //CreateDetailsDialogListener listener = (CreateDetailsDialogListener) getTargetFragment();
        //listener.onFinishCreateDetailsDialog(event);
        //dismiss();
        Intent i = new Intent(getContext(), MainActivity.class);
        getContext().startActivity(i);
    }

    public void setListenerForTime(TextView tv, final int minToDeadline) {
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Date newDate = new Date();
                newDate.setTime(newDate.getTime() + minToDeadline*MILLISECONDS_IN_MINUTE);
                Toast.makeText(getContext(), "Date: " + newDate.toString(), Toast.LENGTH_LONG).show();
                //newEvent.deadline = newDate;
                newEvent.setDeadline(newDate);
            }
        });
    }
}
