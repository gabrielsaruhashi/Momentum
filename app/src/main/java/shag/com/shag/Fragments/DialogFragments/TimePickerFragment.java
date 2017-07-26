package shag.com.shag.Fragments.DialogFragments;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    public interface TimePickerFragmentListener {
        void onFinishTimePickerFragment(String time, int btn, int post);
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);


        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Do something with the time chosen by the user
        int button = getArguments().getInt("button");
        int position = getArguments().getInt("position");

        String amOrPm = "AM";
        String time = null;

        if(hourOfDay > 12) {

            hourOfDay=hourOfDay-12;
            amOrPm = "PM";

        }
        if(hourOfDay==0){
            hourOfDay=12;
            amOrPm="AM";
        }
        if (minute < 10) {
            String min = "0" + minute;
            time = hourOfDay + ":" + min+ " " + amOrPm;

        }
        else {
            time = hourOfDay + ":" + minute + " " + amOrPm;
        }


        TimePickerFragmentListener activity = (TimePickerFragmentListener) getActivity();
        activity.onFinishTimePickerFragment(time, button, position);
        dismiss();
    }
}
