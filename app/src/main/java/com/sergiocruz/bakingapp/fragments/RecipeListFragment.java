package com.sergiocruz.bakingapp.fragments;

import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.sergiocruz.bakingapp.R;
import com.sergiocruz.bakingapp.ThreadExecutor;
import com.sergiocruz.bakingapp.activities.RecipeDetailActivity;
import com.sergiocruz.bakingapp.adapters.RecipeAdapter;
import com.sergiocruz.bakingapp.database.RecipeDatabase;
import com.sergiocruz.bakingapp.database.RecipesDao;
import com.sergiocruz.bakingapp.model.ActivityViewModel;
import com.sergiocruz.bakingapp.model.Ingredient;
import com.sergiocruz.bakingapp.model.Recipe;
import com.sergiocruz.bakingapp.model.RecipeStep;

import java.util.List;

import timber.log.Timber;

public class RecipeListFragment extends Fragment implements RecipeAdapter.RecipeClickListener, RecipeAdapter.FavoriteClickListener, RecipeAdapter.FavoriteLongClickListener {
    public static final String RECYCLER_VIEW_POSITION = "RecyclerView_Position";
    private static final int GRID_SPAN_COUNT = 2;
    private Context mContext;
    private ActivityViewModel viewModel;
    private RecyclerView recyclerView;
    private RecipeAdapter adapter;
    private boolean isTwoPane;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_recipe_list, container, false);

        mContext = getContext();

        Toolbar toolbar = rootView.findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        toolbar.setTitle(getTitle());
        ((TextView) toolbar.findViewById(R.id.toolbar_text)).setShadowLayer(10, 4, 4, R.color.cardview_dark_background);

        setHasOptionsMenu(true);

        isTwoPane = getResources().getBoolean(R.bool.is_two_pane);

        recyclerView = rootView.findViewById(R.id.recipe_list_recyclerview);

        adapter = new RecipeAdapter(this, this, this);
        setupRecyclerView(recyclerView, adapter);

        // Start the ViewModel //TODO favorites
        viewModel = ActivityViewModel.getInstance(this, false);
        viewModel.getAllRecipes().observe(RecipeListFragment.this, new Observer<List<Recipe>>() {
            /**
             * Called when the data is changed.
             * @param recipesList The new data */
            @Override
            public void onChanged(@Nullable List<Recipe> recipesList) {
                adapter.swapRecipesData(recipesList);
            }

        });

        if (savedInstanceState != null) {
            int position = savedInstanceState.getInt(RECYCLER_VIEW_POSITION);
            recyclerView.smoothScrollToPosition(position);
        }

        return rootView;
    }


    private void setupRecyclerView(RecyclerView recyclerView, RecipeAdapter adapter) {
        if (isTwoPane) {
            recyclerView.setLayoutManager(new GridLayoutManager(mContext, GRID_SPAN_COUNT, GridLayoutManager.VERTICAL, false));
        } else {
            recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        }
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public void onRecipeClicked(Recipe recipe) {
        // start Detail Activity with the recipe details
        // Sends the complete Selected Recipe
        Intent intent = new Intent(mContext, RecipeDetailActivity.class);
        startActivity(intent);
        viewModel.setRecipe(recipe);
        viewModel.setRecipeStepNumber(-1);

        Timber.d(recipe.getRecipeName());
    }

    @Override
    public void onFavoriteClicked(Recipe recipe, int position) {
        Integer isFavorite = recipe.getIsFavorite();
        if (isFavorite == null || isFavorite == 0) { // if not favorite make it favorite

            new ThreadExecutor().diskIO().execute(() -> {

                RecipesDao recipesDao = RecipeDatabase.getDatabase(mContext).recipesDao();

                recipe.setIsFavorite(1);
                recipesDao.addRecipe(recipe);

                List<Ingredient> ingredientList = recipe.getIngredientsList();
                Integer columnId = recipesDao.getColumnIdFromRecipeId(recipe.getRecipeId()); // ???
                for (int j = 0; j < ingredientList.size(); j++) {
                    Ingredient ingredient = ingredientList.get(j);
                    ingredient.setRecipeId(columnId);
                    recipesDao.addIngredient(ingredient);
                }

                List<RecipeStep> stepsList = recipe.getStepsList();
                for (int k = 0; k < stepsList.size(); k++) {
                    RecipeStep recipeStep = stepsList.get(k);
                    recipeStep.setRecipeId(columnId);
                    recipesDao.addStep(recipeStep);
                }
            });


        } else {
            Toast.makeText(mContext, "Already in Favorites", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onFavoriteLongClicked(Recipe recipe, int position) {
        Integer isFavorite = recipe.getIsFavorite();
        if (isFavorite != null) { // if it's a favorite remove it from favorites
            if (isFavorite == 1) {
                new ThreadExecutor().diskIO().execute(() -> {
                    RecipeDatabase.getDatabase(mContext).recipesDao().deleteRecipeByColumnId(recipe.getColumnId());
                });
            }
        } else {
            Toast.makeText(mContext, "Click to add to favorites", Toast.LENGTH_LONG).show();
        }
    }

}
