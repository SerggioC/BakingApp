package com.sergiocruz.bakingapp.model;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.os.AsyncTask;

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
    private final RecipeApiController webservice;
    private RecipeDatabase recipeDatabase;

    public RecipesDataRepository(Context context) {
        this.context = context;
        this.webservice = new RecipeApiController();
    }

    public LiveData<List<Recipe>> getData(Boolean getFavorites) {
        final MutableLiveData<List<Recipe>> data = new MutableLiveData<>();
        recipeDatabase = RecipeDatabase.getDatabase(context);

        Boolean hasInternet = NetworkUtils.hasActiveNetworkConnection(context);

        if (hasInternet && !getFavorites) {

            webservice.getApiController().getRecipes("baking.json").enqueue(new Callback<List<Recipe>>() {
                @Override
                public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
                    if (response.isSuccessful()) {
                        List<Recipe> recipesList = response.body();
                        data.setValue(recipesList);

                        addRecipeListToDB(recipesList);


                        Timber.d(recipesList.toString());
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


        } else if (!hasInternet || getFavorites) {
            // Fetch from the database on a background thread

//            new Executor() {
//                @Override
//                public void execute(@NonNull Runnable command) {
//                    command.run();
//                }
//            }.execute((Runnable) () -> {
//                int number = recipeDatabase.recipesDao().getNumberOfRecipes();
//                Timber.i("number of eelements in database = " + number);
//                List<Recipe> recipeList = recipeDatabase.recipesDao().getAllRecipes().getValue();
//                data.setValue(recipeList);
//            });

            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    int number = recipeDatabase.recipesDao().getNumberOfRecipes();
                    Timber.i("number of elements in database = " + number);
                    List<CompleteRecipe> completeRecipeList = recipeDatabase.recipesDao().getAllCompleteRecipes();

                    List<Recipe> recipeList = new ArrayList<>(completeRecipeList.size());
                    for (CompleteRecipe completeRecipe : completeRecipeList) {
                        Recipe newRecipe = new Recipe(
                                completeRecipe.recipe.getRecipeId(),
                                completeRecipe.recipe.getRecipeName(),
                                completeRecipe.recipe.getIngredientsList(),
                                completeRecipe.recipe.getStepsList(),
                                completeRecipe.recipe.getServings(),
                                completeRecipe.recipe.getRecipeImage());
                        recipeList.add(newRecipe);
                    }
                    data.postValue(recipeList);
                    return null;
                }
            }.execute();


        }

        return data;

    }


    public void addNewRecipe(Recipe recipe) {
        List<Recipe> recipeList = new ArrayList<>(1);
        recipeList.add(recipe);
        AsyncTask addNewTask = new addNewRecipeAsyncTask(recipeDatabase, recipeList);
        addNewTask.execute();
    }

    public void addRecipeListToDB(List<Recipe> recipeList) {
        addNewRecipeAsyncTask task = new addNewRecipeAsyncTask(recipeDatabase, recipeList);
        task.execute();
    }

    public void deleteRecipe(Recipe recipe) {
        new deleteAsyncTask(recipeDatabase).execute(recipe);
    }

    private static class addNewRecipeAsyncTask extends AsyncTask<Void, Void, Void> {
        List<Recipe> recipeList;
        private RecipeDatabase db;

        addNewRecipeAsyncTask(RecipeDatabase appDatabase, List<Recipe> recipeList) {
            db = appDatabase;
            this.recipeList = recipeList;
        }

        @Override
        protected Void doInBackground(Void... nothing) {
            RecipesDao recipesDao = db.recipesDao();
            for (int i = 0; i < recipeList.size(); i++) {

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

            //CompleteRecipe completeRecipe = recipesDao.getCompleteRecipe(1);

            return null;
        }
    }

    private static class deleteAsyncTask extends AsyncTask<Recipe, Void, Void> {
        private RecipeDatabase db;

        deleteAsyncTask(RecipeDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final Recipe... params) {
            db.recipesDao().deleteRecipe(params[0]);
            return null;
        }
    }


}
