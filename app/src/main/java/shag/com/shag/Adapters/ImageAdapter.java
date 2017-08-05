package shag.com.shag.Adapters;

import android.content.ClipData;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.parse.ParseException;
import com.parse.ParseFile;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import shag.com.shag.R;

/**
 * Created by gabesaruhashi on 7/18/17.
 */

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<ParseFile> memoryPictures;
    private ImageZoomAdapterCallback callback;
    private ArrayList<String> userImageUrls;


    public ImageAdapter(Context c, ArrayList<ParseFile> memoryPictures, ArrayList<String> uploaderPictures) {
        mContext = c;
        this.memoryPictures = memoryPictures;
        this.userImageUrls = uploaderPictures;
    }

    public int getCount() {
        return memoryPictures.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).
                    inflate(R.layout.item_memory_detail_gridview, parent, false);
            // get current item to be displayed
            ClipData.Item currentItem = (ClipData.Item) getItem(position);

        }

        ImageView ivImage = (ImageView) convertView.findViewById(R.id.ivImage);
        ImageView ivUploaderPicture = (ImageView) convertView.findViewById(R.id.ivUploaderPicture);

        // populate main image
        final String imageUrl = memoryPictures.get(position).getUrl();
        Glide.with(mContext)
                .load(imageUrl)
                .into(ivImage);

        // populate user uploader picture
        final String uploaderPicture = userImageUrls.get(position);
        Glide.with(mContext)
                .load(uploaderPicture)
                .bitmapTransform(new RoundedCornersTransformation(mContext, 15, 0))
                .into(ivUploaderPicture);

        // upon click, initiate dialog fragment through interface
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callback != null) {
                    // convert parse file to bitmap
                    final Bitmap bm = bitmapConverterFromParseFile(memoryPictures.get(position));
                    callback.initiateDialog(bm);
                }
            }
        });

        /*
        // convert parse file to bitmap
        final Bitmap bm = bitmapConverterFromParseFile(memoryPictures.get(position));

        if (bm != null) {
            imageView.setImageBitmap(bm);
        } else {
            // if image was not converted correctly, set it with an error icon
            imageView.setImageResource(R.drawable.ic_error_outline);
        }

        // imageView.setImageResource(memoryPictures.get(position));

        // upon click, initiate dialog fragment through interface
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callback != null) {
                    callback.initiateDialog(bm);
                }
            }
        }); */
        // TODO return with glide
        return convertView;
    }

    // creates a bitmap from parsefile data
    private Bitmap bitmapConverterFromParseFile(ParseFile parseFile) {

        try {
            byte[] bitmapdata = parseFile.getData();
            Bitmap bm = BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length);
            return bm;
        } catch (ParseException e) {
            e.getMessage();
        }
        return null;
    }


    // create interface to pass Bitmap back to activity
    public interface ImageZoomAdapterCallback {
        void initiateDialog(Bitmap bitmap);
    }

    public void setCallback(ImageZoomAdapterCallback callback) {
        this.callback = callback;
    }

}
