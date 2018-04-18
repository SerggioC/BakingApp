package com.sergiocruz.bakingapp.fragments;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.sergiocruz.bakingapp.R;
import com.sergiocruz.bakingapp.ThreadExecutor;
import com.sergiocruz.bakingapp.activities.MainActivity;
import com.sergiocruz.bakingapp.activities.RecipeDetailActivity;
import com.sergiocruz.bakingapp.adapters.RecipeStepAdapter;
import com.sergiocruz.bakingapp.database.RecipeDatabase;
import com.sergiocruz.bakingapp.database.RecipeTypeConverter;
import com.sergiocruz.bakingapp.exoplayer.ExoPlayerVideoHandler;
import com.sergiocruz.bakingapp.model.ActivityViewModel;
import com.sergiocruz.bakingapp.model.CompleteRecipe;
import com.sergiocruz.bakingapp.model.Recipe;
import com.sergiocruz.bakingapp.model.RecipeStep;

import timber.log.Timber;

import static com.sergiocruz.bakingapp.activities.RecipeDetailActivity.EXTRA_WIDGET_RECIPE;
import static com.sergiocruz.bakingapp.fragments.RecipeListFragment.RECYCLER_VIEW_POSITION;

/**
 * A fragment representing a single recipe detail screen.
 * This fragment is either contained in a {@link MainActivity}
 * in two-pane mode (on tablets) or a {@link RecipeDetailActivity}
 * on handsets.
 */
public class RecipeDetailFragment extends Fragment implements RecipeStepAdapter.RecipeStepClickListener {
    public static final int INVALID_POSITION = -1;
    /**
     * The fragment argument representing the recipe item
     * that this fragment represents.
     */
    private Recipe recipe;
    private RecyclerView recyclerView;
    private RecipeStepAdapter adapter;
    private Boolean isTwoPane;
    private ActivityViewModel viewModel;
    private int lastAdapterPosition = 0;
    private NestedScrollView nestedScrollView;

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

        Integer RecipeColumnID = getActivity().getIntent().getIntExtra(EXTRA_WIDGET_RECIPE, INVALID_POSITION);

        viewModel = ActivityViewModel.getInstance(this, false);
        if (RecipeColumnID != INVALID_POSITION) {
            new ThreadExecutor().diskIO().execute(() -> {
                CompleteRecipe completeRecipe = RecipeDatabase.getDatabase(context).recipesDao().getCompleteRecipe(RecipeColumnID);
                recipe = RecipeTypeConverter.convertToRecipe(completeRecipe);
                viewModel.postRecipe(recipe);
                initializeUI(savedInstanceState, rootView, context);
            });
        } else {
            recipe = viewModel.getRecipe().getValue();
            initializeUI(savedInstanceState, rootView, context);
            setLayoutObserver();
        }

        isTwoPane = getResources().getBoolean(R.bool.is_two_pane);

        viewModel.getRecipeStepNumber().observe(this, stepNumber -> {
            changeViewHolderOutline(stepNumber);
            Timber.d("Clicked Recipe number = " + stepNumber);
        });

        return rootView;
    }

    private void initializeUI(Bundle savedInstanceState, View rootView, Context context) {
        TextView recipeNameTextView = rootView.findViewById(R.id.recipe_name);
        recipeNameTextView.setText(recipe.getRecipeName());

        TextView servingSizeTV = rootView.findViewById(R.id.servings_num);
        servingSizeTV.setText(recipe.getServings().toString());

        adapter = new RecipeStepAdapter(context, this);
        adapter.swapRecipeStepData(recipe.getStepsList(), recipe.getIngredientsList());

        recyclerView = rootView.findViewById(R.id.recipe_steps_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        recyclerView.setHasFixedSize(false);
        recyclerView.setAdapter(adapter);

        if (savedInstanceState != null) {
            int position = savedInstanceState.getInt(RECYCLER_VIEW_POSITION);
            recyclerView.smoothScrollToPosition(position);
        }
        nestedScrollView = rootView.findViewById(R.id.nested_scroll_view);
        nestedScrollView.setFocusableInTouchMode(true);
        nestedScrollView.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
    }

    //                if (recipeStepNumber == -1) nestedScrollView.fullScroll(View.FOCUS_UP);

    private void setLayoutObserver() {
        recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Integer recipeStepNumber = viewModel.getRecipeStepNumber().getValue();
                changeViewHolderOutline(recipeStepNumber);
                recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle currentState) {
        currentState.putInt(RECYCLER_VIEW_POSITION, ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition());
    }

    @Override
    public void onRecipeStepClicked(RecipeStep recipeStep, int stepClicked) {
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
            lastAdapterPosition = 0;
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

        if (isTwoPane) scrollIfNeeded(viewHolder.itemView);

        lastAdapterPosition = adapterPosition;
    }

    public void scrollIfNeeded(View view) {
        Rect position = new Rect();
        view.getGlobalVisibleRect(position);

        Rect screen = new Rect(0, 0,
                Resources.getSystem().getDisplayMetrics().widthPixels,
                Resources.getSystem().getDisplayMetrics().heightPixels);

        if (position.bottom + view.getHeight() >= screen.bottom || position.top - 2 * view.getHeight() <= 0) {
            nestedScrollView.smoothScrollTo(0, screen.bottom / 2); // Scroll the view to to middle of the screen
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        viewModel.setRecipeStep(null);
        if (!isTwoPane) {
            ExoPlayerVideoHandler.getInstance().releaseVideoPlayer();
        }
    }

}
