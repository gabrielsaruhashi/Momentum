package shag.com.shag.Activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

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

        // instantiate memories and set adapter
        memories = new ArrayList<Memory>();
        mAdapter = new MemoriesAdapter(memories);
        rvMemories.setAdapter(mAdapter);

        // associate the LayoutManager with the RecylcerView
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        rvMemories.setLayoutManager(linearLayoutManager);
        // populate memory
        populateMemories();
    }

    //TODO create method to populate memories
    private void populateMemories() {

    }
}
