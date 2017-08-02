package shag.com.shag.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import shag.com.shag.R;

public class SelectPublicEventTypeActivity extends AppCompatActivity {

    ImageView food;
    ImageView music;
    private FusedLocationProviderClient mFusedLocationClient;
    Location currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_public_event_type);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            currentLocation=location;
                        }
                    }
                });
        food = (ImageView) findViewById(R.id.ivFood);
        music = (ImageView) findViewById(R.id.ivMusic);

        Glide.with(this).load("http://maps.google.com/mapfiles/ms/icons/yellow-dot.png").into(food);
        Glide.with(this).load("http://maps.google.com/mapfiles/ms/icons/yellow-dot.png").into(music);

        food.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SelectPublicEventTypeActivity.this, SelectPublicMapActivity.class);
                i.putExtra("Event Type", "Public");
                i.putExtra("Category", "Food");
                i.putExtra("CurrentLoc", currentLocation);
                startActivity(i);
            }
        });



        music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SelectPublicEventTypeActivity.this, SelectPublicMapActivity.class);
                i.putExtra("Event Type", "Public");
                i.putExtra("Category", "Music");
                i.putExtra("CurrentLoc", currentLocation);
                startActivity(i);

            }
        });

    }
}
