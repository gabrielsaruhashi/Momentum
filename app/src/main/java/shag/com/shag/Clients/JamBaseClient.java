package shag.com.shag.Clients;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

/**
 * Created by samrabelachew on 7/13/17.
 */


public class JamBaseClient {
    private static JamBaseClient mInstance;
    private RequestQueue mRequestQueue;
    private static Context mCtx;

    private JamBaseClient(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();

    }

    public static synchronized JamBaseClient getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new JamBaseClient(context);
        }
        return mInstance;
    }

    public String openSearch() {
        final String[] result = new String[1];
        // Pass second argument as "null" for GET requests
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, "http://api.jambase.com/events?zipCode=75001&radius=50&startDate=2017-07-13T20%3A00%3A00&endDate=2017-07-14T20%3A00%3A00&page=0&api_key=6dhquzx3559xvcd2un49madm", null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                         result[0] = response.toString();
//                        tv.setText(result);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
            }
        });

    /* Add your Requests to the RequestQueue to execute */
        mRequestQueue.add(req);
        return result[0];
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }


}