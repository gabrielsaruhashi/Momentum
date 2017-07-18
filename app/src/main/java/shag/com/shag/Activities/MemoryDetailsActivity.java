package shag.com.shag.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.GridView;

import shag.com.shag.Adapters.ImageAdapter;
import shag.com.shag.Models.Memory;
import shag.com.shag.R;

public class MemoryDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory_details);

        // unwrap memory
        Memory memory = getIntent().getParcelableExtra(Memory.class.getSimpleName());

        GridView gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(new ImageAdapter(this, memory.getPicturesUrls()));

    }
}
