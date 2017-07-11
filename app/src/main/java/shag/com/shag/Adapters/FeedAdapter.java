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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import shag.com.shag.Models.Event;
import shag.com.shag.Models.User;
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

    // TODO delete dummy data
    User gabriel;

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

        // TODO delete dummy data
        gabriel = new User();
        gabriel.username = "gabesaruhashi";
        gabriel.name="Gabriel S.";
        gabriel.phoneNumber="6505757751";
        gabriel.currentInterestsIds = new ArrayList<Long>(0);
        long number = new Long(3105);
        gabriel.userID= number;

        return viewHolder;
    }

    // associates an inflated view to a new item / binds the values based on the position of the element
    @Override
    public void onBindViewHolder(FeedAdapter.ViewHolder holder, int position) {

        // user image
        String imageUrl = "https://cnet4.cbsistatic.com/img/QJcTT2ab-sYWwOGrxJc0MXSt3UI=/2011/10/27/a66dfbb7-fdc7-11e2-8c7c-d4ae52e62bcc/android-wallpaper5_2560x1600_1.jpg";
        // populate the views
        Event event = events.get(position);
        holder.tvEventName.setText(event.getEventName());
        holder.tvBody.setText(event.getDescription());
        holder.tvRelativeTime.setText(event.getTime());

        // load user profile image using glide
        Glide.with(context)
                .load(imageUrl)
                .bitmapTransform(new RoundedCornersTransformation(context, 15, 0))
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
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return relativeDate;
    }

    // creates ViewHolder class
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // Automatically finds each field by the specified ID
        @BindView(R.id.tvEventName) TextView tvEventName;
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
                        if (isAlreadyInterested(gabriel.getUserID(), event)) {
                            removeEvent(gabriel, event, btJoin);
                        } else {
                            joinEvent(gabriel, event, btJoin);
                        }
                        break;
                    // if user presses viewholder, show more details of activiity
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
                if (isAlreadyInterested(gabriel.getUserID(), event)) {
                    alertDialog.getButton(BUTTON_POSITIVE).setBackgroundColor(ContextCompat.getColor(context, R.color.medium_gray));
                    alertDialog.getButton(BUTTON_POSITIVE).setText("Joined");

                } else {
                    alertDialog.getButton(BUTTON_POSITIVE).setBackgroundColor(ContextCompat.getColor(context, R.color.burnt_orange));
                    alertDialog.getButton(BUTTON_POSITIVE).setText("Join");
                }
                alertDialog.getButton(BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.white));

                // get views
                TextView tvEventName = (TextView) alertDialog.findViewById(R.id.tvEventName);
                TextView tvBody = (TextView) alertDialog.findViewById(R.id.tvBody);
                TextView tvRelativeTime = (TextView) alertDialog.findViewById(R.id.tvRelativeTime);
                ImageView ivProfileImage = (ImageView) alertDialog.findViewById(R.id.ivProfileImage);

                // populate views
                tvEventName.setText('@' + event.eventOwner.getUsername());
                tvBody.setText(event.getDescription());
                tvRelativeTime.setText(event.getTime());

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
                        if (isAlreadyInterested(gabriel.getUserID(), event)) {
                            removeEvent(gabriel, event, joinStatus);
                        } else {
                            joinEvent(gabriel, event, joinStatus);
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

    public void joinEvent(User user, Event event, Button joinStatus) {
        event.participantsIds.add(user.getUserID());
        user.currentInterestsIds.add(event.eventId);
        Log.i("DEBUGAFTERJOIN", event.participantsIds.toString());

        joinStatus.setBackgroundColor(ContextCompat.getColor(context, R.color.medium_gray));
        joinStatus.setText("Joined");

    }

    public void removeEvent(User user, Event event, Button joinStatus) {
        event.participantsIds.remove(user.getUserID());
        user.currentInterestsIds.remove(event.eventId);
        Log.i("DEBUGAFTERJOIN", event.participantsIds.toString());

        joinStatus.setBackgroundColor(ContextCompat.getColor(context, R.color.burnt_orange));
        joinStatus.setText("Join");

    }

    public boolean isAlreadyInterested(long userId, Event event) {
        if (event.participantsIds.contains(userId)) {
            return true;
        }
        return false;
    }

}
