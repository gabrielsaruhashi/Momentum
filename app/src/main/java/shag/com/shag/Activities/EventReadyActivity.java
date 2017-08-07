package shag.com.shag.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseLiveQueryClient;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SubscriptionHandling;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import shag.com.shag.Clients.VolleyRequest;
import shag.com.shag.Models.Event;
import shag.com.shag.Models.Message;
import shag.com.shag.Other.DataParser;
import shag.com.shag.Other.ParseApplication;
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
    String timeOfEventAsString;
    Geocoder geocoder;
    LatLngBounds.Builder bounds;
    String baseUrl = "https://maps.googleapis.com/maps/api/directions/json?";
    Polyline polylineFinal;
    TextView tvDuration;
    TextView tvDepartureTime;
    TextView tvSummary;
    TextView tvDestination;
    TextView tvTime;
    RelativeLayout rlDirectionsInfo;
    String launchMapsUrl = "https://www.google.com/maps/dir/?api=1";
    String walkTime;
    String transitTime;
    String driveTime;
    String bikeTime;
    String eventId;
    Event parseEvent;
    String currentselected;
    boolean firstOpen;
    List<Marker> markerList = new ArrayList<Marker>();
    ArrayList<String> friendNames;
    Button btSendEta;
    CardView cvInstructionsInfo;
    LinearLayout llTransportOptions;
    String origin = "";
    String destination = "";
    String ETA;
    boolean viewedDirections;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_ready1);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        firstOpen = true;

        cvInstructionsInfo = (CardView) findViewById(R.id.cvInstructionsInfo);
        tvDestination = (TextView) findViewById(R.id.tvFinalPlace);
        tvTime = (TextView) findViewById(R.id.tvFinalTime);
        tvDuration = (TextView) findViewById(R.id.tvDurationInfo);
        llTransportOptions = (LinearLayout) findViewById(R.id.llTransportOptions);

        eventId = getIntent().getStringExtra("eventId");

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
        try {
            parseEvent = (Event) query.get(eventId);
            mDestination = new LatLng(parseEvent.getLatitude(), parseEvent.getLongitude());
            timeOfEvent = parseEvent.getTimeOfEvent();
            timeOfEventAsString = parseEvent.getEventTimeString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        viewedDirections = false;

        activity = this;
        mRequestQueue = VolleyRequest.getInstance(this).getRequestQueue();
        mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFrag.getMapAsync(this);
        geocoder = new Geocoder(getContext(), Locale.getDefault());
        bounds = new LatLngBounds.Builder();

        tvDestination.setText(parseEvent.getLocation());
        tvTime.setText(timeOfEventAsString);

        friendNames = new ArrayList<>();
        HashMap<String, ParseGeoPoint> participantsLocations = (HashMap) parseEvent.getParticipantsLocations();
        for (String id : participantsLocations.keySet()) {
            if (!id.equals(ParseApplication.getCurrentUser().getObjectId())) {
                ParseQuery<ParseUser> queryForUser = ParseUser.getQuery();
                try {
                    ParseUser friend = queryForUser.get(id);
                    friendNames.add(friend.getString("name"));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

        ParseLiveQueryClient parseLiveQueryClient = ParseLiveQueryClient.Factory.getClient();
        ParseQuery<Event> parseQuery = ParseQuery.getQuery(Event.class);
        SubscriptionHandling<Event> subscriptionHandling = parseLiveQueryClient.subscribe(parseQuery);
        subscriptionHandling.handleEvent(SubscriptionHandling.Event.UPDATE, new
                SubscriptionHandling.HandleEventCallback<Event>() {
                    @Override
                    public void onEvent(ParseQuery<Event> query, Event object) {
                        if (object.getEventId().equals(eventId)) {
                            parseEvent = object;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    putFriendsOnMap();
                                }
                            });

                        }
                    }
                });
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

        //Place destination marker
        Bitmap bitmap = getBitmapFromVectorDrawable(getContext(), R.drawable.ic_map_marker);
        MarkerOptions destinationMarkerOptions = new MarkerOptions();
        destinationMarkerOptions.position(mDestination);
        destinationMarkerOptions.title(parseEvent.getLocation());
        destinationMarkerOptions.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
        mGoogleMap.addMarker(destinationMarkerOptions);
    }

    //DIFFERENT STATES OF MAP
    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
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
        LatLng latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());

        //put location in db and get friends on map if event details have been defined
        //put current user location in db
        HashMap<String, ParseGeoPoint> participantsLocations = (HashMap) parseEvent.getParticipantsLocations();
        participantsLocations.put(ParseApplication.getCurrentUser().getObjectId(), new ParseGeoPoint(latLng.latitude, latLng.longitude));
        parseEvent.setParticipantsLocations(participantsLocations);

        parseEvent.saveInBackground();
        //putFriendsOnMap();


        //include both markers in view
        if (firstOpen) {
            firstOpen = false;
            bounds.include(mDestination);
            bounds.include(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 300)); //TODO: 75 might not work for diff distances

