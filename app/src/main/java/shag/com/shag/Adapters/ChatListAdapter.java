package shag.com.shag.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
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
import shag.com.shag.Activities.ChatActivity;
import shag.com.shag.Models.Chat;
import shag.com.shag.Models.Event;
import shag.com.shag.Models.Message;
import shag.com.shag.R;

/**
 * Created by gabesaruhashi on 7/14/17.
 */

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> {
    ArrayList<Chat> chats;

    // pass in the Tweets array in the constructor
    public ChatListAdapter(ArrayList<Chat> chats) {
        this.chats = chats;
    }

    ;
    // instantiate context
    Context context;

    @Override
    public ChatListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // get context and inflate view
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // create the view using the item_feed layout
        View feedView = inflater.inflate(R.layout.item_chat, parent, false);

        // return a new holder instance
        ChatListAdapter.ViewHolder viewHolder = new ChatListAdapter.ViewHolder(feedView);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(final ChatListAdapter.ViewHolder holder, int position) {

        // populate the views
        Chat chat = chats.get(position);
        final Event event = chat.getParcelableEvent();
        String iconImageUrl = chat.getChatIconUrl();
        //TODO delete this later
        if (iconImageUrl == null) {
            iconImageUrl = "https://cnet4.cbsistatic.com/img/QJcTT2ab-sYWwOGrxJc0MXSt3UI=/2011/10/27/a66dfbb7-fdc7-11e2-8c7c-d4ae52e62bcc/android-wallpaper5_2560x1600_1.jpg";
        }
        holder.tvEventDescription.setText(chat.getDescription().toUpperCase());
        holder.tvParticipants.setText(chat.getChatTitle());

        if (event.category.equals("Chill")) {
            holder.ivCategory.setBackgroundColor(ContextCompat.getColor(context, R.color.chill_color));
        } else if (event.category.equals("Sports")) {
            holder.ivCategory.setBackgroundColor(ContextCompat.getColor(context, R.color.sports_color));
        } else if (event.category.equals("Party")) {
            holder.ivCategory.setBackgroundColor(ContextCompat.getColor(context, R.color.party_color));
        } else if (event.category.equals("Food")) {
            holder.ivCategory.setBackgroundColor(ContextCompat.getColor(context, R.color.food_color));
        } else if (event.category.equals("Music")) {
            holder.ivCategory.setBackgroundColor(ContextCompat.getColor(context, R.color.explore_color));
        } else {
            holder.ivCategory.setBackgroundColor(ContextCompat.getColor(context, R.color.misc_color));
        }

        try {
            holder.tvLastMessage.setVisibility(View.VISIBLE);
            holder.tvLastMessageTime.setVisibility(View.VISIBLE);
            Message lastMessage = ((Message) event.lastMessageSent);
            if (lastMessage.getBody() == null) {
                throw new NullPointerException();
            }
            holder.tvLastMessage.setText(lastMessage.getSenderName() + ": " + lastMessage.getBody());

            String rawTime = (lastMessage.getCreatedDate().toString());
            holder.tvLastMessageTime.setText(getRelativeTimeAgo(rawTime));
        } catch (NullPointerException e) {
            holder.tvLastMessage.setVisibility(View.INVISIBLE);
            holder.tvLastMessageTime.setVisibility(View.INVISIBLE);
        }

        // load user profile image using glide
        Glide.with(context)
                .load(iconImageUrl)
                .centerCrop()
                .into(holder.ivChatIcon);
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    // creates ViewHolder class
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // Automatically finds each field by the specified ID
        @BindView(R.id.tvParticipants)
        TextView tvParticipants;
        @BindView(R.id.tvLastMessage)
        TextView tvLastMessage;
        @BindView(R.id.tvEventDescription)
        TextView tvEventDescription;
        @BindView(R.id.ivChatIcon)
        ImageView ivChatIcon;
        @BindView(R.id.ivCategory)
        ImageView ivCategory;
        @BindView(R.id.tvLastMessageTime)
        TextView tvLastMessageTime;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void onClick(View v) {

            // gets item position
            int position = getAdapterPosition();

            if (position != RecyclerView.NO_POSITION) {
                // get the event at the position
                Chat chat = chats.get(position);
                // start chat activity
                Intent i = new Intent(context, ChatActivity.class);
                // pass chat id in the intent
                i.putExtra("event_id", chat.getEventId());
                i.putExtra("participants_ids", chat.getChatParticipantsIds());
                context.startActivity(i);
            }
        }
    }

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
}
