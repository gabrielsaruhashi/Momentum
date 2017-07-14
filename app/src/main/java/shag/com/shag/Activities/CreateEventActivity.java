package shag.com.shag.Activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
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
