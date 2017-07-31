package shag.com.shag.Activities;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.relex.circleindicator.CircleIndicator;
import shag.com.shag.Adapters.ImageAdapter;
import shag.com.shag.Adapters.ImageSliderAdapter;
import shag.com.shag.Clients.FacebookClient;
import shag.com.shag.Fragments.DialogFragments.ImageZoomDialogFragment;
import shag.com.shag.Models.Memory;
import shag.com.shag.Other.ParseApplication;
import shag.com.shag.R;


public class MemoryDetailsActivity extends AppCompatActivity implements ImageAdapter.ImageZoomAdapterCallback{
    //@BindView(R.id.tvMemoryName) TextView tvMemoryName;
    @BindView(R.id.btAddPicture) Button btAddPicture;
    @BindView(R.id.btFacebookShare) Button btFacebookShare;
    // @BindView(R.id.ivMemoryBannerPicture) ImageView ivMemoryBannerPicture;
    GridView gridView;
    // adapters and arraylist
    ImageAdapter imageAdapter;
    ImageSliderAdapter sliderAdapter;
    ArrayList<ParseFile> pictures;
    FacebookClient fbClient;
    // create constant for start activity
    final static int SELECT_IMAGE = 16;
    private ParseObject memoryDbObject;
    private Memory memory;
    private String memoryId;
    private int initialNumberOfPictures;
    // slideshow components
    private static ViewPager mPager;
    private static int currentSliderPage = 0;
    CircleIndicator indicator;
    private TreeSet facebookPermissionsSet;
    private ArrayList<String> participantsIds;
    private ArrayList<String> participantsFacebookIds;
    private long facebookAlbumId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory_details);
        ButterKnife.bind(this);
        // TODO pass entire memory instead
        // memory = getIntent().getParcelableExtra(Memory.class.getSimpleName());
        memoryId = getIntent().getStringExtra(Memory.class.getSimpleName());

        // instantiate client and facebook permission set
        fbClient = ParseApplication.getFacebookRestClient();
        facebookPermissionsSet = ParseApplication.getFacebookPermissionsSet();

        // get memory
        ParseQuery<Memory> query = ParseQuery.getQuery("Memory");
        try {
            //memoryDbObject = query.get(memoryId);
            // memory = Memory.fromParseObject(memoryDbObject);
            memory = query.get(memoryId);
        } catch (ParseException e) {
            e.getMessage();
        }
        // initialize participantsIds
        participantsIds = memory.getParticipantsIds();
        // initialize pictures, adapter and gridView
        pictures = memory.getPicturesParseFiles();

        // save original number of pictures to later check if user added pictures
        initialNumberOfPictures = pictures.size();

        // set adapters
        imageAdapter = new ImageAdapter(this, pictures);
        sliderAdapter = new ImageSliderAdapter(MemoryDetailsActivity.this, pictures);

        // get gridview
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


        // populate image slider
        initImageSlider();

        // instantiate facebook album id
        facebookAlbumId =  Long.valueOf(memory.getFacebookAlbumId());

        //TODO if album already exists, just add photo
        // add listener to facebook share
        btFacebookShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // check if user already gave publishing permissions
                if (!facebookPermissionsSet.contains("publish_actions")) {
                    // if not, get additional publishing permission
                    LoginManager.getInstance().logInWithPublishPermissions(
                            MemoryDetailsActivity.this,
                            Arrays.asList("publish_actions"));
                }

                // instantiate arraylist of participants facebook id
                participantsFacebookIds = memory.getParticipantsFacebookIds();

                createFacebookAlbum();

            }
        });
    }

    public void createFacebookAlbum() {
        // on click create album, create new album and share pics
        // if album hasnt been created yet
        if (facebookAlbumId == 0) {
            fbClient.postFacebookAlbum(participantsFacebookIds, memory.getMemoryName(), new GraphRequest.Callback() {
                @Override
                public void onCompleted(GraphResponse response) {
                    try {
                        Toast.makeText(MemoryDetailsActivity.this, "Create new album!", Toast.LENGTH_SHORT).show();
                        long facebookAlbumId = response.getJSONObject().getLong("id");
                        // post all pictures of the album
                        if (pictures != null && pictures.size() > 0) {
                            uploadPicturesFb(pictures, facebookAlbumId);
                            memory.setFacebookAlbumId(facebookAlbumId);
                            memory.saveInBackground();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

    }

    public void uploadPicturesFb(ArrayList<ParseFile> pictures, long albumId) {
        for (ParseFile picture: pictures) {
            try {
                byte[] pictureData = picture.getData();
                fbClient.postPictureToAlbum(pictureData, albumId, new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        Toast.makeText(MemoryDetailsActivity.this, "Pictures were uploaded successfully!", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (ParseException e) {
                e.getMessage();
            }

        }
    }


    public void initImageSlider() {

        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(sliderAdapter);
        indicator = (CircleIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(mPager);

        // Auto start of viewpager
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                //TODO check if this is working
                // when the slider page goes through all the pictures in the array
                if (currentSliderPage == pictures.size()) {
                    currentSliderPage = 0;
                }
                // move to next image
                mPager.setCurrentItem(currentSliderPage++, true);
            }
        };
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 2500, 2500);

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
                                     sliderAdapter.notifyDataSetChanged();
                                     indicator.setViewPager(mPager);

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

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // in case user uploads pictures, update the album status

        if (initialNumberOfPictures != pictures.size()) {

            // query for the event, and update it with the last picture uploaded
            // if user hasnt yet created an album, but has uploaded pictures
            if (facebookAlbumId == 0 && pictures.size() > 0) {
                memory.setCoverPictureUrl(pictures.get(pictures.size() - 1).getUrl());
                memory.saveInBackground();
            } else if (facebookAlbumId != 0) { // else query for the facebook pictures, and get the one that has the most amount of likes
                fbClient.getAlbumPhotos(facebookAlbumId, new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {

                        try {
                            // get array with pictures from album
                            JSONArray fbPicturesArray = response.getJSONObject().getJSONArray("data");

                            // support variables
                            String mostLikesUrl = new String();
                            int mostLikes = 0;
                            int totalNumberOfFacebookLikes = 0;

                            // iterate through the pictures array
                            for (int i = 0; i < fbPicturesArray.length(); i++) {
                                JSONObject pictureData = fbPicturesArray.getJSONObject(i);
                                JSONObject likes;
                                // if picture does not have likes, just assign it to null
                                try {
                                    likes = pictureData.getJSONObject("likes");
                                } catch (JSONException e) {
                                    e.getMessage();
                                    likes = null;
                                }


                                // if pic has any likes, add to the likes counter and check if it is the most populatr
                                if (likes != null ) {
                                    int numberOfLikes = likes.getJSONArray("data").length();

                                    totalNumberOfFacebookLikes += numberOfLikes;
                                    // check if current picture has more likes
                                    if (numberOfLikes > mostLikes) {
                                        mostLikes = numberOfLikes;
                                        // get the pictures
                                        mostLikesUrl = pictureData.getJSONArray("images").getJSONObject(0).getString("source");
                                    }
                                }
                            }

                            // at the end of the for loop, update total number of facebook likes and cover picture
                            memory.setTotalFacebookLikes(totalNumberOfFacebookLikes);
                            if (mostLikesUrl != null) { // ensure that the url is valid
                                memory.setCoverPictureUrl(mostLikesUrl);
                            } else {
                                // if there are no images with most likes, just set it to be the last picture added
                                memory.setCoverPictureUrl(pictures.get(pictures.size() - 1).getUrl());
                            }
                            // save
                            memory.saveInBackground();

                        } catch (JSONException e) {
                            e.getMessage();
                        }
                    }
                });
            }

        }

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
