package shag.com.shag.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
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
        myToolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));


    }


    public void onSelectCategory(View view) {
        switch (view.getId()) {
            case R.id.exploreCard: pickedCategory = "Explore";
                break;
            case R.id.foodCard: pickedCategory = "Food";
                break;
            case R.id.miscCard: pickedCategory = "Misc";
                break;
            case R.id.chillCard: pickedCategory = "Chill";
                break;
            case R.id.partyCard: pickedCategory = "Party";
                break;
            case R.id.sportsCard: pickedCategory = "Sports";
                break;
        }
        if (pickedCategory != null) {
            Intent i;
            if (pickedCategory.equals("Explore")) {
                i = new Intent(SelectEventCategoryActivity.this, SelectPublicEventTypeActivity.class);
                i.putExtra("Event Type", "Public");
            }
            else {
                i = new Intent(SelectEventCategoryActivity.this, SelectEventDeadlineActivity.class);
                i.putExtra("Event Type", "Private");
//                i.putExtra(SelectEventDeadlineActivity.EXTRA_CONTACT, contact);
//                ActivityOptionsCompat options = ActivityOptionsCompat.
//                        makeSceneTransitionAnimation(this, (View)ivProfile, "profile");
//                startActivityy(intent, options.toBundle());
            }
            i.putExtra("Category", pickedCategory);
            startActivity(i);
        }
    }

}

