package shag.com.shag.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;

import java.util.Date;

import shag.com.shag.R;

public class SelectEventDetailsActivity extends AppCompatActivity {
    public final static int MILLISECONDS_IN_MINUTE = 60000;
    Button nextButton;
    Date newDate = new Date(new Date().getTime()+MILLISECONDS_IN_MINUTE*60);
    EditText etDescription;
    String category;
    PlaceAutocompleteFragment autocompleteFragment;
    EditText etLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_event_details);

        category = getIntent().getStringExtra("Category");

        etDescription = (EditText) findViewById(R.id.tvDescriptionInput);
        etLocation = (EditText) findViewById(R.id.etLocation);

        TextView tv30 = (TextView) findViewById(R.id.tv30);
        setListenerForTime(tv30, 30);

        TextView tv1h = (TextView) findViewById(R.id.tv1h);
        setListenerForTime(tv1h, 60);

        TextView tv2h = (TextView) findViewById(R.id.tv2h);
        setListenerForTime(tv2h, 120);

        TextView tv3h = (TextView) findViewById(R.id.tv3h);
        setListenerForTime(tv3h, 180);

        TextView tv6h = (TextView) findViewById(R.id.tv6h);
        setListenerForTime(tv6h, 360);

        TextView tv12h = (TextView) findViewById(R.id.tv12h);
        setListenerForTime(tv12h, 720);

//        autocompleteFragment = (PlaceAutocompleteFragment)
//                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        if (getIntent().getStringExtra("Location") != null) {
            etLocation.setText(getIntent().getStringExtra("Location"));
        }
        nextButton = (Button) findViewById(R.id.btNext);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SelectEventDetailsActivity.this,SelectEventFriendsActivity.class);
                i.putExtra("Category", category);
                i.putExtra("Deadline", newDate.getTime());
                i.putExtra("Description", etDescription.getText().toString());
                startActivity(i);
            }
        });

//        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
//            @Override
//            public void onPlaceSelected(Place place) {
//                // TODO: Get info about the selected place.
//                Log.i("SelectEventDetails", "Place: " + place.getName());
//            }
//
//            @Override
//            public void onError(Status status) {
//                // TODO: Handle the error.
//                Log.i("SelectEventDetails", "An error occurred: " + status);
//            }
//        });

    }


    public void setListenerForTime(TextView tv, final int minToDeadline) {
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newDate.setTime(newDate.getTime() + minToDeadline*MILLISECONDS_IN_MINUTE);
                Toast.makeText(SelectEventDetailsActivity.this, "Date: " + newDate.toString(), Toast.LENGTH_LONG).show();
                //newEvent.deadline = newDate;

            }
        });

    }


}
