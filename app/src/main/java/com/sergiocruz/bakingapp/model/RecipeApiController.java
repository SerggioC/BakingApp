package com.sergiocruz.bakingapp.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sergiocruz.bakingapp.interfaces.RecipesApi;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

public class RecipeApiController implements Callback<List<Recipe>> {
    static final String RECIPES_BASE_URL = "https://d17h27t6h515a5.cloudfront.net/";
    AdapterCallback adapterCallback;

    public void init(AdapterCallback adapterCallback) {
        this.adapterCallback = adapterCallback;

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RECIPES_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        RecipesApi recipesApi = retrofit.create(RecipesApi.class);

        Call<List<Recipe>> call = recipesApi.getRecipes("baking.json");
        call.enqueue(this); // <- takes the callback for the response __asynchronously__
    }

    @Override
    public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
        if(response.isSuccessful()) {

            List<Recipe> recipesList = response.body();
            adapterCallback.onRetrofitResponse(recipesList);

            Timber.d(recipesList.toString());
        } else {
            adapterCallback.onRetrofitResponse(null);
            System.out.println(response.errorBody());
        }
    }

    @Override
    public void onFailure(Call<List<Recipe>> call, Throwable t) {
        adapterCallback.onRetrofitResponse(null);
        t.printStackTrace();
    }

    public interface AdapterCallback {
        void onRetrofitResponse(List<Recipe> recipesList);
    }

}