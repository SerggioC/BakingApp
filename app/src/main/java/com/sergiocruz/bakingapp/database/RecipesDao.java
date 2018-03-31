package com.sergiocruz.bakingapp.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.sergiocruz.bakingapp.model.Recipe;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.ABORT;

@Dao
public interface RecipesDao {

    @Query("SELECT * FROM Recipe")
    LiveData<List<Recipe>> getAllRecipes();

    @Query("select * from Recipe where recipeId = :id")
    Recipe getRecipeById(String id);

    @Insert(onConflict = ABORT)
    void addRecipe(Recipe recipe);

    @Delete
    void deleteRecipe(Recipe recipe);

}
