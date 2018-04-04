package com.sergiocruz.bakingapp.model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

public class MainFragmentViewModel extends AndroidViewModel {
    private RecipesDataRepository dataRepository;
    private LiveData<List<Recipe>> recipesList;

    public MainFragmentViewModel(@NonNull Application application) {
        super(application);
        this.dataRepository = new RecipesDataRepository(getApplication().getApplicationContext());
        this.recipesList = dataRepository.getData();
    }

    public LiveData<List<Recipe>> getAllRecipes() {
        return recipesList;
    }


}
