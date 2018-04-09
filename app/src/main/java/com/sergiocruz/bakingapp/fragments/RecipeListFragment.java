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

import com.sergiocruz.bakingapp.R;
import com.sergiocruz.bakingapp.activities.RecipeDetailActivity;
import com.sergiocruz.bakingapp.adapters.RecipeAdapter;
import com.sergiocruz.bakingapp.model.ActivityViewModel;
import com.sergiocruz.bakingapp.model.Recipe;

import java.util.List;

import timber.log.Timber;

public class RecipeListFragment extends Fragment implements RecipeAdapter.RecipeClickListener {
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

        adapter = new RecipeAdapter(this);
        setupRecyclerView(recyclerView, adapter);

        // Start the ViewModel
        viewModel = ActivityViewModel.getInstance(this);
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


}
