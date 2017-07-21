package shag.com.shag.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ramotion.foldingcell.FoldingCell;

import java.util.HashSet;
import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        // get item for selected view
        Memory memory = getItem(position);
        // if cell is exists - reuse it, if not - create the new one from resource
        FoldingCell cell = (FoldingCell) convertView;
        ViewHolder viewHolder;
        if (cell == null) {
            viewHolder = new ViewHolder();
            LayoutInflater vi = LayoutInflater.from(getContext());
            cell = (FoldingCell) vi.inflate(R.layout.item_memory, parent, false);

            // binding view parts to view holder
            viewHolder.ivMemoryBannerPicture = (ImageView) cell.findViewById(R.id.ivMemoryBannerPicture);
            viewHolder.tvMemoryName = (TextView) cell.findViewById(R.id.tvMemoryName);
            viewHolder.ivTitle = (ImageView) cell.findViewById(R.id.ivTitle);
            viewHolder.btnAccessMemory = (Button) cell.findViewById(R.id.content_request_btn);

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

        // bind data from selected element to view through view holder

        Glide.with(mContext)
                .load("http://theinspirationgrid.com/wp-content/uploads/2015/05/photo-nick-venton-02.jpg")
                .bitmapTransform(new RoundedCornersTransformation(mContext, 15, 0))
                .into(viewHolder.ivMemoryBannerPicture);

        viewHolder.tvMemoryName.setText(memory.getMemoryName());

        Glide.with(mContext)
                .load("http://www.designbolts.com/wp-content/uploads/2014/06/Cute-twitter-header-banner-design.jpg")
                .bitmapTransform(new RoundedCornersTransformation(mContext, 15, 0))
                .into(viewHolder.ivTitle);

        // viewHolder.btnAccessMemory.setOnClickListener(mContext);


        return cell;
    }

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
    }

    // View lookup cache
    private static class ViewHolder {
        ImageView ivMemoryBannerPicture;
        TextView tvMemoryName;
        TextView vPalette;
        Button btnAccessMemory;
        ImageView ivTitle;

    }

}
