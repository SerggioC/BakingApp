package com.sergiocruz.bakingapp.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;
import android.arch.persistence.room.TypeConverters;

import com.sergiocruz.bakingapp.model.CompleteRecipe;
import com.sergiocruz.bakingapp.model.Ingredient;
import com.sergiocruz.bakingapp.model.Recipe;
import com.sergiocruz.bakingapp.model.RecipeStep;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface RecipesDao {

    @Query("SELECT * FROM Recipe")
    List<Recipe> getAllRecipes();

    @Query("select * from Recipe where columnId = :id")
    Recipe getRecipeByColumnId(Integer id);

    @Query("SELECT * from Ingredient where recipeId = :id")
    List<Ingredient> getIngredientsForRecipeId(Integer id);

    @Query("SELECT * from RecipeStep where recipeId = :id")
    List<RecipeStep> getStepsForRecipeId(Integer id);

    @Query("SELECT COUNT(*) FROM Recipe")
    Integer getNumberOfRecipes();

    @Transaction
    @Query("SELECT * from Recipe where columnId = :id")
    CompleteRecipe getCompleteRecipe(Integer id);

    @Transaction
    @Query("SELECT * from Recipe")
    List<CompleteRecipe> getAllCompleteRecipes();

    @Query("SELECT columnId FROM Recipe WHERE timeStamp = :timeStamp")
    Integer getColumnIdFromTimeStamp(long timeStamp);

    @Transaction
    @Query("SELECT * from Recipe where isFavorite = 1 ORDER BY timeStamp DESC")
    List<CompleteRecipe> getFavoriteCompleteRecipeList();

    @Transaction
    @Query("SELECT * from Recipe where isFavorite = 1 AND columnId = :columnId")
    CompleteRecipe getCompleteRecipeFromColumnId(Integer columnId);

    @Transaction
    @Query("SELECT * from Recipe where isFavorite = 1 AND columnId = :columnIds")
    List<CompleteRecipe> getCompleteRecipeListFromColumnIds(List<Integer> columnIds);

    @TypeConverters(RecipeTypeConverter.class) //won't work
    @Transaction
    @Query("SELECT * from Recipe where isFavorite = 1 ORDER BY timeStamp DESC")
    List<CompleteRecipe> getFavoriteRecipeListTypeConverted();

    //@Update(onConflict = OnConflictStrategy.REPLACE)
    @Transaction
    @Query("UPDATE Recipe SET isFavorite = :isFavorite WHERE columnId = :columnId")
    void updateRecipe(Integer isFavorite, Integer columnId);


    @Insert(onConflict = REPLACE)
    void addRecipe(Recipe recipe);

    @Insert(onConflict = REPLACE)
    void addIngredientList(List<Ingredient> ingredientList);

    @Insert(onConflict = REPLACE)
    void addStepList(List<RecipeStep> recipeStepList);

    @Insert(onConflict = REPLACE)
    void addIngredient(Ingredient ingredient);

    @Insert(onConflict = REPLACE)
    void addStep(RecipeStep recipeStep);

    @Insert(onConflict = REPLACE)
    void addRecipeList(List<Recipe> recipeList);

    @Query("DELETE FROM Recipe WHERE columnId = :columnId")
    void deleteRecipeByColumnId(Integer columnId);

    @Delete
    void deleteRecipe(Recipe recipe);

}
