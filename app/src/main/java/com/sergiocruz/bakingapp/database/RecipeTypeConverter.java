package com.sergiocruz.bakingapp.database;

import android.arch.persistence.room.TypeConverter;

import com.sergiocruz.bakingapp.model.CompleteRecipe;
import com.sergiocruz.bakingapp.model.Recipe;

import java.util.ArrayList;
import java.util.List;

public class RecipeTypeConverter {

    @TypeConverter
    public static List<Recipe> convertToRecipeList(List<CompleteRecipe> completeRecipeList) {
        List<Recipe> recipeList = new ArrayList<>(completeRecipeList.size());
        for (CompleteRecipe completeRecipe : completeRecipeList) {
            Recipe newRecipe = new Recipe(
                    completeRecipe.getRecipe().getColumnId(),
                    completeRecipe.getRecipe().getRecipeId(),
                    completeRecipe.getRecipe().getRecipeName(),
                    completeRecipe.getIngredientList(),
                    completeRecipe.getRecipeStepList(),
                    completeRecipe.getRecipe().getServings(),
                    completeRecipe.getRecipe().getRecipeImage(),
                    completeRecipe.getRecipe().getIsFavorite(),
                    completeRecipe.getRecipe().getTimeStamp()
            );
            recipeList.add(newRecipe);
        }
        return recipeList;
    }



}
