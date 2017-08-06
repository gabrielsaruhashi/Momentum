package shag.com.shag.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.ButterKnife;
import shag.com.shag.R;

public class SelectEventCategoryActivity extends AppCompatActivity {


    String pickedCategory;
    ImageView sportIcon;
    TextView sportsName;

    ImageView genericIcon;
    TextView genericName;

    int iconResource;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        setContentView(R.layout.activity_select_event_category);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        myToolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));

        sportIcon = (ImageView)findViewById (R.id.sportsIcon);
        sportsName = (TextView) findViewById (R.id.sportsName);




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
        setImageAndText(pickedCategory);
        if (pickedCategory != null) {
            Intent i;
            if (pickedCategory.equals("Explore")) {
                i = new Intent(SelectEventCategoryActivity.this, SelectPublicEventTypeActivity.class);
                i.putExtra("Event Type", "Public");
                i.putExtra("Category", pickedCategory);
                startActivity(i);

            }
            else {
                i = new Intent(SelectEventCategoryActivity.this, SelectEventDeadlineActivity.class);
                i.putExtra("Event Type", "Private");
                Pair<View, String> p1 = Pair.create((View)genericIcon, "iconTransition");
                Pair<View, String> p2 = Pair.create((View)genericName, "nameTransition");
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(this, p1, p2);
                i.putExtra("Category", pickedCategory);
                i.putExtra("Icon", iconResource);
                startActivity(i, options.toBundle());

            }

        }
    }

    private void setImageAndText(String pickedCategory) {
        switch (pickedCategory) {
            case "Food":
                genericIcon =(ImageView) findViewById (R.id.foodIcon);
                genericName =(TextView) findViewById (R.id.foodName);
                iconResource = R.drawable.ic_food1;
                break;
            case "Party":
                genericIcon =(ImageView) findViewById (R.id.partyIcon);
                genericName =(TextView) findViewById (R.id.partyName);
                iconResource = R.drawable.ic_party1;

                break;
            case "Explore":
                genericIcon =(ImageView) findViewById (R.id.exploreIcon);
                genericName =(TextView) findViewById (R.id.exploreName);
                iconResource = R.drawable.ic_noun_956258_cc;

                break;
            case "Sports":
                genericIcon =(ImageView) findViewById (R.id.sportsIcon);
                genericName =(TextView) findViewById (R.id.sportsName);
                iconResource = R.drawable.ic_sports;

                break;
            case "Chill":
                genericIcon =(ImageView) findViewById (R.id.chillIcon);
                genericName =(TextView) findViewById (R.id.chillName);
                iconResource = R.drawable.ic_iying_down;

                break;
            case "Misc":
                genericIcon =(ImageView) findViewById (R.id.miscIcon);
                genericName =(TextView) findViewById (R.id.miscName);
                iconResource = R.drawable.ic_misc1;

                break;

        }
    }

}

