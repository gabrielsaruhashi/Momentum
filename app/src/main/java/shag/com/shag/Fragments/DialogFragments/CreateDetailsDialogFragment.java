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
    private Button btTime;

    public CreateDetailsDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static CreateDetailsDialogFragment newInstance(String category) {
        CreateDetailsDialogFragment frag = new CreateDetailsDialogFragment();
        Bundle args = new Bundle();
        args.putString("category", category);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_details, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        etDescription = (EditText) view.findViewById(R.id.etDescription);
        // Fetch arguments from bundle and set title
        String category = getArguments().getString("category");
        getDialog().setTitle(category);
        // Show soft keyboard automatically and request focus to field
        etDescription.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }


}
