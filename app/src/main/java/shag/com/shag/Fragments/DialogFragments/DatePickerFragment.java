package shag.com.shag.Fragments.DialogFragments;


/**
 * Created by samrabelachew on 7/24/17.
 */


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.util.Calendar;


public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {


    public interface DatePickerFragmentListener {
        void onFinishDatePickerFragment(String date, int btn, int post);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        int button = getArguments().getInt("button");
        int position = getArguments().getInt("position");
        // Do something with the date chosen by the user
        String calendarDay= "" + month + "/" + day + "/" + year;

        DatePickerFragmentListener v = (DatePickerFragmentListener) getActivity();
        v.onFinishDatePickerFragment(calendarDay,button, position);
        dismiss();
    }
}