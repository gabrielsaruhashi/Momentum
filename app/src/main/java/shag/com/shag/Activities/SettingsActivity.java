package shag.com.shag.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import shag.com.shag.Clients.VolleyRequest;
import shag.com.shag.R;

public class SettingsActivity extends AppCompatActivity {
    Button btn;
    TextView tv;
    private RequestQueue mRequestQueue;
    PlaceAutocompleteFragment autocompleteFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        //mRequestQueue = Volley.newRequestQueue(this);
        mRequestQueue = VolleyRequest.getInstance(this.getApplicationContext()).
                getRequestQueue();

        btn = (Button) findViewById(R.id.button2);
        tv = (TextView) findViewById(R.id.tv2);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //onStartFoodRequest("47.668692","-122.387006");
            }
        });

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i("SettingsActivity", "Place: " + place.getName());
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("SettingsActivity", "An error occurred: " + status);
            }
        });

    }

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

                                String restaurantName = restaurant.getString("name");
                                Double restaurantLat = restaurant.getJSONObject("location").getDouble("latitude");
                                Double restaurantLng = restaurant.getJSONObject("location").getDouble("longitude");
                            }
                            //tv.setText(restaurantArray.toString());
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






    private void openSearch() {
        // Pass second argument as "null" for GET requests
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, "http://api.jambase.com/events?zipCode=75001&radius=50&startDate=2017-07-13T20%3A00%3A00&endDate=2017-07-14T20%3A00%3A00&page=0&api_key=6dhquzx3559xvcd2un49madm", null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String result = response.toString();
                        tv.setText(result);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
            }
        });

    /* Add your Requests to the RequestQueue to execute */
        mRequestQueue.add(req);


    }





}
