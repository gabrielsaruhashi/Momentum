package shag.com.shag.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.google.android.gms.location.FusedLocationProviderClient;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import shag.com.shag.Clients.VolleyRequest;
import shag.com.shag.Models.Event;
import shag.com.shag.Models.Memory;
import shag.com.shag.R;

import static com.facebook.FacebookSdk.getApplicationContext;
import static shag.com.shag.R.id.map;


public class MapFragment extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private RequestQueue mRequestQueue;

    GoogleMap mGoogleMap;
    SupportMapFragment mapFrag;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    ParseUser currentUser;
    Marker mCurrLocationMarker;
    private FusedLocationProviderClient mFusedLocationClient;
    Location currentLocation;

    // inflation happens inside onCreateView
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // inflate the layout
        View v = inflater.inflate(R.layout.fragment_map, container, false);

        currentUser = ParseUser.getCurrentUser();

        mapFrag = (SupportMapFragment) getChildFragmentManager().findFragmentById(map);
        mapFrag.getMapAsync(this);

        populateMap();


        return v;
    }

    private void populateMap() {
        //query for all events of a user
        ParseQuery<Memory> query = ParseQuery.getQuery("Memory");
        //include objects in event
        query.whereEqualTo("participants_id", currentUser.getObjectId());
        //limit query to all memories a user is a participant in
        //query.whereContainedIn("participants_id", currentUser.getObjectId());
        query.findInBackground(new FindCallback<Memory>() {
            @Override
            public void done(List<Memory> memoriesList, ParseException e) {
                if (e == null) {
                    Log.d("memory", "Retrieved " + memoriesList.size() + " scores");
                    //when Memories are retrieved, begin finding events tied to them
                    findAssociatedEvent(memoriesList);
                } else {
                    Log.d("memory", "Error: " + e.getMessage());
                }
            }
        });
        //obtain their location and first photo in memories
        //iterate through this and create markers

        //TESTING
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            currentLocation = location;

                            mGoogleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                                @Override
                                public View getInfoWindow(Marker marker) {
                                    return null;
                                }

                                @Override
                                public View getInfoContents(Marker marker) {
                                    // Getting view from the layout file info_window_layout

                                    View v = getActivity().getLayoutInflater().inflate(R.layout.item_info_window_layout, null);

                                    // Getting the position from the marker
                                    LatLng latLng = marker.getPosition();

                                    ImageView ivPhoto = (ImageView) v.findViewById(R.id.ivMemory) ;
                                    ivPhoto.setBackgroundResource(R.drawable.ic_camera);
                                    //Glide.with(getActivity()).load(marker.getTag()).centerCrop().into(ivPhoto);

                                    // Getting reference to the TextView to set longitude
                                    TextView tvLng = (TextView) v.findViewById(R.id.tvDate);

                                    // Setting the longitude
                                    tvLng.setText("Longitude:"+ latLng.longitude);

                                    // Returning the view containing InfoWindow contents
                                    return v;

                                }


                            });
                            //Place current location marker
                            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                            MarkerOptions markerOptions = new MarkerOptions();
                            markerOptions.position(latLng);
                            //markerOptions.title("Current Position");
                            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                            Marker marker = mGoogleMap.addMarker(markerOptions);
                            String url = "http://i.imgur.com/Gwb6TqH.png";
                            marker.setTag(url);
                            marker.showInfoWindow();


                            // mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);

                        }
                    }
                });






        //move map camera
        //mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
    }
    private Bitmap bitmapConverterFromParseFile(ParseFile parseFile) {

        try {
            byte[] bitmapdata = parseFile.getData();
            Bitmap bm = BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length);
            return bm;
        } catch (ParseException e) {
            e.getMessage();
        }
        return null;
    }
    private void findAssociatedEvent(List<Memory> memoriesList) {
        mGoogleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                // Getting view from the layout file info_window_layout

                View v = getActivity().getLayoutInflater().inflate(R.layout.item_info_window_layout, null);

                // Getting the position from the marker
                LatLng latLng = marker.getPosition();

                ImageView ivPhoto = (ImageView) v.findViewById(R.id.ivMemory) ;
                ivPhoto.setBackgroundResource(R.drawable.ic_camera);
                //Glide.with(getActivity()).load(marker.getTag()).centerCrop().into(ivPhoto);

                // Getting reference to the TextView to set longitude
                TextView tvLng = (TextView) v.findViewById(R.id.tvDate);

                // Setting the longitude
                tvLng.setText("Longitude:"+ latLng.longitude);

                // Returning the view containing InfoWindow contents
                return v;

            }


        });
        for(final Memory memory : memoriesList){
            String associatedEvent = memory.getString("event_id");
            //query for associated event to get text info (time)
            ParseQuery<Event> eventQuery = ParseQuery.getQuery("Event");
            eventQuery.getInBackground(associatedEvent, new GetCallback<Event>() {
                @Override
                public void done(Event object, ParseException e) {
                    if (e == null) {
                        Double lat = object.getDouble("latitude");
                        Double lng = object.getDouble("longitude");
                        Date timeOfEvent = object.getDate("event_time");
                        String readableTime = new SimpleDateFormat("dd/MM/yyyy").format(timeOfEvent);
                        ArrayList<ParseFile> pictures = (ArrayList<ParseFile>) memory.get("pictures_parse_files");
                        //if we made it this far, let's create a marker
                        if (pictures!=null) {

                            LatLng latLng = new LatLng(lat, lng);
                            MarkerOptions markerOptions = new MarkerOptions();
                            markerOptions.position(latLng);
                            //markerOptions.title("Current Position");
                            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                            Marker marker = mGoogleMap.addMarker(markerOptions);

                            final Bitmap bm = bitmapConverterFromParseFile(pictures.get(0));
                            if (bm != null) {
                                marker.setTag(bm);
                            }
                            marker.showInfoWindow();



                        }

                    } else {
                        // something went wrong
                        e.printStackTrace();
                    }
                }
            });
        }
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


        //move map camera
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

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
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create();
                alertDialog.show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
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
        String jamBaseApi = "6dhquzx3559xvcd2un49madm";


        String jamBaseUrl = "http://api.jambase.com/events?";
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, jamBaseUrl + "zipCode=" + zipcode +
                "&radius=" + radius + "&startDate=+" + year + "-" + formattedMonth + "-" + day + "T" + time.substring(0, 2) + "%3A" + time.substring(3, 5) + "%3A" + time.substring(6)
                + "&endDate=+" + year + "-" + formattedMonth + "-" + finalDay + "T23%3A59%3A59&page=0&api_key=" + jamBaseApi, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        JSONArray eventArray = null;
                        try {
                            eventArray = response.getJSONArray("Events");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
//                        cardPagerSize = 0;
//                        cardViews.setVisibility(View.VISIBLE);
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
                                        //cardPagerSize++;

                                        Bitmap bitmap = getBitmapFromVectorDrawable(getContext(), R.drawable.ic_map_marker);

                                        Marker newMarker = mGoogleMap.addMarker(new MarkerOptions().position(musicLatLng)
                                                .title(artist).icon(BitmapDescriptorFactory.fromBitmap(bitmap)));

//                                    MarkerOptions markerOptions = new MarkerOptions();
//                                    markerOptions.position(musicLatLng);
//                                    markerOptions.title(artist);
//                                    markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bitmap));

//                                        newMarker.setTag(publicEventData);
//                                        cardData.add(publicEventData);
//                                        markerOptionsArrayList.add(newMarker);
//                                        mapCardAdapter.notifyDataSetChanged();
//                                        indicator.setViewPager(viewPager);


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

                        // animateMarkers();


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
//                            cardPagerSize = 0;
//                            cardViews.setVisibility(View.VISIBLE);
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
                                // cardPagerSize++;

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
//                                cardData.add(publicEventData);
//                                markerOptionsArrayList.add(newMarker);
//                                mapCardAdapter.notifyDataSetChanged();
//                                indicator.setViewPager(viewPager);


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
                            //animateMarkers();

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

//    public void animateMarkers() {
//        Marker one = markerOptionsArrayList.get(viewPager.getCurrentItem());
//        one.showInfoWindow();
//        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(one.getPosition()));
//
//        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                Marker oldMarker = markerOptionsArrayList.get(position);
//                oldMarker.showInfoWindow();
//                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(oldMarker.getPosition()));
//
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//
//            }
//        });
//
//
//    }
}
