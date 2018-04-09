package com.sergiocruz.bakingapp.fragments;

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

import com.google.android.exoplayer2.ui.PlayerView;
import com.sergiocruz.bakingapp.R;
import com.sergiocruz.bakingapp.model.ActivityViewModel;
import com.sergiocruz.bakingapp.model.RecipeStep;

import java.util.List;

public class RecipeStepFragment extends Fragment {
    private ActivityViewModel viewModel;
    private List<RecipeStep> stepsList;
    private Integer stepNumber;
    private TextView stepDetailTV;
    private PlayerView exoPlayerView;

    public RecipeStepFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTransitions();
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

        viewModel = ActivityViewModel.getInstance(this);
        viewModel.getRecipeStep().observe(this, recipeStep -> {
            stepNumber = viewModel.getRecipeStepNumber().getValue();
            updateFragmentUI(recipeStep);
        });

        RecipeStep recipeStep = viewModel.getRecipeStep().getValue();
        stepNumber = viewModel.getRecipeStepNumber().getValue();
        if (stepNumber == null) stepNumber = -1;
        stepsList = viewModel.getRecipe().getValue().getStepsList();
        setUpFragmentUI(rootView, recipeStep);

        return rootView;
    }

    private void setUpFragmentUI(View rootView, RecipeStep recipeStep) {
        stepDetailTV = rootView.findViewById(R.id.recipe_step_detail_TextView);
        exoPlayerView = rootView.findViewById(R.id.exoPlayerView);

        updateFragmentUI(recipeStep);

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

    private void updateFragmentUI(RecipeStep recipeStep) {
        if (stepNumber == -1 || recipeStep == null) {
            stepDetailTV.setText(R.string.select_step);
        } else {
            stepDetailTV.setText(getString(R.string.step_number) + " " + stepNumber + "\n" + recipeStep.getDescription());
        }

        exoPlayerView.setDefaultArtwork(BitmapFactory.decodeResource(getResources(), R.drawable.ic_chef));
    }

    public class DetailsTransition extends TransitionSet {
        public DetailsTransition() {
            setOrdering(ORDERING_TOGETHER);
            addTransition(new ChangeBounds())
                    .addTransition(new ChangeTransform())
                    .addTransition(new ChangeImageTransform())
                    .addTransition(new ChangeClipBounds());
        }
    }

    private void setTransitions() {
        this.setSharedElementEnterTransition(new DetailsTransition());
        this.setEnterTransition(new DetailsTransition());
        this.setExitTransition(new DetailsTransition());
        this.setSharedElementReturnTransition(new DetailsTransition());
    }

}
