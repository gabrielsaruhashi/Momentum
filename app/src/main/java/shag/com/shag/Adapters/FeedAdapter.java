package shag.com.shag.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.parse.GetCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import shag.com.shag.Activities.ChatActivity;
import shag.com.shag.Models.Event;
import shag.com.shag.Models.Message;
import shag.com.shag.Other.ParseApplication;
import shag.com.shag.R;

/**
 * Created by gabesaruhashi on 7/10/17.
 */

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.ViewHolder> {

    // list of tweets
    ArrayList<Event> events;

    // pass in the Tweets array in the constructor
    public FeedAdapter(ArrayList<Event> events) {
        this.events = events;
    }

    ;
    // initialize context
    Context context;

    // current user's info
    private ParseUser currentUser;
    private String userFacebookId;

    // creates and inflates a new view; for each row, inflate the layout and cache references
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // get context and inflate view
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // create the view using the item_feed layout
        View feedView = inflater.inflate(R.layout.item_feed, parent, false);

        // Return a new holder instance
        FeedAdapter.ViewHolder viewHolder = new FeedAdapter.ViewHolder(feedView);

        // instantiate id's
        currentUser = ParseApplication.getCurrentUser();

        // instantiate user facebook id
        HashMap data = (HashMap) currentUser.getMap("authData");
        HashMap facebookData = (HashMap) data.get("facebook");
        userFacebookId = (String) facebookData.get("id");

        return viewHolder;
    }

    // associates an inflated view to a new item / binds the values based on the position of the element
    @Override
    public void onBindViewHolder(FeedAdapter.ViewHolder holder, int position) {
        // populate the views
        Event event = events.get(position);
        if (!event.getIsEventPrivate()) {
            //showMap(event, holder);
        }
        holder.tvBody.setText(event.getDescription());
        //TODO getDeadline is returning null
        holder.tvRelativeTime.setText(getTimeRemaining(event.getDeadline()));
        if (event.getEventOwner() != null) {
            holder.tvEventOwnerName.setText(event.getEventOwner().getString("name"));
        }

        //TODO: change categories
        if (event.getCategory().equals("Chill")) {
            holder.ivCategory.setImageResource(R.drawable.ic_iying_down);
            holder.ivCategory.setBackgroundResource(R.drawable.chill_circle);
        } else if (event.getCategory().equals("Sports")) {
            holder.ivCategory.setImageResource(R.drawable.ic_sports);
            holder.ivCategory.setBackgroundResource(R.drawable.sports_circle);
        } else if (event.getCategory().equals("Party")) {
            holder.ivCategory.setImageResource(R.drawable.ic_party1);
            holder.ivCategory.setBackgroundResource(R.drawable.party_circle);
        } else if (event.getCategory().equals("Food")) {
            holder.ivCategory.setImageResource(R.drawable.ic_food1);
            holder.ivCategory.setBackgroundResource(R.drawable.food_circle);
        } else if (event.getCategory().equals("Music")) {
            holder.ivCategory.setImageResource(R.drawable.ic_music1);
            holder.ivCategory.setBackgroundResource(R.drawable.music_circle);
        } else {
            holder.ivCategory.setImageResource(R.drawable.ic_misc1);
            holder.ivCategory.setBackgroundResource(R.drawable.misc_circle);
        }
        ColorFilter filterWhite = new LightingColorFilter(Color.BLACK, Color.WHITE);
        holder.ivCategoryBar.setBackgroundResource(findCorrectColor(event));
        holder.ivCategory.setColorFilter(filterWhite);

        if (isAlreadyInterested(currentUser.getObjectId(), event)) {
            holder.btJoin.setBackgroundColor(ContextCompat.getColor(context, R.color.medium_gray));
            holder.btJoin.setText("Joined");

        } else {
            holder.btJoin.setBackgroundColor(ContextCompat.getColor(context, findCorrectColor(event)));
            holder.btJoin.setText("Join");
        }

        // get icon url
        String url = "";
        if (event.getEventOwner() != null) {
            url = event.getEventOwner().getString("profile_image_url").replace("_normal", "");
        }
        // load user profile image using glide
        Glide.with(context)
                .load(url)
                .bitmapTransform(new CropCircleTransformation(context))
                .into(holder.ivProfileImage);

        ArrayList<String> participants = event.getParticipantsIds();
        //ignore index zero, which is the event owner
        boolean isMe1 = loadUserIntoIndex(participants, 1, holder.ivFriend1);
        boolean isMe2 = loadUserIntoIndex(participants, 2, holder.ivFriend2);
        boolean isMe3 = loadUserIntoIndex(participants, 3, holder.ivFriend3);

//        if (!isMe1) {
//            loadUserIntoIndex(participants, 4, holder.ivFriend1);
//        } else if (!isMe2) {
//            loadUserIntoIndex(participants, 4, holder.ivFriend2);
//        } else if (!isMe3) {
//            loadUserIntoIndex(participants, 4, holder.ivFriend3);
//        }

        showLastMessage(event, holder);
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    // creates ViewHolder class
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // Automatically finds each field by the specified ID
        @BindView(R.id.tvEventOwnerName)
        TextView tvEventOwnerName;
        @BindView(R.id.tvBody)
        TextView tvBody;
        @BindView(R.id.tvRelativeTime)
        TextView tvRelativeTime;
        @BindView(R.id.ivProfileImage)
        ImageView ivProfileImage;
        @BindView(R.id.btJoin)
        Button btJoin;
        @BindView(R.id.ivCategory)
        ImageView ivCategory;
        @BindView(R.id.ivFriend1)
        ImageView ivFriend1;
        @BindView(R.id.ivFriend2)
        ImageView ivFriend2;
        @BindView(R.id.ivFriend3)
        ImageView ivFriend3;
        @BindView(R.id.ivCategoryBar)
        ImageView ivCategoryBar;
        @BindView(R.id.ivMap)
        ImageView ivMap;
        @BindView(R.id.tvLastMessageTime)
        TextView tvLastMessageTime;
        @BindView(R.id.tvLastMessage)
        TextView tvLastMessage;
        @BindView(R.id.icRightArrow)
        ImageView icRightArrow;
        @BindView(R.id.ivDivider) ImageView ivDivider;
        @BindView(R.id.rlChatInfo)
        RelativeLayout rlChatInfo;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            // sets click listener for events' details
            itemView.setOnClickListener(this);
            // set click listener for quick join shortcut
            btJoin.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            // gets item position
            int position = getAdapterPosition();

            if (position != RecyclerView.NO_POSITION) {
                // get the event at the position
                Event event = events.get(position);

                // set up switch to manage the different click listeners
                switch (v.getId()) {
                    case R.id.btJoin:
                        if (isAlreadyInterested(currentUser.getObjectId(), event)) {
                            removeEvent(currentUser.getObjectId(), event, btJoin);
                            //onBindViewHolder(this, position);
                        } else {
                            joinEvent(currentUser.getObjectId(), event, btJoin);
                            //onBindViewHolder(this, position);
                        }
                        break;
                    // if user presses viewholder, show more details of activity
                    default:
                        //showMoreDetails(event, btJoin);
                        break;
                }

            }
        }
    }

    // easy access to the context object in the recyclerview
    private Context getContext() {
        return context;
    }

    // clear the adapter
    public void clear() {
        events.clear();
        notifyDataSetChanged();
    }
    /*
    // when user clicks itemView, shows more details (map, meeting time, friends that are going, etc)
    private void showMoreDetails(final Event event, final Button joinStatus) {
        // inflate message_item.xml view
        View messageView = LayoutInflater.from(context).
                inflate(R.layout.fragment_event_details, null);
        // Create alert dialog builder
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        // set message_item.xml to AlertDialog builder
        alertDialogBuilder.setView(messageView);
        Log.i("DEBUGSHOW", event.participantsIds.toString());
        // Create alert dialog
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                // check if user already joined the event
                if (isAlreadyInterested(currentUser.getObjectId(), event)) {
                    alertDialog.getButton(BUTTON_POSITIVE).setBackgroundColor(ContextCompat.getColor(context, R.color.medium_gray));
                    alertDialog.getButton(BUTTON_POSITIVE).setText("Joined");
                } else {
                    alertDialog.getButton(BUTTON_POSITIVE).setBackgroundColor(ContextCompat.getColor(context, colorId));
                    alertDialog.getButton(BUTTON_POSITIVE).setText("Join");
                }
                alertDialog.getButton(BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.white));
                // get views
                TextView tvEventOwnerName = (TextView) alertDialog.findViewById(R.id.tvEventOwnerName);
                TextView tvBody = (TextView) alertDialog.findViewById(R.id.tvBody);
                TextView tvRelativeTime = (TextView) alertDialog.findViewById(R.id.tvRelativeTime);
                ImageView ivProfileImage = (ImageView) alertDialog.findViewById(R.id.ivProfileImage);
                // populate views
                tvEventOwnerName.setText('@' + event.getEventOwnerName());
                tvBody.setText(event.getDescription());
                tvRelativeTime.setText(event.getDeadline().toString());
                //TODO upload image of event owner
                Glide.with(context)
                        .load(event.user.profileImageUrl)
                        .bitmapTransform(new RoundedCornersTransformation(context, 15, 0))
                        .into(ivProfileImage);
            }
        });
        // Configure dialog button (OK)
        alertDialog.setButton(BUTTON_POSITIVE, "Join",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //TODO get current user
                        // if user is already interested, remove; else, join
                        if (isAlreadyInterested(currentUser.getObjectId(), event)) {
                            removeEvent(currentUser.getObjectId(), event, joinStatus);
                        } else {
                            joinEvent(currentUser.getObjectId(), event, joinStatus);
                        }
                    }
                });
        // Configure dialog button (Cancel)
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        // Display the dialog
        alertDialog.show();
    } */

    public void joinEvent(final String userId, final Event event, final Button joinStatus) {
        final ArrayList<String> updatedParticipantsIds = event.getParticipantsIds();
        updatedParticipantsIds.add(currentUser.getObjectId());

        final ArrayList<String> updatedParticipantsFacebookIds = event.getParticipantsFacebookIds();
        updatedParticipantsFacebookIds.add(userFacebookId);

        // specify which class to query
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
        // include user pointer
        //query.include("User_event_owner");

        // return object with specific id
        query.getInBackground(event.getEventId(), new GetCallback<ParseObject>() {
            public void done(ParseObject object, com.parse.ParseException e) {
                if (e == null) {
                    // update local object
                    event.setParticipantsIds(updatedParticipantsIds);
                    event.setParticipantsFacebookIds(updatedParticipantsFacebookIds);
                    // update database
                    object.put("participants_id", updatedParticipantsIds);
                    object.put("participants_facebook_ids", updatedParticipantsFacebookIds);
                    object.saveInBackground();

                    // get hashmap & category
                    String category = object.getString("category");

                    HashMap<String, List<Object>> hm = (HashMap) currentUser.getMap("categories_tracker");

                    // update category counter
                    List<Object> categoryData = hm.get(category);
                    int oldCounter = (int) categoryData.get(0);
                    categoryData.set(0, oldCounter + 1);
                    hm.put(category, categoryData);

                    currentUser.put("categories_tracker", hm);
                    currentUser.saveInBackground();
                    /*
                    // update recent friends tracker
                    ArrayList<String> outdatedRecentFriends = (ArrayList) currentUser.getList("recent_friends_ids");
                    currentUser.put("recent_friends_ids", outdatedRecentFriends.add(event.getEventOwner().getObjectId())); */

                    // update UI
                    joinStatus.setBackgroundColor(ContextCompat.getColor(context, R.color.medium_gray));
                    joinStatus.setText("Joined");

                    // subscribes user to this "channel" for notifications
                    ParsePush.subscribeInBackground(event.getEventId());

                    sendJoinedMessage(event.getEventId());
                } else {
                    e.getMessage();
                }
            }
        });
    }

    public void removeEvent(final String userId, final Event event, final Button joinStatus) {
        // update participants id
        final ArrayList<String> updatedParticipantsIds = event.getParticipantsIds();
        updatedParticipantsIds.remove(currentUser.getObjectId());
        // update facebook array
        final ArrayList<String> updatedParticipantsFacebookIds = event.getParticipantsFacebookIds();
        updatedParticipantsFacebookIds.remove(userFacebookId);

        // specify which class to query
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
        // include user pointer
        //query.include("User_event_owner");

        // return object with specific id
        query.getInBackground(event.getEventId(), new GetCallback<ParseObject>() {
            public void done(ParseObject object, com.parse.ParseException e) {
                if (e == null) {
                    // update local object
                    event.setParticipantsIds(updatedParticipantsIds);
                    event.setParticipantsFacebookIds(updatedParticipantsFacebookIds);
                    // update database
                    object.put("participants_id", updatedParticipantsIds);
                    object.put("participants_facebook_ids", updatedParticipantsFacebookIds);

                    // get hashmap & category
                    String category = object.getString("category");
                    HashMap<String, List<Object>> hm = (HashMap) currentUser.getMap("categories_tracker");

                    // update category counter
                    List<Object> categoryData = hm.get(category);
                    int oldCounter = (int) categoryData.get(0);
                    categoryData.set(0, oldCounter - 1);
                    hm.put(category, categoryData);

                    object.saveInBackground();

                    // update UI
                    joinStatus.setBackgroundColor(ContextCompat.getColor(context, findCorrectColor(event)));
                    joinStatus.setText("Join");

                    // unsubscribes user from this "channel" so they no longer receive notifications
                    ParsePush.unsubscribeInBackground(event.getEventId());
                } else {
                    e.getMessage();
                }
            }
        });
    }

    public boolean isAlreadyInterested(String userId, Event event) {
        ArrayList<String> participants = event.getParticipantsIds();
        if (participants.contains(userId)) {
            return true;
        }
        return false;
    }


    public String getTimeRemaining(Date date) {
        String timeRemaining = "";
        int difference = (int) (TimeUnit.MILLISECONDS.toMinutes(date.getTime() - (new Date()).getTime()));
        int hours = difference / 60;
        if (hours > 0) {
            timeRemaining += hours + "h ";
        }
        int minutes = difference - 60 * hours;
        timeRemaining += minutes + "m";
        return timeRemaining;
    }

    public void sendJoinedMessage(String eventId) {
        Message m = new Message();
        m.setSenderId("InuSHuTqkn");
        m.setBody("Hey! " + currentUser.get("name") + " just joined the chat.");
        m.setEventId(eventId);
        m.setSenderName("Shaggy");
        try {
            m.save();
            HashMap<String, String> payload = new HashMap<>();
            payload.put("customData", currentUser.get("name") + " just joined the chat.");
            payload.put("title", "New message in channel");
            payload.put("channelID", eventId);
            payload.put("senderID", "InuSHuTqkn");
            payload.put("token", ""); //not being used rn
            ParseCloud.callFunctionInBackground("pushChannelTest", payload);
            //TODO: note, this message does not appear as last message in chats (we could fix that though)
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void showMap(Event event, FeedAdapter.ViewHolder holder) {
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        String baseUrl = "https://maps.googleapis.com/maps/api/staticmap?";
        String destination = "";
        try {
            Address destinationLocation = geocoder.getFromLocation(event.getLatitude(), event.getLongitude(), 1).get(0);
            destination = destinationLocation.getAddressLine(0);
            destination = destination.replace(" ", "+");
        } catch (IOException e) {
            e.printStackTrace();
        }

        baseUrl += "center=" + destination;
        baseUrl += "&zoom=17";
        baseUrl += "&size=450x250";
        baseUrl += "&markers=color:blue%7C" + destination;
        baseUrl += "&sensor=false";
        baseUrl += "&key=AIzaSyD5ty8DSE8Irio8xdCvCQMltWpuVDioHTI";
        baseUrl += "&format=png&maptype=roadmap&style=feature:landscape.man_made%7Ccolor:0xebf6fd&style=feature:" +
                "landscape.natural%7Ccolor:0xf3fdfc&style=feature:landscape.natural.landcover%7Ccolor:0xf1fdfc&style=" +
                "feature:landscape.natural.terrain%7Ccolor:0xf8fdf9&style=feature:poi.park%7Ccolor:0xe8fecf&style=feature:" +
                "water%7Celement:geometry.fill%7Ccolor:0xc6eafe&size=480x360";

        holder.ivMap.setVisibility(View.VISIBLE);
        Glide.with(context)
                .load(baseUrl)
                .into(holder.ivMap);
    }

    public boolean loadUserIntoIndex(ArrayList<String> participants, int friendIndex, ImageView view) {
        if (participants.size() > friendIndex) {
            try {
                ParseUser user = ParseUser.getQuery().get(participants.get(friendIndex));
                //if (!user.getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {
                Glide.with(context)
                        .load(user.getString("profile_image_url").replace("_normal", ""))
                        .bitmapTransform(new CropCircleTransformation(context))
                        .into(view);
                return true;
                // } else {
                //    view.setVisibility(View.GONE);
                //     return false;
                // }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        //load failed but should not try again
        view.setVisibility(View.GONE);
        return true;
    }

    public int findCorrectColor(Event event) {
        if (event.getCategory().equals("Chill")) {
            return R.color.chill_color;
        } else if (event.getCategory().equals("Sports")) {
            return R.color.sports_color;
        } else if (event.getCategory().equals("Party")) {
            return R.color.party_color;
        } else if (event.getCategory().equals("Food")) {
            return R.color.food_color;
        } else if (event.getCategory().equals("Music")) {
            return R.color.music_color;
        }

        return R.color.misc_color;
    }

    public String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();

            relativeDate = relativeDate.replace(" seconds", "s");
            relativeDate = relativeDate.replace(" second", "s");
            relativeDate = relativeDate.replace(" minutes", "m");
            relativeDate = relativeDate.replace(" minute", "m");
            relativeDate = relativeDate.replace(" hours", "h");
            relativeDate = relativeDate.replace(" hour", "h");
            relativeDate = relativeDate.replace(" ago", "");

        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }

    public void showLastMessage(final Event event, ViewHolder holder) {
        ArrayList<String> participants = event.getParticipantsIds();
        if (!participants.contains(currentUser.getObjectId())) {
            holder.ivDivider.setVisibility(View.GONE);
            holder.rlChatInfo.setVisibility(View.GONE);
            return;
        }

        holder.icRightArrow.setColorFilter(ContextCompat.getColor(context, findCorrectColor(event)));
        holder.ivDivider.setVisibility(View.VISIBLE);
        holder.rlChatInfo.setVisibility(View.VISIBLE);

        try {
            Message lastMessage = event.getParseObject("last_message_sent").fetch();
            if (lastMessage.getBody() == null) {
                throw new NullPointerException();
            }
            String name = lastMessage.getSenderName();
            name = name.substring(0, name.indexOf(" "));
            holder.tvLastMessage.setText(name + ": " + lastMessage.getBody());

            String rawTime = (lastMessage.getCreatedDate().toString());
            holder.tvLastMessageTime.setText(getRelativeTimeAgo(rawTime));
        } catch (Exception e) {
            holder.tvLastMessage.setText("Tap to chat");
            holder.tvLastMessageTime.setVisibility(View.INVISIBLE);
        }

        holder.rlChatInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("event_id", event.getEventId());
                intent.putExtra("participants_ids", event.getParticipantsIds());
                context.startActivity(intent);
            }
        });
    }
}