package com.sergiocruz.bakingapp.ui.widgets;

/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.RemoteViews;

import com.sergiocruz.bakingapp.R;
import com.sergiocruz.bakingapp.ThreadExecutor;
import com.sergiocruz.bakingapp.activities.MainActivity;
import com.sergiocruz.bakingapp.activities.RecipeDetailActivity;
import com.sergiocruz.bakingapp.database.RecipeDatabase;
import com.sergiocruz.bakingapp.model.CompleteRecipe;
import com.sergiocruz.bakingapp.model.Ingredient;

import java.util.List;

public class RecipeWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // Start the intent service update widget action,
        // the service takes care of updating the widgets UI

        RecipeDatabase db = RecipeDatabase.getDatabase(context);

        new ThreadExecutor().diskIO().execute(() -> {
            List<CompleteRecipe> completeRecipesList = db.recipesDao().getFavoriteCompleteRecipeList();
            CompleteRecipe recipe = completeRecipesList.get(0);

            new ThreadExecutor().mainThread().execute(() -> updateRecipeWidgets(context, appWidgetManager, recipe, appWidgetIds));
        });


        //PlantWateringService.startActionUpdatePlantWidgets(context);
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        PlantWateringService.startActionUpdatePlantWidgets(context);
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // Perform any action when one or more AppWidget instances have been deleted
    }

    @Override
    public void onEnabled(Context context) {
        // Perform any action when an AppWidget for this provider is instantiated
    }

    @Override
    public void onDisabled(Context context) {
        // Perform any action when the last AppWidget instance for this provider is deleted
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, CompleteRecipe recipe, int appWidgetId) {
        RemoteViews remoteViews = getRecipeRemoteViews(context, recipe);
        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }

    /**
     * Updates all widget instances given the widget Ids and display information
     */
    public static void updateRecipeWidgets(Context context, AppWidgetManager appWidgetManager, CompleteRecipe recipe, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, recipe, appWidgetId);
        }
    }

    /**
     * Creates and returns the RemoteViews to be displayed in the single recipe mode widget
     */
    private static RemoteViews getRecipeRemoteViews(Context context, CompleteRecipe recipe) {
        // Set the click handler to open the DetailActivity for the recipe ID,
        // or the MainActivity if plant ID is invalid
        Intent intent;
        if (recipe == null) {
            intent = new Intent(context, MainActivity.class);
        } else {
            // Set on click to open the corresponding detail activity
            intent = new Intent(context, RecipeDetailActivity.class);
            intent.putExtra(RecipeDetailActivity.EXTRA_WIDGET_RECIPE, recipe.getRecipe().getColumnId());
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.recipe_widget);

        // Update Recipe image, recipe name and ingredients
        String recipeImageUrl = recipe != null ? recipe.getRecipe().getRecipeImage() : null;
        if (TextUtils.isEmpty(recipeImageUrl)) {
            views.setImageViewResource(R.id.widget_image_background, R.mipmap.ic_launcher);
        } else {
            views.setImageViewUri(R.id.widget_image_background, Uri.parse(recipeImageUrl));
        }
        views.setTextViewText(R.id.widget_recipe_name, recipe != null ? recipe.getRecipe().getRecipeName() : context.getString(R.string.app_name));
        views.setTextViewText(R.id.widget_recipe_ingredients, getFormattedIngredientList(recipe != null ? recipe.getIngredientList() : null));

        // Widgets allow click handlers to only launch pending intents
        views.setOnClickPendingIntent(R.id.widget_root, pendingIntent);

        return views;
    }

    private static String getFormattedIngredientList(List<Ingredient> ingredientList) {
        if (ingredientList == null) return "";
        int size = ingredientList.size();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < size; i++) {
            Ingredient ingredient = ingredientList.get(i);
            stringBuilder
                    .append(ingredient.getQuantity()).append(" ")
                    .append(ingredient.getMeasure()).append(" ")
                    .append(ingredient.getIngredient()).append("\n");
        }
        return stringBuilder.toString();
    }


}
