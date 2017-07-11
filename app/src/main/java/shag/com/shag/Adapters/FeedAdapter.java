package shag.com.shag.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import shag.com.shag.R;

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
    public class ViewHolder extends RecyclerView.ViewHolder {

        // Automatically finds each field by the specified ID
        @BindView(R.id.tvEventName) TextView tvEventName;
        @BindView(R.id.tvBody) TextView tvBody;
        @BindView(R.id.tvRelativeTime) TextView tvRelativeTime;
        @BindView(R.id.ivProfileImage) ImageView ivProfileImage;


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return context;
    }
}
