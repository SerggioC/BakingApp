package com.sergiocruz.bakingapp.ui.widgets;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.sergiocruz.bakingapp.R;
import com.sergiocruz.bakingapp.database.RecipeDatabase;
import com.sergiocruz.bakingapp.database.RecipesDao;
import com.sergiocruz.bakingapp.model.CompleteRecipe;
import com.sergiocruz.bakingapp.model.Ingredient;
import com.sergiocruz.bakingapp.model.Recipe;

import java.util.List;

import timber.log.Timber;

import static com.sergiocruz.bakingapp.ui.widgets.RecipeWidgetProvider.WIDGET_RECIPE_BUNDLE;
import static com.sergiocruz.bakingapp.ui.widgets.RecipeWidgetProvider.WIDGET_RECIPE_EXTRA;
import static com.sergiocruz.bakingapp.utils.AndroidUtils.capitalize;


public class ListViewWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        Timber.i("onGetViewFactory");
        return new ListViewRemoteViewsFactory(this, intent);
    }
}

// RemoteViewsFactory is an adapter
class ListViewRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private final int mAppWidgetId;
    private int recipeColumnId = -1;
    private final Intent intent;
    private Context mContext;
    private List<Ingredient> ingredientList;

    public ListViewRemoteViewsFactory(Context context, Intent intent) {
        mContext = context;
        this.intent = intent;
        mAppWidgetId = intent.getIntExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
        getDataFromBundle(intent);
        Timber.i("ListViewRemoteViewsFactory Constructor mAppWidgetId=" + mAppWidgetId + " recipeColumnId= " + recipeColumnId);
    }

    private void getDataFromBundle(Intent intent) {
        Bundle bundle = intent.getBundleExtra(WIDGET_RECIPE_BUNDLE);
        if (bundle != null) {
            Recipe recipe = bundle.getParcelable(WIDGET_RECIPE_EXTRA);
            if (recipe != null){
                ingredientList = recipe.getIngredientsList();
                recipeColumnId = recipe.getColumnId();
            }
        }
    }

    @Override
    public void onCreate() {
        Timber.i("onCreate RemoteViewsFactory");
        getDataFromBundle(intent);
        //getDataSynchronously();
    }

    // Called on start and when notifyAppWidgetViewDataChanged is called
    @Override
    public void onDataSetChanged() {
        Timber.i("onDataSetChanged RemoteViewsFactory");

        getDataFromBundle(intent);
        //getDataSynchronously();
    }

    private void getDataSynchronously() {
        RecipesDao recipesDao = RecipeDatabase.getDatabase(mContext).recipesDao();
        recipeColumnId = WidgetConfiguration.loadFromPreferences(mContext, mAppWidgetId);
        CompleteRecipe completeRecipe = recipesDao.getCompleteRecipeFromColumnId(recipeColumnId);
        ingredientList = completeRecipe.getIngredientList();
    }

    @Override
    public void onDestroy() {
        Timber.i("onDestroy RemoteViewsFactory");
    }

    @Override
    public int getCount() {
        if (ingredientList == null) return 0;
        return ingredientList.size();
    }

    /**
     * This method acts like the onBindViewHolder method in an Adapter
     *
     * @param position The current position of the item in the ListView to be displayed
     * @return The RemoteViews object to display for the provided position
     */
    @Override
    public RemoteViews getViewAt(int position) {
        if (ingredientList == null || ingredientList.size() == 0) return null;

        RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.widget_ingredient_row);

        // Update the widget
        Ingredient ingredient = ingredientList.get(position);
        remoteViews.setTextViewText(R.id.ingredient_row, capitalize(ingredient.getIngredient()));
        remoteViews.setTextViewText(R.id.quantity_row, ingredient.getQuantity() + " " + ingredient.getMeasure());
        boolean checked = ingredient.getChecked() != null && ingredient.getChecked() == 1;
        if (checked) {
            remoteViews.setTextColor(R.id.ingredient_row, ContextCompat.getColor(mContext, R.color.green_done));
            remoteViews.setTextColor(R.id.quantity_row, ContextCompat.getColor(mContext, R.color.green_done));
        }


//        // Fill in the onClick PendingIntent Template using the specific ingredient Id for each item individually
//        Bundle extras = new Bundle();
//        extras.putLong(EXTRA_INGREDIENT_ID, ingredient.getIngredientId());
//        Intent clickIntent = new Intent();
//        clickIntent.putExtras(extras);
//        remoteViews.setOnClickFillInIntent(R.id.widget_row, clickIntent);

        return remoteViews;

    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1; // Treat all items in the ListView the same
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}

