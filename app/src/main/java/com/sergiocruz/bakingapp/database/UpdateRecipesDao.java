package com.sergiocruz.bakingapp.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

@Dao
public interface UpdateRecipesDao {

    @Query("UPDATE Recipe SET servings = :isFavorite WHERE columnId = :columnId")
    void updateRecipe(Integer isFavorite, Integer columnId);

    @Query("UPDATE Recipe SET recipeName = :newname WHERE columnId = :coluna")
    void updateRecipeName(String newname, int coluna);

}
