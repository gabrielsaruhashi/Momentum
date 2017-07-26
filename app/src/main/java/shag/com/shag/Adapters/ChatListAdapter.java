package shag.com.shag.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import shag.com.shag.Activities.ChatActivity;
import shag.com.shag.Models.Chat;
import shag.com.shag.Models.Event;
import shag.com.shag.R;

/**
 * Created by gabesaruhashi on 7/14/17.
 */

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> {
    ArrayList<Chat> chats;
    // pass in the Tweets array in the constructor
    public ChatListAdapter(ArrayList<Chat> chats) { this.chats = chats; };
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
    public void onBindViewHolder(ChatListAdapter.ViewHolder holder, int position) {

        // populate the views
        Chat chat = chats.get(position);
        Event event = chat.getParcelableEvent();
        String iconImageUrl = chat.getChatIconUrl();
        //TODO delete this later
        if (iconImageUrl == null) {
            iconImageUrl = "https://cnet4.cbsistatic.com/img/QJcTT2ab-sYWwOGrxJc0MXSt3UI=/2011/10/27/a66dfbb7-fdc7-11e2-8c7c-d4ae52e62bcc/android-wallpaper5_2560x1600_1.jpg";
        }
        holder.tvBody.setText(chat.getDescription());
        holder.tvParticipants.setText(chat.getChatTitle());

        if (event.category.equals("Movie")) {
            holder.ivCategory.setBackgroundColor(ContextCompat.getColor(context, R.color.chill_color));
        } else if (event.category.equals("Basketball")) {
            holder.ivCategory.setBackgroundColor(ContextCompat.getColor(context, R.color.sports_color));
        } else if (event.category.equals("Party")) {
            holder.ivCategory.setBackgroundColor(ContextCompat.getColor(context, R.color.party_color));
        } else if (event.category.equals("Food")) {
            holder.ivCategory.setBackgroundColor(ContextCompat.getColor(context, R.color.food_color));
        } else if (event.category.equals("Music")) {
            holder.ivCategory.setBackgroundColor(ContextCompat.getColor(context, R.color.music_color));
        } else {
            holder.ivCategory.setBackgroundColor(ContextCompat.getColor(context, R.color.misc_color));
        }

        // load user profile image using glide
        Glide.with(context)
                .load(iconImageUrl)
                .bitmapTransform(new RoundedCornersTransformation(context, 15, 0))
                .into(holder.ivChatIcon);
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    // creates ViewHolder class
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // Automatically finds each field by the specified ID
        @BindView(R.id.tvParticipants) TextView tvParticipants;
        @BindView(R.id.tvBody) TextView tvBody;
        @BindView(R.id.ivChatIcon) ImageView ivChatIcon;
        @BindView(R.id.ivCategory) ImageView ivCategory;

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
                String x = chat.getEventId();
                ArrayList<String> y = chat.getChatParticipantsIds();
                Event z = chat.getParcelableEvent();
                i.putExtra("event_id", chat.getEventId());
                i.putExtra("participants_ids", chat.getChatParticipantsIds());
                i.putExtra("event", chat.getParcelableEvent());
                context.startActivity(i);
            }
        }
    }
}
