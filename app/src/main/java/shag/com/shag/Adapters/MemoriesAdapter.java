package shag.com.shag.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import shag.com.shag.Activities.MemoryDetailsActivity;
import shag.com.shag.Models.Memory;
import shag.com.shag.R;

/**
 * Created by gabesaruhashi on 7/18/17.
 */

public class MemoriesAdapter extends RecyclerView.Adapter<MemoriesAdapter.ViewHolder> {

    private ArrayList<Memory> memories;
    private Context mContext;
    private final static int REQUEST_OPEN_MEMORIES = 10;
    int color=-1;


    public MemoriesAdapter(Context context, ArrayList<Memory> memories) {
        mContext = context;
        this.memories = memories;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // get context and inflate view
        mContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View contactView = inflater.inflate(R.layout.cell_title_layout, parent, false);

        ViewHolder viewHolder = new ViewHolder(contactView);

        return viewHolder;

    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

        final Memory memory = memories.get(position);

        // populate views
        String category = memory.getCategory();
        if (category!=null) {
            switch (category) {
                case "Food":
                    color = R.color.food_color;

                    break;
                case "Party":
                    color = R.color.party_color;

                    break;
                case "Explore":
                    color = R.color.explore_color;

                    break;
                case "Music":
                    color = R.color.explore_color;

                    break;
                case "Sports":
                    color = R.color.sports_color;
                    break;
                case "Chill":
                    color = R.color.chill_color;

                    break;
                case "Misc":
                    color = R.color.misc_color;
                    break;

            }
        }
        if (color!=-1) {
            viewHolder.colorColumn.setBackgroundResource(color);
        }
        viewHolder.tvMemoryName.setText(memory.getMemoryName());

        if (memory.getPicturesParseFiles() != null && memory.getPicturesParseFiles().size() != 0) {
            viewHolder.tvPhotoCount.setText("" + memory.getPicturesParseFiles().size());
        }

        if (memory.getLocation() != null) {
            viewHolder.tvLocation.setText(memory.getLocation());
        }

        viewHolder.ivCoverPicture.setAdjustViewBounds(true);
        Glide.with(mContext)
                .load(memory.getCoverPictureUrl() != null ? memory.getCoverPictureUrl() : "http://i.imgur.com/Gwb6TqH.png")
                .fitCenter()
                .into(viewHolder.ivCoverPicture);


        // if participants
        ArrayList<String> participants = memory.getParticipantsIds();

        if (participants.size() > 1) {
            try {
                ParseUser user = ParseUser.getQuery().get(participants.get(1));
                Glide.with(mContext)
                        .load(user.getString("profile_image_url").replace("_normal", ""))
                        .bitmapTransform(new CropCircleTransformation(mContext))
                        .into(viewHolder.ivFriend1);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            viewHolder.ivFriend1.setVisibility(View.GONE);
        }

        if (participants.size() > 2) {
            try {
                ParseUser user = ParseUser.getQuery().get(participants.get(2));
                Glide.with(mContext)
                        .load(user.getString("profile_image_url").replace("_normal", ""))
                        .bitmapTransform(new CropCircleTransformation(mContext))
                        .into(viewHolder.ivFriend2);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            viewHolder.ivFriend2.setVisibility(View.GONE);
        }

        if (participants.size() > 3) {
            try {
                ParseUser user = ParseUser.getQuery().get(participants.get(3));
                Glide.with(mContext)
                        .load(user.getString("profile_image_url").replace("_normal", ""))
                        .bitmapTransform(new CropCircleTransformation(mContext))
                        .into(viewHolder.ivFriend3);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            viewHolder.ivFriend3.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return memories.size();
    }

    // View lookup cache
    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tvMemoryName) TextView tvMemoryName;
        @BindView(R.id.ivCoverPicture) ImageView ivCoverPicture;
        @BindView(R.id.tvPhotoCount) TextView tvPhotoCount;
        @BindView(R.id.tvLocation) TextView tvLocation;
        @BindView(R.id.ivFriend1) ImageView ivFriend1;
        @BindView(R.id.ivFriend2) ImageView ivFriend2;
        @BindView(R.id.ivFriend3) ImageView ivFriend3;
        @BindView(R.id.colorColumn) RelativeLayout colorColumn;

        public ViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            // set on click listener to open detailed view of memory
            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    if (position != RecyclerView.NO_POSITION) {
                        Memory memory = memories.get(position);
                        Intent i = new Intent(mContext, MemoryDetailsActivity.class);
                        i.putExtra(Memory.class.getSimpleName(), memory.getMemoryId());
                        // pass position to update the right memory when detail activity finishes
                        i.putExtra("position", position);
                        ActivityOptionsCompat options = ActivityOptionsCompat.
                                makeSceneTransitionAnimation((Activity) mContext, ivCoverPicture, "memoryDetail");
                        ((Activity) mContext).startActivityForResult(i, REQUEST_OPEN_MEMORIES, options.toBundle());

                    }

                }
            });
        }


    }

}
