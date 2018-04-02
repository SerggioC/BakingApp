package com.sergiocruz.bakingapp.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.sergiocruz.bakingapp.model.Ingredient;
import com.sergiocruz.bakingapp.model.Recipe;
import com.sergiocruz.bakingapp.model.RecipeStep;

@Database(entities = {Recipe.class, Ingredient.class, RecipeStep.class}, version = 1)
public abstract class RecipeDatabase extends RoomDatabase {
    private static final String RECIPE_DATABASE_NAME = "recipes.db";
    private static RecipeDatabase DATABASE_INSTANCE;

    public static RecipeDatabase getDatabase(Context context) {
        if (DATABASE_INSTANCE == null) {
            DATABASE_INSTANCE = Room
                    .databaseBuilder(context.getApplicationContext(), RecipeDatabase.class, RECIPE_DATABASE_NAME)
                    .build();
        }
        return DATABASE_INSTANCE;
    }

    public abstract RecipesDao recipesDao();
}
