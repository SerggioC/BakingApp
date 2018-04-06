package com.sergiocruz.bakingapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sergiocruz.bakingapp.R;
import com.sergiocruz.bakingapp.activities.MainActivity;
import com.sergiocruz.bakingapp.activities.RecipeDetailActivity;
import com.sergiocruz.bakingapp.adapters.RecipeStepAdapter;
import com.sergiocruz.bakingapp.model.Recipe;
import com.sergiocruz.bakingapp.model.RecipeStep;

import static com.sergiocruz.bakingapp.fragments.MainFragment.RECYCLER_VIEW_POSITION;

/**
 * A fragment representing a single recipe detail screen.
 * This fragment is either contained in a {@link MainActivity}
 * in two-pane mode (on tablets) or a {@link RecipeDetailActivity}
 * on handsets.
 */
public class RecipeDetailFragment extends Fragment implements RecipeStepAdapter.RecipeStepClickListener{
    /**
     * The fragment argument representing the recipe item
     * that this fragment represents.
     */
    public static final String ARG_RECIPE_ITEM = "recipe_item";

    private Recipe recipe;
    private RecyclerView recyclerView;
    private RecipeStepAdapter adapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RecipeDetailFragment() {
    }

    // https://stackoverflow.com/questions/44272914/sharing-data-between-fragments-using-new-architecture-component-viewmodel
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        ViewModelProviders.of(RecipeDetailFragment.this).get(MainFragmentViewModel.class).getAllRecipes().observe(this, new Observer<List<Recipe>>() {
//            @Override
//            public void onChanged(@Nullable List<Recipe> recipeList) {
//                // TODO Do stuff
//                adapter.swapRecipeStepData(d);
//
//            }
//        });

        if (savedInstanceState != null) {
            recipe = savedInstanceState.getParcelable(ARG_RECIPE_ITEM);
        }

//        if (getArguments().containsKey(ARG_RECIPE_ITEM)) {


        //recipe = getArguments().getParcelable(ARG_RECIPE_ITEM);

        //mItem = DummyContent.ITEM_MAP.get(getArguments().getString(ARG_RECIPE_ITEM));

//            Activity activity = this.getActivity();
//            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar);
//            if (appBarLayout != null) {
//                appBarLayout.setTitle(mItem.content);
//            }
    }
    //}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recipe_detail, container, false);

        Context context = getContext();

        TextView recipeNameTextView = rootView.findViewById(R.id.recipe_name);
        recipeNameTextView.setText(recipe.getRecipeName());

        TextView servingSizeTV = rootView.findViewById(R.id.servings_num);
        servingSizeTV.setText(recipe.getServings() + "");

        recyclerView = rootView.findViewById(R.id.recipe_steps_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        recyclerView.setHasFixedSize(true);
        adapter = new RecipeStepAdapter(context, this);
        adapter.swapRecipeStepData(recipe.getStepsList(), recipe.getIngredientsList());
        recyclerView.setAdapter(adapter);

        if (savedInstanceState != null) {
            int position = savedInstanceState.getInt(RECYCLER_VIEW_POSITION);
            recyclerView.smoothScrollToPosition(position);
        }

        return rootView;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    @Override
    public void onSaveInstanceState(Bundle currentState) {
        currentState.putParcelable(ARG_RECIPE_ITEM, recipe);
        currentState.putInt(RECYCLER_VIEW_POSITION, ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition());
    }


    @Override
    public void onRecipeStepClicked(RecipeStep recipeStep) {
        // TODO update fragment or swap fragment
    }
}
