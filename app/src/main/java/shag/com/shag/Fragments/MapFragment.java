package shag.com.shag.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

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
import com.google.android.gms.location.places.Places;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import me.relex.circleindicator.CircleIndicator;
import shag.com.shag.Adapters.MapCardAdapter;
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


    CircleIndicator indicator;
    GoogleMap mGoogleMap;
    SupportMapFragment mapFrag;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    Button musicFab;
    Button foodFab;
    Geocoder geocoder;
    List<Address> addresses;
    MapCardAdapter mapCardAdapter;
    ViewPager viewPager;
    RelativeLayout cardViews;
    ArrayList<HashMap<String, Object>> cardData = new ArrayList<>();
    int cardPagerSize = 0;

    // inflation happens inside onCreateView
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // inflate the layout
        View v = inflater.inflate(R.layout.fragment_map, container, false);
        mRequestQueue = VolleyRequest.getInstance(this.getContext()).
                getRequestQueue();


        musicFab = (Button) v.findViewById(R.id.btn_Test2);
        foodFab= (Button) v.findViewById(R.id.btn_Test3);


        mapFrag = (SupportMapFragment) getChildFragmentManager().findFragmentById(map);
        mapFrag.getMapAsync(this);


        cardViews = (RelativeLayout) v.findViewById(R.id.eventDetails);
        mapCardAdapter = new MapCardAdapter(getContext(), cardData);
        viewPager = (ViewPager) v.findViewById(R.id.pagerMapCards);
        viewPager.setAdapter(mapCardAdapter);
        viewPager.setOffscreenPageLimit(3);

        indicator = (CircleIndicator) v.findViewById(R.id.indicator);
        indicator.setViewPager(viewPager);


        musicFab.setOnClickListener(new View.OnClickListener() {
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

        foodFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStartFoodRequest(mLastLocation.getLatitude() + "", mLastLocation.getLongitude() + "");

            }
        });

