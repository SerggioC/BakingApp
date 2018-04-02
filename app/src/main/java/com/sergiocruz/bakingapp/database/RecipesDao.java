package com.sergiocruz.bakingapp.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import com.sergiocruz.bakingapp.model.CompleteRecipe;
import com.sergiocruz.bakingapp.model.Ingredient;
import com.sergiocruz.bakingapp.model.Recipe;
import com.sergiocruz.bakingapp.model.RecipeStep;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.ABORT;

@Dao
public interface RecipesDao {

    @Query("SELECT * FROM Recipe")
    List<Recipe> getAllRecipes();

    @Query("select * from Recipe where recipeId = :id")
    Recipe getRecipeById(String id);

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

    @Query("SELECT columnId FROM Recipe WHERE recipeId = :recipeId")
    Integer getColumnIdFromRecipeId(Integer recipeId);


    @Insert(onConflict = ABORT)
    void addRecipe(Recipe recipe);

    @Insert(onConflict = ABORT)
    void addIngredientList(List<Ingredient> ingredientList);

    @Insert(onConflict = ABORT)
    void addStepList(List<RecipeStep> recipeStepList);

    @Insert(onConflict = ABORT)
    void addIngredient(Ingredient ingredient);

    @Insert(onConflict = ABORT)
    void addStep(RecipeStep recipeStep);

    @Insert(onConflict = ABORT)
    void addRecipe(List<Recipe> recipeList);




    @Delete
    void deleteRecipe(Recipe recipe);
}