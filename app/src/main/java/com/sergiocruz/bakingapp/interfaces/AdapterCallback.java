package com.sergiocruz.bakingapp.interfaces;

import com.sergiocruz.bakingapp.model.Recipe;

import java.util.List;

public interface AdapterCallback {
    void onResponse(List<Recipe> recipesList);
}
