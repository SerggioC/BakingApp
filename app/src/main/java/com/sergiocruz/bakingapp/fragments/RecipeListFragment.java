package com.sergiocruz.bakingapp.fragments;

import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
    public static final String ONLINE = "online";
    public static final String FAVORITES = "favorites";
    private static final int GRID_SPAN_COUNT = 2;
    private Context mContext;
    private ActivityViewModel viewModel;
    private RecyclerView recyclerView;
    private RecipeAdapter adapter;
    private boolean isTwoPane;
    private Menu mMenu;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_recipe_list, container, false);

        mContext = getContext();

        setHasOptionsMenu(true);
        android.support.v7.widget.Toolbar toolbar = rootView.findViewById(R.id.toolbar);
        ((TextView) toolbar.findViewById(R.id.toolbar_text)).setShadowLayer(10, 4, 4, R.color.cardview_dark_background);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        isTwoPane = getResources().getBoolean(R.bool.is_two_pane);

        recyclerView = rootView.findViewById(R.id.recipe_list_recyclerview);

        adapter = new RecipeAdapter(this, this, this);
        setupRecyclerView(recyclerView, adapter);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        String resourceOnOff = prefs.getString(getString(R.string.pref_menu_key), ONLINE);
        Boolean favorites;

        if (resourceOnOff.equals(ONLINE)) {
            favorites = false;
        } else if (resourceOnOff.equals(FAVORITES)){
            favorites = true;
        }


        // Start the ViewModel
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_options, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        this.mMenu = menu;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        String resourceOnOff = prefs.getString(getString(R.string.pref_menu_key), ONLINE);
        toggleMenuIcon2(resourceOnOff);
    }

    private void toggleMenuIcon2(String position) {
        switch (position) {
            case FAVORITES:
                mMenu.findItem(R.id.menu_online).setVisible(true);
                mMenu.findItem(R.id.menu_favorite).setVisible(false);
                break;
            case ONLINE:
                mMenu.findItem(R.id.menu_online).setVisible(false);
                mMenu.findItem(R.id.menu_favorite).setVisible(true);
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        switch (item.getItemId()) {
            case R.id.menu_online:
                prefs.edit().putString(getString(R.string.pref_menu_key), ONLINE).apply();
                toggleMenuIcon2(ONLINE);
                loadFromInternet();
                return true;
            case R.id.menu_favorite:
                prefs.edit().putString(getString(R.string.pref_menu_key), FAVORITES).apply();
                toggleMenuIcon2(FAVORITES);
                loadFromFavorites();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void loadFromFavorites() {
        viewModel = ActivityViewModel.getInstance(this, true);
    }

    private void loadFromInternet() {
        viewModel = ActivityViewModel.getInstance(this, false);
    }

    private void toggleMenuIcon(String position) {
        switch (position) {
            case FAVORITES:
                getActivity().findViewById(R.id.menu_favorite).setVisibility(View.GONE);
                getActivity().findViewById(R.id.menu_online).setVisibility(View.VISIBLE);
                break;
            case ONLINE:
                getActivity().findViewById(R.id.menu_favorite).setVisibility(View.VISIBLE);
                getActivity().findViewById(R.id.menu_online).setVisibility(View.GONE);
                break;
        }
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
    public void onRecipeClicked(Recipe recipe) {
        // start Detail Activity with the recipe details
        // Sends the complete Selected Recipe
        Intent intent = new Intent(mContext, RecipeDetailActivity.class);
        startActivity(intent);
        viewModel.setRecipe(recipe);
        //viewModel.setRecipeStepNumber(-1);

        Timber.d(recipe.getRecipeName());
    }

    @Override
    public void onFavoriteClicked(Recipe recipe, int position) {
        Integer isFavorite = recipe.getIsFavorite();
        if (isFavorite == null || isFavorite == 0) { // if not favorite make it favorite

            // Save Favorite recipe to database
            new ThreadExecutor().diskIO().execute(() -> {
                RecipesDao recipesDao = RecipeDatabase.getDatabase(mContext).recipesDao();

                recipe.setIsFavorite(1);
                long timeStamp = System.currentTimeMillis();
                recipe.setTimeStamp(timeStamp);
                recipesDao.addRecipe(recipe);

                List<Ingredient> ingredientList = recipe.getIngredientsList();
                Integer columnId = recipesDao.getColumnIdFromTimeStamp(timeStamp);
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
