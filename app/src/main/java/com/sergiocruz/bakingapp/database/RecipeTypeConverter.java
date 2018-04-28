package com.sergiocruz.bakingapp.database;

import android.arch.persistence.room.TypeConverter;
import android.content.Context;

import com.sergiocruz.bakingapp.model.CompleteRecipe;
import com.sergiocruz.bakingapp.model.Ingredient;
import com.sergiocruz.bakingapp.model.Recipe;
import com.sergiocruz.bakingapp.model.RecipeStep;

import java.util.ArrayList;
import java.util.List;

public class RecipeTypeConverter {

    @TypeConverter
    public static List<Recipe> convertToRecipeList(List<CompleteRecipe> completeRecipeList) {
        if (completeRecipeList == null) return null;
        List<Recipe> recipeList = new ArrayList<>(completeRecipeList.size());
        for (CompleteRecipe completeRecipe : completeRecipeList) {
            recipeList.add(convertToRecipe(completeRecipe));
        }
        return recipeList;
    }

    @TypeConverter
    public static Recipe convertToRecipe(CompleteRecipe completeRecipe) {
        if (completeRecipe == null) return null;
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
        return newRecipe;
    }

    public static Integer saveRecipeToDB(Recipe recipe, Context context) {
        RecipesDao recipesDao = RecipeDatabase.getDatabase(context).recipesDao();

        recipe.setIsFavorite(1);
        long timeStamp = System.currentTimeMillis();
        recipe.setTimeStamp(timeStamp);
        recipesDao.addRecipe(recipe);

        List<Ingredient> ingredientList = recipe.getIngredientsList();
        Integer columnId = recipesDao.getColumnIdFromTimeStamp(timeStamp);
        for (int j = 0; j < ingredientList.size(); j++) {
            Ingredient ingredient = ingredientList.get(j);
            ingredient.setRecipeId(columnId);
            recipesDao.addIngredient(ingredient);
        }

        List<RecipeStep> stepsList = recipe.getStepsList();
        for (int k = 0; k < stepsList.size(); k++) {
            RecipeStep recipeStep = stepsList.get(k);
            recipeStep.setRecipeId(columnId);
            recipesDao.addStep(recipeStep);
        }
        return columnId;
    }
}
