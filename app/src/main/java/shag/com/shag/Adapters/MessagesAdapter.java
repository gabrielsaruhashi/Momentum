package shag.com.shag.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import shag.com.shag.Models.Message;
import shag.com.shag.R;

/**
 * Created by gabesaruhashi on 7/14/17.
 */

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder>{

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
        final boolean isMe = message.getSenderId()!= null && message.getSenderId().equals(mUserId);

        if (isMe) {
            holder.imageMe.setVisibility(View.VISIBLE);
            holder.imageOther.setVisibility(View.INVISIBLE);
            holder.body.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
            holder.tvOtherName.setVisibility(View.GONE);
        } else {
            holder.imageOther.setVisibility(View.VISIBLE);
            holder.imageMe.setVisibility(View.INVISIBLE);
            holder.body.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
            holder.tvOtherName.setVisibility(View.VISIBLE);
            if (message.getSenderName() != null) {
                holder.tvOtherName.setText(message.getSenderName());
                Log.i("DEBUG_NAME", message.getSenderName());
            }
        }

        final ImageView profileView = isMe ? holder.imageMe : holder.imageOther;
        //Glide.with(mContext).load(getProfileUrl(message.getSenderId())).into(profileView);

        if (message.getSenderProfileImageUrl() == null) {
            // generate a unique, random gravatar
            Glide.with(mContext).load(getProfileUrl(message.getSenderId())).centerCrop()
                    .bitmapTransform(new RoundedCornersTransformation(mContext, 30, 0)).into(profileView);
        } else {
            Glide.with(mContext).load(message.getSenderProfileImageUrl()).centerCrop()
                    .bitmapTransform(new RoundedCornersTransformation(mContext, 30, 0)).into(profileView);
        }

        String messageBody = message.getBody();
        holder.body.setText(messageBody);
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
        ImageView imageMe;
        TextView body;
        TextView tvOtherName;

        public ViewHolder(View itemView) {
            super(itemView);
            imageOther = (ImageView)itemView.findViewById(R.id.ivProfileOther);
            imageMe = (ImageView)itemView.findViewById(R.id.ivProfileMe);
            body = (TextView)itemView.findViewById(R.id.tvBody);
            tvOtherName = (TextView) itemView.findViewById(R.id.tvOtherName);
        }
    }
}
