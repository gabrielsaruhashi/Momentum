package shag.com.shag.Adapters;

import android.content.Context;
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
import shag.com.shag.Models.User;
import shag.com.shag.R;

/**
 * Created by gabesaruhashi on 7/13/17.
 */

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder> {
    // instantiate variables
    ArrayList<User> friends;
    Context context;

    // pass in the Tweets array in the constructor
    public FriendsAdapter(ArrayList<User> friends) { this.friends = friends; };

    @Override
    public FriendsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // get context and inflate view
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // create the view using the item_friend layout
        View feedView = inflater.inflate(R.layout.item_friend, parent, false);

        // Return a new holder instance
        FriendsAdapter.ViewHolder viewHolder = new FriendsAdapter.ViewHolder(feedView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(FriendsAdapter.ViewHolder holder, int position) {
        // get position
        User friend = friends.get(position);
        holder.tvFriendName.setText(friend.getName());
        Glide.with(context)
                .load(friend.getImageUrl())
                .into(holder.ivProfileImage);

    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    // creates ViewHolder class
    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ivProfileImage) ImageView ivProfileImage;
        @BindView(R.id.tvFriendName) TextView tvFriendName;
        @BindView(R.id.tvFriendUsername) TextView getTvFriendUsername;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }
}
