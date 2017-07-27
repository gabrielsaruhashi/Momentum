package shag.com.shag.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.parse.ParseException;
import com.parse.ParseFile;

import java.util.ArrayList;

import shag.com.shag.R;

/**
 * Created by gabesaruhashi on 7/27/17.
 */

public class ImageSliderAdapter extends PagerAdapter {

    private ArrayList<ParseFile> images;
    private LayoutInflater inflater;
    private Context context;

    public ImageSliderAdapter(Context context, ArrayList<ParseFile> images) {
        this.context = context;
        this.images=images;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return images.size();
    }

    // create the page for the position passed to it as an argument. Here we inflate() the slide.xml layout
    @Override
    public Object instantiateItem(ViewGroup view, int position) {
        View myImageLayout = inflater.inflate(R.layout.item_memory_image_slide, view, false);
        ImageView myImage = (ImageView) myImageLayout
                .findViewById(R.id.image);
        // myImage.setImageResource(images.get(position));

        // convert parse file to bitmap
        final Bitmap bm = bitmapConverterFromParseFile(images.get(position));


        if (bm != null) {
            myImage.setImageBitmap(bm);
        } else {
            // if image was not converted correctly, set it with an error icon
            myImage.setImageResource(R.drawable.ic_error_outline);
        }

        // the inflated view is added to the ViewPager using addView() is returned
        view.addView(myImageLayout, 0);
        return myImageLayout;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
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
}