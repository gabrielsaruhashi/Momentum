package shag.com.shag.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import shag.com.shag.Adapters.CategoriesListAdapter;
import shag.com.shag.R;

public class CreateEventActivity extends AppCompatActivity {
    ExpandableListView listView;
    private ExpandableListAdapter listAdapter;
    private List<String> listDataHeader;
    private HashMap<String, List<String>> listHash;
    @BindView(R.id.ivBanner) ImageView ivBanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        setContentView(R.layout.activity_create_event);

        initData();
        listView = (ExpandableListView) findViewById(R.id.lvCategories);

        listAdapter = new CategoriesListAdapter(this,listDataHeader, listHash);
        listView.setAdapter(listAdapter);

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
