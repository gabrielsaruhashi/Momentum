package shag.com.shag.Fragments.DialogFragments;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.HashMap;

import shag.com.shag.Models.Poll;
import shag.com.shag.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CreatePollFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CreatePollFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreatePollFragment extends DialogFragment{

    public interface CreatePollFragmentListener {
        void onFinishCreatePollFragment(Poll poll);
    }

    private EditText mQuestion;
    private EditText mAnswer1;
    private EditText mAnswer2;
    private EditText mAnswer3;
    private EditText mAnswer4;

    private Button createPoll;


    private OnFragmentInteractionListener mListener;

    public CreatePollFragment() {
        // Required empty public constructor
    }


    public static CreatePollFragment newInstance() {
        CreatePollFragment fragment = new CreatePollFragment();
        //Bundle args = new Bundle();
        return fragment;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_poll, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        mQuestion = (EditText) view.findViewById(R.id.etQuestionInput);
        mAnswer1 = (EditText) view.findViewById(R.id.etAnswer1);
        mAnswer2 = (EditText) view.findViewById(R.id.etAnswer2);
        mAnswer3 = (EditText) view.findViewById(R.id.etAnswer3);
        mAnswer4 = (EditText) view.findViewById(R.id.etAnswer4);
        createPoll = (Button) view.findViewById(R.id.btSubmitPoll);

        if (mAnswer1.getText().toString()!= null && mAnswer2.getText().toString()!=null) {
            createPoll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Poll poll = new Poll();

                    poll.setQuestion(mQuestion.getText().toString());

                    ArrayList<String> pollChoices = new ArrayList<String>();
                    pollChoices.add(mAnswer1.getText().toString());
                    pollChoices.add(mAnswer2.getText().toString());
                    if(mAnswer3.getText().toString()!= null){
                        pollChoices.add(mAnswer3.getText().toString());
                    }
                    if(mAnswer4.getText().toString()!= null){
                        pollChoices.add(mAnswer4.getText().toString());
                    }
                    poll.setChoices(pollChoices);

                    HashMap<String,Integer> pollScoring = new HashMap<String, Integer>();
                    for (String val: pollChoices){
                        pollScoring.put(val,0);
                    }
                    poll.setScores(pollScoring);

                    poll.setPeopleVoted(new ArrayList<String>());
                    CreatePollFragmentListener activity = (CreatePollFragmentListener) getActivity();
                    activity.onFinishCreatePollFragment(poll);
                    dismiss();
                }
            });
        }
    }



    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
