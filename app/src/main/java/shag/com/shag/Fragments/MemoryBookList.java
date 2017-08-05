package shag.com.shag.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import shag.com.shag.Adapters.MemoriesAdapter;
import shag.com.shag.Models.Memory;
import shag.com.shag.Other.ParseApplication;
import shag.com.shag.R;

/**
 * Created by gabesaruhashi on 7/14/17.
 */

public class MemoryBookList extends Fragment {

    Context context;
    ArrayList<Memory> memories;
    MemoriesAdapter mAdapter;
    ListView lvMemories;

    ParseUser currentUser;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // inflate the layout
        View v = inflater.inflate(R.layout.fragment_memory_book_list, container, false);
        // get our list view
        lvMemories = (ListView) v.findViewById(R.id.lvMainList);

        // instantiate memories and set adapter
        memories = new ArrayList<Memory>();

        /*
        // add custom btn handler to first list item
        memories.get(0).setRequestBtnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "CUSTOM HANDLER FOR FIRST BUTTON", Toast.LENGTH_SHORT).show();
            }
        }); */

        // set adapter
        mAdapter = new MemoriesAdapter(context, memories);

        // set elements to adapter
        lvMemories.setAdapter(mAdapter);

        // instantiate current user
        currentUser = ParseApplication.getCurrentUser();
        populateMemories();

        return v;
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
                    memories.addAll(objects);
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


}