package com.sergiocruz.bakingapp.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sergiocruz.bakingapp.interfaces.RecipesApi;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 *  Get data using retrofit and serialize automatically with GSON
 * */
public class RecipeApiController {
    private final String RECIPES_BASE_URL = "https://d17h27t6h515a5.cloudfront.net/";
    private RecipesApi recipesApi;

    public RecipesApi getApiController() {
        if (recipesApi != null) return recipesApi;

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RECIPES_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        recipesApi = retrofit.create(RecipesApi.class);

        return recipesApi;
    }
}