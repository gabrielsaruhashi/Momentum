package shag.com.shag.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import shag.com.shag.Models.Message;
import shag.com.shag.Other.ParseApplication;
import shag.com.shag.R;

/**
 * Created by gabesaruhashi on 7/14/17.
 */

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {

    private List<Message> mMessages;
    private Context mContext;
    private String mUserId;

    public MessagesAdapter(Context context, String userId, List<Message> messages) {
        mMessages = messages;
        this.mUserId = userId;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.item_message_chat, parent, false);

        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final Message message = mMessages.get(position);


            holder.myLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Message");

                //if no one liked it before
                if (message.getLikedUsers() == null) {
                    holder.myLikeNumber.setText("1");
                    message.setLikedUsers(new ArrayList<String>());
                    message.updateLikedUsers((ArrayList<String>) message.getLikedUsers(),
                            ParseApplication.getCurrentUser().getObjectId());
                    holder.myLike.setColorFilter(Color.RED);
                    message.saveInBackground();


                }
                //if user hasn't liked it before
                else if (!message.getLikedUsers().contains(ParseApplication.getCurrentUser().getObjectId())) {
                    int newNumber = message.getLikedUsers().size()+1;
                    holder.myLikeNumber.setText(newNumber+"");
                    message.updateLikedUsers((ArrayList<String>) message.getLikedUsers(),
                            ParseApplication.getCurrentUser().getObjectId());
                    holder.myLike.setColorFilter(Color.RED);
                    message.saveInBackground();

                }


            }
        });

        holder.theirLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Message");

                //if no one liked it before
                if (message.getLikedUsers() == null) {
                    holder.theirLikeNumber.setText("1");
                    message.setLikedUsers(new ArrayList<String>());
                    message.updateLikedUsers((ArrayList<String>) message.getLikedUsers(),
                            ParseApplication.getCurrentUser().getObjectId());
                    holder.theirLike.setColorFilter(Color.RED);
                    message.saveInBackground();


                }
                //if user hasn't liked it before
                else if (!message.getLikedUsers().contains(ParseApplication.getCurrentUser().getObjectId())) {
                    int newNumber = message.getLikedUsers().size()+1;
                    holder.theirLikeNumber.setText(newNumber+"");
                    message.updateLikedUsers((ArrayList<String>) message.getLikedUsers(),
                            ParseApplication.getCurrentUser().getObjectId());
                    holder.theirLike.setColorFilter(Color.RED);
                    message.saveInBackground();

                }

            }
        });

        // TODO alter this logic to use the sender's info pointer, not the id
        final boolean isMe = message.getSenderId() != null && message.getSenderId().equals(mUserId);
        String messageBody = message.getBody();
        if (isMe) {
            holder.theirBody.setVisibility(View.GONE);
            holder.theirLike.setVisibility(View.GONE);
            holder.theirLikeNumber.setVisibility(View.GONE);
            holder.myLike.setVisibility(View.VISIBLE);
            holder.myLikeNumber.setVisibility(View.VISIBLE);
            if (message.getLikedUsers()!=null) {
                holder.myLikeNumber.setText(message.getLikedUsers().size()+"");
            }
            //set like to be red if user has liked it before
            if (message.getLikedUsers() != null && message.getLikedUsers().contains(ParseApplication.getCurrentUser().getObjectId())) {
                holder.myLike.setColorFilter(Color.RED);

            }


            holder.myBody.setVisibility(View.VISIBLE);
            holder.myBody.setText(messageBody);
            if (checkDisplayName(message, position)) {
                //i did not send the last message, show a little space between my message and previous
                holder.tvOtherName.setVisibility(View.INVISIBLE);
                holder.imageOther.setVisibility(View.GONE);
            } else {
                //i did send the last message, show no space
                holder.tvOtherName.setVisibility(View.GONE);
                holder.imageOther.setVisibility(View.GONE);


            }
        } else {
            holder.myBody.setVisibility(View.GONE);
            holder.theirBody.setVisibility(View.VISIBLE);
            holder.theirBody.setText(messageBody);
            holder.myLike.setVisibility(View.GONE);
            holder.theirLike.setVisibility(View.VISIBLE);
            holder.myLikeNumber.setVisibility(View.GONE);
            holder.theirLikeNumber.setVisibility(View.VISIBLE);
            if (message.getLikedUsers()!=null) {
                holder.theirLikeNumber.setText(message.getLikedUsers().size()+"");
            }
            if (message.getLikedUsers()!=null &&
                    message.getLikedUsers().contains(ParseApplication.getCurrentUser().getObjectId())) {
                holder.theirLike.setColorFilter(Color.RED);

            }

            if (message.getSenderName() != null) {
                //check if they sent a message previously
                if (checkDisplayName(message, position)) {
                    holder.imageOther.setVisibility(View.VISIBLE);
                    holder.tvOtherName.setVisibility(View.VISIBLE);
                    String name = message.getSenderName();
                    String firstName = name;
                    if (name.indexOf(" ") > 0) {
                        firstName = name.substring(0, name.indexOf(" "));
                    }
                    holder.tvOtherName.setText(firstName);
                    Log.i("DEBUG_NAME", message.getSenderName());

                    if (message.getSenderProfileImageUrl() == null) {
                        // generate a unique, random gravatar
                        Glide.with(mContext).load(getProfileUrl(message.getSenderId())).centerCrop()
                                .bitmapTransform(new RoundedCornersTransformation(mContext, 30, 0)).into(holder.imageOther);
                    } else {
                        Glide.with(mContext).load(message.getSenderProfileImageUrl()).centerCrop()
                                .bitmapTransform(new CropCircleTransformation(mContext)).into(holder.imageOther);
                    }

                } else {
                    //they sent the last message, don't show name and pic again
                    holder.tvOtherName.setVisibility(View.GONE);
                    holder.imageOther.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    // Create a gravatar image based on the hash value obtained from userId
    private static String getProfileUrl(final String userId) {
        String hex = "";
        try {
            final MessageDigest digest = MessageDigest.getInstance("MD5");
            final byte[] hash = digest.digest(userId.getBytes());
            final BigInteger bigInt = new BigInteger(hash);
            hex = bigInt.abs().toString(16);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "http://www.gravatar.com/avatar/" + hex + "?d=identicon";
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageOther;
        //ImageView imageMe;
        TextView theirBody;
        TextView tvOtherName;
        TextView myBody;
        ImageView myLike;
        ImageView theirLike;
        TextView myLikeNumber;
        TextView theirLikeNumber;

        public ViewHolder(View itemView) {
            super(itemView);
            imageOther = (ImageView) itemView.findViewById(R.id.ivProfileOther);
            //imageMe = (ImageView) itemView.findViewById(R.id.ivProfileMe);
            theirBody = (TextView) itemView.findViewById(R.id.tvTheirBody);
            myBody = (TextView) itemView.findViewById(R.id.tvMyBody);
            tvOtherName = (TextView) itemView.findViewById(R.id.tvOtherName);
            myLike = (ImageView) itemView.findViewById(R.id.ivMyLike);
            theirLike = (ImageView) itemView.findViewById(R.id.ivTheirLike);
            myLikeNumber = (TextView) itemView.findViewById(R.id.tvMyLikeNumber);
            theirLikeNumber = (TextView) itemView.findViewById(R.id.tvTheirLikeNumber);


        }

    }

    //to determine whether to show the other user's name above a message
    public boolean checkDisplayName(Message message, int position) {
        //it's the first message we're displaying (at the top), should show name
        if (position == mMessages.size() - 1) {
            return true;
        }

        //if the message right before this came from the same user, don't show name
        Message previous = mMessages.get(position + 1);
        if (previous.getSenderId() == null) {
            return true;
        }

        if (previous.getSenderId().equals(message.getSenderId())) {
            return false;
        }

        //otherwise, show name
        return true;
    }
}
