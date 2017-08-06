package shag.com.shag.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.florent37.materialviewpager.header.MaterialViewPagerHeaderDecorator;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseLiveQueryClient;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SubscriptionHandling;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import shag.com.shag.Activities.SelectEventCategoryActivity;
import shag.com.shag.Adapters.FeedAdapter;
import shag.com.shag.Clients.FacebookClient;
import shag.com.shag.Fragments.DialogFragments.PickCategoryDialogFragment;
import shag.com.shag.Models.Event;
import shag.com.shag.Other.ParseApplication;
import shag.com.shag.Other.RelevanceComparator;
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
    private ArrayList<Long> facebookFriendsIds;
    private SwipeRefreshLayout swipeContainer;
    private ParseUser currentUser;

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

        // setup rvEvents
        rvEvents.addItemDecoration(new MaterialViewPagerHeaderDecorator());
        // attach the adapter to the RecyclerView
        rvEvents.setAdapter(adapter);
        // Set layout manager to position the items
        rvEvents.setLayoutManager(new LinearLayoutManager(getContext()));

        // add line divider decorator
        //RecyclerView.ItemDecoration itemDecoration = new
                //DividerItemDecorator(rvEvents.getContext(), DividerItemDecorator.VERTICAL_LIST);
        //rvEvents.addItemDecoration(itemDecoration);



        //swipe refresh
        swipeContainer = (SwipeRefreshLayout) v.findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                fetchTimelineAsync(0);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(R.color.colorAccent,
                R.color.colorPrimary,
                R.color.colorPrimaryDark);



        // gets friends and call populatefeed() the first time
        facebookFriendsIds = ParseApplication.getFacebookFriends();

        // populate feed
        populateFeed();

        // setups FAB to work
        myFab = (FloatingActionButton) v.findViewById(R.id.myFAB);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // creates the Create dialog fragment
                Intent i = new Intent(getContext(), SelectEventCategoryActivity.class);
                getContext().startActivity(i);
            }
        });

        // instantiate current user
        currentUser = ParseApplication.getCurrentUser();

        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void fetchTimelineAsync(int page) {
        // Send the network request to fetch the updated data
        // `client` here is an instance of Android Async HTTP
        // getHomeTimeline is an example endpoint.

        adapter.clear();
        populateFeed();
        swipeContainer.setRefreshing(false);
    }

    /*
    public void getFacebookFriends() {
        client = ParseApplication.getFacebookRestClient();
        client.getFriendsUsingApp(
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        // gets friends ids

                        try {
                            JSONObject obj = response.getJSONObject();
                            //obj should never be null but occasionally is-- need to log in again
                            if (obj == null) {
                                Intent intent = new Intent(getContext(), LoginActivity.class); //sometimes this doesn't work
                                getContext().startActivity(intent);
                            } else {
                                JSONArray friends = obj.getJSONArray("data");
                                for (int i = 0; i < friends.length(); i++) {
                                    User friend = User.fromJson(friends.getJSONObject(i));
                                    facebookFriendsIds.add(friend.fbUserID);
                                }
                                // populates initial feed
                                populateFeed();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );
    } */

    //TODO improve populate feed method
    public void populateFeed() {
        ParseQuery<Event> query = new ParseQuery("Event");
        query.whereContainedIn("event_owner_fb_id", facebookFriendsIds);
        //query.include("User_event_owner");
        query.include("last_message_sent");
        query.findInBackground(new FindCallback<Event>() {
            @Override
            public void done(List<Event> eventsList, ParseException e) {
                if (e == null) {
                    //TODO see if it is possible to improve this logic
                    // calculate the relevance of each event before adding to arraylist
                    for (Event event : eventsList) {

                        //if (event.getDeadline().getTime() > (new Date()).getTime()) {
                            events.add(event);
                        //}
                        event.setRelevance(calculateEventRelevance(event, currentUser));
                        Log.i("FEED_RELEVANCE", event.getDescription() + " has relevance: " + event.getRelevance());
                    }
                    // sort events based on relevance
                    Collections.sort(events, new RelevanceComparator());
                    // put in descending oreder
                    Collections.reverse(events);

                    adapter.notifyDataSetChanged();
                    startLiveQueries();

                } else {
                    Log.d("feedfragment", "Error: " + e.getMessage());
                }
            }
        });


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

    // returns the relevance of a specific event in the timeline
    public static Double calculateEventRelevance(Event event, ParseUser user) {
        // get relevant information for recommendation algorithm
        HashMap<String, Integer> recentFriendsMap = (HashMap) user.getMap("recent_friends_map");
        HashMap<String, List<Object>> categoriesTracker = (HashMap) user.getMap("categories_tracker");

        // get the chill coefficient based on the user's profile
        Double chillCoefficient = getChillCoefficient(event.getCategory(), categoriesTracker);
        Double closenessCoefficient = getClosenessCoefficient(event.getEventOwnerId(), recentFriendsMap);
        Double relevanceCoefficient = (chillCoefficient + closenessCoefficient) / 2.0;
        return relevanceCoefficient;
    }

    public static Double getChillCoefficient(String input, HashMap<String, List<Object>> hm) {

        // get the raw counter for the specific input key, if it exists
        int rawInterest = (hm.get(input).get(0) != null) ? (int) hm.get(input).get(0) : 0;


        double totalCounter = 0;

        // iterate through the hashmap to add values
        for (List<Object> ob : hm.values()) {
            totalCounter += (int) ob.get(0);
        }

        // return the coefficient Raw Interest / Total Interest if total interest > 0
        return (totalCounter > 0) ? rawInterest / totalCounter : Double.valueOf(0);
    }

    public static Double getClosenessCoefficient(String input, HashMap hm) {
        // get the raw counter for the specific input key, if it exists
        int rawInterest = (hm.get(input) != null) ? (int) hm.get(input) : 0;
        double totalCounter = 0;

        // iterate through the hashmap to add values
        for (Object ob : hm.values()) {
            totalCounter += (int) ob;
        }

        // return the coefficient Raw Interest / Total Interest if total interest > 0
        return (totalCounter > 0) ? rawInterest / totalCounter : Double.valueOf(0);
    }

    public void startLiveQueries() {
        final ArrayList<String> eventIds = new ArrayList<>();
        for (Event event : events) {
            eventIds.add(event.getEventId());
        }

        ParseLiveQueryClient parseLiveQueryClient = ParseLiveQueryClient.Factory.getClient();
        ParseQuery<Event> parseQuery = ParseQuery.getQuery(Event.class);
        SubscriptionHandling<Event> subscriptionHandling = parseLiveQueryClient.subscribe(parseQuery);
        // Listen for CREATE events
        subscriptionHandling.handleEvent(SubscriptionHandling.Event.UPDATE, new
                SubscriptionHandling.HandleEventCallback<Event>() {
                    @Override
                    public void onEvent(ParseQuery<Event> query, Event object) {
                        String newEventId = object.getEventId();
                        if (eventIds.contains(newEventId)) {
                            ParseQuery<Event> eventQuery = ParseQuery.getQuery(Event.class);
                            //eventQuery.include("User_event_owner");
                            eventQuery.include("last_message_sent");
                            try {
                                Event event = eventQuery.get(newEventId);
                                int index = eventIds.indexOf(newEventId);
                                events.set(index, event);
                                itemChanged(index);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    //execute adapter refresh on ui thread
    public void itemChanged(final int index) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyItemChanged(index);
            }
        });
    }


}
