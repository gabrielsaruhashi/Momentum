package shag.com.shag.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import shag.com.shag.Models.Poll;
import shag.com.shag.R;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by samrabelachew on 7/20/17.
 */


public class PollsAdapter extends RecyclerView.Adapter<PollsAdapter.ViewHolder> {
    ArrayList<Poll> polls;
    //Poll poll;
    // instantiate context
    Context context;
    RadioButton rb1;
    RadioButton rb2;

    public PollsAdapter(ArrayList<Poll> polls) {
        this.polls = polls;
    }

    ;

    @Override
    public PollsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // get context and inflate view
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        ParseObject.registerSubclass(Poll.class);

        // create the view using the item_feed layout
        View feedView = inflater.inflate(R.layout.item_poll, parent, false);

        // return a new holder instance
        PollsAdapter.ViewHolder viewHolder = new ViewHolder(feedView);
        return viewHolder;

    }


    // creates ViewHolder class
    public class ViewHolder extends RecyclerView.ViewHolder {

        // Automatically finds each field by the specified ID
        @BindView(R.id.tvQuestion)
        TextView tvQuestion;

        @BindView(R.id.rgPoll)
        RadioGroup radio;

        @BindView(R.id.radioChoice1)
        RadioButton radioButton1;
        @BindView(R.id.radioChoice2)
        RadioButton radioButton2;
        @BindView(R.id.radioChoice3)
        RadioButton radioButton3;
        @BindView(R.id.radioChoice4)
        RadioButton radioButton4;

        //Button btVote;
        @BindView(R.id.btVote)
        Button btVote;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            final int[] choice = {3};
            btVote.setOnClickListener(new View.OnClickListener() {


                @Override
                public void onClick(View v) {
                    final Poll poll = polls.get(getAdapterPosition());
                    String id = poll.getObjectId();

                    // specify which class to query
                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Poll");
                    // return object with specific id
                    query.getInBackground(poll.getPollId(), new GetCallback<ParseObject>() {
                        public void done(ParseObject object, com.parse.ParseException e) {
                            if (e == null) {
                                //update local object
                                poll.updateScores(poll.getScores(), poll.getChoices().get(choice[0]));
                                poll.updatePeopleVoted(poll.getPeopleVoted(), ParseUser.getCurrentUser().getObjectId());
                                radioButton1.setText(poll.getChoices().get(0) + ": " + poll.getScores().get(poll.getChoices().get(0)));
                                radioButton2.setText(poll.getChoices().get(1) + ": " + poll.getScores().get(poll.getChoices().get(1)));
                                radioButton3.setText(poll.getChoices().get(2) + ": " + poll.getScores().get(poll.getChoices().get(2)));
                                radioButton4.setText(poll.getChoices().get(3) + ": " + poll.getScores().get(poll.getChoices().get(3)));

                                if (poll.getPeopleVoted().contains(ParseUser.getCurrentUser().getObjectId())) {
                                    btVote.setText("Voted");
                                    btVote.setEnabled(false);
                                }
                                //update parse object
                                object.put("people_voted", poll.getPeopleVoted());
                                object.put("scores", poll.getScores());

                                object.saveInBackground();

                            } else {
                                e.getMessage();
                            }
                        }
                    });

                }
            });


            radio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {

                    View radioButton = radio.findViewById(checkedId);
                    int index = radio.indexOfChild(radioButton);

                    // Add logic here


                    switch (index) {
                        case 0: // first button
                            choice[0] = 0;
                            Toast.makeText(getApplicationContext(), "Selected button number " + index, Toast.LENGTH_LONG).show();
                            break;
                        case 1: // second button
                            choice[0] = 1;
                            Toast.makeText(getApplicationContext(), "Selected button number " + index, Toast.LENGTH_LONG).show();
                            break;
                        case 2: // third button
                            choice[0] = 2;
                            Toast.makeText(getApplicationContext(), "Selected button number " + index, Toast.LENGTH_LONG).show();
                            break;
                        case 3: // fourth button
                            choice[0] = 3;
                            Toast.makeText(getApplicationContext(), "Selected button number " + index, Toast.LENGTH_LONG).show();
                            break;
                    }
                }
            });


        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // populate the views

        Poll poll = polls.get(position);
        String id = poll.getObjectId();
        String quet = poll.getQuestion();
        holder.tvQuestion.setText(poll.getQuestion());
        List<String> peeps = poll.getPeopleVoted();
        String me =ParseUser.getCurrentUser().getObjectId();
        if (!poll.getPeopleVoted().contains(ParseUser.getCurrentUser().getObjectId())) {
            holder.radioButton1.setText(poll.getChoices().get(0));
            holder.radioButton2.setText(poll.getChoices().get(1));
            holder.radioButton3.setVisibility(View.INVISIBLE);
            holder.radioButton4.setVisibility(View.INVISIBLE);

            if (poll.getChoices().size() == 3) {
                holder.radioButton3.setVisibility(View.VISIBLE);
                holder.radioButton3.setText(poll.getChoices().get(2));
            } else if (poll.getChoices().size() == 4) {
                holder.radioButton3.setVisibility(View.VISIBLE);
                holder.radioButton4.setVisibility(View.VISIBLE);
                holder.radioButton3.setText(poll.getChoices().get(2));
                holder.radioButton4.setText(poll.getChoices().get(3));
            }
            holder.btVote.setText("Vote");
            holder.btVote.setEnabled(true);
        } else {
            holder.radioButton1.setText(poll.getChoices().get(0) + ": " + poll.getScores().get(poll.getChoices().get(0)));
            holder.radioButton2.setText(poll.getChoices().get(1) + ": " + poll.getScores().get(poll.getChoices().get(1)));
            holder.radioButton3.setText(poll.getChoices().get(2) + ": " + poll.getScores().get(poll.getChoices().get(2)));
            holder.radioButton4.setText(poll.getChoices().get(3) + ": " + poll.getScores().get(poll.getChoices().get(3)));
            holder.btVote.setText("Voted");
            holder.btVote.setEnabled(false);
        }


    }


    @Override
    public int getItemCount() {
        return polls.size();
    }


}
