package shag.com.shag.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import shag.com.shag.Clients.JamBaseClient;
import shag.com.shag.R;

public class SettingsActivity extends AppCompatActivity {
    Button btn;
    TextView tv;
    JamBaseClient jamBaseClient;
    private RequestQueue mRequestQueue;
    String resp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mRequestQueue = Volley.newRequestQueue(this);


        btn = (Button) findViewById(R.id.button2);
        tv = (TextView) findViewById(R.id.tv2);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSearch();
            }
        });


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
