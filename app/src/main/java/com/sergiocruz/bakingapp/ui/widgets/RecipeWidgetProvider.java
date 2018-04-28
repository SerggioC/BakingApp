package com.sergiocruz.bakingapp.ui.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.RemoteViews;

import com.sergiocruz.bakingapp.R;
import com.sergiocruz.bakingapp.activities.MainActivity;
import com.sergiocruz.bakingapp.activities.RecipeDetailActivity;
import com.sergiocruz.bakingapp.database.RecipeDatabase;
import com.sergiocruz.bakingapp.database.RecipeTypeConverter;
import com.sergiocruz.bakingapp.database.RecipesDao;
import com.sergiocruz.bakingapp.model.CompleteRecipe;
import com.sergiocruz.bakingapp.model.Ingredient;
import com.sergiocruz.bakingapp.model.Recipe;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class RecipeWidgetProvider extends AppWidgetProvider {

    public static final String WIDGET_RECIPE_EXTRA = "widget_recipe_extra";
    public static final String WIDGET_RECIPE_BUNDLE = "widget_recipe_bundle";

    static void updateRecipeAppWidget(Context context, AppWidgetManager appWidgetManager, Recipe recipe, int appWidgetId) {
        RemoteViews remoteViews = getRecipeRemoteViews(context, recipe, appWidgetId);
        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.widget_listview);
    }

    /**
     * Creates and returns the RemoteViews to be displayed in the single recipe mode widget
     */
    private static RemoteViews getRecipeRemoteViews(Context context, Recipe recipe, int appWidgetId) {
        // Set the click handler to open the DetailActivity for the recipe ID,
        // or the MainActivity if recipe ID is invalid
        Intent intent;
        if (recipe == null) {
            intent = new Intent(context, MainActivity.class);
        } else {
            // Set on click to open the corresponding detail activity
            intent = new Intent(context, RecipeDetailActivity.class);
            intent.putExtra(RecipeDetailActivity.EXTRA_WIDGET_RECIPE, recipe.getColumnId());
        }

        // Construct the RemoteViews object.  It takes the package name (in our case, it's our
        // package, but it needs this because on the other side it's the widget host inflating
        // the layout from our package).
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.recipe_widget_layout);

        Intent serviceIntent = new Intent(context, ListViewWidgetService.class);
        serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        serviceIntent.setData(Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME)));

        Bundle bundle = new Bundle();
        bundle.putParcelable(WIDGET_RECIPE_EXTRA, recipe);
        serviceIntent.putExtra(WIDGET_RECIPE_BUNDLE, bundle);

        remoteViews.setRemoteAdapter(R.id.widget_listview, serviceIntent);

        // Update Recipe image, recipe name and ingredients
        String recipeImageUrl = recipe != null ? recipe.getRecipeImage() : null;
        if (TextUtils.isEmpty(recipeImageUrl)) {
            remoteViews.setImageViewResource(R.id.widget_image_background, R.mipmap.ic_launcher);
        } else {
            remoteViews.setImageViewUri(R.id.widget_image_background, Uri.parse(recipeImageUrl));
        }

        remoteViews.setTextViewText(R.id.widget_recipe_name, recipe == null ? context.getString(R.string.app_name) :
                recipe.getRecipeName() + " " + context.getString(R.string.ingredients));

        // using Factory Service...
        //remoteViews.setTextViewText(R.id.widget_recipe_ingredients, getFormattedIngredientList(recipe != null ? recipe.getIngredientsList() : null));

        // Widgets allow click handlers to only launch pending intents
        // On clicking the widget launch the associated activity
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.widget_root, pendingIntent);


        return remoteViews;
    }

    // Ignore Using Factory Service to provide data
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

    // Start the intent service update widget action,
    // the service takes care of updating the widgets UI
    // Called every updatePeriodMillis milliseconds
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        new AsyncTask<Void, Void, List<Recipe>>() {
            @Override
            protected List<Recipe> doInBackground(Void[] objects) {
                RecipesDao recipesDao = RecipeDatabase.getDatabase(context).recipesDao();
                List<Integer> recipeColumnIds = WidgetConfiguration.loadAllFromPreferences(context, appWidgetIds);
                int size = recipeColumnIds.size();
                List<CompleteRecipe> completeRecipeList = new ArrayList<>(size);
                for (int i = 0; i < size; i++) {
                    completeRecipeList.add(recipesDao.getCompleteRecipeFromColumnId(recipeColumnIds.get(i)));
                }

                return RecipeTypeConverter.convertToRecipeList(completeRecipeList);
            }

            @Override
            protected void onPostExecute(List<Recipe> recipeList) {
                if (recipeList == null) return;

                for (int i = 0; i < recipeList.size(); i++) {
                    updateRecipeAppWidget(context, appWidgetManager, recipeList.get(i), appWidgetIds[i]);
                }
            }
        }.execute();

        super.onUpdate(context, appWidgetManager, appWidgetIds);

    }

    // This is called when the widget is first placed
    // and any time the widget is resized.
    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }

    // This is called every time an App Widget is deleted from the App Widget host.
    // Delete AppWidgetIds from preferences on delete widget
    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        WidgetConfiguration.deleteFromPreferences(context, appWidgetIds);
    }

    @Override
    public void onEnabled(Context context) {
        Timber.i("Enabling widget");
        // Perform any action when an AppWidget for this provider is instantiated
    }

    @Override
    public void onDisabled(Context context) {
        Timber.i("Disabling widget");
        // Perform any action when the last AppWidget instance for this provider is deleted
    }


}
