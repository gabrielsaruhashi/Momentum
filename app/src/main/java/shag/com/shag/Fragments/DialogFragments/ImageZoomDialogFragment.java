package shag.com.shag.Fragments.DialogFragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    public static ImageZoomDialogFragment newInstance(Bitmap bitmap) {
        ImageZoomDialogFragment frag = new ImageZoomDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable("BitmapImage", bitmap);
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
        // Set the Bitmap into the
        // ImageView
        ivImageZoom.setImageBitmap(bm);

    }

}
