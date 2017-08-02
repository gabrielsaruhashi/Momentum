package shag.com.shag.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import shag.com.shag.Models.Message;
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
    public void onBindViewHolder(ViewHolder holder, int position) {

        Message message = mMessages.get(position);

        // TODO alter this logic to use the sender's info pointer, not the id
        final boolean isMe = message.getSenderId() != null && message.getSenderId().equals(mUserId);
        boolean showPic = true;
        String messageBody = message.getBody();
        if (isMe) {
            holder.imageOther.setVisibility(View.GONE);
            holder.theirBody.setVisibility(View.INVISIBLE);
            holder.tvOtherName.setVisibility(View.GONE);
            //holder.myBody.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
            holder.myBody.setText(messageBody);
            showPic = false;
        } else {
            holder.myBody.setVisibility(View.INVISIBLE);
            holder.imageOther.setVisibility(View.VISIBLE);
            //holder.theirBody.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
            holder.tvOtherName.setVisibility(View.VISIBLE);
            holder.theirBody.setText(messageBody);

            if (message.getSenderName() != null) {
                //check if they sent a message previously
                if (checkDisplayName(message, position)) {
                    String name = message.getSenderName();
                    String firstName = name;
                    if (name.indexOf(" ") > 0) {
                        firstName = name.substring(0, name.indexOf(" "));
                    }
                    holder.tvOtherName.setText(firstName);
                    Log.i("DEBUG_NAME", message.getSenderName());

                } else {
                    //they sent the last message, don't show name and pic again
                    showPic = false;
                    holder.tvOtherName.setVisibility(View.GONE);
                }
            }
        }

        if (showPic) {
            if (message.getSenderProfileImageUrl() == null) {
                // generate a unique, random gravatar
                Glide.with(mContext).load(getProfileUrl(message.getSenderId())).centerCrop()
                        .bitmapTransform(new RoundedCornersTransformation(mContext, 30, 0)).into(holder.imageOther);
            } else {
                if (isMe) {
                    holder.tvOtherName.setVisibility(View.INVISIBLE);
                } else {
                    Glide.with(mContext).load(message.getSenderProfileImageUrl()).centerCrop()
                            .bitmapTransform(new CropCircleTransformation(mContext)).into(holder.imageOther);
                }
            }
        } else {
            if (isMe) {
                holder.imageOther.setVisibility(View.GONE);
            } else {
                holder.imageOther.setVisibility(View.INVISIBLE);
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

        public ViewHolder(View itemView) {
            super(itemView);
            imageOther = (ImageView) itemView.findViewById(R.id.ivProfileOther);
            //imageMe = (ImageView) itemView.findViewById(R.id.ivProfileMe);
            theirBody = (TextView) itemView.findViewById(R.id.tvTheirBody);
            myBody = (TextView) itemView.findViewById(R.id.tvMyBody);
            tvOtherName = (TextView) itemView.findViewById(R.id.tvOtherName);
        }

    }

    public boolean checkDisplayName(Message message, int position) {
        //it's the first message we're displaying (at the top), should show name
        if (position == mMessages.size() - 1) {
            return true;
        }

        Message previous = mMessages.get(position + 1);
        if (previous.getSenderId().equals(message.getSenderId())) {
            return false;
        }

        return true;
    }
}
