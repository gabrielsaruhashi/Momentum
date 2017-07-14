package shag.com.shag.Activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import shag.com.shag.Adapters.CategoriesListAdapter;
import shag.com.shag.Fragments.DialogFragments.PickCategoryDialogFragment;
import shag.com.shag.R;

public class CreateEventActivity extends AppCompatActivity {
    ExpandableListView listView;
    private ExpandableListAdapter listAdapter;
    private List<String> listDataHeader;
    private HashMap<String, List<String>> listHash;
    private ImageView ivBanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        setContentView(R.layout.activity_create_event);
        // populates the category hash map
        initData();

        // get views
        listView = (ExpandableListView) findViewById(R.id.lvCategories);
        ivBanner = (ImageView) findViewById(R.id.ivBanner);

        // sets up the adapter
        listAdapter = new CategoriesListAdapter(this,listDataHeader, listHash);
        listView.setAdapter(listAdapter);

        // populate banner view
        Glide.with(CreateEventActivity.this)
                .load("http://via.placeholder.com/300.png")
                .into(ivBanner);


        // create listener for item child click
        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                /* You must make use of the View v, find the view by id and extract the text as below*/
                TextView tvCategoryItem= (TextView) v.findViewById(R.id.lblListItem);
                String data= tvCategoryItem.getText().toString();
                Toast.makeText(CreateEventActivity.this, "Picked event: " + data, Toast.LENGTH_LONG).show();
                return true;  // i missed this
            }
        });

        // Begin the transaction
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        // Replace the contents of the container with the new fragment
        ft.replace(R.id.selectCategoryContainer, new PickCategoryDialogFragment());
        // or ft.add(R.id.your_placeholder, new FooFragment());

        // Complete the changes added above
        ft.commit();
    }

    public void initData() {
        listDataHeader = new ArrayList<>();
        listHash = new HashMap<>();

        listDataHeader.add("Popular");
        listDataHeader.add("Sports");
        listDataHeader.add("Party");
        listDataHeader.add("Chilling");

        List<String> popular = new ArrayList<>();
        popular.add("Food");
        popular.add("Do Whatever");
        popular.add("Party");
        popular.add("Study");

        List<String> sports = new ArrayList<>();
        sports.add("Basketball");
        sports.add("Run");
        sports.add("Gym");
        sports.add("Soccer");

        List<String> party = new ArrayList<>();
        party.add("Beer Pong");
        party.add("Happy Hour");
        party.add("Dance");
        party.add("Bars");

        List<String> chilling = new ArrayList<>();
        chilling.add("Netflix");
        chilling.add("Game");
        chilling.add("Shop");
        chilling.add("Movie");

        listHash.put(listDataHeader.get(0), popular);
        listHash.put(listDataHeader.get(1), sports);
        listHash.put(listDataHeader.get(2), party);
        listHash.put(listDataHeader.get(3), chilling);

    }
}
