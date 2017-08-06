package shag.com.shag.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.github.florent37.materialviewpager.header.MaterialViewPagerHeaderDecorator;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseLiveQueryClient;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SubscriptionHandling;

import java.util.ArrayList;
import java.util.List;

import shag.com.shag.Adapters.MemoriesAdapter;
import shag.com.shag.Models.Memory;
import shag.com.shag.Other.ParseApplication;
import shag.com.shag.R;

/**
 * Created by gabesaruhashi on 8/2/17.
 */

public class MemoryListFragment extends Fragment {
    Context context;

    ArrayList<Memory> memories;
    MemoriesAdapter mAdapter;
    ParseUser currentUser;
    ListView lvMemories;

    private final static int REQUEST_OPEN_MEMORIES = 10;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_memory_book_list, container, false);

        // get our list view
        lvMemories = (ListView) v.findViewById(R.id.lvMainList);
        context = getContext();

        // instantiate memories and set adapter
        memories = new ArrayList<Memory>();

        // set adapter
        mAdapter = new MemoriesAdapter(context, memories);

        // set elements to adapter
        lvMemories.setAdapter(mAdapter);

        //
        lvMemories.addItemDecoration(new MaterialViewPagerHeaderDecorator());

        // instantiate current user
        currentUser = ParseApplication.getCurrentUser();

        // populate memory
        populateMemories();

        // setup live queries
        setupLiveQuery();

        return v;
    }

    private void setupLiveQuery() {
        // listen for create Memory events
        ParseLiveQueryClient parseLiveQueryClient = ParseLiveQueryClient.Factory.getClient();

        ParseQuery<Memory> parseQuery = ParseQuery.getQuery(Memory.class);
        // create the query condition
        //parseQuery.whereContainedIn("participants_ids", Arrays.asList(currentUser.getObjectId()));



        // Connect to Parse server
        SubscriptionHandling<Memory> subscriptionHandling = parseLiveQueryClient.subscribe(parseQuery);

        // Listen for CREATE events
        subscriptionHandling.handleEvent(SubscriptionHandling.Event.CREATE, new
                SubscriptionHandling.HandleEventCallback<Memory>() {
                    @Override
                    public void onEvent(ParseQuery<Memory> query, final Memory object) {
                        if (object.getParticipantsIds().contains(currentUser.getObjectId())) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    memories.add(0, object);
                                    mAdapter.notifyDataSetChanged();
                                    lvMemories.smoothScrollToPosition(0);

                                }
                            });
                        }
                    }


                });
    }

    private void populateMemories() {
        ParseQuery<Memory> query = ParseQuery.getQuery("Memory");
        // auxiliary list
        List list = new ArrayList();
        list.add(currentUser.getObjectId());

        // create the query condition
        query.whereContainedIn("participants_ids", list);

        query.findInBackground(new FindCallback<Memory>() {
            @Override
            public void done(List<Memory> objects, ParseException e) {
                if (e == null) {
                    memories.clear();
                    memories.addAll(0, objects);
                    mAdapter.notifyDataSetChanged();


                } else {
                    e.getMessage();
                }
            };
    /*
    ArrayList<ParseObject> memoriesList = (ArrayList) currentUser.getList("Memories_list");
    //for (ParseObject memory : memoriesList) {
    for (int i = 0; i < memoriesList.size(); i++) {
        try {
            ParseObject memoryObject = memoriesList.get(i).fetchIfNeeded();
            Memory memory = Memory.fromParseObject(memoryObject);
            memories.add(0, memory);
            mAdapter.notifyItemInserted(memories.size() - 1);
            rvMemories.smoothScrollToPosition(0);
        } catch (ParseException e) {
            e.getMessage();
        } */

        /*
        memoriesList.get(i).getParseObject("Memory").fetchIfNeededInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                Memory memory = Memory.fromParseObject(object);
                memories.add(0, memory);
                mAdapter.notifyItemInserted(memories.size() - 1);
                rvMemories.smoothScrollToPosition(0);
            }
        });*/
        });


    }


    public void changeCoverPictureUrl(Intent data) {
        if (data != null) {
            int position = data.getIntExtra("position", 0);
            String newCoverPictureUrl = data.getStringExtra("pictureCoverUrl");

            Memory memory = memories.get(position);
            memory.setCoverPictureUrl(newCoverPictureUrl);
            mAdapter.notifyDataSetChanged();

            Toast.makeText(context, "Your album was updated!", Toast.LENGTH_SHORT).show();
        }
    }
}
