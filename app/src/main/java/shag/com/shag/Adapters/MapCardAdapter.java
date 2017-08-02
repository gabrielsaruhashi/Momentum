package shag.com.shag.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import shag.com.shag.Activities.SelectEventDeadlineActivity;
import shag.com.shag.R;

/**
 * Created by samrabelachew on 7/30/17.
 */


public class MapCardAdapter extends PagerAdapter {
    private ArrayList<HashMap<String, Object>> cardData;
    private LayoutInflater inflater;
    private Context context;

//    @Override
//    public float getPageWidth (int position) {
//        return 0.93f;
//    }
    @Override
    public int getCount() {
        return cardData.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return o == view;
    }

    public MapCardAdapter(Context context, ArrayList<HashMap<String, Object>> cardData) {
        this.context = context;
        this.cardData = cardData;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public Object instantiateItem(final ViewGroup container, int position) {
        final View card = inflater.inflate(R.layout.item_public_event_card, container, false);

        final HashMap<String,Object> data= cardData.get(position);

        TextView cardTitle = (TextView) card.findViewById(R.id.tvEventNameTitle);
        TextView cardDescription = (TextView) card.findViewById(R.id.tvDescription);
        TextView cardLocation = (TextView) card.findViewById(R.id.tvLocation);
        TextView cardTime = (TextView) card.findViewById(R.id.tvTime);
        ImageView titleBackground = (ImageView) card.findViewById(R.id.ivBanner);

        Button cardCreate = (Button) card.findViewById(R.id.createButton);

        cardTitle.setText((String) data.get("Name"));
        cardDescription.setText((String) data.get("Description"));
        cardLocation.setText((String) data.get("Location"));

        cardCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, SelectEventDeadlineActivity.class);
                i.putExtra("Location", (String) data.get("Location"));
                i.putExtra("Category",(String) data.get("Category"));
                i.putExtra("Event Type", "Public");
                i.putExtra("Lat",(Double) data.get("Lat"));
                i.putExtra("Lng",(Double) data.get("Lng"));
                i.putExtra("Place Name",(String) data.get("Place Name"));
                context.startActivity(i);
            }
        });



        //to container add layout instead of button
        container.addView(card);
        //return layout instead of button
        return card;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        //cast to LinearLayout
        container.removeView((FrameLayout) object);
    }


//
//
//    //adds view (map card) to arraylist of views
//    public int addView (View v)
//    {
//        return addView (v, views.size());
//    }
//
//    //adds view (map card) to arraylist of views @ any position
//    public int addView (View v, int position)
//    {
//        views.add(position, v);
//        return position;
//    }
//
//    //returns number when pager roemoves
//    public int removeView (ViewPager pager, View v)
//    {
//        return removeView (pager, views.indexOf (v));
//    }
//
//
//    public int removeView (ViewPager pager, int position)
//    {
//        pager.setAdapter (null);
//        views.remove (position);
//        pager.setAdapter (this);
//        return position;
//    }
//
//    //returns view based on position
//    public View getView (int position)
//    {
//        return views.get(position);
//    }


}
