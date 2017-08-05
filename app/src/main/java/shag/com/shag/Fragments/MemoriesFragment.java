package shag.com.shag.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import shag.com.shag.Adapters.MemoriesAdapter;
import shag.com.shag.Adapters.MemoryFragmentPagerAdapter;
import shag.com.shag.Models.Memory;
import shag.com.shag.R;

public class MemoriesFragment extends Fragment {

    ViewPager mViewPager;
    TabLayout tabLayout;
    private FragmentManager fragmentManager;

    Context context;
    ArrayList<Memory> memories;
    MemoriesAdapter mAdapter;
    ParseUser currentUser;
    ListView lvMemories;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_memories, container, false);

        // get our list view
        //lvMemories = (ListView) v.findViewById(R.id.lvMainList);
        context = getContext();

        mViewPager = (ViewPager) v.findViewById(R.id.view_pager);
        mViewPager.setAdapter(new MemoryFragmentPagerAdapter(getFragmentManager(), context));
        TabLayout tabLayout = (TabLayout) v.findViewById(R.id.tabber);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);

        tabLayout.setSelectedTabIndicatorHeight(2);
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_camera);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_food);

        return v;
    }

    private void populateMemories() {
        ParseQuery<Memory> query = ParseQuery.getQuery("Memory");
        // auxiliary list
        List list = new ArrayList();
        list.add(currentUser.getObjectId());

        // create the query condition
        query.whereContainedIn("participants_ids", list);

        query.findInBackground(new FindCallback<Memory>() {
            @Override
            public void done(List<Memory> objects, ParseException e) {
            if (e == null) {
                memories.clear();
                memories.addAll(objects);
                mAdapter.notifyDataSetChanged();


            } else {
                e.getMessage();
            }
        };
    /*
    ArrayList<ParseObject> memoriesList = (ArrayList) currentUser.getList("Memories_list");
    //for (ParseObject memory : memoriesList) {
    for (int i = 0; i < memoriesList.size(); i++) {
        try {
            ParseObject memoryObject = memoriesList.get(i).fetchIfNeeded();
            Memory memory = Memory.fromParseObject(memoryObject);
            memories.add(0, memory);
            mAdapter.notifyItemInserted(memories.size() - 1);
            rvMemories.smoothScrollToPosition(0);
        } catch (ParseException e) {
            e.getMessage();
        } */

        /*
        memoriesList.get(i).getParseObject("Memory").fetchIfNeededInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                Memory memory = Memory.fromParseObject(object);
                memories.add(0, memory);
                mAdapter.notifyItemInserted(memories.size() - 1);
                rvMemories.smoothScrollToPosition(0);
            }
        });*/
    });


    }
}
