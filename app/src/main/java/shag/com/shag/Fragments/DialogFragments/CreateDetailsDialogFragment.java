package shag.com.shag.Fragments.DialogFragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

import shag.com.shag.Models.Event;
import shag.com.shag.R;

/**
 * Created by gabesaruhashi on 7/11/17.
 */

public class CreateDetailsDialogFragment extends DialogFragment  {
    private EditText etDescription;
    private Button btSend;
    private Button btCancel;
    private Button btInvite;
    private Button btLocation;
    private ImageButton btTime;
    private Event newEvent;
    private LinearLayout llExpireOptions;
    public final static int MILLISECONDS_IN_MINUTE = 60000;

    public CreateDetailsDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    // gets the category name from pick category fragment
    public static CreateDetailsDialogFragment newInstance(String category) {
        CreateDetailsDialogFragment frag = new CreateDetailsDialogFragment();
        Bundle args = new Bundle();
        args.putString("category", category);
        frag.setArguments(args);
        return frag;
    }

    // inflate the view
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_details, container);
    }

    // 1) defines the listener interface with a method passing back data result.
    public interface CreateDetailsDialogListener {
        void onFinishCreateDetailsDialog(Event newEvent);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // instantiate new event
        newEvent = new Event();

        // get views
        etDescription = (EditText) view.findViewById(R.id.etDescription);
        btSend = (Button) view.findViewById(R.id.btSend);
        llExpireOptions = (LinearLayout) view.findViewById(R.id.llExpireOptions);
        llExpireOptions.setVisibility(View.GONE);
        btTime = (ImageButton) view.findViewById(R.id.btTime);

        // Fetch arguments from bundle and set title
        String category = getArguments().getString("category");
        getDialog().setTitle(category);
        // Show soft keyboard automatically and request focus to field
        etDescription.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        // Setup a callback when the "submit" button is pressed
        btSend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // populate newEvent
                newEvent.description = etDescription.getText().toString();
                // send back to pick category dialog
                sendBackResult(newEvent);
            }
        });

        btTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (llExpireOptions.getVisibility() == View.GONE) {
                    llExpireOptions.setVisibility(View.VISIBLE);
                } else {
                    llExpireOptions.setVisibility(View.GONE);
                }
            }
        });

        TextView tv30 = (TextView) view.findViewById(R.id.tv30);
        setListenerForTime(tv30, 30);

        TextView tv1h = (TextView) view.findViewById(R.id.tv1h);
        setListenerForTime(tv1h, 60);

        TextView tv2h = (TextView) view.findViewById(R.id.tv2h);
        setListenerForTime(tv2h, 120);

        TextView tv3h = (TextView) view.findViewById(R.id.tv3h);
        setListenerForTime(tv3h, 180);

        TextView tv6h = (TextView) view.findViewById(R.id.tv6h);
        setListenerForTime(tv6h, 360);

        TextView tv12h = (TextView) view.findViewById(R.id.tv12h);
        setListenerForTime(tv12h, 720);
    }

    // Call this method to send the data back to the parent fragment
    public void sendBackResult(Event event) {
        // Notice the use of `getTargetFragment` which will be set when the dialog is displayed
        CreateDetailsDialogListener listener = (CreateDetailsDialogListener) getTargetFragment();

        listener.onFinishCreateDetailsDialog(event);
        dismiss();
    }

    public void setListenerForTime(TextView tv, final int minToDeadline) {
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Date newDate = new Date();
                newDate.setTime(newDate.getTime() + minToDeadline*MILLISECONDS_IN_MINUTE);
                Toast.makeText(getContext(), "Date: " + newDate.toString(), Toast.LENGTH_LONG).show();
                newEvent.setDeadline(newDate);
            }
        });
    }
}
