package shag.com.shag.Fragments.DialogFragments;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import shag.com.shag.R;

/**
 * Created by gabesaruhashi on 7/20/17.
 */

public class ImageZoomDialogFragment extends DialogFragment {
    private ImageView ivImageZoom;

    public ImageZoomDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static ImageZoomDialogFragment newInstance(Bitmap bitmap, int screenWidth, int screenHeight) {
        ImageZoomDialogFragment frag = new ImageZoomDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable("BitmapImage", bitmap);
        args.putInt("ScreenWidth", screenWidth);
        args.putInt("ScreenHeight", screenHeight);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_picture_zoom, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        ivImageZoom = (ImageView) view.findViewById(R.id.ivImageZoom);
        // Fetch arguments from bundle and set title
        Bitmap bm = getArguments().getParcelable("BitmapImage");
        int screenWidth = getArguments().getInt("ScreenWidth");
        int screenHeight = getArguments().getInt("ScreenHeight");

        BitmapDrawable resizedBitmap = resizeImageZoom(bm, screenWidth, screenHeight);
        // Set the Bitmap into the
        // ImageView
        ivImageZoom.setBackground(resizedBitmap);

        getDialog().getWindow().setBackgroundDrawable(null);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE); //before

    }

    public BitmapDrawable resizeImageZoom(Bitmap bitmap, int screenWidth, int screenHeight) {

        // Get target image siz
        int bitmapHeight = bitmap.getHeight();
        int bitmapWidth = bitmap.getWidth();

        // Scale the image down to fit perfectly into the screen
        // The value (250 in this case) must be adjusted for phone/tables displays
        while(bitmapHeight > (screenHeight - 125) || bitmapWidth > (screenWidth - 125)) {
            bitmapHeight = bitmapHeight / 2;
            bitmapWidth = bitmapWidth / 2;
        }

        // Create resized bitmap image
        BitmapDrawable resizedBitmap = new BitmapDrawable(this.getResources(), Bitmap.createScaledBitmap(bitmap, bitmapWidth, bitmapHeight, false));
        return resizedBitmap;
    }

}
