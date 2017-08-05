package shag.com.shag.Adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.text.SpannableStringBuilder;

import shag.com.shag.Fragments.FeedFragment;
import shag.com.shag.Fragments.MemoriesFragment;
import shag.com.shag.Fragments.MemoryListFragment;

/**
 * Created by gabesaruhashi on 7/11/17.
 */

public class MainFragmentPagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 2;
    private String tabTitles[] = new String[] { "Feed", "Memories" };
    private Context context;
    private FeedFragment feedFragment;
    private MemoryListFragment memoryListFragment;
    private MemoriesFragment memoriesFragment;
    Drawable myDrawable;

    public MainFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        if  (position == 0) {
            feedFragment = getFeedInstance();
            return feedFragment;
        } else if (position == 1) {
            memoriesFragment = getMemoriesFragmentInstance();
            return memoriesFragment;
        } else {
            return null;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {

        // generate title based on item postion

//        switch (position) {
//            case 0:
//                myDrawable = ContextCompat.getDrawable(context, R.drawable.ic_home);
//                        //().getDrawable(R.drawable.ic_home);
//                break;
//            // return tabTitles[position];
//            case 1:
//                myDrawable = ContextCompat.getDrawable(context, R.drawable.ic_movie_roll_tape);
//                break;
//            default:
//                break;
//        }
        SpannableStringBuilder sb = new SpannableStringBuilder("   " + tabTitles[position]); // space added before text for convenience
//        try {
//            myDrawable.setBounds(0, 0, myDrawable.getIntrinsicWidth(), myDrawable.getIntrinsicHeight());
//            ImageSpan span = new ImageSpan(myDrawable);
//            sb.setSpan(span, 0, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        } catch (Exception e) {
//            e.printStackTrace();
//            // TODO: handle exception
//        }
        return sb;

        //return tabTitles[position];
    }

    private FeedFragment getFeedInstance() {
        if (feedFragment == null) {
            feedFragment = new FeedFragment();
        }
        return feedFragment;
    }

    private MemoriesFragment getMemoriesFragmentInstance() {
        if (memoriesFragment == null) {
            memoriesFragment = new MemoriesFragment();
        }

        return memoriesFragment;
    }

}
