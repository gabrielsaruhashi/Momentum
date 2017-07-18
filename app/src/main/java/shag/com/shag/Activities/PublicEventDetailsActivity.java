package shag.com.shag.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;

import shag.com.shag.R;

public class PublicEventDetailsActivity extends AppCompatActivity {
    TextView name;
    TextView location;
    TextView description;
    TextView category;
    Button makeEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_event_details);

        name = (TextView) findViewById(R.id.tvEventName);
        location = (TextView) findViewById(R.id.tvLocation);
        description = (TextView) findViewById(R.id.tvDescription);
        category = (TextView) findViewById(R.id.tvCategory);

        makeEvent = (Button) findViewById(R.id.btnCreateEvent);

        Intent intent = getIntent();
        final HashMap<String, String> dataMap = (HashMap<String, String>)intent.getSerializableExtra("Data");


        name.setText(dataMap.get("Name"));
        location.setText(dataMap.get("Location"));
        description.setText(dataMap.get("Description"));
        category.setText(dataMap.get("Category"));

        makeEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PublicEventDetailsActivity.this, SelectEventDetailsActivity.class);
                i.putExtra("Location", dataMap.get("Location"));
                i.putExtra("Category", dataMap.get("Category"));
                startActivity(i);
            }
        });

    }
}
