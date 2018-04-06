package com.sergiocruz.bakingapp.model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import java.util.List;

public class MainFragmentViewModel extends AndroidViewModel {
    private RecipesDataRepository dataRepository;
    private LiveData<List<Recipe>> recipesList;
    private MutableLiveData<RecipeStep> recipeStep;
    private MutableLiveData<Integer> recipeStepNumber;

    public MainFragmentViewModel(@NonNull Application application) {
        super(application);
        this.dataRepository = new RecipesDataRepository(getApplication().getApplicationContext());
        this.recipesList = dataRepository.getData();
    }

    public LiveData<List<Recipe>> getAllRecipes() {
        return recipesList;
    }

    public LiveData<RecipeStep> getRecipeStep() {
        return recipeStep;
    }

    public void setRecipeStep(RecipeStep recipeStep) {
        this.recipeStep.setValue(recipeStep);
    }

    public LiveData<Integer> getRecipeStepNumber() {
        return recipeStepNumber;
    }

    public void setRecipeStepNumber(Integer recipeStepNumber) {
        this.recipeStepNumber.setValue(recipeStepNumber);
    }
}
