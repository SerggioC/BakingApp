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

import com.sergiocruz.bakingapp.R;
import com.sergiocruz.bakingapp.ThreadExecutor;
import com.sergiocruz.bakingapp.activities.MainActivity;
import com.sergiocruz.bakingapp.activities.RecipeDetailActivity;
import com.sergiocruz.bakingapp.adapters.IngredientAdapter;
import com.sergiocruz.bakingapp.adapters.RecipeStepAdapter;
import com.sergiocruz.bakingapp.database.RecipeDatabase;
import com.sergiocruz.bakingapp.database.RecipeTypeConverter;
import com.sergiocruz.bakingapp.model.ActivityViewModel;
import com.sergiocruz.bakingapp.model.CompleteRecipe;
import com.sergiocruz.bakingapp.model.Ingredient;
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
public class RecipeDetailFragment extends Fragment implements RecipeStepAdapter.RecipeStepClickListener, IngredientAdapter.IngredientClickListener {
    public static final int INVALID_POSITION = -1;
    public static final String RECIPE_STEP_POSITION = "outstate_step_position";
    /**
     * The fragment argument representing the recipe item
     * that this fragment represents.
     */
    private Recipe recipe;
    private RecyclerView ingredientsRecyclerView;
    private RecyclerView stepsRecyclerView;
    private IngredientAdapter ingredientAdapter;
    private RecipeStepAdapter recipeStepsAdapter;
    private Boolean isTwoPane;
    private ActivityViewModel viewModel;
    private int lastAdapterPosition = 0;
    private NestedScrollView nestedScrollView;
    private int savedStepPosition = -1;
    private int savedRecyclerViewPosition;
    private boolean hasSavedState = false;
    private Context mContext;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RecipeDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recipe_detail, container, false);
        mContext = getContext();

        // If entering from Widget
        Integer RecipeColumnID = getActivity().getIntent().getIntExtra(EXTRA_WIDGET_RECIPE, INVALID_POSITION);
        viewModel = ActivityViewModel.getInstance(this, true, false);
        if (RecipeColumnID != INVALID_POSITION) {
            new ThreadExecutor().diskIO().execute(() -> {
                CompleteRecipe completeRecipe = RecipeDatabase.getDatabase(mContext).recipesDao().getCompleteRecipe(RecipeColumnID);
                recipe = RecipeTypeConverter.convertToRecipe(completeRecipe);
                new ThreadExecutor().mainThread().execute(() -> {
                    viewModel.setRecipe(recipe);
                    initializeUI(savedInstanceState, rootView, mContext);
                });

            });
        } else {
            recipe = viewModel.getRecipe().getValue();
            initializeUI(savedInstanceState, rootView, mContext);
            setLayoutObserver();
        }

        isTwoPane = getResources().getBoolean(R.bool.is_two_pane);

        viewModel.getRecipeStepNumber().observe(this, stepNumber -> {
            if (!hasSavedState) {
                savedStepPosition = stepNumber;
                changeViewHolderOutline(stepNumber);
                Timber.d("Clicked Recipe number = " + stepNumber);
                hasSavedState = false;
            }

        });

        return rootView;
    }

    private void initializeUI(Bundle savedInstanceState, View rootView, Context context) {

        ingredientAdapter = new IngredientAdapter(this);
        ingredientAdapter.swapIngredientsData(recipe);

        ingredientsRecyclerView = rootView.findViewById(R.id.recipe_ingredients_recyclerview);
        ingredientsRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        ingredientsRecyclerView.setHasFixedSize(false);
        ingredientsRecyclerView.setAdapter(ingredientAdapter);

        recipeStepsAdapter = new RecipeStepAdapter(context, this);
        recipeStepsAdapter.swapRecipeStepData(recipe);

        stepsRecyclerView = rootView.findViewById(R.id.recipe_steps_recyclerview);
        stepsRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        stepsRecyclerView.setHasFixedSize(false);
        stepsRecyclerView.setAdapter(recipeStepsAdapter);

        if (savedInstanceState != null) {
            savedRecyclerViewPosition = savedInstanceState.getInt(RECYCLER_VIEW_POSITION);
            savedStepPosition = savedInstanceState.getInt(RECIPE_STEP_POSITION);
            viewModel.setRecipeStepNumber(savedStepPosition);
            hasSavedState = true;
        } else {
            hasSavedState = false;
        }

        nestedScrollView = rootView.findViewById(R.id.nested_scroll_view);
        nestedScrollView.setFocusableInTouchMode(true);
        nestedScrollView.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS); // Scroll to top
    }

    // if (recipeStepNumber == -1) nestedScrollView.fullScroll(View.FOCUS_UP);
    // Update Recipe step outline after the recyclerview has drawn
    private void setLayoutObserver() {
        stepsRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                changeViewHolderOutline(savedStepPosition);
                stepsRecyclerView.smoothScrollToPosition(savedRecyclerViewPosition);
                stepsRecyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle currentState) {
        Timber.i("savedStepPosition= " + savedStepPosition);
        currentState.putInt(RECYCLER_VIEW_POSITION, ((LinearLayoutManager) stepsRecyclerView.getLayoutManager()).findFirstVisibleItemPosition());
        currentState.putInt(RECIPE_STEP_POSITION, savedStepPosition);
    }

    @Override
    public void onRecipeStepClicked(RecipeStep recipeStep, int stepClicked) {
        hasSavedState = false;
        savedStepPosition = stepClicked;
        viewModel.setRecipeStepNumber(stepClicked);
        viewModel.setRecipeStep(recipeStep);

        if (!isTwoPane) {
            ImageView image = stepsRecyclerView.getLayoutManager().findViewByPosition(stepClicked + 1).findViewById(R.id.step_image);
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

    @Override
    public void onIngredientClicked(Ingredient ingredient, boolean isChecked) {
        new ThreadExecutor().diskIO().execute(() -> {
            RecipeDatabase.getDatabase(mContext).recipesDao().updateIngredientStatus(isChecked ? 1 : 0, ingredient.getIngredientId());
        });

    }

    private void changeViewHolderOutline(int stepClicked) {
        int adapterPosition = stepClicked + 1;
        if (adapterPosition == lastAdapterPosition) return;

        if (stepsRecyclerView == null) return;
        RecipeStepAdapter.RecipeStepViewHolder viewHolder = (RecipeStepAdapter.RecipeStepViewHolder) stepsRecyclerView.findViewHolderForAdapterPosition(adapterPosition);
        if (viewHolder == null) return;
        viewHolder.itemView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.step_background_selected));

        if (lastAdapterPosition > 0) {
            viewHolder = (RecipeStepAdapter.RecipeStepViewHolder) stepsRecyclerView.findViewHolderForAdapterPosition(lastAdapterPosition);
            viewHolder.itemView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.step_background));
        }

        scrollIfNeeded(viewHolder.itemView);

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
        viewModel.setRecipeStepNumber(-1);
    }

}
