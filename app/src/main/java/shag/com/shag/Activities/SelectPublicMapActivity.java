
package shag.com.shag.Activities;

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
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
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

import static android.provider.ContactsContract.CommonDataKinds.Website.URL;
import static com.raizlabs.android.dbflow.config.FlowManager.getContext;
import static shag.com.shag.R.id.map;

public class SelectPublicMapActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private RequestQueue mRequestQueue;
    CircleIndicator indicator;
    GoogleMap mGoogleMap;
    SupportMapFragment mapFrag;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    Geocoder geocoder;
    List<Address> addresses;
    MapCardAdapter mapCardAdapter;
    ViewPager viewPager;
    RelativeLayout cardViews;
    ArrayList<HashMap<String, Object>> cardData = new ArrayList<>();
    int cardPagerSize = 0;
    HashMap<String, MarkerOptions> markers;
    ArrayList<Marker> markerOptionsArrayList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_public_map);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        myToolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mRequestQueue = VolleyRequest.getInstance(this).getRequestQueue();

        mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(map);
        mapFrag.getMapAsync(this);


        cardViews = (RelativeLayout) findViewById(R.id.eventDetails);
        mapCardAdapter = new MapCardAdapter(getContext(), cardData);
        viewPager = (ViewPager) findViewById(R.id.pagerMapCards);
        viewPager.setAdapter(mapCardAdapter);
