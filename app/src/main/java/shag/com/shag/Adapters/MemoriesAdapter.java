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

import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import shag.com.shag.Activities.MemoryDetailsActivity;
import shag.com.shag.Models.Memory;
import shag.com.shag.R;

/**
 * Created by gabesaruhashi on 7/18/17.
 */

public class MemoriesAdapter extends ArrayAdapter<Memory> {
    private HashSet<Integer> unfoldedIndexes = new HashSet<>();
    private View.OnClickListener defaultRequestBtnClickListener;
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
        /*
        if (memory.getTotalFacebookLikes() != 0) {

        }
        viewHolder.tvLoveCount.setText(memory.getTotalFacebookLikes());
        viewHolder.tvPhotoCount.setText(memory.getPicturesParseFiles().size()); */

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


        return contactView;

        /*
        // get item for selected view
        final Memory memory = getItem(position);
        // if cell is exists - reuse it, if not - create the new one from resource
        FoldingCell cell = (FoldingCell) convertView;
        ViewHolder viewHolder;
        if (cell == null) {
            viewHolder = new ViewHolder();
            LayoutInflater vi = LayoutInflater.from(getContext());
            cell = (FoldingCell) vi.inflate(R.layout.item_memory, parent, false);

            // binding view parts to view holder
            viewHolder.ivMemoryBannerPicture = (ImageView) cell.findViewById(R.id.ivMemoryBannerPicture);
            // viewHolder.tvMemoryName = (TextView) cell.findViewById(R.id.tvMemoryName);
            viewHolder.btnAccessMemory = (Button) cell.findViewById(R.id.content_request_btn);
            // set listener
            viewHolder.btnAccessMemory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent memoryDetailIntent = new Intent(mContext, MemoryDetailsActivity.class);
                    memoryDetailIntent.putExtra(Memory.class.getSimpleName(), memory.getMemoryId());
                    mContext.startActivity(memoryDetailIntent);
                }
            });

            // content card
            viewHolder.tvMemoryName = (TextView) cell.findViewById(R.id.tvMemoryName);
            // title card
            viewHolder.ivTitle = (ImageView) cell.findViewById(R.id.ivTitle);

            //TODO dummy data
            viewHolder.fakeFriend1 = (ImageView) cell.findViewById(R.id.fake_pic1);
            viewHolder.fakeFriend2 = (ImageView) cell.findViewById(R.id.fake_pic2);
            viewHolder.fakeFriend3 = (ImageView) cell.findViewById(R.id.fake_pic3);

            cell.setTag(viewHolder);
        } else {
            // for existing cell set valid valid state(without animation)
            if (unfoldedIndexes.contains(position)) {
                cell.unfold(true);
            } else {
                cell.fold(true);
            }
            viewHolder = (ViewHolder) cell.getTag();
        }

        viewHolder.tvMemoryName.setText(memory.getMemoryName());


        //TODO use real data to populate views

        Glide.with(mContext)
                .load(memory.getCoverPictureUrl() != null ? memory.getCoverPictureUrl() : "http://www.comedycentral.co.uk/sites/default/files/styles/image-w-1200-h-600-scale-crop/public/mtv_uk/arc/2014/05/27/f8bf5a4f-0745-45e6-b57a-3cc95f1bd3cf.jpg?itok=neQ-BYZn")
                .into(viewHolder.ivTitle);


        Glide.with(mContext)
                .load("https://s3.amazonaws.com/classconnection/455/flashcards/8794455/jpg/belachew_samrawit_s01238564-155DC9A147C4F218FC9.jpg")
                .bitmapTransform(new RoundedCornersTransformation(mContext, 25, 0))
                .into(viewHolder.fakeFriend1);

        Glide.with(mContext)
                .load("https://media.licdn.com/mpr/mpr/shrinknp_200_200/AAEAAQAAAAAAAAnvAAAAJDVhNWUzYjMxLTA0OWYtNGU5Yy05MTM4LTQwY2QzZTFmMzkzYQ.jpg")
                .bitmapTransform(new RoundedCornersTransformation(mContext, 25, 0))
                .into(viewHolder.fakeFriend2);

        Glide.with(mContext)
                .load("https://yt3.ggpht.com/-QQ6nz0pWCk0/AAAAAAAAAAI/AAAAAAAAAAA/3NXhoHLM_LQ/s900-c-k-no-mo-rj-c0xffffff/photo.jpg")
                .bitmapTransform(new RoundedCornersTransformation(mContext, 25, 0))
                .into(viewHolder.fakeFriend3);

        // viewHolder.btnAccessMemory.setOnClickListener(mContext);


        return cell;
        */
    }
    /*
    // simple methods for register cell state changes
    public void registerToggle(int position) {
        if (unfoldedIndexes.contains(position))
            registerFold(position);
        else
            registerUnfold(position);
    }

    public void registerFold(int position) {
        unfoldedIndexes.remove(position);
    }

    public void registerUnfold(int position) {
        unfoldedIndexes.add(position);
    }

    public View.OnClickListener getDefaultRequestBtnClickListener() {
        return defaultRequestBtnClickListener;
    }

    public void setDefaultRequestBtnClickListener(View.OnClickListener defaultRequestBtnClickListener) {
        this.defaultRequestBtnClickListener = defaultRequestBtnClickListener;
    } */

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
        @BindView(R.id.tvLoveCount) TextView tvLoveCount;
        @BindView(R.id.tvPhotoCount) TextView tvPhotoCount;


        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }


    }

}
