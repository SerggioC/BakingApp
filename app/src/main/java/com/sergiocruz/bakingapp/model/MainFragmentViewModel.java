package com.sergiocruz.bakingapp.model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

public class MainFragmentViewModel extends AndroidViewModel {

    private LiveData<List<Recipe>> recipesList;

    public MainFragmentViewModel(@NonNull Application application) {
        super(application);
    }

    public void init(RecipesDataRepository dataRepository, Boolean getFavorites) {
        if (this.recipesList != null) {
            return;
        }
        this.recipesList = dataRepository.getData(getFavorites);
    }

    public LiveData<List<Recipe>> getAllRecipes() {
        return this.recipesList;
    }



}
