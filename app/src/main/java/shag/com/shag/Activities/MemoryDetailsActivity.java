package shag.com.shag.Activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import shag.com.shag.Adapters.ImageAdapter;
import shag.com.shag.Models.Memory;
import shag.com.shag.R;

public class MemoryDetailsActivity extends AppCompatActivity {
    @BindView(R.id.tvMemoryName) TextView tvMemoryName;
    @BindView(R.id.btAddPicture) Button btAddPicture;
    @BindView(R.id.ivMemoryBannerPicture) ImageView ivMemoryBannerPicture;
    GridView gridView;

    ImageAdapter imageAdapter;
    ArrayList<String> pictures;
    // create constant for startactivityh
    final static int SELECT_IMAGE = 16;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory_details);
        ButterKnife.bind(this);
        // unwrap memory
        Memory memory = getIntent().getParcelableExtra(Memory.class.getSimpleName());

        // initialize pictures, adapter and gridView
        pictures = memory.getPicturesUrls();
        imageAdapter = new ImageAdapter(this, pictures);
        gridView = (GridView) findViewById(R.id.gridview);

        // pass the images to the imageAdapter
        gridView.setAdapter(imageAdapter);

        // add listener to add picture
        btAddPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);//
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_IMAGE);
            }
        });
    }

    // when user returns from picking pictures
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_IMAGE)
        {
            if (resultCode == Activity.RESULT_OK)
            {
                if (data != null)
                {
                    // get bitmap from picture selected
                    try
                    {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(MemoryDetailsActivity.this.getContentResolver(), data.getData());
                        
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            } else if (resultCode == Activity.RESULT_CANCELED) { // in case user cancels selection
                Toast.makeText(MemoryDetailsActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
