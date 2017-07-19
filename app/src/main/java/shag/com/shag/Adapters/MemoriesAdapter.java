package shag.com.shag.Adapters;

import android.content.Context;
import android.content.Intent;
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
import shag.com.shag.Activities.MemoryDetailsActivity;
import shag.com.shag.Models.Memory;
import shag.com.shag.R;

/**
 * Created by gabesaruhashi on 7/18/17.
 */

public class MemoriesAdapter extends RecyclerView.Adapter<MemoriesAdapter.ViewHolder> {
    // list of memories
    ArrayList<Memory> memories;
    Context context;

    // pass in the memories array in the constructor
    public MemoriesAdapter(ArrayList<Memory> memories) { this.memories = memories; }

    // creates and inflates a new view; for each row, inflate the layout and cache references
    @Override
    public MemoriesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // memories = new ArrayList<Memory>();
        // get context and inflate view
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // create the view using the item_feed layout
        View memoriesView = inflater.inflate(R.layout.item_memory, parent, false);

        // Return a new holder instance
        MemoriesAdapter.ViewHolder viewHolder = new MemoriesAdapter.ViewHolder(memoriesView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MemoriesAdapter.ViewHolder holder, int position) {
        Memory memory = memories.get(position);
        holder.rootView.setTag(memory);
        holder.tvMemoryName.setText(memory.getMemoryName());

        // TODO add banner picture
        // load user profile image using glide
        Glide.with(context)
                .load("http://theinspirationgrid.com/wp-content/uploads/2015/05/photo-nick-venton-02.jpg")
                .bitmapTransform(new RoundedCornersTransformation(context, 15, 0))
                .into(holder.ivMemoryBannerPicture);
    }

    @Override
    public int getItemCount() {
        return memories.size();
    }

    // creates ViewHolder class
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // Automatically finds each field by the specified ID
        final View rootView;
        @BindView(R.id.tvMemoryName) TextView tvMemoryName;
        @BindView(R.id.ivMemoryBannerPicture) ImageView ivMemoryBannerPicture;
        @BindView(R.id.vPalette) View vPalette;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            rootView = itemView;

            // set click listener for memory
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            // gets item position
            int position = getAdapterPosition();

            if (position != RecyclerView.NO_POSITION) {
                // get the memory at the position
                Memory memory = memories.get(position);
                // set up intent
                Intent i = new Intent(context, MemoryDetailsActivity.class);
                i.putExtra(Memory.class.getSimpleName(), memory);
            }

        }
    }

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return context;
    }

}
