package com.sergiocruz.bakingapp.fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.transition.ChangeBounds;
import android.support.transition.ChangeClipBounds;
import android.support.transition.ChangeImageTransform;
import android.support.transition.ChangeTransform;
import android.support.transition.TransitionSet;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.sergiocruz.bakingapp.R;
import com.sergiocruz.bakingapp.model.ActivityViewModel;
import com.sergiocruz.bakingapp.model.RecipeStep;

import java.util.List;

public class RecipeStepFragment extends Fragment {
    //    private RecipeStep recipeStep;
//    private int stepNumber;
    private ActivityViewModel viewModel;
    private Integer stepNumber;
    private List<RecipeStep> stepsList;

    public RecipeStepFragment() {
    }

//    public void setRecipeStep(RecipeStep recipeStep) {
//        this.recipeStep = recipeStep;
//    }
//
//    public void setStepNumber(int stepNumber) {
//        this.stepNumber = stepNumber;
//    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTransitions();
    }

    private void setTransitions() {
        this.setSharedElementEnterTransition(new DetailsTransition());
        this.setEnterTransition(new DetailsTransition());
        this.setExitTransition(new DetailsTransition());
        this.setSharedElementReturnTransition(new DetailsTransition());
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     * This is optional, and non-graphical fragments can return null (which
     * is the default implementation).  This will be called between
     * {@link #onCreate(Bundle)} and {@link #onActivityCreated(Bundle)}.
     * <p>
     * <p>If you return a View from here, you will later be called in
     * {@link #onDestroyView} when the view is being released.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate
     *                           any views in the fragment,
     * @param container          If non-null, this is the parent view that the fragment's
     *                           UI should be attached to.  The fragment should not add the view itself,
     *                           but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recipe_step, container, false);

        viewModel = ViewModelProviders.of(getActivity()).get(ActivityViewModel.class);
        viewModel.getRecipeStep().observe(this, new Observer<RecipeStep>() {
            @Override
            public void onChanged(@Nullable RecipeStep recipeStep) {
                stepNumber = viewModel.getRecipeStepNumber().getValue();
                populateFragmentUI(rootView, recipeStep, stepNumber);
            }
        });
        RecipeStep recipeStep = viewModel.getRecipeStep().getValue();
        stepNumber = viewModel.getRecipeStepNumber().getValue();
        if (stepNumber == null) stepNumber = -1;
        stepsList = viewModel.getRecipe().getValue().getStepsList();
        populateFragmentUI(rootView, recipeStep, stepNumber);


        return rootView;
    }

    private void populateFragmentUI(View rootView, RecipeStep recipeStep, Integer stepNumber) {
        TextView stepDetailTV = rootView.findViewById(R.id.recipe_step_detail_TextView);

        if (recipeStep == null || recipeStep == null) {
            stepDetailTV.setText(R.string.select_step);
        } else {
            stepDetailTV.setText(getString(R.string.step_number) + " " + stepNumber + "\n" + recipeStep.getDescription());
        }

        com.google.android.exoplayer2.ui.PlayerView exoPlayerView = rootView.findViewById(R.id.exoPlayerView);
        exoPlayerView.setDefaultArtwork(BitmapFactory.decodeResource(getResources(), R.drawable.ic_chef));
        //ViewCompat.setTransitionName(exoPlayerView, getString(R.string.step_detail_transition_name));

        ImageButton next = rootView.findViewById(R.id.next_btn);
        next.setOnClickListener(v -> {
            int nextStepNumber = stepNumber + 1;
            if (nextStepNumber > stepsList.size() - 1) return;
            viewModel.setRecipeStepNumber(nextStepNumber);
            viewModel.setRecipeStep(stepsList.get(nextStepNumber));
        });

        ImageButton previous = rootView.findViewById(R.id.previous_btn);
        previous.setOnClickListener(v -> {
            int previousStepNumber = stepNumber - 1;
            if (previousStepNumber < 0) return;
            viewModel.setRecipeStepNumber(previousStepNumber);
            viewModel.setRecipeStep(stepsList.get(previousStepNumber));
        });

    }

    public class DetailsTransition extends TransitionSet {
        public DetailsTransition() {
            setDuration(400);
            setOrdering(ORDERING_TOGETHER);
            addTransition(new ChangeBounds())
                    .addTransition(new ChangeTransform())
                    .addTransition(new ChangeImageTransform())
                    .addTransition(new ChangeClipBounds());
        }
    }


}
