package shag.com.shag.Fragments.DialogFragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ramotion.paperonboarding.PaperOnboardingFragment;
import com.ramotion.paperonboarding.PaperOnboardingPage;

import java.util.ArrayList;

import shag.com.shag.R;

/**
 * Created by gabesaruhashi on 7/10/17.
 */

public class OnboardingDialogFragment extends DialogFragment {

    FragmentManager fragmentManager;

    public OnboardingDialogFragment() {
        // Empty constructor required for DialogFragment
    }

    public static OnboardingDialogFragment newInstance(String title) {
        OnboardingDialogFragment frag = new OnboardingDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_onboarding, container);
    }



    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Fetch arguments from bundle and set title
        String title = getArguments().getString("title");
        getDialog().setTitle(title);
        // Convert the fragment into the onboarding procedure
        fragmentManager = getChildFragmentManager();
        final PaperOnboardingFragment onBoardingFragment = PaperOnboardingFragment.newInstance(getDataForOnboarding());

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragment_container, onBoardingFragment);
        fragmentTransaction.commit();

    }

    private ArrayList<PaperOnboardingPage> getDataForOnboarding() {
        // prepare data
        PaperOnboardingPage scr1 = new PaperOnboardingPage("Private Feed", "Check what your friends are up to",
                Color.parseColor("#678FB4"), R.drawable.ic_person, R.drawable.ic_chat);
        PaperOnboardingPage scr2 = new PaperOnboardingPage("Explore", "Discover what is going on around you, and invite your friends to explore with you",
                Color.parseColor("#65B0B4"), R.drawable.ic_globe, R.drawable.ic_map_marker);
        PaperOnboardingPage scr3 = new PaperOnboardingPage("Memories", "Make these moments last forever by sharing pictures of all the events",
                Color.parseColor("#9B90BC"), R.drawable.ic_movie_roll_tape, R.drawable.ic_camera);

        ArrayList<PaperOnboardingPage> elements = new ArrayList<>();
        elements.add(scr1);
        elements.add(scr2);
        elements.add(scr3);
        return elements;
    }


}
