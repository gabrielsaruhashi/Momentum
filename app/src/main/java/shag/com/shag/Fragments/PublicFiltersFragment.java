package shag.com.shag.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import shag.com.shag.R;

/**
 * Created by samrabelachew on 7/21/17.
 */

public class PublicFiltersFragment extends Fragment {
    Button btn2;
    Button btn3;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.item_filters, container, false);

        btn2 = (Button) v.findViewById(R.id.btn_Test2);
        btn3= (Button) v.findViewById(R.id.btn_Test3);

        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //onStartFoodRequest(mLastLocation.getLatitude() + "", mLastLocation.getLongitude() + "");

            }
        });


        return v;
    }
}