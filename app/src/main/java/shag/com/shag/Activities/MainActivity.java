package shag.com.shag.Activities;

import android.Manifest.permission;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.login.LoginManager;
import com.ramotion.paperonboarding.PaperOnboardingFragment;

import shag.com.shag.Adapters.MainFragmentPagerAdapter;
import shag.com.shag.Fragments.DialogFragments.OnboardingDialogFragment;
import shag.com.shag.Fragments.MemoryListFragment;
import shag.com.shag.R;

public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {
    Toolbar myToolbar;
    ViewPager viewPager;
    TabLayout tabLayout;
    private int[] tabIcons = {
            R.drawable.ic_home,
            R.drawable.ic_movie_roll_tape,
    };

    private FragmentManager fragmentManager;
    PaperOnboardingFragment onBoardingFragment;
    static boolean isNew;

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

        // unwrap to see whether is new
        isNew = getIntent().getBooleanExtra("isNew", false);

        // if new, show onboarding dialog
        if (isNew) {
            showOnboardingDialog();
            isNew = false;
        }

        //add ability to open a specific fragment with intent data
        int position = 0;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            position = extras.getInt("viewpager_position");
        }

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new MainFragmentPagerAdapter(getSupportFragmentManager(),
                MainActivity.this));
        //viewPager.setOffscreenPageLimit(3);

        // Give the TabLayout the ViewPager
        tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

        viewPager.setCurrentItem(position);

        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setupTabIcons();

        // check for all permissions needed
        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }

    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        myToolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));
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
        Intent i = new Intent(this, MemoriesActivity.class);
        startActivity(i);
    } */


    public void onChatView(MenuItem item) {
        // launch chat activity
        Intent i = new Intent(this, ChatListActivity.class);
        startActivity(i);
    }

    // on activity result for memory list fragment
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        MemoryListFragment memoryListFragment = (MemoryListFragment) getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.viewpager + ":" + viewPager.getCurrentItem());
        memoryListFragment.changeCoverPictureUrl(data);
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

}
