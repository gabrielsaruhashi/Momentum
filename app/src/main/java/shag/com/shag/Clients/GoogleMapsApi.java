package shag.com.shag.Clients;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import static com.raizlabs.android.dbflow.config.FlowManager.getContext;

/**
 * Created by samrabelachew on 7/11/17.
 */

public class GoogleMapsApi {

    GoogleApiClient mGoogleApiClient;

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
}

