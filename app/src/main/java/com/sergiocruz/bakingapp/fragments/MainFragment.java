package com.sergiocruz.bakingapp.fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
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
import com.sergiocruz.bakingapp.adapters.RecipeAdapter;
import com.sergiocruz.bakingapp.model.MainFragmentViewModel;
import com.sergiocruz.bakingapp.model.Recipe;
import com.sergiocruz.bakingapp.model.RecipesDataRepository;

import java.util.List;

import timber.log.Timber;

public class MainFragment extends Fragment implements RecipeAdapter.RecipeClickListener {

    public static final String RECYCLER_VIEW_POSITION = "RecyclerView_Position";
    private Context mContext;
    private MainFragmentViewModel viewModel;
    private RecyclerView recyclerView;
    private RecipeAdapter adapter;
    private boolean mIsTwoPane;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View rootView = inflater.inflate(R.layout.recipe_list_fragment, container, false);

        mContext = getContext();


        Toolbar toolbar = rootView.findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        toolbar.setTitle(getTitle());
        ((TextView) toolbar.findViewById(R.id.toolbar_text)).setShadowLayer(10, 4, 4, R.color.cardview_dark_background);

        setHasOptionsMenu(true);

        mIsTwoPane = getResources().getBoolean(R.bool.is_two_pane);

        recyclerView = rootView.findViewById(R.id.recipe_list_recyclerview);

        adapter = new RecipeAdapter(mContext, this);
        setupRecyclerView(recyclerView, adapter);

        viewModel = ViewModelProviders.of(MainFragment.this).get(MainFragmentViewModel.class);
        viewModel.init(new RecipesDataRepository(mContext), false);

        viewModel.getAllRecipes().observe(MainFragment.this, new Observer<List<Recipe>>() {
            /**
             * Called when the data is changed.
             * @param recipesList The new data
             */
            @Override
            public void onChanged(@Nullable List<Recipe> recipesList) {
                adapter.swapRecipesData(recipesList, false, false);
            }

        });
        if (savedInstanceState != null) {
            //
            int position = savedInstanceState.getInt(RECYCLER_VIEW_POSITION);
            recyclerView.smoothScrollToPosition(position);
        }

        return rootView;
    }


    private void setupRecyclerView(RecyclerView recyclerView, RecipeAdapter adapter) {
        if (mIsTwoPane) {
            recyclerView.setLayoutManager(new GridLayoutManager(mContext, 3, GridLayoutManager.VERTICAL, false));
        } else {
            recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        }
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

    }




    @Override
    public void onRecipeClicked(Recipe recipe, View itemView) {
        // start fragment with the recipe
        Timber.d(recipe.getRecipeName());
    }


}
