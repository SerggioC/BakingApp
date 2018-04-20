package com.sergiocruz.bakingapp.model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import java.util.List;

public class ActivityViewModel extends AndroidViewModel {
    private static ActivityViewModel INSTANCE;
    private RecipesDataRepository dataRepository;
    private LiveData<List<Recipe>> recipesList;

    private static MutableLiveData<Recipe> recipe;
    private static MutableLiveData<RecipeStep> recipeStep;
    private static MutableLiveData<Integer> recipeStepNumber;

    private static Boolean thisGetFavorites;

    public ActivityViewModel(@NonNull Application application) {
        super(application);
        this.dataRepository = new RecipesDataRepository(getApplication().getApplicationContext());
        this.recipesList = dataRepository.getData(thisGetFavorites);
    }

    public static ActivityViewModel getInstance(Fragment fragment, Boolean getFavorites) {
        thisGetFavorites = getFavorites;
        if (INSTANCE == null) {
            INSTANCE = ViewModelProviders.of(fragment).get(ActivityViewModel.class);
            initMutableLiveData();
        }
        return INSTANCE;
    }

    public static ActivityViewModel getInstance(AppCompatActivity activity, Boolean getFavorites) {
        thisGetFavorites = getFavorites;
        if (INSTANCE == null) {
            INSTANCE = ViewModelProviders.of(activity).get(ActivityViewModel.class);
            initMutableLiveData();
        }
        return INSTANCE;
    }

    public static void initMutableLiveData() {
        if (recipe == null) recipe = new MutableLiveData<>();
        if (recipeStep == null) recipeStep = new MutableLiveData<>();
        if (recipeStepNumber == null) recipeStepNumber = new MutableLiveData<>();
        recipeStepNumber.setValue(-1);
    }

    public LiveData<List<Recipe>> getAllRecipes() {
        return recipesList;
    }


    public void setRecipe(Recipe recipe) {
        this.recipe.setValue(recipe);
    }

    // Background Thread
    public void postRecipe(Recipe recipe) {
        this.recipe.postValue(recipe);
    }

    public LiveData<Recipe> getRecipe() {
        return recipe;
    }

    public void setRecipeStep(RecipeStep recipeStep) {
        this.recipeStep.setValue(recipeStep);
    }

    public LiveData<RecipeStep> getRecipeStep() {
        return recipeStep;
    }

    public LiveData<Integer> getRecipeStepNumber() {
        return recipeStepNumber;
    }

    public void setRecipeStepNumber(Integer recipeStepNumber) {
        this.recipeStepNumber.setValue(recipeStepNumber);
    }


}
