package shag.com.shag.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;
import java.util.Locale;

import shag.com.shag.Clients.VolleyRequest;
import shag.com.shag.R;

import static com.raizlabs.android.dbflow.config.FlowManager.getContext;
import static shag.com.shag.Fragments.MapFragment.getBitmapFromVectorDrawable;

public class EventReadyActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    private RequestQueue mRequestQueue;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    GoogleMap mGoogleMap;
    SupportMapFragment mapFrag;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    Activity activity;
    LatLng mDestination;
    Date timeOfEvent;
    Geocoder geocoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_ready);

        //TODO: put this back
        //destination = getIntent().getParcelableExtra("destination");
        //timeOfEvent = getIntent().getParcelableExtra("timeOfEvent");

        mDestination = new LatLng(47.6101, -122.2015);
        timeOfEvent = new Date((new Date()).getTime() + 24*60*60*1000);

        activity = this;

        mRequestQueue = VolleyRequest.getInstance(this).getRequestQueue();
        mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFrag.getMapAsync(this);
        geocoder = new Geocoder(getContext(), Locale.getDefault());
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    //CREATING CLIENT
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
        mGoogleApiClient.connect();
    }

    //INITIAL MAP
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                buildGoogleApiClient();
                mGoogleMap.setMyLocationEnabled(true);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        } else {
            buildGoogleApiClient();
            mGoogleMap.setMyLocationEnabled(true);
        }
    }

    //DIFFERENT STATES OF MAP
    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        //mLocationRequest.setInterval(1000);
        //mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, (com.google.android.gms.location.LocationListener) this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        //stop location updates when Activity is no longer active
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }


    public void onLocationChanged(Location location) {
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);

        //mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));

        //TODO plot end location
        Bitmap bitmap = getBitmapFromVectorDrawable(getContext(),R.drawable.ic_map_marker);
        MarkerOptions destinationMarkerOptions = new MarkerOptions();
        destinationMarkerOptions.position(mDestination);
        destinationMarkerOptions.title("Destination");
        destinationMarkerOptions.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
        mGoogleMap.addMarker(destinationMarkerOptions);

        LatLngBounds bounds = new LatLngBounds(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), mDestination);
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 0));
    }

    //GETTING PERMISSIONS
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(activity,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create();
                alertDialog.show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(getContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mGoogleMap.setMyLocationEnabled(true);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getContext(), "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void findTravelOptions(View view) {
        //LatLng lastLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        String origin = "";
        String destination = "";
        try {
            Address currentLocation = geocoder.getFromLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude(), 1).get(0);
            origin = currentLocation.getAddressLine(0);
            origin = origin.replace(" ", "+");

            Address destinationLocation = geocoder.getFromLocation(mDestination.latitude, mDestination.longitude, 1).get(0);
            destination = destinationLocation.getAddressLine(0);
            destination = destination.replace(" ", "+");
        } catch (IOException e) {
            e.printStackTrace();
        }
        String baseUrl = "https://maps.googleapis.com/maps/api/directions/json?";
        baseUrl += "&origin=" + origin;
        baseUrl += "&destination=" + destination;
        baseUrl += "&arrival_time=" + (timeOfEvent.getTime() / 1000);

        //car (default)
        String url = baseUrl + "&key=" + "AIzaSyD5ty8DSE8Irio8xdCvCQMltWpuVDioHTI";
        JsonObjectRequest drivingRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString("status");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
            }
        });

        //bus
        String transitUrl = baseUrl + "&mode=transit";
        transitUrl += "&key=" + "AIzaSyD5ty8DSE8Irio8xdCvCQMltWpuVDioHTI";
        JsonObjectRequest transitRequest = new JsonObjectRequest(Request.Method.GET, transitUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString("status");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
            }
        });

        //walking
        String walkingUrl = baseUrl + "&mode=walking";
        walkingUrl += "&key=" + "AIzaSyD5ty8DSE8Irio8xdCvCQMltWpuVDioHTI";
        JsonObjectRequest walkingRequest = new JsonObjectRequest(Request.Method.GET, walkingUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString("status");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
            }
        });

        //cycling
        String cyclingUrl = baseUrl + "&mode=cycling";
        cyclingUrl += "&key=" + "AIzaSyD5ty8DSE8Irio8xdCvCQMltWpuVDioHTI";
        JsonObjectRequest cyclingRequest = new JsonObjectRequest(Request.Method.GET, cyclingUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString("status");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
            }
        });


        //TODO: let the user choose which one
        //go full screen with map
        // add the request object to the queue to be executed
        VolleyRequest.getInstance(getApplicationContext()).addToRequestQueue(drivingRequest);
        VolleyRequest.getInstance(getApplicationContext()).addToRequestQueue(transitRequest);
        VolleyRequest.getInstance(getApplicationContext()).addToRequestQueue(walkingRequest);
        VolleyRequest.getInstance(getApplicationContext()).addToRequestQueue(cyclingRequest);
    }


}
