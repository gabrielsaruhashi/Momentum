package shag.com.shag.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import shag.com.shag.Adapters.FeedAdapter;
import shag.com.shag.Clients.FacebookClient;
import shag.com.shag.Fragments.DialogFragments.PickCategoryDialogFragment;
import shag.com.shag.Models.Event;
import shag.com.shag.Models.User;
import shag.com.shag.Other.DividerItemDecorator;
import shag.com.shag.Other.ParseApplication;
import shag.com.shag.R;

/**
 * Created by gabesaruhashi on 7/11/17.
 */

public class FeedFragment extends Fragment implements PickCategoryDialogFragment.CategoryDialogListener {

    // list of tweets
    ArrayList<Event> events;
    // recycler view
    RecyclerView rvEvents;
    // the adapter wired to the new view
    FeedAdapter adapter;
    FacebookClient client;
    // TODO remove fake gabe
    User fakeGabriel;
    FloatingActionButton myFab;

    // inflation happens inside onCreateView
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // inflate the layout
        View v = inflater.inflate(R.layout.fragment_feed, container, false);

        // initialize the list of tweets
        events = new ArrayList<>();
        // construct the adater from the data source
        adapter = new FeedAdapter(events);
        // initialize recycler view
        rvEvents = (RecyclerView) v.findViewById(R.id.rvEvents);

        // attach the adapter to the RecyclerView
        rvEvents.setAdapter(adapter);
        // Set layout manager to position the items
        rvEvents.setLayoutManager(new LinearLayoutManager(getContext()));

        // add line divider decorator
        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecorator(rvEvents.getContext(), DividerItemDecorator.VERTICAL_LIST);
        rvEvents.addItemDecoration(itemDecoration);

        //TODO: find the best place to ask for this permission
        // oginManager.getInstance().logInWithReadPermissions(getActivity(), Arrays.asList("user_friends"));

        populateFeed();


        // setups FAB to work
        myFab = (FloatingActionButton) v.findViewById(R.id.myFAB);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // creates the Create dialog fragment
                showCreateDialog();
            }
        });

        // TODO remove fake Gabe
        fakeGabriel = new User();
        fakeGabriel.username = "gabesaruhashi";
        fakeGabriel.name="Gabriel S.";
        fakeGabriel.phoneNumber="6505757751";

        return v;
    }

    // TODO correct this method
    public void populateFeed() {
        //client = new FacebookClient(rvEvents.getContext());
        client = ParseApplication.getFacebookRestClient();
        client.getFriendsUsingApp(
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        Log.d("DEBUGRESPONSE", response.toString());
                        try {
                            JSONArray users = response.getJSONObject().getJSONArray("data");
                            for (int i = 0; i < users.length(); i++) {
                                User u = User.fromJson(users.getJSONObject(i));
                                //TODO: get userID, query data
                                ArrayList<Event> userPosts = u.events;
                                for (int j = 0; j < userPosts.size(); j++) {
                                    //TODO: check time of event and permissions
                                    //to determine if it should be shown to curr user
                                    events.add(userPosts.get(j));
                                }
                            }
                            //TODO: sort events somehow
                            Collections.sort(events, new Comparator<Event>() {
                                @Override
                                public int compare(Event event, Event t1) {
                                    return event.deadline.compareTo(t1.deadline);
                                }
                            });
                            adapter.notifyDataSetChanged();

                            //idea: make a TreeSet, add events to it so they compare by date
                            //then iterate through treeSet and add to events array then notify adapter
                            rvEvents.smoothScrollToPosition(0);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );

        /*
        Event fakeEvent = new Event();
        fakeEvent.eventId = new Long(123);
        fakeEvent.eventName = "Party at Zuck's";
        fakeEvent.location = "Facebook Seattle";
        fakeEvent.genre = "Partay";
        fakeEvent.deadline = new Date();
        fakeEvent.deadline.setTime(new Date().getTime() + 45*60000); //45 min from now
        fakeEvent.time = "4pm";
        fakeEvent.latLng = new LatLng(47.628883, -122.342606);
        fakeEvent.eventOwner=fakeGabriel;
        fakeEvent.participantsIds= new ArrayList<Long>();

        ArrayList<String> friends = new ArrayList<String>(3);
        friends.add("Gabriel");
        friends.add("Samra");
        friends.add("Hana");
        fakeEvent.friendsAtEvent = friends;

        events.add(0, fakeEvent);
        adapter.notifyItemInserted(events.size() - 1);
        rvEvents.smoothScrollToPosition(0);
        /*

        Event fakeEvent2 = new Event();
        fakeEvent2.eventId = new Long(198);
        fakeEvent2.eventName = "Party at Bill's";
        fakeEvent2.location = "Facebook Seattle";
        fakeEvent2.genre = "Professional Gathering";
        fakeEvent2.time = "4pm";
        fakeEvent2.latLng = new LatLng(47.621397, -122.338092);
        fakeEvent2.eventOwner=fakeGabriel;
        fakeEvent2.participantsIds= new ArrayList<Long>();

        ArrayList<String> friends2 = new ArrayList<String>(3);
        friends2.add("Gabriel");
        friends2.add("Samra");
        friends2.add("Hana");
        fakeEvent2.friendsAtEvent = friends2;

        events.add(0, fakeEvent2);
        adapter.notifyItemInserted(events.size() - 1);
        rvEvents.smoothScrollToPosition(0);

        MapFragment fragment = new MapFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("first event",fakeEvent);
        bundle.putParcelable("second event", fakeEvent2);
        fragment.setArguments(bundle);
        getActivity().getSupportFragmentManager().beginTransaction().commit();*/
        //TODO: replace with real populate: *.
       

    }

    // create category dialog fragment
    private void showCreateDialog() {
        FragmentManager fm = getFragmentManager();
        PickCategoryDialogFragment pickCategoryDialogFragment = new PickCategoryDialogFragment();
        // SETS the target fragment for use later when sending results
        pickCategoryDialogFragment.setTargetFragment(FeedFragment.this, 300);
        // show fragment
        pickCategoryDialogFragment.show(fm, "fragment_create_pick_category");
    }

    // This is called when the creation dialog is completed and the results have been passed
    @Override
    public void onFinishCategoryDialog(Event createdEvent) {
        //TODO remove fake gabriel
        //createdEvent.eventOwner = fakeGabriel;
        // createdEvent.eventName = "SHIT IS WORKING";

        events.add(createdEvent);
        //adapter.notifyItemInserted(events.size() - 1);
        //TODO: account for case where event has no deadline OR force user to enter it OR set default deadline
        Collections.sort(events, new Comparator<Event>() {
            @Override
            public int compare(Event event, Event t1) {
                return event.deadline.compareTo(t1.deadline);
            }
        });
        adapter.notifyDataSetChanged();
        rvEvents.smoothScrollToPosition(0);
    }
}
