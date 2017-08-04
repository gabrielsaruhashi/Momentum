package shag.com.shag.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import shag.com.shag.Models.CalendarEvent;
import shag.com.shag.R;

/**
 * Created by gabesaruhashi on 7/30/17.
 */

public class ConflictingEventsAdapter extends RecyclerView.Adapter<ConflictingEventsAdapter.ViewHolder>{
    ArrayList<CalendarEvent> calendarEvents;
    Context context;

    // pass in the calendarEvents array in the constructor
    public ConflictingEventsAdapter(ArrayList<CalendarEvent> calendarEvents) { this.calendarEvents = calendarEvents; };

    @Override
    public ConflictingEventsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // get context and inflate view
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // create the view using the item_feed layout
        View feedView = inflater.inflate(R.layout.item_conflicting_calendar_event, parent, false);

        // Return a new holder instance
        ConflictingEventsAdapter.ViewHolder viewHolder = new ConflictingEventsAdapter.ViewHolder(feedView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ConflictingEventsAdapter.ViewHolder holder, int position) {
        CalendarEvent calendarEvent = calendarEvents.get(position);
        holder.tvConflictName.setText(calendarEvent.getTitle());
        holder.tvDescription.setText(calendarEvent.getDescription());
        holder.tvLocation.setText(calendarEvent.getEventLocation());
        holder.tvEventTime.setText(calendarEvent.getdStart() + " - " + calendarEvent.getdEnd());
    }

    @Override
    public int getItemCount() {
        return calendarEvents.size();
    }

    // creates ViewHolder class
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        // Automatically finds each field by the specified ID
        @BindView(R.id.tvConflictName) TextView tvConflictName;
        @BindView(R.id.tvDescription) TextView tvDescription;
        @BindView(R.id.tvLocation) TextView tvLocation;
        @BindView(R.id.tvEventTime) TextView tvEventTime;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();

            if (position != RecyclerView.NO_POSITION) {
                CalendarEvent calendarEvent = calendarEvents.get(position);

            }
        }
    }



}
