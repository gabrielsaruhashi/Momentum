package shag.com.shag.Fragments.DialogFragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import shag.com.shag.Adapters.CategoriesListAdapter;
import shag.com.shag.Models.Event;
import shag.com.shag.R;

/**
 * Created by gabesaruhashi on 7/11/17.
 */

public class PickCategoryDialogFragment extends DialogFragment implements CreateDetailsDialogFragment.CreateDetailsDialogListener {

    ExpandableListView listView;
    private ExpandableListAdapter listAdapter;
    private List<String> listDataHeader;
    private HashMap<String, List<String>> listHash;
    private ImageView ivBanner;
    private Event newEvent;

    public PickCategoryDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    /*
    public static PickCategoryDialogFragment newInstance(String title) {

        EditNameDialogFragment frag = new EditNameDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_pick_category, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // populates the category hash map
        initData();

        // get views
        listView = (ExpandableListView) view.findViewById(R.id.lvCategories);
        ivBanner = (ImageView) view.findViewById(R.id.ivBanner);

        // sets up the adapter
        listAdapter = new CategoriesListAdapter(listView.getContext(),listDataHeader, listHash);
        listView.setAdapter(listAdapter);

        // populate banner view
        Glide.with(listView.getContext())
                .load("http://via.placeholder.com/300.png")
                .into(ivBanner);


        // create listener for item child click
        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                /* You must make use of the View v, find the view by id and extract the text as below*/
                TextView tvCategoryItem= (TextView) v.findViewById(R.id.lblListItem);
                String category= tvCategoryItem.getText().toString();
                Toast.makeText(listView.getContext(), "Picked event: " + category, Toast.LENGTH_LONG).show();
                showCreateDetailsDialog(category);
                return true;  // i missed this
            }
        });

    }

    // Defines the listener interface
    public interface CategoryDialogListener {
        void onFinishCategoryDialog(Event event);
    }

    // Call this method to send the data back to the parent fragment
    public void sendBackResult(Event event) {
        // Notice the use of `getTargetFragment` which will be set when the dialog is displayed
        CategoryDialogListener listener = (CategoryDialogListener) getTargetFragment();
        listener.onFinishCategoryDialog(event);
        // dismiss current dialog, move back to feed fragment
        dismiss();
    }
    // creates new dialog
    public void showCreateDetailsDialog(String category) {
        // launch create detail fragment activity
        FragmentManager fm = getFragmentManager();
        CreateDetailsDialogFragment createDetailsDialogFragment = CreateDetailsDialogFragment.newInstance(category);

        // SETS the target fragment for use later when sending results
        createDetailsDialogFragment.setTargetFragment(PickCategoryDialogFragment.this, 300);

        // creates the create details fragment
        createDetailsDialogFragment.show(fm, "fragment_create_details");
    }

    // This is called when the dialog is completed and the results have been passed
    @Override
    public void onFinishCreateDetailsDialog(Event createdEvent) {
        Log.i("DEBUGPick", createdEvent.toString());
        sendBackResult(createdEvent);
    }


    // populate the categories hash map
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
