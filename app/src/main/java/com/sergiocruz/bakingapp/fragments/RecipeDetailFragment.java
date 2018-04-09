package com.sergiocruz.bakingapp.fragments;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.sergiocruz.bakingapp.R;
import com.sergiocruz.bakingapp.activities.MainActivity;
import com.sergiocruz.bakingapp.activities.RecipeDetailActivity;
import com.sergiocruz.bakingapp.adapters.RecipeStepAdapter;
import com.sergiocruz.bakingapp.model.ActivityViewModel;
import com.sergiocruz.bakingapp.model.Recipe;
import com.sergiocruz.bakingapp.model.RecipeStep;

import timber.log.Timber;

import static com.sergiocruz.bakingapp.fragments.RecipeListFragment.RECYCLER_VIEW_POSITION;

/**
 * A fragment representing a single recipe detail screen.
 * This fragment is either contained in a {@link MainActivity}
 * in two-pane mode (on tablets) or a {@link RecipeDetailActivity}
 * on handsets.
 */
public class RecipeDetailFragment extends Fragment implements RecipeStepAdapter.RecipeStepClickListener {
    /**
     * The fragment argument representing the recipe item
     * that this fragment represents.
     */
    public static final String ARG_RECIPE_ITEM = "recipe_item";
    private Recipe recipe;
    private RecyclerView recyclerView;
    private RecipeStepAdapter adapter;
    private Boolean isTwoPane;
    private ActivityViewModel viewModel;
    private int lastAdapterPosition = 0;
    private Integer stepNumber;

    private void setThisStepNumber(Integer stepNumber) {
        this.stepNumber = stepNumber;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RecipeDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recipe_detail, container, false);

        Context context = getContext();

        viewModel = ViewModelProviders.of(getActivity()).get(ActivityViewModel.class);
        recipe = viewModel.getRecipe().getValue();

        TextView recipeNameTextView = rootView.findViewById(R.id.recipe_name);
        recipeNameTextView.setText(recipe.getRecipeName());

        TextView servingSizeTV = rootView.findViewById(R.id.servings_num);
        servingSizeTV.setText(new StringBuilder(recipe.getServings()).toString());

        adapter = new RecipeStepAdapter(context, this);
        adapter.swapRecipeStepData(recipe.getStepsList(), recipe.getIngredientsList());

        recyclerView = rootView.findViewById(R.id.recipe_steps_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        if (savedInstanceState != null) {
            int position = savedInstanceState.getInt(RECYCLER_VIEW_POSITION);
            recyclerView.smoothScrollToPosition(position);
        }

        isTwoPane = getResources().getBoolean(R.bool.is_two_pane);

        if (isTwoPane) {
            viewModel.getRecipeStepNumber().observe(this, new Observer<Integer>() {
                @Override
                public void onChanged(@Nullable Integer stepNumber) {
                    changeViewHolderOutline(stepNumber);
                    Timber.d("Clicked Recipe number = " + stepNumber);
                }
            });
        }
        return rootView;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            /**
             * Callback method to be invoked when the global layout state or the visibility of views
             * within the view tree changes
             */
            @Override
            public void onGlobalLayout() {
                LiveData<Integer> recipeStepNumber = viewModel.getRecipeStepNumber();
                Integer stepN = recipeStepNumber.getValue() == null ? -1 : recipeStepNumber.getValue();

                changeViewHolderOutline(stepN);
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle currentState) {
        //currentState.putParcelable(ARG_RECIPE_ITEM, recipe);
        currentState.putInt(RECYCLER_VIEW_POSITION, ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition());
    }


    @Override
    public void onRecipeStepClicked(RecipeStep recipeStep, int stepClicked) {

        changeViewHolderOutline(stepClicked);

        viewModel.setRecipeStepNumber(stepClicked);
        viewModel.setRecipeStep(recipeStep);

        if (!isTwoPane) {
            ImageView image = recyclerView.getLayoutManager().findViewByPosition(stepClicked + 1).findViewById(R.id.step_image);
            String transitionName = getString(R.string.step_detail_transition_name);
            ViewCompat.setTransitionName(image, transitionName);

            RecipeStepFragment recipeStepFragment = new RecipeStepFragment();
            getFragmentManager()
                    .beginTransaction()
                    .addSharedElement(image, transitionName)
                    .replace(R.id.recipe_detail_fragment_container, recipeStepFragment)
                    .addToBackStack(null)
                    .commit();
        }

    }

    private void changeViewHolderOutline(int stepClicked) {
        int adapterPosition = stepClicked + 1;
        if (adapterPosition == lastAdapterPosition) return;

        RecipeStepAdapter.RecipeStepViewHolder viewHolder = (RecipeStepAdapter.RecipeStepViewHolder) recyclerView.findViewHolderForAdapterPosition(adapterPosition);
        if (viewHolder == null) return;
        viewHolder.itemView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.step_background_selected));

        if (lastAdapterPosition > 0) {
            viewHolder = (RecipeStepAdapter.RecipeStepViewHolder) recyclerView.findViewHolderForAdapterPosition(lastAdapterPosition);
            viewHolder.itemView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.step_background));
        }

        lastAdapterPosition = adapterPosition;
    }
}
