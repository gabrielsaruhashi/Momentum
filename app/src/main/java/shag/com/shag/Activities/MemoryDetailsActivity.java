package shag.com.shag.Activities;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import shag.com.shag.Adapters.ImageAdapter;
import shag.com.shag.Fragments.DialogFragments.ImageZoomDialogFragment;
import shag.com.shag.Models.Memory;
import shag.com.shag.R;

public class MemoryDetailsActivity extends AppCompatActivity implements ImageAdapter.ImageZoomAdapterCallback{
    //@BindView(R.id.tvMemoryName) TextView tvMemoryName;
    @BindView(R.id.btAddPicture) Button btAddPicture;
    // @BindView(R.id.ivMemoryBannerPicture) ImageView ivMemoryBannerPicture;
    GridView gridView;

    ImageAdapter imageAdapter;
    ArrayList<ParseFile> pictures;
    // create constant for start activity
    final static int SELECT_IMAGE = 16;

    private Memory memory;
    private String memoryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory_details);
        ButterKnife.bind(this);
        // unwrap memory
        // memory = getIntent().getParcelableExtra(Memory.class.getSimpleName());
        memoryId = getIntent().getStringExtra(Memory.class.getSimpleName());

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Memory");
        try {
            ParseObject object = query.get(memoryId);
            memory = Memory.fromParseObject(object);
        } catch (ParseException e) {
            e.getMessage();
        }

        // initialize pictures, adapter and gridView
        pictures = memory.getPicturesParseFiles();
        imageAdapter = new ImageAdapter(this, pictures);
        gridView = (GridView) findViewById(R.id.gridview);

        // setup callback for image zoom
        imageAdapter.setCallback(this);

        // pass the images to the imageAdapter
        gridView.setAdapter(imageAdapter);

        // add listener to add picture
        btAddPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent();
                photoPickerIntent.setType("image/*");
                photoPickerIntent.setAction(Intent.ACTION_GET_CONTENT);//
                photoPickerIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(Intent.createChooser(photoPickerIntent, "Select Picture"), SELECT_IMAGE);
            }
        });
    }

    // when user returns from picking pictures
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        // when an Image is picked
        if (requestCode == SELECT_IMAGE && resultCode == RESULT_OK  && data != null) {

            // retrieve a collection of selected images
            ClipData clipData = data.getClipData();

            // iterate over these images
            if (clipData != null ) {
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    try {
                        Bitmap selectedImageBitmap = MediaStore.Images.Media.getBitmap(MemoryDetailsActivity.this.getContentResolver(), clipData.getItemAt(i).getUri());
                        // Convert it to byte
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        selectedImageBitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
                        byte[] image = stream.toByteArray();

                        // save uploaded picture to the cloud as a parsefile
                        final ParseFile file = new ParseFile("image.JPEG", image);
                        file.saveInBackground();

                        // update database
                        ParseQuery<ParseObject> query = ParseQuery.getQuery("Memory");
                        query.getInBackground(memoryId, new GetCallback<ParseObject>() {
                             @Override
                             public void done(ParseObject updatedMemory, ParseException e) {
                                 if (e == null) {
                                     // update pictures
                                     // update local array
                                     pictures.add(file);
                                     imageAdapter.notifyDataSetChanged();
                                     updatedMemory.put("pictures_parse_files", pictures);
                                     updatedMemory.saveInBackground();
                                 } else {
                                     e.getMessage();
                                 }
                             }
                         });


                    } catch (IOException e) {
                        e.getMessage();
                    }
                }
        }

        } else if (resultCode == Activity.RESULT_CANCELED) { // in case user cancels selection
            Toast.makeText(MemoryDetailsActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
        }
    }

    // initiate image zoom dialog
    private void showImageZoomDialog(Bitmap bm, int screenWidth, int screenHeight) {
        FragmentManager fm = getSupportFragmentManager();
        ImageZoomDialogFragment imageZoomDialogFragment = ImageZoomDialogFragment.newInstance(bm, screenWidth, screenHeight);
        imageZoomDialogFragment.show(fm, "fragment_picture_zoom");
    }

    @Override
    public void initiateDialog(Bitmap bitmap) {
        // Get screen size
        Display display = this.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int screenWidth = size.x;
        int screenHeight = size.y;


        showImageZoomDialog(bitmap, screenWidth, screenHeight);
    }


}