//        cardExit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                cardView.animate()
//                        .translationX(cardView.getWidth())
//                        .alpha(0.0f)
//                        .setDuration(300)
//                        .setListener(new AnimatorListenerAdapter() {
//                            @Override
//                            public void onAnimationEnd(Animator animation) {
//                                super.onAnimationEnd(animation);
//                                cardView.setVisibility(View.GONE);
//                            }
//                        });
//
//            }
//        });


        return v;
    }





    //CREATING CLIENT
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
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
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,  this);
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

                    // permission denied, Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getContext(), "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }


            // permissions this app might request
        }
    }


    //JAMBASE API
    public void onStartMusicRequest(String zipcode, String radius) {
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
        String jamBaseApi = "6dhquzx3559xvcd2un49madm";


        String jamBaseUrl = "http://api.jambase.com/events?";
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, jamBaseUrl + "zipCode=" + zipcode+
                "&radius="+radius+"&startDate=+"+year+"-" + formattedMonth + "-"+day+"T"+time.substring(0,2)+"%3A"+time.substring(3,5)+"%3A"+time.substring(6)
                +"&endDate=+"+year+"-"+ formattedMonth +"-"+finalDay+"T23%3A59%3A59&page=0&api_key="+jamBaseApi, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        JSONArray eventArray = null;
                        try {
                            eventArray = response.getJSONArray("Events");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        cardPagerSize=0;
                        cardViews.setVisibility(View.VISIBLE);
                        for (int i = 0; i < eventArray.length(); i++) {
                            try {
                                final Double musicLat = eventArray.getJSONObject(i).getJSONObject("Venue").getDouble("Latitude");
                                final Double musicLng = eventArray.getJSONObject(i).getJSONObject("Venue").getDouble("Longitude");
                                final String concertPlace = eventArray.getJSONObject(i).getJSONObject("Venue").getString("Name");
                                String streetName = eventArray.getJSONObject(i).getJSONObject("Venue").getString("Address");
                                String city = eventArray.getJSONObject(i).getJSONObject("Venue").getString("City");
                                String state = eventArray.getJSONObject(i).getJSONObject("Venue").getString("StateCode");
                                String zipcode = eventArray.getJSONObject(i).getJSONObject("Venue").getString("ZipCode");


                                final String locationAddress=streetName+" "+city+", "+state+ ", "+ zipcode;

                                JSONArray getArtistArray = null;
                                try {
                                    getArtistArray = eventArray.getJSONObject(i).getJSONArray("Artists");
                                    String artist = getArtistArray.getJSONObject(0).getString("Name");
                                    LatLng musicLatLng = new LatLng(musicLat, musicLng);

                                    final HashMap<String,Object> publicEventData = new HashMap<>();
                                    publicEventData.put("Name", artist);
                                    publicEventData.put("Description", "Music Concert");
                                    publicEventData.put("Location", locationAddress);
                                    publicEventData.put("Category", "Music");
                                    publicEventData.put("Lat", musicLat);
                                    publicEventData.put("Lng", musicLng);
                                    publicEventData.put("Place Name",concertPlace);
                                    cardPagerSize++;

                                    Bitmap bitmap = getBitmapFromVectorDrawable(getContext(),R.drawable.ic_map_marker);

                                    MarkerOptions markerOptions = new MarkerOptions();
                                    markerOptions.position(musicLatLng);
                                    markerOptions.title(artist);
                                    markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
                                    mGoogleMap.addMarker(markerOptions).setTag(publicEventData);
                                    cardData.add(publicEventData);
                                    mapCardAdapter.notifyDataSetChanged();
                                    indicator.setViewPager(viewPager);

//                                    mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
//                                        @Override
//                                        public void onInfoWindowClick(Marker marker) {
//
//                                            final HashMap<String,String> data=(HashMap<String, String>) marker.getTag();
//
//                                            cardTitle.setText(data.get("Name"));
//                                            cardDescription.setText(data.get("Description"));
//                                            cardLocation.setText(data.get("Location"));
//
//                                            cardView.setVisibility(View.VISIBLE);
//
//
//                                            fab.setOnClickListener(new View.OnClickListener() {
//                                                @Override
//                                                public void onClick(View v) {
//
//                                                    Intent i = new Intent(getContext(), SelectEventDetailsActivity.class);
//                                                    i.putExtra("Location", data.get("Location"));
//                                                    i.putExtra("Category", data.get("Category"));
//                                                    i.putExtra("Event Type", "Public");
//                                                    i.putExtra("Lat", musicLat);
//                                                    i.putExtra("Lng", musicLng);
//                                                    i.putExtra("Place Name", concertPlace);
//                                                    getContext().startActivity(i);
//                                                }
//                                            });
//
//                                        }
//
//                                    });

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

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
//        cardViews.setVisibility(View.VISIBLE);
        return req;

    }



    public static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }



    //ZOMATO API
    public void onStartFoodRequest(String lat, String lng) {
        VolleyRequest.getInstance(getApplicationContext()).addToRequestQueue(getFoodRequest(lat,lng));
    }

    public JsonObjectRequest getFoodRequest(String lat, String lng){
        // Pass second argument as "null" for GET requests
        String zomatoUrl = "https://developers.zomato.com/api/v2.1/";
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, zomatoUrl+"geocode?lat="+lat+"&lon="+lng, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("response", "aww yeah");
                        try {
                            JSONArray restaurantArray = response.getJSONArray("nearby_restaurants");
                            cardPagerSize=0;
                            cardViews.setVisibility(View.VISIBLE);

                            for (int i = 0; i < restaurantArray.length(); i++) {

                                JSONObject restaurant = restaurantArray.getJSONObject(i).getJSONObject("restaurant");
                                final String restaurantName = restaurant.getString("name");
                                final Double restaurantLat = restaurant.getJSONObject("location").getDouble("latitude");
                                final Double restaurantLng = restaurant.getJSONObject("location").getDouble("longitude");
                                LatLng restaurantLatLng = new LatLng(restaurantLat, restaurantLng);

                                String streetName = restaurant.getJSONObject("location").getString("address");
                                //String city = restaurant.getJSONObject("location").getString("city");
                                //String zipcode = restaurant.getJSONObject("location").getString("zipcode");
                                final String locationAddress=streetName;

                                final String genre = restaurant.getString("cuisines");

                                final HashMap<String, Object> publicEventData = new HashMap<>();
                                publicEventData.put("Name", restaurantName);
                                publicEventData.put("Description", genre);
                                publicEventData.put("Location", locationAddress);
                                publicEventData.put("Category", "Food");
                                publicEventData.put("Lat", restaurantLat);
                                publicEventData.put("Lng", restaurantLng);
                                publicEventData.put("Place Name",restaurantName);
                                cardPagerSize++;

                                Bitmap bitmap = getBitmapFromVectorDrawable(getContext(),R.drawable.ic_map_marker);

                                final MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions.position(restaurantLatLng);
                                markerOptions.title(restaurantName);
                                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
                                mGoogleMap.addMarker(markerOptions)
                                        .setTag(publicEventData);
                                cardData.add(publicEventData);
                                mapCardAdapter.notifyDataSetChanged();
                                indicator.setViewPager(viewPager);



//                                mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
//                                    @Override
//                                    public void onInfoWindowClick(Marker marker) {
//
//                                        final HashMap<String,String> data=(HashMap<String, String>) marker.getTag();
//
//                                            cardTitle.setText(data.get("Name"));
//                                            cardDescription.setText(data.get("Description"));
//                                            cardLocation.setText(data.get("Location"));
//                                            cardView.setVisibility(View.VISIBLE);
//
//                                        fab.setOnClickListener(new View.OnClickListener() {
//                                            @Override
//                                            public void onClick(View v) {
//
//
//                                                Intent i = new Intent(getContext(), SelectEventDetailsActivity.class);
//                                                i.putExtra("Location", data.get("Location"));
//                                                i.putExtra("Category", data.get("Category"));
//                                                i.putExtra("Event Type", "Public");
//                                                i.putExtra("Food Details", data.get("Description"));
//                                                i.putExtra("Lat",restaurantLat);
//                                                i.putExtra("Lng",restaurantLng);
//                                                i.putExtra("Place Name",restaurantName);
//                                                getContext().startActivity(i);
//                                            }
//                                        });
//
//                                    }
//
//                                });

                                mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                    @Override
                                    public boolean onMarkerClick(Marker marker) {
                                        return false;
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
//        cardViews.setVisibility(View.VISIBLE);
        return req;
    }

}
