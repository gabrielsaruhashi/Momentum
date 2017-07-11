package shag.com.shag.Adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import shag.com.shag.Fragments.FeedFragment;

/**
 * Created by gabesaruhashi on 7/11/17.
 */

public class MainFragmentPagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 3;
    private String tabTitles[] = new String[] { "Feed", "Map", "Chats" };
    private Context context;
    private FeedFragment feedFragment;

    public MainFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public Fragment getItem(int position) {
        if  (position == 0) {
            feedFragment = getFeedInstance();
            return feedFragment;
        } else {
            return null;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // generate title based on item postion
        return tabTitles[position];
    }

    private FeedFragment getFeedInstance() {
        if (feedFragment == null) {
            feedFragment = new FeedFragment();
        }
        return feedFragment;
    }
}
