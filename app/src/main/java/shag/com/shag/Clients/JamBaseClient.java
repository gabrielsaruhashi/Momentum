package shag.com.shag.Clients;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

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