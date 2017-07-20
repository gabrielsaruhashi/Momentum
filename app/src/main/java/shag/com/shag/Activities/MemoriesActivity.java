package shag.com.shag.Activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import shag.com.shag.Adapters.MemoriesAdapter;
import shag.com.shag.Models.Memory;
import shag.com.shag.R;

public class MemoriesActivity extends AppCompatActivity {
    Context context;

    RecyclerView rvMemories;
    ArrayList<Memory> memories;
    MemoriesAdapter mAdapter;
    ParseUser currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memories);
        context = this;
        // get reference to xml recyclerview
        rvMemories = (RecyclerView) findViewById(R.id.rvMemories);

        // instantiate memories and set adapter
        memories = new ArrayList<Memory>();
        // set adapter
        mAdapter = new MemoriesAdapter(memories);
        rvMemories.setAdapter(mAdapter);

        // instantiate current user
        currentUser = ParseUser.getCurrentUser();

        // associate the LayoutManager with the RecylcerView
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        rvMemories.setLayoutManager(linearLayoutManager);
        // populate memory
        populateMemories();
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
