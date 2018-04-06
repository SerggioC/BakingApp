package com.sergiocruz.bakingapp.fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sergiocruz.bakingapp.R;
import com.sergiocruz.bakingapp.model.MainFragmentViewModel;
import com.sergiocruz.bakingapp.model.RecipeStep;

public class RecipeStepFragment extends Fragment {
    private RecipeStep recipeStep;
    private int stepNumber;
    private Boolean isTwoPane;

    public RecipeStepFragment() {
    }

    public void setRecipeStep(RecipeStep recipeStep) {
        this.recipeStep = recipeStep;
    }

    public void setStepNumber(int stepNumber) {
        this.stepNumber = stepNumber;
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

        Context context = getContext();

        isTwoPane = getResources().getBoolean(R.bool.is_two_pane);

        if (isTwoPane) {
            ViewModelProviders.of(this).get(MainFragmentViewModel.class).getRecipeStep().observe(this, new Observer<RecipeStep>() {
                @Override
                public void onChanged(@Nullable RecipeStep recipeStep) {
                    setRecipeStep(recipeStep);
                    populateFragmentUI(rootView);
                }
            });
        }

        populateFragmentUI(rootView);

        return rootView;
    }

    private void populateFragmentUI(View rootView) {
        TextView stepDetailTV = rootView.findViewById(R.id.recipe_step_detail_TextView);
        stepDetailTV.setText("Step number " + stepNumber + "\n" + recipeStep.getDescription());

        com.google.android.exoplayer2.ui.PlayerView exoPlayerView = rootView.findViewById(R.id.exoPlayerView);
        exoPlayerView.setDefaultArtwork(BitmapFactory.decodeResource(getResources(), R.drawable.ic_chef));
    }


}
