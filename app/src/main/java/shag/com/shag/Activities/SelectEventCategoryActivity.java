package shag.com.shag.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import butterknife.ButterKnife;
import shag.com.shag.R;

public class SelectEventCategoryActivity extends AppCompatActivity {


    String pickedCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        setContentView(R.layout.activity_select_event_category);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        myToolbar.setTitleTextColor(0xFFFFFFFF);


    }


    public void onSelectCategory(View view) {
        switch (view.getId()) {
            case R.id.chillCard: pickedCategory = "Location";
                break;
            case R.id.foodCard: pickedCategory = "Food";
                break;
            case R.id.miscCard: pickedCategory = "Misc";
                break;
            case R.id.musicCard: pickedCategory = "Music";
                break;
            case R.id.partyCard: pickedCategory = "Party";
                break;
            case R.id.sportsCard: pickedCategory = "Sports";
                break;
        }
        if (pickedCategory != null) {
            Intent i;
            if (pickedCategory.equals("Location")) {
                i = new Intent(SelectEventCategoryActivity.this, SelectPublicEventTypeActivity.class);
                i.putExtra("Event Type", "Public");
            }
            else {
                i = new Intent(SelectEventCategoryActivity.this, SelectEventDeadlineActivity.class);
                i.putExtra("Event Type", "Private");
            }
            i.putExtra("Category", pickedCategory);
            startActivity(i);
        }
    }

}

