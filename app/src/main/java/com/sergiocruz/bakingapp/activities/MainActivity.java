package com.sergiocruz.bakingapp.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.sergiocruz.bakingapp.R;
import com.sergiocruz.bakingapp.adapters.RecipeAdapter;
import com.sergiocruz.bakingapp.model.RecipeApiController;
import com.sergiocruz.bakingapp.helpers.TimberImplementation;
import com.sergiocruz.bakingapp.model.Recipe;
import com.sergiocruz.bakingapp.utils.NetworkUtils;

import java.util.List;

import timber.log.Timber;

/**
 * An activity representing a list of recipes. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link RecipeDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class MainActivity extends AppCompatActivity implements RecipeAdapter.RecipeClickListener, RecipeApiController.AdapterCallback{

    private boolean mIsTwoPane;
    private Context mContext;
    private RecyclerView recyclerView;
    private RecipeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TimberImplementation.init();

        mContext = this;

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
        ((TextView) toolbar.findViewById(R.id.toolbar_text)).setShadowLayer(10, 4, 4, R.color.cardview_dark_background);

        mIsTwoPane = getResources().getBoolean(R.bool.is_two_pane);

        recyclerView = findViewById(R.id.recipe_list_recyclerview);
        adapter = new RecipeAdapter(this, this);
        setupRecyclerView(recyclerView, adapter);

        if (NetworkUtils.hasActiveNetworkConnection(mContext)) {
            getDataFromInternet();
        } else {
            getDataFromLocalDataBase();
        }

    }

    private void setupRecyclerView(RecyclerView recyclerView, RecipeAdapter adapter) {
        if (mIsTwoPane) {
            recyclerView.setLayoutManager(new GridLayoutManager(mContext, 3, GridLayoutManager.VERTICAL, false));
        } else {
            recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        }
        recyclerView.setAdapter(adapter);
    }

    // Get data using retrofit and serialize automatically with GSON
    private void getDataFromInternet() {
        new RecipeApiController().init(this);
    }

    private void getDataFromLocalDataBase() {

    }

    @Override
    public void onRecipeClicked(Recipe recipe, View itemView) {
        // start fragment with the recipe
        Timber.d(recipe.getRecipeName());
    }

    @Override
    public void onRetrofitResponse(List<Recipe> recipesList) {
        adapter.swapRecipesData(recipesList, false, false);
    }
}
