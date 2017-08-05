package shag.com.shag.Adapters;

/**
 * Created by samrabelachew on 8/4/17.
 */

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import shag.com.shag.Fragments.MapFragment;
import shag.com.shag.Fragments.MemoryListFragment;

/**
 * Created by gabesaruhashi on 7/11/17.
 */

public class MemoryFragmentPagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 2;
    private Context context;
    private MemoryListFragment memoryBook;
    private MapFragment mapFragment;
    Drawable myDrawable;

    public MemoryFragmentPagerAdapter(FragmentManager fm, Context context) {
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
            memoryBook = getMemoryListInstance();
            return memoryBook;
        } else if (position == 1) {
            mapFragment = getMapInstance();
            return mapFragment;
        } else {
            return null;
        }
    }



    private MapFragment getMapInstance() {
        if (mapFragment == null) {
            mapFragment = new MapFragment();
        }
        return mapFragment;
    }

    private MemoryListFragment getMemoryListInstance() {
        if (memoryBook == null) {
            memoryBook = new MemoryListFragment();
        }

        return memoryBook;
    }

}
