package shag.com.shag.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.login.LoginManager;

import shag.com.shag.Adapters.MainFragmentPagerAdapter;
import shag.com.shag.R;

public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //add ability to open a specific fragment with intent data
        int position = 0;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            position = extras.getInt("viewpager_position");
        }

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new MainFragmentPagerAdapter(getSupportFragmentManager(),
                MainActivity.this));
        //viewPager.setOffscreenPageLimit(3);

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

        viewPager.setCurrentItem(position);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

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

    public void onMemoriesView(MenuItem item) {
        //launch profile view
        Intent i = new Intent(this, MemoriesActivity.class);
        startActivity(i);
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
}
