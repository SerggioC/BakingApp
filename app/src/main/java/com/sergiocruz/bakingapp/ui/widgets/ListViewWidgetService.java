package com.sergiocruz.bakingapp.ui.widgets;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.sergiocruz.bakingapp.ThreadExecutor;
import com.sergiocruz.bakingapp.database.RecipeDatabase;
import com.sergiocruz.bakingapp.database.RecipesDao;
import com.sergiocruz.bakingapp.model.CompleteRecipe;
import com.sergiocruz.bakingapp.model.Ingredient;

import java.util.List;


public class ListViewWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListViewRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}

// RemoteViewsFactory is an adapter
class ListViewRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private final int mAppWidgetId;
    private Context context;
    private List<Ingredient> ingredientList;
    private String recipeName;

    public ListViewRemoteViewsFactory(Context applicationContext, Intent intent) {
        context = applicationContext;
        mAppWidgetId = intent.getIntExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public void onCreate() {

    }

    //called on start and when notifyAppWidgetViewDataChanged is called
    @Override
    public void onDataSetChanged() {

        new ThreadExecutor().diskIO().execute(() -> {
            RecipesDao recipesDao = RecipeDatabase.getDatabase(context).recipesDao();
            Integer recipeColumnId = WidgetConfiguration.loadFromPreferences(context, mAppWidgetId);
            CompleteRecipe completeRecipe = recipesDao.getCompleteRecipeFromColumnId(recipeColumnId);

            ingredientList = completeRecipe.getIngredientList();
            recipeName = completeRecipe.getRecipe().getRecipeName();
        });

    }

    @Override
    public void onDestroy() {}

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
//        if (ingredientList == null || ingredientList.getCount() == 0) return null;
//        ingredientList.moveToPosition(position);
//        int idIndex = ingredientList.getColumnIndex(PlantContract.PlantEntry._ID);
//        int createTimeIndex = ingredientList.getColumnIndex(PlantContract.PlantEntry.COLUMN_CREATION_TIME);
//        int waterTimeIndex = ingredientList.getColumnIndex(PlantContract.PlantEntry.COLUMN_LAST_WATERED_TIME);
//        int plantTypeIndex = ingredientList.getColumnIndex(PlantContract.PlantEntry.COLUMN_PLANT_TYPE);
//
//        long plantId = ingredientList.getLong(idIndex);
//        int plantType = ingredientList.getInt(plantTypeIndex);
//        long createdAt = ingredientList.getLong(createTimeIndex);
//        long wateredAt = ingredientList.getLong(waterTimeIndex);
//        long timeNow = System.currentTimeMillis();
//
//        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.plant_widget);
//
//        // Update the plant image
//        int imgRes = PlantUtils.getPlantImageRes(context, timeNow - createdAt, timeNow - wateredAt, plantType);
//        views.setImageViewResource(R.id.widget_plant_image, imgRes);
//        views.setTextViewText(R.id.widget_plant_name, String.valueOf(plantId));
//        // Always hide the water drop in GridView mode
//        views.setViewVisibility(R.id.widget_water_button, View.GONE);
//
//        // Fill in the onClick PendingIntent Template using the specific plant Id for each item individually
//        Bundle extras = new Bundle();
//        extras.putLong(PlantDetailActivity.EXTRA_PLANT_ID, plantId);
//        Intent fillInIntent = new Intent();
//        fillInIntent.putExtras(extras);
//        views.setOnClickFillInIntent(R.id.widget_plant_image, fillInIntent);

        return null;

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