//            getDirections("walking", false);
//            getDirections("driving", false);
//            getDirections("driving", false);
//            getDirections("bicycling", false);
//            getDirections("transit", false);
        }
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

    public void getWalkingDirections(View view) {
        currentselected = "walking";
        deselectOtherViews();
        ImageView bt = (ImageView) view;
        bt.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.black), PorterDuff.Mode.MULTIPLY);
        getDirections("walking", true);
    }

    public void getDrivingDirections(View view) {
        currentselected = "driving";
        deselectOtherViews();
        ImageView bt = (ImageView) view;
        bt.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.black), PorterDuff.Mode.MULTIPLY);
        getDirections("driving", true);
    }

    public void getTransitDirections(View view) {
        currentselected = "transit";
        deselectOtherViews();
        ImageView bt = (ImageView) view;
        bt.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.black), PorterDuff.Mode.MULTIPLY);
        getDirections("transit", true);
    }

    public void getBikingDirections(View view) {
        deselectOtherViews();
        currentselected = "biking";
        ImageView bt = (ImageView) view;
        bt.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.black), PorterDuff.Mode.MULTIPLY);
        getDirections("bicycling", true);
    }

    public void getDirections(final String mode, final boolean showOnMap) {

        try {
            List<Address> addresses = geocoder.getFromLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude(), 1);
            List<Address> addresses2 = geocoder.getFromLocation(mDestination.latitude, mDestination.longitude, 1);

            if (addresses.size() > 0 && addresses2.size()>0) {
                Address currentLocation = addresses.get(0);
                origin = currentLocation.getAddressLine(0);
                origin = origin.replace(" ", "+");

                Address destinationLocation = geocoder.getFromLocation(mDestination.latitude, mDestination.longitude, 1).get(0);
                destination = destinationLocation.getAddressLine(0);
                destination = destination.replace(" ", "+");
                plotDirections(origin,destination, mode, showOnMap);

            } else {
                //do the API call
                //set origin = a string address
                //set destination = a string address

                final String googleUrl = "https://maps.googleapis.com/maps/api/geocode/json?";
                String latlngOrigin = "latlng="+mLastLocation.getLatitude()+","+mLastLocation.getLongitude();
                final String latlngDestination = "latlng="+mDestination.latitude+","+mDestination.longitude;
                final String api = "&key=AIzaSyD5ty8DSE8Irio8xdCvCQMltWpuVDioHTI";





                JsonObjectRequest reqOrigin = new JsonObjectRequest(Request.Method.GET, googleUrl+latlngOrigin+api,
                        null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONArray resultArray = null;
                        JSONObject firstAddress = null;
                        String formattedAddress = "";
                        try {
                            resultArray = response.getJSONArray("results");
                            firstAddress = resultArray.getJSONObject(0);
                            formattedAddress = firstAddress.getString("formatted_address");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        formattedAddress = formattedAddress.replace(" ", "+");
                        origin=formattedAddress;

                        JsonObjectRequest reqDestination = new JsonObjectRequest(Request.Method.GET, googleUrl+latlngDestination+api,
                                null, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                JSONArray resultArray = null;
                                JSONObject firstAddress = null;
                                String formattedAddress = "";

                                try {
                                    resultArray = response.getJSONArray("results");
                                    firstAddress = resultArray.getJSONObject(0);
                                    formattedAddress = firstAddress.getString("formatted_address");

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                formattedAddress = formattedAddress.replace(" ", "+");
                                destination=formattedAddress;
                                plotDirections(origin,destination, mode, showOnMap);




                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                VolleyLog.e("Error: ", error.getMessage());
                            }
                        });
                        VolleyRequest.getInstance(getApplicationContext()).addToRequestQueue(reqDestination);




                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.e("Error: ", error.getMessage());
                    }
                });
                VolleyRequest.getInstance(getApplicationContext()).addToRequestQueue(reqOrigin);








            }
        } catch (IOException e) {
            e.printStackTrace();
        }


