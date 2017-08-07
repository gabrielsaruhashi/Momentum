package shag.com.shag.Activities;

import android.Manifest.permission;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.login.LoginManager;
import com.github.florent37.materialviewpager.MaterialViewPager;
import com.github.florent37.materialviewpager.header.HeaderDesign;
import com.ramotion.paperonboarding.PaperOnboardingFragment;

import shag.com.shag.Adapters.MainFragmentPagerAdapter;
import shag.com.shag.Fragments.DialogFragments.OnboardingDialogFragment;
import shag.com.shag.Fragments.MemoryListFragment;
import shag.com.shag.R;

public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, MemoryListFragment.OnMemoryBookPositionChangedListener {
    Toolbar toolbar;
    MaterialViewPager mViewPager;
    ViewPager viewPager;
    MaterialViewPager.Listener listener;
    MainFragmentPagerAdapter adapterViewPager;

    private int[] tabIcons = {
            R.drawable.ic_home,
            R.drawable.ic_movie_roll_tape,
    };

    private FragmentManager fragmentManager;
    PaperOnboardingFragment onBoardingFragment;
    static boolean isNewUser;

    // The request code used in ActivityCompat.requestPermissions()
    // and returned in the Activity's onRequestPermissionsResult()
    int PERMISSION_ALL = 1;
    String[] PERMISSIONS = {android.Manifest.permission.READ_CONTACTS, android.Manifest.permission.SEND_SMS, permission.RECEIVE_SMS,
            permission.READ_CALENDAR, permission.READ_EXTERNAL_STORAGE, permission.ACCESS_FINE_LOCATION,
            permission.ACCESS_COARSE_LOCATION} ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // if new user, display onboarding
        checkIfNewUser(getIntent().getBooleanExtra("isNew", false));

        // get reference to material viewpager
        mViewPager = (MaterialViewPager) findViewById(R.id.viewpager);

        // get toolbar reference
        toolbar = mViewPager.getToolbar();
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        //int position = getIntent().getIntExtra("viewpager_position", 0);

        // instantiate initial listener
        listener = new MaterialViewPager.Listener() {
            @Override
            public HeaderDesign getHeaderDesign(int page) {
                switch (page) {
                    case 0:
                        return HeaderDesign.fromColorResAndUrl(
                                R.color.green,
                                "http://phandroid.s3.amazonaws.com/wp-content/uploads/2014/06/android_google_moutain_google_now_1920x1080_wallpaper_Wallpaper-HD_2560x1600_www.paperhi.com_-640x400.jpg");
                    case 1:
                        return HeaderDesign.fromColorResAndUrl(
                                R.color.blue,
                                "http://www.hdiphonewallpapers.us/phone-wallpapers/540x960-1/540x960-mobile-wallpapers-hd-2218x5ox3.jpg");
                }

                //execute others actions if needed (ex : modify your header logo)

                return null;
            }
        };

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        viewPager = mViewPager.getViewPager();

        // instantiate and attach viewpager adapter
        adapterViewPager = new MainFragmentPagerAdapter(getSupportFragmentManager(),
                MainActivity.this);
        viewPager.setAdapter(adapterViewPager);

        mViewPager.setMaterialViewPagerListener(listener);

        mViewPager.getViewPager().setOffscreenPageLimit(mViewPager.getViewPager().getAdapter().getCount());
        mViewPager.getPagerTitleStrip().setViewPager(mViewPager.getViewPager());


        // check for all permissions needed
        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }

    }


    public void checkIfNewUser(boolean isNew) {
        // if new, show onboarding dialog
        if (isNew) {
            showOnboardingDialog();
            isNewUser = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    public void onProfileView(MenuItem item) {
        //launch profile view
        Intent i = new Intent(this, UserProfileActivity.class);
        startActivity(i);
    }

    public void onSettingsView(MenuItem item) {
        //launch profile view
        Intent i = new Intent(this, SettingsActivity.class);
        startActivity(i);
    }

    /*
    public void onMemoriesView(MenuItem item) {
        //launch profile view
        Intent i = new Intent(this, MemoriesFragment.class);
        startActivity(i);
    } */


    public void onChatView(MenuItem item) {
        // launch chat activity
        Intent i = new Intent(this, ChatListActivity.class);
        startActivity(i);
    }

    // on activity result for memory list fragment
    // change cover picture of memory album
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            MemoryListFragment memoryListFragment = (MemoryListFragment) getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.viewpager + ":" + viewPager.getCurrentItem());
            //MemoryListFragment memoryListFragment = (MemoryListFragment) viewPager.getRegistered;
            memoryListFragment.changeCoverPictureUrl(data);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LoginManager.getInstance().logOut();
    }

    @Override
    public void onBackPressed() {
        //do nothing
    }

    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    private void showOnboardingDialog() {
        FragmentManager fm = getSupportFragmentManager();
        OnboardingDialogFragment onboardingDialogFragment = OnboardingDialogFragment.newInstance("Welcome!");
        onboardingDialogFragment.show(fm, "fragment_onboarding");
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }


    @Override
    public void changeMaterialVPListener(final String imageUrl) {
        final MaterialViewPager.Listener newListener = new MaterialViewPager.Listener() {
            @Override
            public HeaderDesign getHeaderDesign(int page) {
                switch (page) {
                    case 0:
                        return HeaderDesign.fromColorResAndUrl(
                                R.color.green,
                                "http://phandroid.s3.amazonaws.com/wp-content/uploads/2014/06/android_google_moutain_google_now_1920x1080_wallpaper_Wallpaper-HD_2560x1600_www.paperhi.com_-640x400.jpg");
                    case 1:
                        return HeaderDesign.fromColorResAndUrl(
                                R.color.blue,
                                imageUrl);
                }
                return null;
            }
        };
        mViewPager.setMaterialViewPagerListener(newListener);
    }

}
