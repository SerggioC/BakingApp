package com.sergiocruz.bakingapp.interfaces;

import com.sergiocruz.bakingapp.model.Recipe;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
* Retrofit API interface
*/
public interface RecipesApi {
    @GET("/topher/2017/May/59121517_baking/{filename}")
    Call<List<Recipe>> getRecipes(@Path("filename") String filename);
}