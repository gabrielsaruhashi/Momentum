package shag.com.shag.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import shag.com.shag.R;

public class SelectPublicEventTypeActivity extends AppCompatActivity {

    ImageView food;
    ImageView music;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_public_event_type);

        food = (ImageView) findViewById(R.id.ivFood);
        music = (ImageView) findViewById(R.id.ivMusic);

        Glide.with(this).load("http://maps.google.com/mapfiles/ms/icons/yellow-dot.png").into(food);
        Glide.with(this).load("http://maps.google.com/mapfiles/ms/icons/yellow-dot.png").into(food);

        food.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SelectPublicEventTypeActivity.this, SelectPublicMapActivity.class);
                i.putExtra("Event Type", "Public");
                i.putExtra("Category", "Food");
                startActivity(i);
            }
        });



        music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SelectPublicEventTypeActivity.this, SelectPublicMapActivity.class);
                i.putExtra("Event Type", "Public");
                i.putExtra("Category", "Music");
                startActivity(i);

            }
        });

    }
}
