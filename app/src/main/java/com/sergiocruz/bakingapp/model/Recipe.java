package com.sergiocruz.bakingapp.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Recipe implements Parcelable {

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Recipe> CREATOR = new Parcelable.Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };

    @Expose(serialize = false, deserialize = false)
    @PrimaryKey(autoGenerate = true)
    private Integer columnId;

    @SerializedName("id")
    private Integer recipeId;

    @SerializedName("name")
    private String recipeName;

    @Ignore
    @SerializedName("ingredients")
    private List<Ingredient> ingredientsList;

    @Ignore
    @SerializedName("steps")
    private List<RecipeStep> stepsList;

    @SerializedName("servings")
    private Integer servings;

    @SerializedName("image")
    private String recipeImage;

    @Expose(serialize = false, deserialize = false)
    private Integer isFavorite;

    @Ignore // Room ignore -> GSON serialization
    public Recipe(Integer recipeId, String recipeName, List<Ingredient> ingredientsList, List<RecipeStep> stepsList, Integer servings, String recipeImage) {
        this.recipeId = recipeId;
        this.recipeName = recipeName;
        this.ingredientsList = ingredientsList;
        this.stepsList = stepsList;
        this.servings = servings;
        this.recipeImage = recipeImage;
    }

    public Recipe(Integer columnId, Integer recipeId, String recipeName, Integer servings, String recipeImage, Integer isFavorite) {
        this.columnId = columnId;
        this.recipeId = recipeId;
        this.recipeName = recipeName;
        this.servings = servings;
        this.recipeImage = recipeImage;
        this.isFavorite = isFavorite;
    }

    protected Recipe(Parcel in) {
        columnId = in.readByte() == 0x00 ? null : in.readInt();
        recipeId = in.readByte() == 0x00 ? null : in.readInt();
        recipeName = in.readString();
        if (in.readByte() == 0x01) {
            ingredientsList = new ArrayList<Ingredient>();
            in.readList(ingredientsList, Ingredient.class.getClassLoader());
        } else {
            ingredientsList = null;
        }
        if (in.readByte() == 0x01) {
            stepsList = new ArrayList<RecipeStep>();
            in.readList(stepsList, RecipeStep.class.getClassLoader());
        } else {
            stepsList = null;
        }
        servings = in.readByte() == 0x00 ? null : in.readInt();
        recipeImage = in.readString();
        isFavorite = in.readByte() == 0x00 ? null : in.readInt();
    }

    public Integer getIsFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(Integer isFavorite) {
        this.isFavorite = isFavorite;
    }

    public Integer getColumnId() {
        return columnId;
    }

    public void setColumnId(Integer columnId) {
        this.columnId = columnId;
    }

    public Integer getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(Integer recipeId) {
        this.recipeId = recipeId;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    public List<Ingredient> getIngredientsList() {
        return ingredientsList;
    }

    public void setIngredientsList(List<Ingredient> ingredientsList) {
        this.ingredientsList = ingredientsList;
    }

    public List<RecipeStep> getStepsList() {
        return stepsList;
    }

    public void setStepsList(List<RecipeStep> stepsList) {
        this.stepsList = stepsList;
    }

    public Integer getServings() {
        return servings;
    }

    public void setServings(Integer servings) {
        this.servings = servings;
    }

    public String getRecipeImage() {
        return recipeImage;
    }

    public void setRecipeImage(String recipeImage) {
        this.recipeImage = recipeImage;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (columnId == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(columnId);
        }
        if (recipeId == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(recipeId);
        }
        dest.writeString(recipeName);
        if (ingredientsList == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(ingredientsList);
        }
        if (stepsList == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(stepsList);
        }
        if (servings == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(servings);
        }
        dest.writeString(recipeImage);
        if (isFavorite == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(isFavorite);
        }
    }

}
