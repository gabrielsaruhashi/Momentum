package shag.com.shag.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
    RecyclerView rvMemories;

    private final static int REQUEST_OPEN_MEMORIES = 10;
    private OnMemoryBookPositionChangedListener listener;

    // Store the listener (activity) that will have events fired once the fragment is attached
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnMemoryBookPositionChangedListener) {
            listener = (OnMemoryBookPositionChangedListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement MyListFragment.OnItemSelectedListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_memory_book_list, container, false);

        // get our list view
        rvMemories = (RecyclerView) v.findViewById(R.id.rvMemoriesList);
        context = getContext();

        // instantiate memories and set adapter
        memories = new ArrayList<Memory>();

        // set adapter
        mAdapter = new MemoriesAdapter(context, memories);

        // set elements to adapter
        rvMemories.setAdapter(mAdapter);
        // Set layout manager to position the items
        rvMemories.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));

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
                                    rvMemories.smoothScrollToPosition(0);

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

    // method to dynamically change cover picture on back pressed
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

    // interface with main activity to dynamically change header pictures
    public interface OnMemoryBookPositionChangedListener {
        // This can be any number of events to be sent to the activity
        public void changeMaterialVPListener(String imageUrl);
    }

}
