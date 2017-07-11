package shag.com.shag.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;

import shag.com.shag.Adapters.FeedAdapter;
import shag.com.shag.Clients.FacebookClient;
import shag.com.shag.Models.Event;
import shag.com.shag.Models.User;
import shag.com.shag.Other.DividerItemDecorator;
import shag.com.shag.R;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by gabesaruhashi on 7/11/17.
 */

public class FeedFragment extends Fragment {

    // list of tweets
    ArrayList<Event> events;
    // recycler view
    RecyclerView rvEvents;
    // the adapter wired to the new view
    FeedAdapter adapter;
    FacebookClient client;

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

        populateFeed();

        //TODO: find an appropriate place to ask for this permission
        LoginManager.getInstance().logInWithReadPermissions(getActivity(), Arrays.asList("user_friends"));
        client = new FacebookClient(rvEvents.getContext());
        client.getFriendsUsingApp(new GraphRequest.Callback() {
                                      public void onCompleted(GraphResponse response) {
                                          try {
                                              JSONArray users = response.getJSONObject().getJSONArray("data");
                                              User u = User.fromJson(users.getJSONObject(0));
                                              Toast.makeText(getApplicationContext(), "User: " + u.getName(), Toast.LENGTH_LONG).show();
                                          } catch (JSONException e) {
                                              e.printStackTrace();
                                          }
                                      }
                                  }
        );

        return v;
    }

    public void populateFeed() {
        User gabriel = new User();
        gabriel.username = "gabesaruhashi";
        gabriel.name="Gabriel S.";
        gabriel.phoneNumber="6505757751";
        gabriel.currentInterestsIds = new ArrayList<Long>(0);

        Event fakeEvent = new Event();
        fakeEvent.eventId = new Long(123);
        fakeEvent.eventName = "Party at Zuck's";
        fakeEvent.location = "Facebook Seattle";
        fakeEvent.genre = "Partay";
        fakeEvent.time = "4pm";
        fakeEvent.eventOwner=gabriel;
        fakeEvent.participantsIds= new ArrayList<Long>(0);
        ArrayList<String> friends = new ArrayList<String>(3);
        friends.add("Gabriel");
        friends.add("Samra");
        friends.add("Hana");
        fakeEvent.friendsAtEvent = friends;

        events.add(0, fakeEvent);
        adapter.notifyItemInserted(events.size() - 1);
        rvEvents.smoothScrollToPosition(0);
    }

}
