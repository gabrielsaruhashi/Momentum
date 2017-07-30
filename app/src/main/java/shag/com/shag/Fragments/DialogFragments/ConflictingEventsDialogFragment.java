package shag.com.shag.Fragments.DialogFragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import shag.com.shag.Adapters.ConflictingEventsAdapter;
import shag.com.shag.Models.CalendarEvent;
import shag.com.shag.Other.DividerItemDecorator;
import shag.com.shag.R;

/**
 * Created by gabesaruhashi on 7/30/17.
 */

public class ConflictingEventsDialogFragment extends DialogFragment {

    private RecyclerView rvConflictingEvents;
    private ConflictingEventsAdapter adapter;

    public ConflictingEventsDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static ConflictingEventsDialogFragment newInstance(ArrayList<CalendarEvent> calendarEvents) {
        ConflictingEventsDialogFragment frag = new ConflictingEventsDialogFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList("conflicting_events", calendarEvents);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_conflicting_calendar_events, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        rvConflictingEvents = (RecyclerView) view.findViewById(R.id.rvConflictingEvents);
        // Fetch arguments from bundle and set title
        ArrayList<CalendarEvent> calendarEvents = getArguments().getParcelableArrayList("conflicting_events");
        // instantiate adapter
        adapter = new ConflictingEventsAdapter(calendarEvents);

        // set events
        rvConflictingEvents.setAdapter(adapter);
        // Set layout manager to position the items
        rvConflictingEvents.setLayoutManager(new LinearLayoutManager(getContext()));

        // add line divider decorator
        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecorator(rvConflictingEvents.getContext(), DividerItemDecorator.VERTICAL_LIST);
        rvConflictingEvents.addItemDecoration(itemDecoration);


    }


}