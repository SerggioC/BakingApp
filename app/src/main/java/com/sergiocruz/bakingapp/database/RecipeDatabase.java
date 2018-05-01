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

@Database(entities = {Recipe.class, Ingredient.class, RecipeStep.class}, version = 4)
public abstract class RecipeDatabase extends RoomDatabase {
    private static final String RECIPE_DATABASE_NAME = "recipes.db";
    private static RecipeDatabase DATABASE_INSTANCE;

    public static RecipeDatabase getDatabase(Context context) {
        if (DATABASE_INSTANCE == null) {
            DATABASE_INSTANCE = Room
                    .databaseBuilder(context.getApplicationContext(), RecipeDatabase.class, RECIPE_DATABASE_NAME)
                    //.allowMainThreadQueries() // sync
                    //.fallbackToDestructiveMigration() // Destroys the DB and recreates it with the new schema
                    .addMigrations(MIGRATION_1_2)
                    .addMigrations(MIGRATION_2_3)
                    .addMigrations(MIGRATION_3_4)
                    .build();
        }
        return DATABASE_INSTANCE;
    }

    // Migrate DB from version 1 to version 2: add column isFavorite
    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE Recipe ADD COLUMN isFavorite INTEGER");
        }
    };

    // Migrate DB from version 2 to version 3: add column timeStamp
    private static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE Recipe ADD COLUMN timeStamp INTEGER");
        }
    };

    // Migrate DB from version 3 to version 4: add column checked ingredient
    private static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE Ingredient ADD COLUMN checked INTEGER");
        }
    };

    public abstract RecipesDao recipesDao();
}