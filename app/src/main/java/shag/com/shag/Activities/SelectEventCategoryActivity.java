package shag.com.shag.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import butterknife.ButterKnife;
import shag.com.shag.R;

public class SelectEventCategoryActivity extends AppCompatActivity {
//    ExpandableListView listView;
//    private ExpandableListAdapter listAdapter;
//    private List<String> listDataHeader;
//    private HashMap<String, List<String>> listHash;
    Button btnNext;
    String pickedCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        setContentView(R.layout.activity_select_event_category);
        btnNext = (Button) findViewById(R.id.btNext);
        // populates the category hash map
        //initData();

        // get views
//        listView = (ExpandableListView) findViewById(R.id.lvCategories);
//
//        // sets up the adapter
//        listAdapter = new CategoriesListAdapter(this,listDataHeader, listHash);
//        listView.setAdapter(listAdapter);



        // create listener for item child click
//        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
//
//            @Override
//            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
//                /* You must make use of the View v, find the view by id and extract the text as below*/
//                TextView tvCategoryItem= (TextView) v.findViewById(R.id.lblListItem);
//                pickedCategory= tvCategoryItem.getText().toString();
//                Toast.makeText(SelectEventCategoryActivity.this, "Picked event: " + pickedCategory, Toast.LENGTH_LONG).show();
//                return true;  // i missed this
//            }
//        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pickedCategory != null) {
                    Intent i = new Intent(SelectEventCategoryActivity.this, SelectEventDetailsActivity.class);
                    i.putExtra("Category", pickedCategory);
                    i.putExtra("Event Type", "Private");

//                    if (getIntent().getStringExtra("Location") != null) {
//                        i.putExtra("Location", getIntent().getStringExtra("Location"));
//                    }
                    startActivity(i);
                }
            }
        });


    }

//    public void initData() {
//        listDataHeader = new ArrayList<>();
//        listHash = new HashMap<>();
//
//        listDataHeader.add("Popular");
//        listDataHeader.add("Sports");
//        listDataHeader.add("Party");
//        listDataHeader.add("Chilling");
//
//        List<String> popular = new ArrayList<>();
//        popular.add("Food");
//        popular.add("Do Whatever");
//        popular.add("Party");
//        popular.add("Study");
//
//        List<String> sports = new ArrayList<>();
//        sports.add("Basketball");
//        sports.add("Run");
//        sports.add("Gym");
//        sports.add("Soccer");
//
//        List<String> party = new ArrayList<>();
//        party.add("Beer Pong");
//        party.add("Happy Hour");
//        party.add("Dance");
//        party.add("Bars");
//
//        List<String> chilling = new ArrayList<>();
//        chilling.add("Netflix");
//        chilling.add("Game");
//        chilling.add("Shop");
//        chilling.add("Movie");
//
//        listHash.put(listDataHeader.get(0), popular);
//        listHash.put(listDataHeader.get(1), sports);
//        listHash.put(listDataHeader.get(2), party);
//        listHash.put(listDataHeader.get(3), chilling);
//
//    }
    public void onSelectCategory(View view) {
        switch (view.getId()) {
            case R.id.chillCard: pickedCategory = "Chill";
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
    }

}

