package shag.com.shag.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.parse.ParseException;
import com.parse.ParseFile;

import java.util.ArrayList;

import shag.com.shag.R;

/**
 * Created by gabesaruhashi on 7/18/17.
 */

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<ParseFile> memoryPictures;
    private ImageZoomAdapterCallback callback;


    public ImageAdapter(Context c, ArrayList<ParseFile> memoryPictures) {
        mContext = c;
        this.memoryPictures = memoryPictures;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        final ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }
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
        });
        // TODO return with glide
        return imageView;
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
    /*
    // references to our images
    private Integer[] mThumbIds = {
            R.drawable.sample_2, R.drawable.sample_3,
            R.drawable.sample_4, R.drawable.sample_5,
            R.drawable.sample_6, R.drawable.sample_7,
            R.drawable.sample_0, R.drawable.sample_1,
            R.drawable.sample_2, R.drawable.sample_3,
            R.drawable.sample_4, R.drawable.sample_5,
            R.drawable.sample_6, R.drawable.sample_7,
            R.drawable.sample_0, R.drawable.sample_1,
            R.drawable.sample_2, R.drawable.sample_3,
            R.drawable.sample_4, R.drawable.sample_5,
            R.drawable.sample_6, R.drawable.sample_7
    }; */

    // create interface to pass Bitmap back to activity
    public interface ImageZoomAdapterCallback {
        void initiateDialog(Bitmap bitmap);
    }

    public void setCallback(ImageZoomAdapterCallback callback) {
        this.callback = callback;
    }

}
