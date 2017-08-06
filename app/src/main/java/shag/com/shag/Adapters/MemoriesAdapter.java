package shag.com.shag.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import shag.com.shag.Activities.MemoryDetailsActivity;
import shag.com.shag.Models.Memory;
import shag.com.shag.R;

/**
 * Created by gabesaruhashi on 7/18/17.
 */

public class MemoriesAdapter extends ArrayAdapter<Memory> {

    private Context mContext;
    private final static int REQUEST_OPEN_MEMORIES = 10;
    int color=-1;


    public MemoriesAdapter(Context context, List<Memory> objects) {
        super(context, 0, objects);
        mContext = context;
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        final Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.cell_title_layout, parent, false);

        final ViewHolder viewHolder = new ViewHolder(contactView);

        final Memory memory = getItem(position);

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
                .centerCrop()
                .into(viewHolder.ivCoverPicture);

        // set on click listener to open detailed view of memory
        contactView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, MemoryDetailsActivity.class);
                i.putExtra(Memory.class.getSimpleName(), memory.getMemoryId());
                // pass position to update the right memory when detail activity finishes
                i.putExtra("position", position);
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation((Activity) mContext, (View) viewHolder.ivCoverPicture, "memoryDetail");
                ((Activity) mContext).startActivityForResult(i, REQUEST_OPEN_MEMORIES, options.toBundle());
            }
        });

        // if participants
        ArrayList<String> participants = memory.getParticipantsIds();

        if (participants.size() > 1) {
            try {
                ParseUser user = ParseUser.getQuery().get(participants.get(1));
                Glide.with(context)
                        .load(user.getString("profile_image_url").replace("_normal", ""))
                        .bitmapTransform(new CropCircleTransformation(context))
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
                Glide.with(context)
                        .load(user.getString("profile_image_url").replace("_normal", ""))
                        .bitmapTransform(new CropCircleTransformation(context))
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
                Glide.with(context)
                        .load(user.getString("profile_image_url").replace("_normal", ""))
                        .bitmapTransform(new CropCircleTransformation(context))
                        .into(viewHolder.ivFriend3);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            viewHolder.ivFriend3.setVisibility(View.GONE);
        }

        return contactView;
    }

    // View lookup cache
    public class ViewHolder {


        @BindView(R.id.tvMemoryName) TextView tvMemoryName;
        @BindView(R.id.ivCoverPicture) ImageView ivCoverPicture;
        @BindView(R.id.tvPhotoCount) TextView tvPhotoCount;
        @BindView(R.id.tvLocation) TextView tvLocation;
        @BindView(R.id.ivFriend1) ImageView ivFriend1;
        @BindView(R.id.ivFriend2) ImageView ivFriend2;
        @BindView(R.id.ivFriend3) ImageView ivFriend3;
        @BindView(R.id.colorColumn) RelativeLayout colorColumn;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }


    }

}
