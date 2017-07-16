package shag.com.shag.Adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import shag.com.shag.Fragments.ChatListFragment;
import shag.com.shag.Fragments.FeedFragment;
import shag.com.shag.Fragments.MapFragment;

/**
 * Created by gabesaruhashi on 7/11/17.
 */

public class MainFragmentPagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 3;
    private String tabTitles[] = new String[] { "Feed", "Map", "Chats" };
    private Context context;
    private FeedFragment feedFragment;
    private MapFragment mapFragment;
    private ChatListFragment chatListFragment;
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
            mapFragment = getMapInstance();
            return mapFragment;
        } else if (position == 2) {
            chatListFragment = getChatListInstance();
            return chatListFragment;
        }
        else {
            return null;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        /*
        // generate title based on item postion

        switch (position) {
            case 0:
                myDrawable = ContextCompat.getDrawable(context, R.drawable.ic_home);
                        //().getDrawable(R.drawable.ic_home);
                break;
            // return tabTitles[position];
            default:
                break;
        }
        SpannableStringBuilder sb = new SpannableStringBuilder("   " + tabTitles[position]); // space added before text for convenience
        try {
            myDrawable.setBounds(5, 5, myDrawable.getIntrinsicWidth(), myDrawable.getIntrinsicHeight());
            ImageSpan span = new ImageSpan(myDrawable, DynamicDrawableSpan.ALIGN_BASELINE);
            sb.setSpan(span, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } catch (Exception e) {
            e.printStackTrace();
            // TODO: handle exception
        }
        return sb;
        */
        return tabTitles[position];
    }

    private FeedFragment getFeedInstance() {
        if (feedFragment == null) {
            feedFragment = new FeedFragment();
        }
        return feedFragment;
    }

    private MapFragment getMapInstance() {
        if (mapFragment == null) {
            mapFragment = new MapFragment();
        }
        return mapFragment;
    }

    private ChatListFragment getChatListInstance() {
        if (chatListFragment == null) {
            chatListFragment = new ChatListFragment();
        }
        return chatListFragment;
    }
}
