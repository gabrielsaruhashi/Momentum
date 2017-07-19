package shag.com.shag.Activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;

import shag.com.shag.Adapters.MemoriesAdapter;
import shag.com.shag.Models.Memory;
import shag.com.shag.R;

public class MemoriesActivity extends AppCompatActivity {
    Context context;

    RecyclerView rvMemories;
    ArrayList<Memory> memories;
    MemoriesAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memories);
        context = this;

        rvMemories = (RecyclerView) findViewById(R.id.rvMemories);
        memories = new ArrayList<Memory>();
        // set adapter
        mAdapter = new MemoriesAdapter(memories);
        rvMemories.setAdapter(mAdapter);

        // associate the LayoutManager with the RecylcerView
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        rvMemories.setLayoutManager(linearLayoutManager);
        // populate memory
        populateMemories();
    }

    private void populateMemories() {
        ParseUser user = ParseUser.getCurrentUser();
        ArrayList<String> memories_ids = (ArrayList) user.getList("memories_ids");
        for (String memory_id : memories_ids) {
            ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Memory");
            query.getInBackground(memory_id, new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject object, ParseException e) {
                    if (e == null) {
                        ArrayList<String> participantsIds = (ArrayList) object.getList("participantsIds");
                        if (participantsIds != null && participantsIds.size() > 1) {
                            // TODO: display all pictures from memory in some order
                        }
                    } else {
                        e.printStackTrace();
                    }
                }
            });

        }
    }
}
