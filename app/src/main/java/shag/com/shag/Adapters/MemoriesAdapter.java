package shag.com.shag.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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


    public MemoriesAdapter(Context context, List<Memory> objects) {
        super(context, 0, objects);
        mContext = context;
    }

    @Override
    public View getView(int position, final View convertView, ViewGroup parent) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.cell_title_layout, parent, false);

        ViewHolder viewHolder = new ViewHolder(contactView);

        final Memory memory = getItem(position);

        // populate views
        viewHolder.tvMemoryName.setText(memory.getMemoryName());

        if (memory.getPicturesParseFiles() != null && memory.getPicturesParseFiles().size() != 0) {
            viewHolder.tvPhotoCount.setText("" + memory.getPicturesParseFiles().size());
        }


        viewHolder.ivCoverPicture.setAdjustViewBounds(true);
        Glide.with(mContext)
                .load(memory.getCoverPictureUrl() != null ? memory.getCoverPictureUrl() : "http://www.comedycentral.co.uk/sites/default/files/styles/image-w-1200-h-600-scale-crop/public/mtv_uk/arc/2014/05/27/f8bf5a4f-0745-45e6-b57a-3cc95f1bd3cf.jpg?itok=neQ-BYZn")
                .centerCrop()
                .into(viewHolder.ivCoverPicture);

        contactView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, MemoryDetailsActivity.class);
                i.putExtra(Memory.class.getSimpleName(), memory.getMemoryId());
                mContext.startActivity(i);
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
        /*
        ImageView ivMemoryBannerPicture;
        TextView tvMemoryName;
        TextView vPalette;
        Button btnAccessMemory;
        ImageView ivTitle;

        // dummy data
        ImageView fakeFriend1;
        ImageView fakeFriend2;
        ImageView fakeFriend3; */

        @BindView(R.id.tvMemoryName) TextView tvMemoryName;
        @BindView(R.id.ivCoverPicture) ImageView ivCoverPicture;
        @BindView(R.id.tvPhotoCount) TextView tvPhotoCount;
        @BindView(R.id.ivFriend1) ImageView ivFriend1;
        @BindView(R.id.ivFriend2) ImageView ivFriend2;
        @BindView(R.id.ivFriend3) ImageView ivFriend3;




        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }


    }

}
