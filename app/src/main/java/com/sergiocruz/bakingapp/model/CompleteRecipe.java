package com.sergiocruz.bakingapp.model;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Relation;

import java.util.List;

/**
 * Join the Room DB Query in on nested Object including
 * the List of objects that are not parsed by Room
 */
public class CompleteRecipe {

    @Embedded
    public Recipe recipe;

    @Relation(parentColumn = "columnId", entityColumn = "recipeId")
    public List<Ingredient> ingredientList;

    @Relation(parentColumn = "columnId", entityColumn = "recipeId")
    public List<RecipeStep> recipeStepList;

}