//        baseUrl += "&origin=" + origin;
//        launchMapsUrl += "&origin=" + origin;
//        baseUrl += "&destination=" + destination;
//        launchMapsUrl += "&destination=" + destination;
//        baseUrl += "&arrival_time=" + (timeOfEvent.getTime() / 1000);
//        launchMapsUrl += "&arrival_time=" + (timeOfEvent.getTime() / 1000);
//        //END TODO
//
//        String url = baseUrl + "&mode=" + mode;
//        url += "&key=" + "AIzaSyD5ty8DSE8Irio8xdCvCQMltWpuVDioHTI";
//        launchMapsUrl += "&travelmode=" + mode;
//        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        String instructions = response.toString();
//                        populateInfoFromJson(response, mode);
//                        if (showOnMap) {
//                            FetchUrl fetchUrl = new FetchUrl();
//                            fetchUrl.execute(instructions);
//                            rlDirectionsInfo.setVisibility(View.VISIBLE);
//                        }
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                VolleyLog.e("Error: ", error.getMessage());
//            }
//        });
//
//        VolleyRequest.getInstance(getApplicationContext()).addToRequestQueue(request);
    }

    private void plotDirections(String origin, String destination, final String mode, final boolean showOnMap) {
        baseUrl += "&origin=" + origin;
        launchMapsUrl += "&origin=" + origin;
        baseUrl += "&destination=" + destination;
        launchMapsUrl += "&destination=" + destination;
        baseUrl += "&arrival_time=" + (timeOfEvent.getTime() / 1000);
        launchMapsUrl += "&arrival_time=" + (timeOfEvent.getTime() / 1000);
        //END TODO

        String url = baseUrl + "&mode=" + mode;
        url += "&key=" + "AIzaSyD5ty8DSE8Irio8xdCvCQMltWpuVDioHTI";
        launchMapsUrl += "&travelmode=" + mode;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(final JSONObject response) {
                        final String instructions = response.toString();
                        if (showOnMap) {
                            final FetchUrl fetchUrl = new FetchUrl();
                            //rlDirectionsInfo.setVisibility(View.VISIBLE)
                            if (viewedDirections) {
                                Animation animation = new TranslateAnimation(0, 0, 0, 400);
                                animation.setDuration(500);
                                animation.setFillAfter(true);
                                animation.setAnimationListener(new Animation.AnimationListener() {
                                    @Override
                                    public void onAnimationStart(Animation animation) {
                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation) {
                                        fetchUrl.execute(instructions);
                                        populateInfoFromJson(response, mode);
                                        Animation animation2 = new TranslateAnimation(0, 0, 400, 0);
                                        animation2.setDuration(750);
                                        animation2.setFillAfter(true);
                                        //llTransportOptions.startAnimation(animation);
                                        cvInstructionsInfo.startAnimation(animation2);
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) {
                                    }
                                });
                                //llTransportOptions.startAnimation(animation);
                                cvInstructionsInfo.startAnimation(animation);
                            } else {
                                populateInfoFromJson(response, mode);
                                fetchUrl.execute(instructions);
                                Animation animation = new TranslateAnimation(0, 0, 400, 0);
                                animation.setDuration(750);
                                animation.setFillAfter(true);
                                //llTransportOptions.startAnimation(animation);
                                cvInstructionsInfo.startAnimation(animation);
                                cvInstructionsInfo.setVisibility(View.VISIBLE);
                                viewedDirections = true;
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
            }
        });

        VolleyRequest.getInstance(getApplicationContext()).addToRequestQueue(request);
    }

    private JsonObjectRequest getAddress(String address) {
        final String[] formattedAddress = {null};

        JsonObjectRequest newReq = new JsonObjectRequest(Request.Method.GET, address, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONArray resultArray = null;
                JSONObject firstAddress = null;
                try {
                    resultArray = response.getJSONArray("results");
                    firstAddress = resultArray.getJSONObject(0);
                    formattedAddress[0] = firstAddress.getString("formatted_address");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                formattedAddress[0] = formattedAddress[0].replace(" ", "+");



            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
            }
        });

        return newReq;
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                Log.d("ParserTask", jsonData[0].toString());
                DataParser parser = new DataParser();
                Log.d("ParserTask", parser.toString());

                // Starts parsing data
                routes = parser.parse(jObject);
                Log.d("ParserTask", "Executing routes");
                Log.d("ParserTask", routes.toString());

            } catch (Exception e) {
                Log.d("ParserTask", e.toString());
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(getColor(R.color.colorAccent));

                Log.d("onPostExecute", "onPostExecute lineoptions decoded");

            }

            // Drawing polyline in the Google Map for the i-th route
            if (lineOptions != null) {
                if (polylineFinal != null) {
                    polylineFinal.remove();
                }
                polylineFinal = mGoogleMap.addPolyline(lineOptions);
            } else {
                Log.d("onPostExecute", "without Polylines drawn");
            }
        }
    }

    // Fetches data from url passed
    private class FetchUrl extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... instructions) {

            // For storing data from web service
            String data = "";

            try {
                //TODO: reorganize this code so it's faster!
                // Fetching the data from web service
                data = instructions[0];
                Log.d("Background Task data", data.toString());
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();
            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);

        }
    }

    public void populateInfoFromJson(JSONObject json, String mode) {
        try {
            JSONArray array = json.getJSONArray("routes");
            JSONObject object = array.getJSONObject(0);
            String summary = object.getString("summary");
            JSONArray legs = object.getJSONArray("legs");
            JSONObject step = legs.getJSONObject(0);
            String duration = step.getJSONObject("duration").getString("text");
            ETA = duration;
            if (summary.equals("")) {
                summary = "Public Transportation";
            }

//            if (mode.equals("walking")) {
//                walkTime = duration;
//                ImageView bt = (ImageView) findViewById(R.id.ibWalk);
//                bt.setText(walkTime);
//            } else if (mode.equals("driving")) {
//                driveTime = duration;
//                ImageView bt = (ImageView) findViewById(R.id.ibDrive);
//                bt.setText(driveTime);
//            } else if (mode.equals("transit")) {
//                transitTime = duration;
//                ImageView bt = (ImageView) findViewById(R.id.ibTransit);
//                bt.setText(transitTime);
//            } else {
//                bikeTime = duration;
//                Button bt = (Button) findViewById(R.id.ibBike);
//                bt.setText(bikeTime);
//            }

            //rlDirectionsInfo.setVisibility(View.VISIBLE);
            //tvSummary.setText(summary);
            tvDuration.setText("Arrival in " + duration);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        //tvSummary.setText();
    }

    public void sendAnEta(View view) {
        Message m = new Message();
        m.setSenderId("InuSHuTqkn");
        m.setBody(ParseApplication.getCurrentUser().get("name") + " is on the way! ETA = " + ETA);
        m.setEventId(eventId);
        m.setSenderName("Shaggy");
        try {
            m.save();
            HashMap<String, String> payload = new HashMap<>();
            payload.put("customData", ParseApplication.getCurrentUser().get("name") + " is on the way!");
            payload.put("title", "New message in channel");
            payload.put("channelID", eventId);
            payload.put("senderID", "InuSHuTqkn");
            payload.put("token", ""); //not being used rn
            ParseCloud.callFunctionInBackground("pushChannelTest", payload);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void openGoogle(View view) {
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(launchMapsUrl));
        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
        startActivity(intent);
    }

    public void putFriendsOnMap() {
        for (Marker marker : markerList) {
            marker.remove();
        }
        markerList.clear();

        HashMap<String, ParseGeoPoint> participantsLocations = (HashMap) parseEvent.getParticipantsLocations();
        int i = 0;
        for (String id : participantsLocations.keySet()) {
            if (!id.equals(ParseApplication.getCurrentUser().getObjectId())) {
                ParseGeoPoint point = participantsLocations.get(id);
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.title(friendNames.get(i));
                markerOptions.position(new LatLng(point.getLatitude(), point.getLongitude()));
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                Marker marker = mGoogleMap.addMarker(markerOptions);
                markerList.add(marker);
                i++;
            }
        }
    }

    public void deselectOtherViews() {
        ImageView btWalk = (ImageView) findViewById(R.id.ibWalk);
        btWalk.getBackground().clearColorFilter();
        ImageView btDrive = (ImageView) findViewById(R.id.ibDrive);
        btDrive.getBackground().clearColorFilter();
        ImageView btBike = (ImageView) findViewById(R.id.ibBike);
        btBike.getBackground().clearColorFilter();
        ImageView btTransit = (ImageView) findViewById(R.id.ibTransit);
        btTransit.getBackground().clearColorFilter();

        //btSendEta.setEnabled(true);

    }
}

