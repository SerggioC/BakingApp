package com.sergiocruz.bakingapp.model;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;

import com.sergiocruz.bakingapp.ThreadExecutor;
import com.sergiocruz.bakingapp.database.RecipeDatabase;
import com.sergiocruz.bakingapp.database.RecipesDao;
import com.sergiocruz.bakingapp.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class RecipesDataRepository {
    private final Context context;

    // Repository controlling Online/Local Database data switching
    RecipeApiController webservice;
    RecipeDatabase recipeDatabase;

    public RecipesDataRepository(Context context) {
        this.context = context;
        this.webservice = new RecipeApiController();
    }


    public LiveData<List<Recipe>> getData() {

        // TODO LATER
        Boolean getFavorites = false;


        final MutableLiveData<List<Recipe>> data = new MutableLiveData<>();

        recipeDatabase = RecipeDatabase.getDatabase(context);

        Boolean hasInternet = NetworkUtils.hasActiveNetworkConnection(context);

        ThreadExecutor threadExecutor = new ThreadExecutor();

        if (hasInternet && !getFavorites) {
            threadExecutor.networkIO().execute(() -> getDataFromNetwork(data, threadExecutor));
        } else if (!hasInternet || getFavorites) {
            threadExecutor.diskIO().execute(() -> getDataFromDB(data));
        }

        return data;

    }

    private void getDataFromDB(MutableLiveData<List<Recipe>> data) {
        int number = recipeDatabase.recipesDao().getNumberOfRecipes();
        Timber.i("number of elements in database = " + number);
        List<CompleteRecipe> completeRecipeList = recipeDatabase.recipesDao().getAllCompleteRecipes();

        List<Recipe> recipeList = new ArrayList<>(completeRecipeList.size());
        for (CompleteRecipe completeRecipe : completeRecipeList) {
            Recipe newRecipe = new Recipe(
                    completeRecipe.getRecipe().getRecipeId(),
                    completeRecipe.getRecipe().getRecipeName(),
                    completeRecipe.getIngredientList(),
                    completeRecipe.getRecipeStepList(),
                    completeRecipe.getRecipe().getServings(),
                    completeRecipe.getRecipe().getRecipeImage());
            recipeList.add(newRecipe);
        }
        data.postValue(recipeList);
    }

    private void getDataFromNetwork(MutableLiveData<List<Recipe>> data, ThreadExecutor threadExecutor) {
        webservice.getApiController().getRecipes("baking.json").enqueue(new Callback<List<Recipe>>() {

            @Override
            public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
                if (response.isSuccessful()) {
                    List<Recipe> recipesList = response.body();
                    data.setValue(recipesList);

                    threadExecutor.diskIO().execute(() -> addRecipeListToDB(recipesList));

                } else {
                    data.setValue(null);
                    Timber.w("Wrong response on Api Call= " + call.toString() + " " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<List<Recipe>> call, Throwable t) {
                data.setValue(null);
                Timber.w(t, "Fail on Api Call= " + call.toString());
            }
        });
    }

    private void addRecipeListToDB(List<Recipe> recipeList) {

        RecipesDao recipesDao = recipeDatabase.recipesDao();

        int size = recipeList.size();
        for (int i = 0; i < size; i++) {

            Recipe recipe = recipeList.get(i);
            recipesDao.addRecipe(recipe);

            List<Ingredient> ingredientList = recipe.getIngredientsList();

            Integer columnId = recipesDao.getColumnIdFromRecipeId(recipe.getRecipeId());
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

        }

    }

}
