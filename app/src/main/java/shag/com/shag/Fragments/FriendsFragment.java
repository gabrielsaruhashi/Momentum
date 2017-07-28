package shag.com.shag.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import shag.com.shag.Adapters.FriendsAdapter;
import shag.com.shag.Clients.FacebookClient;
import shag.com.shag.Models.User;
import shag.com.shag.Other.DividerItemDecorator;
import shag.com.shag.Other.ParseApplication;
import shag.com.shag.R;

/**
 * Created by gabesaruhashi on 7/13/17.
 */

public class FriendsFragment extends Fragment {

    // list of tweets
    ArrayList<User> friends;
    // recycler view
    RecyclerView rvFriends;
    // the adapter wired to the new view
    FriendsAdapter adapter;
    FacebookClient client;


    // inflation happens inside onCreateView
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // inflate the layout
        View v = inflater.inflate(R.layout.fragment_friends, container, false);

        // instantiate friends client
        client = ParseApplication.getFacebookRestClient();

        // initialize the list of tweets
        friends = new ArrayList<>();
        // construct the adater from the data source
        adapter = new FriendsAdapter(friends);
        // initialize recycler view
        rvFriends = (RecyclerView) v.findViewById(R.id.rvFriends);

        // attach the adapter to the RecyclerView
        rvFriends.setAdapter(adapter);
        // Set layout manager to position the items
        rvFriends.setLayoutManager(new LinearLayoutManager(getContext()));

        // add line divider decorator
        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecorator(rvFriends.getContext(), DividerItemDecorator.VERTICAL_LIST);
        rvFriends.addItemDecoration(itemDecoration);

        populateFriendsList();
        // return the view
        return v;
    }

    // get friends
    public void populateFriendsList() {
        client.getFriendsUsingApp(new GraphRequest.Callback() {
                @Override
                public void onCompleted(GraphResponse response) {
                    try {
                        JSONArray users = response.getJSONObject().getJSONArray("data");
                        for (int i = 0; i < users.length(); i++) {
                            User friend = User.fromJson(users.getJSONObject(i));
                            friends.add(friend);
                            adapter.notifyItemInserted(friends.size() - 1);
                            rvFriends.smoothScrollToPosition(0);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
        });
    }


}
