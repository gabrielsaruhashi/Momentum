package shag.com.shag.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
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
import shag.com.shag.Fragments.DialogFragments.DatePickerFragment;
import shag.com.shag.Fragments.DialogFragments.TimePickerFragment;
import shag.com.shag.Models.Poll;
import shag.com.shag.R;

import static com.facebook.FacebookSdk.getApplicationContext;
import static shag.com.shag.R.id.tv0;
import static shag.com.shag.R.id.tv1;
import static shag.com.shag.R.id.tv2;
import static shag.com.shag.R.id.tv3;

/**
 * Created by samrabelachew on 7/20/17.
 */


public class PollsAdapter extends RecyclerView.Adapter<PollsAdapter.ViewHolder>  {

    ArrayList<Poll> polls;
    ArrayList<View> buttons = new ArrayList<>();
    int selectedPosition = -1;

    DataTransferInterface dtInterface;
    private Context context;

    public interface DataTransferInterface {
        public void setValues(ArrayList<View> al);
    }
    public PollsAdapter(Context c, DataTransferInterface dtInterface, ArrayList<Poll> polls) {
        this.polls = polls;
        this.context=c;
        this.dtInterface = dtInterface;

    }


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
        RadioButton rButton0;
        @BindView(R.id.radioChoice2)
        RadioButton rButton1;
        @BindView(R.id.radioChoice3)
        RadioButton rButton2;
        @BindView(R.id.radioChoice4)
        RadioButton rButton3;

        @BindView(tv0)
        TextView tvSet0;
        @BindView(tv1)
        TextView tvSet1;
        @BindView(tv2)
        TextView tvSet2;
        @BindView(tv3)
        TextView tvSet3;


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
                                rButton0.setText(poll.getChoices().get(0) + ": " + poll.getScores().get(poll.getChoices().get(0)));
                                rButton1.setText(poll.getChoices().get(1) + ": " + poll.getScores().get(poll.getChoices().get(1)));
                                rButton2.setText(poll.getChoices().get(2) + ": " + poll.getScores().get(poll.getChoices().get(2)));
                                rButton3.setText(poll.getChoices().get(3) + ": " + poll.getScores().get(poll.getChoices().get(3)));

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
                        case 2: // second button
                            choice[0] = 1;
                            Toast.makeText(getApplicationContext(), "Selected button number " + index, Toast.LENGTH_LONG).show();
                            break;
                        case 4: // third button
                            choice[0] = 2;
                            Toast.makeText(getApplicationContext(), "Selected button number " + index, Toast.LENGTH_LONG).show();
                            break;
                        case 6: // fourth button
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
        selectedPosition = position;
        Poll poll = polls.get(position);
        holder.tvQuestion.setText(poll.getQuestion());
        List<String> peeps = poll.getPeopleVoted();
        String me = ParseUser.getCurrentUser().getObjectId();
        createOnTextClick(holder.tvSet0, selectedPosition,0);
        createOnTextClick(holder.tvSet1, selectedPosition,1);
        createOnTextClick(holder.tvSet2, selectedPosition,2);
        createOnTextClick(holder.tvSet3, selectedPosition,3);
        boolean bool = poll.getChoices().contains("Custom");
        if ( poll.getPollType().equals("Time") && poll.getChoices().contains("Custom") ) {
            //onFinishTimePickerFragment(holder,position);
            buttons.add(holder.rButton0);
            buttons.add(holder.rButton1);
            buttons.add(holder.rButton2);
            buttons.add(holder.rButton3);
            holder.btVote.setText("Vote");
            holder.btVote.setEnabled(true);
            dtInterface.setValues(buttons);


        } else {

            holder.tvSet0.setVisibility(View.GONE);
            holder.tvSet1.setVisibility(View.GONE);
            holder.tvSet2.setVisibility(View.GONE);
            holder.tvSet3.setVisibility(View.GONE);
        }


        //if user hasn't voted
        if (!poll.getPeopleVoted().contains(ParseUser.getCurrentUser().getObjectId())) {
            holder.rButton0.setText(poll.getChoices().get(0));
            holder.rButton1.setText(poll.getChoices().get(1));

            //remove these two later
            holder.rButton2.setText(poll.getChoices().get(2));
            holder.rButton3.setText(poll.getChoices().get(3));

//            holder.rButton2.setVisibility(View.INVISIBLE);
//            holder.rButton3.setVisibility(View.INVISIBLE);
//
//            if (poll.getChoices().size() == 3) {
//                holder.rButton2.setVisibility(View.VISIBLE);
//                holder.rButton2.setText(poll.getChoices().get(2));
//            } else if (poll.getChoices().size() == 4) {
//                holder.rButton2.setVisibility(View.VISIBLE);
//                holder.rButton3.setVisibility(View.VISIBLE);
//                holder.rButton2.setText(poll.getChoices().get(2));
//                holder.rButton3.setText(poll.getChoices().get(3));
//            }
            holder.btVote.setText("Vote");
            holder.btVote.setEnabled(true);

            //if user has voted
        } else {
            holder.rButton0.setText(poll.getChoices().get(0) + ": " + poll.getScores().get(poll.getChoices().get(0)));
            holder.rButton1.setText(poll.getChoices().get(1) + ": " + poll.getScores().get(poll.getChoices().get(1)));
            holder.rButton2.setText(poll.getChoices().get(2) + ": " + poll.getScores().get(poll.getChoices().get(2)));
            holder.rButton3.setText(poll.getChoices().get(3) + ": " + poll.getScores().get(poll.getChoices().get(3)));
            holder.btVote.setText("Voted");
            holder.btVote.setEnabled(false);
        }


    }


    @Override
    public int getItemCount() {
        return polls.size();
    }

    public void showDatePickerDialog(int p, int b) {


        FragmentActivity activity = (FragmentActivity) (context);
        FragmentManager fm = activity.getSupportFragmentManager();
        DialogFragment newFragment = new DatePickerFragment();
        Bundle args = new Bundle();
        args.putInt("button", b);
        args.putInt("position", p);
        newFragment.setArguments(args);
        newFragment.show(fm, "fragment_alert");
    }

    public void showTimePickerDialog(int p, int b) {


        FragmentActivity activity = (FragmentActivity) (context);
        FragmentManager fm = activity.getSupportFragmentManager();
        DialogFragment newFragment = new TimePickerFragment();
        Bundle args = new Bundle();
        args.putInt("button", b);
        args.putInt("position", p);
        newFragment.setArguments(args);
        newFragment.show(fm, "fragment_alert");
    }




    public void createOnTextClick(final TextView txtView, final int selectedPosition, final int but){
        if (txtView.getVisibility()==View.VISIBLE) {
            txtView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showTimePickerDialog(selectedPosition, but);
                    showDatePickerDialog(selectedPosition, but);
                    txtView.setVisibility(View.GONE);
                }
            });
        }
    }

}



