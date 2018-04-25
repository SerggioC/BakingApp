package com.sergiocruz.bakingapp.ui.widgets;

import android.appwidget.AppWidgetManager;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewTreeObserver;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.sergiocruz.bakingapp.R;
import com.sergiocruz.bakingapp.adapters.RecipeAdapter;
import com.sergiocruz.bakingapp.model.ActivityViewModel;
import com.sergiocruz.bakingapp.model.Recipe;
import com.sergiocruz.bakingapp.utils.AndroidUtils;
import com.sergiocruz.bakingapp.utils.NetworkUtils;

import java.util.List;

import static com.sergiocruz.bakingapp.fragments.RecipeListFragment.FAVORITES;
import static com.sergiocruz.bakingapp.fragments.RecipeListFragment.GRID_SPAN_COUNT;
import static com.sergiocruz.bakingapp.fragments.RecipeListFragment.ONLINE;
import static com.sergiocruz.bakingapp.fragments.RecipeListFragment.RECYCLER_VIEW_POSITION;

public class WidgetConfiguration extends AppCompatActivity implements RecipeAdapter.RecipeClickListener {
    public static final String PREFERENCE_FILE_NAME = "bakingapp_widget_preference";
    public static final String PREFERENCE_PREFIX = "recipe_widget_id_column_id_";
    private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private WidgetConfiguration mContext;
    private RecyclerView recyclerView;
    private boolean isTwoPane;
    private RecipeAdapter adapter;
    private ActivityViewModel viewModel;
    private Menu mMenu;

    public WidgetConfiguration() {
        super();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_recipe_list);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        // If they gave us an intent without the widget id, finish();
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }

        mContext = this;

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        ((TextView) toolbar.findViewById(R.id.toolbar_text)).setShadowLayer(10, 4, 4, R.color.cardview_dark_background);


        recyclerView = findViewById(R.id.recipe_list_recyclerview);

        isTwoPane = getResources().getBoolean(R.bool.is_two_pane);
        if (isTwoPane) {
            recyclerView.setLayoutManager(new GridLayoutManager(mContext, GRID_SPAN_COUNT, GridLayoutManager.VERTICAL, false));
        } else {
            recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        }
        recyclerView.setHasFixedSize(true);
        adapter = new RecipeAdapter(this, null, null);
        recyclerView.setAdapter(adapter);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        String resourceOnOff = prefs.getString(getString(R.string.pref_menu_key), ONLINE);
        Boolean getFavorites = resourceOnOff.equals(FAVORITES);
        Boolean hasInternet = NetworkUtils.hasActiveNetworkConnection(this);

        // Start the ViewModel
        viewModel = ActivityViewModel.getInstance(this, getFavorites, hasInternet);
        viewModel.getAllRecipes().observe(WidgetConfiguration.this, new Observer<List<Recipe>>() {
            @Override
            public void onChanged(@Nullable List<Recipe> recipesList) {
                adapter.swapRecipesData(recipesList);
            }
        });

        if (savedInstanceState != null) {
            int position = savedInstanceState.getInt(RECYCLER_VIEW_POSITION);
            recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    recyclerView.smoothScrollToPosition(position);
                    recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            });
        }

    }

    private void saveWidgetConfiguration(Integer recipeColumnId) {

        saveToPreferences(this, mAppWidgetId, recipeColumnId);

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        RemoteViews views = new RemoteViews(this.getPackageName(), R.layout.recipe_widget_layout);
        appWidgetManager.updateAppWidget(mAppWidgetId, views);

        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        setResult(RESULT_OK, resultValue);
        finish();
    }

    // Write the prefix to the SharedPreferences object for this widget
    static void saveToPreferences(Context context, int appWidgetId, Integer recipeColumnId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFERENCE_FILE_NAME, MODE_PRIVATE).edit();
        prefs.putInt(PREFERENCE_PREFIX + appWidgetId, recipeColumnId);
        prefs.commit();
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    static Integer loadFromPreferences(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCE_FILE_NAME, MODE_PRIVATE);
        return prefs.getInt(PREFERENCE_PREFIX + appWidgetId, 0);
    }

    @Override
    public void onBackPressed() {
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        setResult(RESULT_CANCELED, resultValue);
        finish();
        super.onBackPressed();
    }

    @Override
    public void onRecipeClicked(Recipe recipe) {
        saveWidgetConfiguration(recipe.getColumnId());
    }


    @Override
    public void onSaveInstanceState(Bundle currentState) {
        super.onSaveInstanceState(currentState);
        currentState.putInt(RECYCLER_VIEW_POSITION, ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_options, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        this.mMenu = menu;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        String resourceOnOff = prefs.getString(getString(R.string.pref_menu_key), ONLINE);
        toggleMenuIcon(resourceOnOff);
        return true;
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
            AndroidUtils.showCustomToast(mContext,
                    getString(R.string.working_offline),
                    R.mipmap.ic_info, R.color.blue, Toast.LENGTH_LONG);
        }
        toggleMenuIcon(hasInternet ? ONLINE : FAVORITES);
    }



}