//        viewPager.setOffscreenPageLimit(3);
//        viewPager.setClipToPadding(false);
//        viewPager.setPageMargin(12);

        indicator = (CircleIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(viewPager);


        if (getIntent().getStringExtra("Event Type").equals("Public") && getIntent().getStringExtra("Category").equals("Music")) {
            try {
                mLastLocation=getIntent().getParcelableExtra("CurrentLoc");
                addresses = new ArrayList<>();
                geocoder = new Geocoder(getContext(), Locale.getDefault());
                addresses = geocoder.getFromLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude(), 1);
                String postalCode = addresses.get(0).getPostalCode();
                onStartMusicRequest(postalCode, "20");
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        if (getIntent().getStringExtra("Event Type").equals("Public") && getIntent().getStringExtra("Category").equals("Food")) {
            mLastLocation=getIntent().getParcelableExtra("CurrentLoc");
            onStartFoodRequest(mLastLocation.getLatitude() + "", mLastLocation.getLongitude() + "");

        }

    }

    public interface VolleyCallback{
        void onSuccess(String result);
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
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        boolean success = googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        this, R.raw.style_json));

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
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    @Override
    public void onPause() {
        super.onPause();

        //stop location updates when Activity is no longer active
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, SelectPublicMapActivity.this);
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
        Bitmap bitmap = getBitmapFromVectorDrawable(getContext(), R.drawable.ic_map_marker);
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
        mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);


        //move map camera
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

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
                                ActivityCompat.requestPermissions(SelectPublicMapActivity.this,
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
        VolleyRequest.getInstance(getApplicationContext()).addToRequestQueue(getMusicRequest(zipcode, radius));
    }

    public JsonObjectRequest getMusicRequest(String zipcode, String radius) {
        // Pass second argument as "null" for GET requests
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        String year = cal.get(Calendar.YEAR) + "";

        //Todo: take into account days under 10 (do they have a 0 before it for example Aug 3 = 03 or 3)?
        String day = cal.get(Calendar.DAY_OF_MONTH) + "";
        int endDay = (cal.get(Calendar.DAY_OF_MONTH) + 1);
        String finalDay = endDay + "";

        int month = cal.get(Calendar.MONTH) + 1;
        String formattedMonth;
        if (month > 9) {
            formattedMonth = "1" + month;
        } else {
            formattedMonth = "0" + month;

        }
        SimpleDateFormat localDateFormat = new SimpleDateFormat("HH:mm:ss");
        String time = localDateFormat.format(date.getTime());
        String jamBaseApi = "&api_key=6dhquzx3559xvcd2un49madm";
        String endTime = "T23%3A59%3A59&page=0";

        String jamBaseUrl = "http://api.jambase.com/events?";
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, jamBaseUrl + "zipCode=" + zipcode +
                "&radius=" + radius + "&startDate=+" + year + "-" + formattedMonth + "-" + day + "T" + time.substring(0, 2) + "%3A" + time.substring(3, 5) + "%3A" + time.substring(6)
                + "&endDate=+" + year + "-" + formattedMonth + "-" + finalDay + endTime + jamBaseApi, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        JSONArray eventArray = null;
                        try {
                            eventArray = response.getJSONArray("Events");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        cardPagerSize = 0;
                        cardViews.setVisibility(View.VISIBLE);
                        LatLngBounds.Builder b = new LatLngBounds.Builder();
                        for (int i = 0; i < eventArray.length(); i++) {
                            try {
                                if (eventArray.getJSONObject(i).getJSONObject("Venue").getDouble("Latitude") != 0.0) {

                                    final Double musicLat = eventArray.getJSONObject(i).getJSONObject("Venue").getDouble("Latitude");
                                    final Double musicLng = eventArray.getJSONObject(i).getJSONObject("Venue").getDouble("Longitude");
                                    final String concertPlace = eventArray.getJSONObject(i).getJSONObject("Venue").getString("Name");
                                    String streetName = eventArray.getJSONObject(i).getJSONObject("Venue").getString("Address");
                                    String city = eventArray.getJSONObject(i).getJSONObject("Venue").getString("City");
                                    String state = eventArray.getJSONObject(i).getJSONObject("Venue").getString("StateCode");
                                    String zipcode = eventArray.getJSONObject(i).getJSONObject("Venue").getString("ZipCode");


                                    final String locationAddress = streetName + " " + city + ", " + state + ", " + zipcode;

                                    JSONArray getArtistArray = null;
                                    try {
                                        getArtistArray = eventArray.getJSONObject(i).getJSONArray("Artists");
                                        String artist = getArtistArray.getJSONObject(0).getString("Name");
                                        LatLng musicLatLng = new LatLng(musicLat, musicLng);
                                        b.include(musicLatLng);

                                        final HashMap<String, Object> publicEventData = new HashMap<>();
                                        publicEventData.put("Name", artist);
                                        publicEventData.put("Description", "Music Concert");
                                        publicEventData.put("Location", locationAddress);
                                        publicEventData.put("Category", "Music");
                                        publicEventData.put("Lat", musicLat);
                                        publicEventData.put("Lng", musicLng);
                                        publicEventData.put("Place Name", concertPlace);
                                        VolleyRequest.getInstance(getApplicationContext())
                                                .addToRequestQueue(onFindPhotoReference(musicLat.toString(),
                                                        musicLng.toString(),concertPlace,"performance", publicEventData, i));

                                        cardPagerSize++;

                                        Bitmap bitmap = getBitmapFromVectorDrawable(getContext(), R.drawable.ic_map_marker);

                                        Marker newMarker = mGoogleMap.addMarker(new MarkerOptions().position(musicLatLng)
                                                .title(artist).icon(BitmapDescriptorFactory.fromBitmap(bitmap)));

//                                    MarkerOptions markerOptions = new MarkerOptions();
//                                    markerOptions.position(musicLatLng);
//                                    markerOptions.title(artist);
//                                    markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bitmap));

                                        newMarker.setTag(publicEventData);
                                        cardData.add(publicEventData);
                                        markerOptionsArrayList.add(newMarker);
                                        mapCardAdapter.notifyDataSetChanged();
                                        indicator.setViewPager(viewPager);


                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        LatLngBounds bounds = b.build();
                        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 5);
                        mGoogleMap.animateCamera(cu);

                        animateMarkers();


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




    //ZOMATO API
    public void onStartFoodRequest(String lat, String lng) {
        VolleyRequest.getInstance(getApplicationContext()).addToRequestQueue(getFoodRequest(lat, lng));
    }

    public JsonObjectRequest getFoodRequest(String lat, String lng) {
        // Pass second argument as "null" for GET requests
        String zomatoUrl = "https://developers.zomato.com/api/v2.1/";

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, zomatoUrl + "geocode?lat=" + lat + "&lon=" + lng, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("response", "aww yeah");
                        try {
                            JSONArray restaurantArray = response.getJSONArray("nearby_restaurants");
                            cardPagerSize = 0;
                            cardViews.setVisibility(View.VISIBLE);
                            LatLngBounds.Builder b = new LatLngBounds.Builder();

                            for (int i = 0; i < restaurantArray.length(); i++) {

                                JSONObject restaurant = restaurantArray.getJSONObject(i).getJSONObject("restaurant");
                                final String restaurantName = restaurant.getString("name");
                                final Double restaurantLat = restaurant.getJSONObject("location").getDouble("latitude");
                                final Double restaurantLng = restaurant.getJSONObject("location").getDouble("longitude");
                                LatLng restaurantLatLng = new LatLng(restaurantLat, restaurantLng);
                                b.include(restaurantLatLng);
                                String streetName = restaurant.getJSONObject("location").getString("address");
                                //String city = restaurant.getJSONObject("location").getString("city");
                                //String zipcode = restaurant.getJSONObject("location").getString("zipcode");
                                final String locationAddress = streetName;

                                final String genre = restaurant.getString("cuisines");

                                final HashMap<String, Object> publicEventData = new HashMap<>();
                                publicEventData.put("Name", restaurantName);
                                publicEventData.put("Description", genre);
                                publicEventData.put("Location", locationAddress);
                                publicEventData.put("Category", "Food");
                                publicEventData.put("Lat", restaurantLat);
                                publicEventData.put("Lng", restaurantLng);
                                publicEventData.put("Place Name", restaurantName);

                                VolleyRequest.getInstance(getApplicationContext())
                                        .addToRequestQueue(onFindPhotoReference(restaurantLat.toString(),
                                                restaurantLng.toString(),restaurantName,"restaurant", publicEventData, i));


                                cardPagerSize++;

                                Bitmap bitmap = getBitmapFromVectorDrawable(getContext(), R.drawable.ic_map_marker);

                                Marker newMarker = mGoogleMap.addMarker(new MarkerOptions()
                                        .position(restaurantLatLng)
                                        .title(restaurantName)
                                        .icon(BitmapDescriptorFactory
                                                .fromBitmap(bitmap)));

//                                    MarkerOptions markerOptions = new MarkerOptions();
//                                    markerOptions.position(musicLatLng);
//                                    markerOptions.title(artist);
//                                    markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bitmap));

                                newMarker.setTag(publicEventData);


//                                final MarkerOptions markerOptions = new MarkerOptions();
//                                markerOptions.position(restaurantLatLng);
//                                markerOptions.title(restaurantName);
//                                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
//                                mGoogleMap.addMarker(markerOptions)
//                                        .setTag(publicEventData);
                                cardData.add(publicEventData);
                                markerOptionsArrayList.add(newMarker);
                                mapCardAdapter.notifyDataSetChanged();
                                indicator.setViewPager(viewPager);


                                mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                    @Override
                                    public boolean onMarkerClick(Marker marker) {
                                        return false;
                                    }
                                });
                            }
                            LatLngBounds bounds = b.build();
                            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 5);
                            mGoogleMap.animateCamera(cu);
                            animateMarkers();

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

    //GOOGLE PICTURE API
    public JsonObjectRequest onFindPhotoReference(String lat, String lng, String placeName,
                                                  String placeType, final HashMap<String,Object> data, final int position){

        final String photo = "";
        String googleUrl = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
        String location = "location=" + lat + "," + lng;
        String radius = "&radius=500";
        String type = "&type=" + placeType;
        String keyword = "&keyword=" + placeName.replaceAll(" ", "");
        String apikey = "&key=AIzaSyD5ty8DSE8Irio8xdCvCQMltWpuVDioHTI";
        final ArrayList<Object> photoInfo = new ArrayList<>();

        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        JsonObjectRequest request = new JsonObjectRequest(URL, new JSONObject(), future, future);
        mRequestQueue.add(request);


        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, googleUrl+location+radius+type+keyword+apikey, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("response", "aww yeah");
                        try {
                            JSONArray placeArray = response.getJSONArray("results");
                            JSONObject firstPlace = placeArray.getJSONObject(0);
                            JSONObject firstPhoto = firstPlace.getJSONArray("photos").getJSONObject(0);
                            int height=firstPhoto.getInt("height");
                            int width=firstPhoto.getInt("width");
                            String photoReference=firstPhoto.getString("photo_reference");
                            photoInfo.add(height);
                            photoInfo.add(width);
                            photoInfo.add(photoReference);

                            String googleUrl = "https://maps.googleapis.com/maps/api/place/photo?";
                            String maxHeight = "maxheight="+photoInfo.get(0);
                            String photoRef = "&photoreference="+photoInfo.get(2);
                            String apikey = "&key=AIzaSyD5ty8DSE8Irio8xdCvCQMltWpuVDioHTI";
                            String photoUrl = googleUrl+maxHeight+photoRef+apikey;
                            //add new data to hashmap
                            data.put("Photo", photoUrl);

                            //build a new list
                            int x =position;
                            if (position<cardData.size()) {
                                cardData.remove(position);
                                ArrayList<HashMap<String, Object>> newList = new ArrayList<>();
                                newList.addAll(cardData);
                                newList.add(position, data);

                                cardData.clear();
                                cardData.addAll(newList);

                                mapCardAdapter.notifyDataSetChanged();
                                //mapCardAdapter.notifyI();
                            }

                            //String photo =findFirstPhoto(photoInfo);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.e("response", volleyError.toString());
                    }
                });
        return req;

    }

    private String findFirstPhoto(ArrayList<Object> photoInfo) {
        String googleUrl = "https://maps.googleapis.com/maps/api/place/photo?";
        String maxHeight = "maxheight="+photoInfo.get(0);
        String maxWidth = "maxwidth="+photoInfo.get(1);
        String photoReference = "&photoreference="+photoInfo.get(2);
        String apikey = "&key=AIzaSyD5ty8DSE8Irio8xdCvCQMltWpuVDioHTI";
        String photoUrl = googleUrl+maxHeight+maxWidth+photoReference+apikey;

        return photoUrl;

    }

    public void animateMarkers() {
        Marker one = markerOptionsArrayList.get(viewPager.getCurrentItem());
        one.showInfoWindow();
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(one.getPosition()));

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                if (position!=cardPagerSize) {
//                    position+=1;
//                }
//                else{
//                    position=0;
//                }
                Marker marker = markerOptionsArrayList.get(position);
                marker.getTag();
                marker.showInfoWindow();
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }
}