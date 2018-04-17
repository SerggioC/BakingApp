package com.sergiocruz.bakingapp.database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;

import com.sergiocruz.bakingapp.model.Ingredient;
import com.sergiocruz.bakingapp.model.Recipe;
import com.sergiocruz.bakingapp.model.RecipeStep;

@Database(entities = {Recipe.class, Ingredient.class, RecipeStep.class}, version = 2)
public abstract class RecipeDatabase extends RoomDatabase {
    private static final String RECIPE_DATABASE_NAME = "recipes.db";
    private static RecipeDatabase DATABASE_INSTANCE;

    public static RecipeDatabase getDatabase(Context context) {
        if (DATABASE_INSTANCE == null) {
            DATABASE_INSTANCE = Room
                    .databaseBuilder(context.getApplicationContext(), RecipeDatabase.class, RECIPE_DATABASE_NAME)
                    .addMigrations(MIGRATION_1_2)
                    .build();
        }
        return DATABASE_INSTANCE;
    }

    // Migrate from version 1 to version 2: add column isFavorite
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE Recipe ADD COLUMN isFavorite INTEGER");
        }
    };

    public abstract RecipesDao recipesDao();
    public abstract UpdateRecipesDao updateRecipesDao();
}
