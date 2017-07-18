package shag.com.shag.Fragments;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.AuthFailureError;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import shag.com.shag.Activities.PublicEventDetailsActivity;
import shag.com.shag.Clients.VolleyRequest;
import shag.com.shag.R;

import static com.facebook.FacebookSdk.getApplicationContext;
import static shag.com.shag.R.id.map;


public class MapFragment extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

//    final static int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION=2;
//    private static final String KEY_CAMERA_POSITION = "camera_position";
//    private static final String KEY_LOCATION = "location";
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private RequestQueue mRequestQueue;



    GoogleMap mGoogleMap;
    SupportMapFragment mapFrag;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    ToggleButton btn;
    Button btn2;
    Button btn3;
    Button btn4;
    Button btn5;
    Button btn6;
    Marker marker1;
    Geocoder geocoder;
    List<Address> addresses;


    // inflation happens inside onCreateView
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // inflate the layout
        View v = inflater.inflate(R.layout.fragment_map, container, false);

        mRequestQueue = VolleyRequest.getInstance(this.getContext()).
                getRequestQueue();


        btn= (ToggleButton) v.findViewById(R.id.btn_Test);
        btn2 = (Button) v.findViewById(R.id.btn_Test2);
        btn3= (Button) v.findViewById(R.id.btn_Test3);
        btn4 = (Button) v.findViewById(R.id.btn_Test4);
        btn5= (Button) v.findViewById(R.id.btn_Test5);
        btn6 = (Button) v.findViewById(R.id.btn_Test6);
        mapFrag = (SupportMapFragment) getChildFragmentManager().findFragmentById(map);
        mapFrag.getMapAsync(this);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MarkerOptions markerOptions1 = new MarkerOptions();
                markerOptions1.position(new LatLng (47.621397, -122.338092));
                markerOptions1.title("Bill Position");
                markerOptions1.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                marker1 = mGoogleMap.addMarker(markerOptions1);
            }
        });


        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    addresses = new ArrayList<>();
                    geocoder = new Geocoder(getContext(), Locale.getDefault());
                    addresses = geocoder.getFromLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude(), 1);
                    String postalCode = addresses.get(0).getPostalCode();
                    onStartMusicRequest(postalCode,"20");
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStartFoodRequest(mLastLocation.getLatitude() + "", mLastLocation.getLongitude() + "");

            }
        });


        return v;
    }





    //CREATING CLIENT
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    //INITIAL MAP
    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mGoogleMap=googleMap;
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
        }
        else {
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
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {}

    @Override
    public void onPause() {
        super.onPause();

        //stop location updates when Activity is no longer active
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, (com.google.android.gms.location.LocationListener) this);
        }
    }


    public void onLocationChanged(Location location)
    {
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


//
//        MarkerOptions markerOptions2 = new MarkerOptions();
//        markerOptions2.position(firstEvent.latLng);
//        markerOptions2.title("Bill Position");
//        markerOptions2.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
//        marker2 = mGoogleMap.addMarker(markerOptions1);
        //move map camera
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,13));



    }

    //GETTING PERMISSIONS
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
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
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION );
                            }
                        })
                        .create();
                alertDialog.show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION );
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


    //JAMBASE API
    private void onStartMusicRequest(String zipcode, String radius) {
        VolleyRequest.getInstance(getApplicationContext()).addToRequestQueue(getMusicRequest(zipcode,radius));
    }

    public JsonObjectRequest getMusicRequest(String zipcode, String radius){
        // Pass second argument as "null" for GET requests
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        String year = cal.get(Calendar.YEAR) + "";

        //Todo: take into account days under 10 (do they have a 0 before it for example Aug 3 = 03 or 3)?
        String day = cal.get(Calendar.DAY_OF_MONTH) + "";
        int endDay =  (cal.get(Calendar.DAY_OF_MONTH) + 1);
        String finalDay = endDay + "";

        int month = cal.get(Calendar.MONTH) + 1;
        String formattedMonth;
        if (month > 9) {
             formattedMonth = "1" + month;
        }
        else {
             formattedMonth = "0" + month;

        }
        SimpleDateFormat localDateFormat = new SimpleDateFormat("HH:mm:ss");
        String time = localDateFormat.format(date.getTime());


        String jamBaseUrl = "http://api.jambase.com/events?";
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, jamBaseUrl + "zipCode=" + zipcode+
                "&radius="+radius+"&startDate=+"+year+"-" + formattedMonth + "-"+day+"T"+time.substring(0,2)+"%3A"+time.substring(3,5)+"%3A"+time.substring(6)
                +"&endDate=+"+year+"-"+ formattedMonth +"-"+finalDay+"T23%3A59%3A59&page=0&api_key=6dhquzx3559xvcd2un49madm", null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        JSONArray eventArray = null;
                        try {
                            eventArray = response.getJSONArray("Events");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        for (int i = 0; i < eventArray.length(); i++) {
                            try {
                                Double lat = eventArray.getJSONObject(i).getJSONObject("Venue").getDouble("Latitude");
                                Double lng = eventArray.getJSONObject(i).getJSONObject("Venue").getDouble("Longitude");
                                String streetName = eventArray.getJSONObject(i).getJSONObject("Venue").getString("Address");
                                String city = eventArray.getJSONObject(i).getJSONObject("Venue").getString("City");
                                String state = eventArray.getJSONObject(i).getJSONObject("Venue").getString("StateCode");
                                String zipcode = eventArray.getJSONObject(i).getJSONObject("Venue").getString("ZipCode");


                                final String locationAddress=streetName+" "+city+", "+state+ ", "+ zipcode;

                                JSONArray getArtistArray = null;
                                try {
                                    getArtistArray = eventArray.getJSONObject(i).getJSONArray("Artists");
                                    String artist = getArtistArray.getJSONObject(0).getString("Name");
                                    LatLng musicLatLng = new LatLng(lat, lng);

                                    final HashMap<String,String> publicEventData = new HashMap<>();
                                    publicEventData.put("Name", artist);
                                    publicEventData.put("Description", "concert??");
                                    publicEventData.put("Location", locationAddress);
                                    publicEventData.put("Category", "Music");


                                    MarkerOptions markerOptions = new MarkerOptions();
                                    markerOptions.position(musicLatLng);
                                    markerOptions.title(artist);
                                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
                                    mGoogleMap.addMarker(markerOptions).setTag(publicEventData);


                                    mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                                        @Override
                                        public void onInfoWindowClick(Marker marker) {
                                            Intent i = new Intent(getContext(), PublicEventDetailsActivity.class);
                                            i.putExtra("Data", (Serializable) marker.getTag());
//
                                            startActivity(i);
                                        }

                                    });

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }                                //Event event = new Event();
                                //event.setDeadline(eventArray.getJSONObject(i).getInt("Date") + "");
                                // event.setLatLng(new LatLng(eventArray.getJSONObject(i).getJSONObject("Venue").getDouble("Latitude"),
                                //         eventArray.getJSONObject(i).getJSONObject("Venue").getDouble("Longitude")));
                                //event.setCategory("Music");
                                //event.setLocation(eventArray.getJSONObject(i).getJSONObject("Venue").getString("Name"));
                                // Log.d("lookie here", name + new LatLng(lat, lng) + "");

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }



                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
            }
        });
        return req;
    }

    //ZOMATO API
    private void onStartFoodRequest(String lat, String lng) {
        VolleyRequest.getInstance(getApplicationContext()).addToRequestQueue(getFoodRequest(lat,lng));
    }

    public JsonObjectRequest getFoodRequest(String lat, String lng){
        // Pass second argument as "null" for GET requests
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, "https://developers.zomato.com/api/v2.1/geocode?lat="+lat+"&lon="+lng, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("response", "aww yeah");
                        try {
                            JSONArray restaurantArray = response.getJSONArray("nearby_restaurants");
                            for (int i = 0; i < restaurantArray.length(); i++) {

                                JSONObject restaurant = restaurantArray.getJSONObject(i).getJSONObject("restaurant");
                                final String restaurantName = restaurant.getString("name");
                                Double restaurantLat = restaurant.getJSONObject("location").getDouble("latitude");
                                Double restaurantLng = restaurant.getJSONObject("location").getDouble("longitude");
                                LatLng restaurantLatLng = new LatLng(restaurantLat, restaurantLng);

                                String streetName = restaurant.getJSONObject("location").getString("address");
                                //String city = restaurant.getJSONObject("location").getString("city");
                                //String zipcode = restaurant.getJSONObject("location").getString("zipcode");
                                final String locationAddress=streetName;

                                final String genre = restaurant.getString("cuisines");

                                final HashMap<String,String> publicEventData = new HashMap<>();
                                publicEventData.put("Name", restaurantName);
                                publicEventData.put("Description", genre);
                                publicEventData.put("Location", locationAddress);
                                publicEventData.put("Category", "Food");


                                final MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions.position(restaurantLatLng);
                                markerOptions.title(restaurantName);
                                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                                mGoogleMap.addMarker(markerOptions).setTag(publicEventData);




                                mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                                    @Override
                                    public void onInfoWindowClick(Marker marker) {
                                        Intent i = new Intent(getContext(), PublicEventDetailsActivity.class);
                                        i.putExtra("Data", (Serializable) marker.getTag());
//
                                        startActivity(i);
                                    }

                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //error occur
                    }
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Accept", "application/json");
                params.put("user-key", "09e67b4492d86e45473c0af26442ab3d");
                return params;
            }
        };
        return req;
    }

}
