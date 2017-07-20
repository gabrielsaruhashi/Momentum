package shag.com.shag.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.parse.GetCallback;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import shag.com.shag.Models.Event;
import shag.com.shag.R;

import static android.content.DialogInterface.BUTTON_POSITIVE;

/**
 * Created by gabesaruhashi on 7/10/17.
 */

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.ViewHolder> {

    // list of tweets
    ArrayList<Event> events;
    // pass in the Tweets array in the constructor
    public FeedAdapter(ArrayList<Event> events) { this.events = events; };
    // initialize context
    Context context;

    // current user's info
    long currentUserFacebookId;
    String currentUserId;

    // creates and inflates a new view; for each row, inflate the layout and cache references
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // get context and inflate view
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // create the view using the item_feed layout
        View feedView = inflater.inflate(R.layout.item_feed, parent, false);

        // Return a new holder instance
        FeedAdapter.ViewHolder viewHolder = new FeedAdapter.ViewHolder(feedView);

        // instantiate id's
        currentUserId = ParseUser.getCurrentUser().getObjectId();

        return viewHolder;
    }

    // associates an inflated view to a new item / binds the values based on the position of the element
    @Override
    public void onBindViewHolder(FeedAdapter.ViewHolder holder, int position) {
        // populate the views
        Event event = events.get(position);
        holder.tvBody.setText(event.getDescription());
        holder.tvRelativeTime.setText(getTimeRemaining(event.deadline));
        holder.tvEventOwnerName.setText(event.getEventOwnerName());

        if (isAlreadyInterested(currentUserId, event)) {
            holder.btJoin.setBackgroundColor(ContextCompat.getColor(context, R.color.medium_gray));
            holder.btJoin.setText("Joined");

        } else {
            holder.btJoin.setBackgroundColor(ContextCompat.getColor(context, R.color.burnt_orange));
            holder.btJoin.setText("Join");
        }

        // get icon url
        String url = "";
        if (event.getEventOwner() != null) {
            url = event.getEventOwner().getImageUrl();
        }
        // load user profile image using glide
        Glide.with(context)
                .load(url)
                .bitmapTransform(new RoundedCornersTransformation(context, 15, 0))
                .placeholder(R.drawable.ic_person)
                .into(holder.ivProfileImage);

    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    // getRelativeTimeAgo("Mon Apr 01 21:16:23 +0000 2014");
    public String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();

            relativeDate = relativeDate.replace(" seconds", "s");
            relativeDate = relativeDate.replace(" second", "s");
            relativeDate = relativeDate.replace(" minutes", "m");
            relativeDate = relativeDate.replace(" minute", "m");
            relativeDate = relativeDate.replace(" hours", "h");
            relativeDate = relativeDate.replace(" hour", "h");
            relativeDate = relativeDate.replace(" ago", "");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }

    // creates ViewHolder class
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // Automatically finds each field by the specified ID
        @BindView(R.id.tvEventOwnerName) TextView tvEventOwnerName;
        @BindView(R.id.tvBody) TextView tvBody;
        @BindView(R.id.tvRelativeTime) TextView tvRelativeTime;
        @BindView(R.id.ivProfileImage) ImageView ivProfileImage;
        @BindView(R.id.btJoin) Button btJoin;


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            // sets click listener for events' details
            itemView.setOnClickListener(this);
            // set click listener for quick join shortcut
            btJoin.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            // gets item position
            int position = getAdapterPosition();

            if (position != RecyclerView.NO_POSITION) {
                // get the event at the position
                Event event = events.get(position);

                // set up switch to manage the different click listeners
                switch (v.getId()) {
                    case R.id.btJoin:
                        //TODO replace gabriel
                        if (isAlreadyInterested(currentUserId, event)) {
                            removeEvent(currentUserId, event, btJoin);
                        } else {
                            joinEvent(currentUserId, event, btJoin);
                        }
                        break;
                    // if user presses viewholder, show more details of activity
                    default:
                        showMoreDetails(event, btJoin);
                        break;
                }

            }
        }
    }

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return context;
    }

    public void clear() {
        events.clear();
        notifyDataSetChanged();
    }
    // when user clicks itemView, shows more details (map, meeting time, friends that are going, etc)
    private void showMoreDetails(final Event event, final Button joinStatus) {
        // inflate message_item.xml view
        View messageView = LayoutInflater.from(context).
                inflate(R.layout.fragment_event_details, null);
        // Create alert dialog builder
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        // set message_item.xml to AlertDialog builder
        alertDialogBuilder.setView(messageView);

        Log.i("DEBUGSHOW", event.participantsIds.toString());

        // Create alert dialog
        final AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                // check if user already joined the event
                if (isAlreadyInterested(currentUserId, event)) {
                    alertDialog.getButton(BUTTON_POSITIVE).setBackgroundColor(ContextCompat.getColor(context, R.color.medium_gray));
                    alertDialog.getButton(BUTTON_POSITIVE).setText("Joined");

                } else {
                    alertDialog.getButton(BUTTON_POSITIVE).setBackgroundColor(ContextCompat.getColor(context, R.color.burnt_orange));
                    alertDialog.getButton(BUTTON_POSITIVE).setText("Join");
                }
                alertDialog.getButton(BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.white));

                // get views
                TextView tvEventOwnerName = (TextView) alertDialog.findViewById(R.id.tvEventOwnerName);
                TextView tvBody = (TextView) alertDialog.findViewById(R.id.tvBody);
                TextView tvRelativeTime = (TextView) alertDialog.findViewById(R.id.tvRelativeTime);
                ImageView ivProfileImage = (ImageView) alertDialog.findViewById(R.id.ivProfileImage);

                // populate views
                tvEventOwnerName.setText('@' + event.getEventOwnerName());
                tvBody.setText(event.getDescription());
                tvRelativeTime.setText(event.getDeadline().toString());

                //TODO upload image of event owner
                /*
                Glide.with(context)
                        .load(event.user.profileImageUrl)
                        .bitmapTransform(new RoundedCornersTransformation(context, 15, 0))
                        .into(ivProfileImage); */

            }
        });

        // Configure dialog button (OK)
        alertDialog.setButton(BUTTON_POSITIVE, "Join",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       //TODO get current user
                        // if user is already interested, remove; else, join
                        if (isAlreadyInterested(currentUserId, event)) {
                            removeEvent(currentUserId, event, joinStatus);
                        } else {
                            joinEvent(currentUserId, event, joinStatus);
                        }
                    }
                });

        // Configure dialog button (Cancel)
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) { dialog.cancel(); }
                });

        // Display the dialog
        alertDialog.show();
    }

    public void joinEvent(final String userId, final Event event, final Button joinStatus) {
       final ArrayList<String> updatedParticipantsIds = event.getParticipantsIds();
        updatedParticipantsIds.add(currentUserId);

        // specify which class to query
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
        // return object with specific id
        query.getInBackground(event.getEventId(), new GetCallback<ParseObject>() {
            public void done(ParseObject object, com.parse.ParseException e) {
                if (e == null) {
                    // update local object
                    event.setParticipantsIds(updatedParticipantsIds);
                    // update database
                    object.put("participants_id", updatedParticipantsIds);
                    object.saveInBackground();
                    // update UI
                    joinStatus.setBackgroundColor(ContextCompat.getColor(context, R.color.medium_gray));
                    joinStatus.setText("Joined");

                    // subscribes user to this "channel" for notifications
                    ParsePush.subscribeInBackground(event.eventId);

                } else {
                    e.getMessage();
                }
            }
        });
    }
    //// TODO: 7/14/17 make sure user leaves event and put in db
    public void removeEvent(final String userId, final Event event, final Button joinStatus) {
        // update participants id
        final ArrayList<String> updatedParticipantsIds = event.getParticipantsIds();
        updatedParticipantsIds.remove(currentUserId);

        // specify which class to query
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
        // return object with specific id
        query.getInBackground(event.getEventId(), new GetCallback<ParseObject>() {
            public void done(ParseObject object, com.parse.ParseException e) {
                if (e == null) {
                    // update local object
                    event.setParticipantsIds(updatedParticipantsIds);
                    // update database
                    object.put("participants_id", updatedParticipantsIds);
                    object.saveInBackground();

                    // update UI
                    joinStatus.setBackgroundColor(ContextCompat.getColor(context, R.color.burnt_orange));
                    joinStatus.setText("Join");

                    // unsubscribes user from this "channel" so they no longer receive notifications
                    ParsePush.unsubscribeInBackground(event.eventId);
                } else {
                    e.getMessage();
                }
            }
        });
    }

    public boolean isAlreadyInterested(String userId, Event event) {
        if (event.participantsIds.contains(userId)) {
            return true;
        }
        return false;
    }

    public String getTimeRemaining(Date date) {
        String timeRemaining = "";
        int difference = (int) (TimeUnit.MILLISECONDS.toMinutes(date.getTime() - (new Date()).getTime()));
        int hours = difference / 60;
        if (hours > 0) {
            timeRemaining += hours + "h ";
        }
        int minutes = difference - 60 * hours;
        timeRemaining += minutes + "m";
        return timeRemaining;
    }

}
