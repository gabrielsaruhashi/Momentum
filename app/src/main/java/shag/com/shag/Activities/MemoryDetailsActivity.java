package shag.com.shag.Activities;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import com.parse.ParseLiveQueryClient;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SubscriptionHandling;

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
    private ViewPager mPager;
    private int currentSliderPage = 0;
    CircleIndicator indicator;
    private TreeSet facebookPermissionsSet;
    private ArrayList<String> participantsIds;
    private ArrayList<String> participantsFacebookIds;
    private long facebookAlbumId;
    private ArrayList<String> userPicturesIds;
    private Handler handler;
    private Runnable Update;
    private int adapterPosition;
    private ArrayList<String> userImageUrls;
    private ParseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory_details);
        ButterKnife.bind(this);

        // instantiate current user
        currentUser = ParseApplication.getCurrentUser();

        // unwrap intents
        memoryId = getIntent().getStringExtra(Memory.class.getSimpleName());
        adapterPosition = getIntent().getIntExtra("position", 0);

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

        // instantiate userImageUrls. If there are no picture, instantiate new array list
        userImageUrls = (pictures == null) ? new ArrayList<String>() : memory.getUserImageUrls();

        // support variable to check whether user added pictures
        initialNumberOfPictures = pictures.size();

        // initialize user pictures for the parse live query
        userPicturesIds = new ArrayList<String>();

        // set adapters
        imageAdapter = new ImageAdapter(this, pictures, userImageUrls);

        // for shared animation
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
        setupLiveQuery();

        // instantiate facebook album id
        facebookAlbumId =  Long.valueOf(memory.getFacebookAlbumId());

        // check if user already gave publishing permissions
        if (!facebookPermissionsSet.contains("publish_actions")) {
            // if not, get additional publishing permission
            LoginManager.getInstance().logInWithPublishPermissions(
                    MemoryDetailsActivity.this,
                    Arrays.asList("publish_actions"));
        }

        //TODO if album already exists, just add photo
        // add listener to facebook share
        btFacebookShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                // instantiate arraylist of participants facebook id
                participantsFacebookIds = memory.getParticipantsFacebookIds();

                createFacebookAlbum();

            }
        });

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        myToolbar.setTitle(memory.getMemoryName());


    }

    public void setupLiveQuery() {
        // listen for updates in this memory
        ParseLiveQueryClient parseLiveQueryClient = ParseLiveQueryClient.Factory.getClient();
        ParseQuery<Memory> parseQuery = ParseQuery.getQuery(Memory.class);
        parseQuery.whereEqualTo("event_id", memory.getEventId());

        // Connect to Parse server
        SubscriptionHandling<Memory> subscriptionHandling = parseLiveQueryClient.subscribe(parseQuery);

        // Listen for UPDATE events
        subscriptionHandling.handleEvent(SubscriptionHandling.Event.UPDATE, new
                SubscriptionHandling.HandleEventCallback<Memory>() {
                    @Override
                    public void onEvent(ParseQuery<Memory> query, Memory object) {
                        // get live query's updated pictures
                        ArrayList<ParseFile> liveQueryPictures = object.getPicturesParseFiles();
                        //TODO discover if there is a way to fix in case other user uploads at the same time
                        // listen for new pictures
                        if (pictures.size() < liveQueryPictures.size()) {
                            // get new pictures
                            ArrayList<ParseFile> newPictures = new ArrayList<ParseFile>(liveQueryPictures.subList(pictures.size(),liveQueryPictures.size()));

                            // iterate through new pictures to verify which ones were shared by current user
                            for (ParseFile file: newPictures) {
                                // if one of the new pictures was posted by user, remove it from the new pictures array
                                if (userPicturesIds.contains(file.getName())) {
                                    newPictures.remove(file);
                                }
                            }

                            // add new pictures to the bottom
                            pictures.addAll(newPictures);

                            // RecyclerView updates need to be run on the UI thread
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    imageAdapter.notifyDataSetChanged();
                                    sliderAdapter.notifyDataSetChanged();
                                    indicator.setViewPager(mPager);

                                }
                            });
                        }
                    }


                });
    }
    public void createFacebookAlbum() {
        // on click create album, create new album and share pics
        ArrayList<Integer> contributorsArrayList = getIntegerArray(participantsFacebookIds);
        int[] contributors =  toIntArray(contributorsArrayList);

        // if album hasnt been created yet
        if (facebookAlbumId == 0) {
            fbClient.postFacebookAlbum(contributors, memory.getMemoryName(), new GraphRequest.Callback() {
                @Override
                public void onCompleted(GraphResponse response) {
                    try {
                        Toast.makeText(MemoryDetailsActivity.this, "Successfully created new album!", Toast.LENGTH_SHORT).show();
                        long facebookAlbumId = response.getJSONObject().getLong("id");
                        // post all pictures of the album
                        if (pictures != null && pictures.size() > 0) {
                            // upload pictures to facebook
                            uploadPicturesFb(pictures, facebookAlbumId);

                            // store facebook album id to the database
                            memory.setFacebookAlbumId(facebookAlbumId);
                            // set the index of the last picture shared
                            memory.setIndexOfLastPictureShared(pictures.size());

                            memory.saveInBackground();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }  else { // else upload new pictures to album
            // check if user uploaded new pictures
            if (memory.getIndexOfLastPictureShared() < pictures.size()) {
                ArrayList<ParseFile> newPictures = new ArrayList<ParseFile>(pictures.subList(memory.getIndexOfLastPictureShared(), pictures.size()));
                // post pictures
                uploadPicturesFb(newPictures, facebookAlbumId);
                // update index of last picture shared on facebook
                memory.setIndexOfLastPictureShared(pictures.size());
                memory.saveInBackground();
            } else {
                Toast.makeText(MemoryDetailsActivity.this, "No new pictures to be posted", Toast.LENGTH_SHORT).show();
            }
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
        handler = new Handler();
        Update = new Runnable() {
            public void run() {
                // when the slider page goes through all the pictures in the array
                if (currentSliderPage >= pictures.size()) {
                    currentSliderPage = 0;
                }
                // move to next image
                mPager.setCurrentItem(currentSliderPage++, true);
                Log.i("Debug_Page", ""+currentSliderPage);
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
            final ArrayList<ParseFile> userUploadedPictures = new ArrayList<>();
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

                        // add to array
                        userUploadedPictures.add(file);

                        // save user profile picture to array
                        userImageUrls.add(currentUser.getString("profile_image_url"));

                        // keep track of files uploaded by user
                        userPicturesIds.add(file.getName());

                        file.saveInBackground();

                    } catch (IOException e) {
                        e.getMessage();
                    }
                }

                // after going through all the picture, update database
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Memory");
                query.getInBackground(memoryId, new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject updatedMemory, ParseException e) {
                        if (e == null) {
                            // update pictures
                            // update local array
                            pictures.addAll(userUploadedPictures);
                            imageAdapter.notifyDataSetChanged();
                            sliderAdapter.notifyDataSetChanged();
                            indicator.setViewPager(mPager);

                            updatedMemory.put("pictures_parse_files", pictures);
                            // update senders
                            updatedMemory.put("user_image_urls", userImageUrls);
                            updatedMemory.saveInBackground();
                        } else {
                            e.getMessage();
                        }
                    }
                });
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
            if (facebookAlbumId == 0 && pictures.size() > 0 && memory.getCoverPictureUrl() == null) {
                memory.setCoverPictureUrl(pictures.get(0).getUrl());
                memory.saveInBackground();
                // return intent to update local memory album
                Intent returnIntent = new Intent();
                returnIntent.putExtra("pictureCoverUrl",pictures.get(0).getUrl());
                returnIntent.putExtra("position", adapterPosition);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();

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
                            int mostLikesIndex = 0;
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


                                // if pic has any likes, add to the likes counter and check if it is more popular
                                if (likes != null ) {
                                    int numberOfLikes = likes.getJSONArray("data").length();

                                    totalNumberOfFacebookLikes += numberOfLikes;
                                    // check if current picture has more likes
                                    if (numberOfLikes > mostLikes) {
                                        // save the highest number of likes
                                        mostLikes = numberOfLikes;
                                        // get the picture
                                        mostLikesUrl = pictureData.getJSONArray("images").getJSONObject(0).getString("source");
                                        mostLikesIndex = i;
                                    }
                                }
                            }

                            // at the end of the for loop, update total number of facebook likes and cover picture
                            memory.setTotalFacebookLikes(totalNumberOfFacebookLikes);

                            if (mostLikesUrl != null) { // ensure that the url is valid
                                memory.setCoverPictureUrl(mostLikesUrl);
                                // reorder memories, so that the one with most likes becomes the first element
                                ParseFile mostLikedPicture = pictures.get(mostLikesIndex);
                                pictures.remove(mostLikesIndex);
                                pictures.add(0, mostLikedPicture);

                                // save the new pictures array
                                memory.setPicturesParseFiles(pictures);

                            } else {
                                // if there are no images with most likes, just set it to be the first picture added
                                memory.setCoverPictureUrl(pictures.get(0).getUrl());
                            }
                            // save
                            memory.saveInBackground();

                            // stop counter thread (not working rn)
                            handler.removeCallbacks(Update);

                        } catch (JSONException e) {
                            e.getMessage();
                        }
                    }
                });
            }

        }

    }


    // helper to return an arraylist of integers with the name of the contributors
    private ArrayList<Integer> getIntegerArray(ArrayList<String> stringArray) {
        ArrayList<Integer> result = new ArrayList<Integer>();
        for(String stringValue : stringArray) {
            try {
                //Convert String to Integer, and store it into integer array list.
                result.add(Integer.parseInt(stringValue));
            } catch(NumberFormatException nfe) {
                //System.out.println("Could not parse " + nfe);
                Log.w("NumberFormat", "Parsing failed! " + stringValue + " can not be an integer");
            }
        }
        return result;
    }

    int[] toIntArray(ArrayList<Integer> list){
        int[] ret = new int[list.size()];
        for(int i = 0;i < ret.length;i++)
            ret[i] = list.get(i);
        return ret;
    }

    @Override
    public void onBackPressed() {
        if (memory.getCoverPictureUrl() == null && memory.getPicturesParseFiles().size() > 0) {
            // return intent to update local memory album
            Intent returnIntent = new Intent();
            returnIntent.putExtra("pictureCoverUrl",pictures.get(0).getUrl());
            returnIntent.putExtra("position", adapterPosition);
            setResult(Activity.RESULT_OK, returnIntent);
        }

        supportFinishAfterTransition();
    }


}
