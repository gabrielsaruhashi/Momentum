package shag.com.shag.Fragments;

import android.content.Intent;
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
import android.widget.Toast;

import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import shag.com.shag.Activities.CreateEventActivity;
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
    FloatingActionButton myFab;
    private static ArrayList<Long> facebookFriendsIds;

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

        // initialize facebook friendsIds
        facebookFriendsIds = new ArrayList<Long>();

        // gets friends and call populatefeed() the first time
        getFacebookFriends();

        // setups FAB to work
        myFab = (FloatingActionButton) v.findViewById(R.id.myFAB);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // creates the Create dialog fragment
                Intent i = new Intent(getContext(), CreateEventActivity.class);
                getContext().startActivity(i);
            }
        });

        return v;
    }

    public void getFacebookFriends() {
        client = ParseApplication.getFacebookRestClient();
        client.getFriendsUsingApp(
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        // gets friends ids
                        try {
                            JSONArray friends = response.getJSONObject().getJSONArray("data");
                            for (int i = 0; i < friends.length(); i++) {
                                User friend = User.fromJson(friends.getJSONObject(i));
                                facebookFriendsIds.add(friend.fbUserID);
                            }
                            // populates initial feed
                            populateFeed();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );
    }
    //TODO improve populate feed method
    public void populateFeed() {
        for (int i = 0; i < facebookFriendsIds.size(); i++) {
            ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Event");
            query.whereEqualTo("event_owner_fb_id", facebookFriendsIds.get(i));
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> itemList, ParseException e) {
                    if (e == null) {
                        for (ParseObject item : itemList) {
                            //Convert each item found to an event
                            Event event = Event.fromParseObject(item);
                            //add event to list to be displayed
                            events.add(event);
                        }

                        //TODO: move this somewhere else, it is currently over-sorting
                        //Sort the events shown to user in order of soonest deadline
                        Collections.sort(events, new Comparator<Event>() {
                            @Override
                            public int compare(Event event, Event t1) {
                                return event.deadline.compareTo(t1.deadline);
                            }
                        });
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.d("feedfragment", "Error: " + e.getMessage());
                    }
                }
            });
        }
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
    //Body commented out because we need the method declaration
    @Override
    public void onFinishCategoryDialog(Event createdEvent) {
        Toast.makeText(getContext(), "Event created", Toast.LENGTH_SHORT).show();
        /*
        events.add(createdEvent);
        //adapter.notifyItemInserted(events.size() - 1);
        Collections.sort(events, new Comparator<Event>() {
            @Override
            public int compare(Event event, Event t1) {
                return event.deadline.compareTo(t1.deadline);
            }
        });
        adapter.notifyDataSetChanged();
        rvEvents.smoothScrollToPosition(0);*/
    }

}
