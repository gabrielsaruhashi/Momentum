package shag.com.shag.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import shag.com.shag.Models.Poll;
import shag.com.shag.R;

/**
 * Created by samrabelachew on 7/20/17.
 */


public class PollsAdapter extends RecyclerView.Adapter<PollsAdapter.ViewHolder> {
    ArrayList<Poll> polls;
    Poll poll;
    // instantiate context
    Context context;
    RadioButton rb1;
    RadioButton rb2;

    public PollsAdapter(ArrayList<Poll> polls) { this.polls = polls; };

    @Override
    public PollsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // get context and inflate view
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // create the view using the item_feed layout
        View feedView = inflater.inflate(R.layout.item_poll, parent, false);

        // return a new holder instance
        PollsAdapter.ViewHolder viewHolder = new ViewHolder(feedView);
        return viewHolder;

    }

    // creates ViewHolder class
    public class ViewHolder extends RecyclerView.ViewHolder{
        // Automatically finds each field by the specified ID
        @BindView(R.id.tvParticipants) TextView tvParticipants;
        @BindView(R.id.tvBody)
        TextView tvBody;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }


    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // populate the views
        poll = polls.get(position);
    }



    @Override
    public int getItemCount() {
        return polls.size();
    }


}
