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
import android.view.ViewTreeObserver;
import android.widget.TextView;
import android.widget.Toast;

import com.sergiocruz.bakingapp.R;
import com.sergiocruz.bakingapp.SimpleIdlingResource;
import com.sergiocruz.bakingapp.ThreadExecutor;
import com.sergiocruz.bakingapp.activities.RecipeDetailActivity;
import com.sergiocruz.bakingapp.adapters.RecipeAdapter;
import com.sergiocruz.bakingapp.database.RecipeDatabase;
import com.sergiocruz.bakingapp.database.RecipeTypeConverter;
import com.sergiocruz.bakingapp.model.ActivityViewModel;
import com.sergiocruz.bakingapp.model.Recipe;
import com.sergiocruz.bakingapp.utils.NetworkUtils;

import java.util.List;

import timber.log.Timber;

import static com.sergiocruz.bakingapp.utils.AndroidUtils.showCustomToast;

public class RecipeListFragment extends Fragment implements
        RecipeAdapter.RecipeClickListener, RecipeAdapter.FavoriteClickListener,
        RecipeAdapter.FavoriteLongClickListener {
    public static final String RECYCLER_VIEW_POSITION = "RecyclerView_Position";
    public static final String ONLINE = "online";
    public static final String FAVORITES = "favorites";
    public static final int GRID_SPAN_COUNT = 2;
    // SimpleIdlingResource variable will be null in production
    @Nullable
    SimpleIdlingResource simpleIdlingResource;
    private Context mContext;
    private ActivityViewModel viewModel;
    private RecyclerView recyclerView;
    private RecipeAdapter adapter;
    private boolean isTwoPane;
    private Menu mMenu;

    /**
     * method that returns the IdlingResource variable. It will
     * instantiate a new instance of SimpleIdlingResource if the IdlingResource is null.
     * This method will only be called from test.
     */
    @Nullable
    public SimpleIdlingResource getSimpleIdlingResource() {
        if (simpleIdlingResource == null)
            simpleIdlingResource = new SimpleIdlingResource();
        return simpleIdlingResource;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        simpleIdlingResource = getSimpleIdlingResource();
        /**
         * The IdlingResource is null in production as set by the @Nullable annotation which means
         * the value is allowed to be null.
         *
         * If the idle state is true, Espresso can perform the next action.
         * If the idle state is false, Espresso will wait until it is true before
         * performing the next action.
         */
        if (simpleIdlingResource != null) {
            simpleIdlingResource.setIdleState(false);
        }
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
        Boolean getFavorites = resourceOnOff.equals(FAVORITES);
        Boolean hasInternet = NetworkUtils.hasActiveNetworkConnection(mContext);

        // Start the ViewModel
        viewModel = ActivityViewModel.getInstance(this, getFavorites, hasInternet);
        viewModel.getAllRecipes().observe(RecipeListFragment.this, new Observer<List<Recipe>>() {
            /** Called when the data has changed.
             *  @param recipesList The new data */
            @Override
            public void onChanged(@Nullable List<Recipe> recipesList) {
                adapter.swapRecipesData(recipesList);

            }
        });

        recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                if (savedInstanceState != null) {
                    int position = savedInstanceState.getInt(RECYCLER_VIEW_POSITION);
                    recyclerView.smoothScrollToPosition(position);
                }

                // Set Idle state to true after all done
                // for testing only
                if (simpleIdlingResource != null) {
                    simpleIdlingResource.setIdleState(true);
                }

                recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });


        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle currentState) {
        currentState.putInt(RECYCLER_VIEW_POSITION, ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition());
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
        toggleMenuIcon(resourceOnOff);
    }

    private void toggleMenuIcon(String position) {
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
        switch (item.getItemId()) {
            case R.id.menu_online:
                loadFromInternet();
                return true;
            case R.id.menu_favorite:
                loadFromFavorites();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void loadFromFavorites() {
        Boolean hasInternet = NetworkUtils.hasActiveNetworkConnection(mContext);
        viewModel.updateData(true, hasInternet);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        prefs.edit().putString(getString(R.string.pref_menu_key), FAVORITES).apply();
        toggleMenuIcon(FAVORITES);
    }

    private void loadFromInternet() {
        Boolean hasInternet = NetworkUtils.hasActiveNetworkConnection(mContext);
        viewModel.updateData(false, hasInternet);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        prefs.edit().putString(getString(R.string.pref_menu_key), ONLINE).apply();
        if (!hasInternet) {
            showCustomToast(mContext,
                    getString(R.string.working_offline),
                    R.mipmap.ic_info, R.color.blue, Toast.LENGTH_LONG);
        }
        toggleMenuIcon(hasInternet ? ONLINE : FAVORITES);
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

        Timber.d(recipe.getRecipeName());
    }

    @Override
    public void onFavoriteClicked(Recipe recipe, int position) {
        Integer isFavorite = recipe.getIsFavorite();
        if (isFavorite == null || isFavorite == 0) { // if not favorite make it favorite
            // Save Favorite recipe to database
            new ThreadExecutor().diskIO().execute(() ->
                    RecipeTypeConverter.saveRecipeToDB(recipe, mContext));
        } else {
            showCustomToast(mContext, getString(R.string.already_saved), 0, R.color.brown700, Toast.LENGTH_SHORT);
        }
    }


    @Override
    public void onFavoriteLongClicked(Recipe recipe, int position) {
        Integer isFavorite = recipe.getIsFavorite();
        if (isFavorite != null) { // if it's a favorite remove it from favorites
            if (isFavorite == 1) {
                new ThreadExecutor().diskIO().execute(() ->
                        RecipeDatabase.getDatabase(mContext).recipesDao().deleteRecipeByColumnId(recipe.getColumnId()));
            }
        } else {
            Toast.makeText(mContext, R.string.click_to_save, Toast.LENGTH_LONG).show();
        }
    }

}
