package com.sergiocruz.bakingapp.model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.sergiocruz.bakingapp.database.RecipeDatabase;

import java.util.List;

public class MainFragmentViewModel extends AndroidViewModel {

    private final LiveData<List<Recipe>> recipesList;

    private RecipeDatabase appDatabase;

    public MainFragmentViewModel(Application application) {
        super(application);
        appDatabase = RecipeDatabase.getDatabase(this.getApplication());
        recipesList = appDatabase.recipesDao().getAllRecipes();
    }

    public LiveData<List<Recipe>> getAllRecipes() {
        return recipesList;
    }

    public void deleteRecipe(Recipe recipe) {
        new deleteAsyncTask(appDatabase).execute(recipe);
    }

    private static class deleteAsyncTask extends AsyncTask<Recipe, Void, Void> {
        private RecipeDatabase db;
        deleteAsyncTask(RecipeDatabase appDatabase) {
            db = appDatabase;
        }
        @Override
        protected Void doInBackground(final Recipe... params) {
            db.recipesDao().deleteRecipe(params[0]);
            return null;
        }
    }

}
