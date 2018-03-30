package com.sergiocruz.bakingapp.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

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

    @PrimaryKey
    @SerializedName("id")
    private int recipeId;
    @SerializedName("name")
    private String recipeName;
    @SerializedName("ingredients")
    private List<Ingredient> ingredientsList;
    @SerializedName("steps")
    private List<RecipeStep> stepsList;
    @SerializedName("servings")
    private Integer servings;
    @SerializedName("image")
    private String recipeImage;

    public Recipe(int recipeId, String recipeName, List<Ingredient> ingredientsList, List<RecipeStep> stepsList, Integer servings, String recipeImage) {
        this.recipeId = recipeId;
        this.recipeName = recipeName;
        this.ingredientsList = ingredientsList;
        this.stepsList = stepsList;
        this.servings = servings;
        this.recipeImage = recipeImage;
    }

    protected Recipe(Parcel in) {
        recipeId = in.readInt();
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
    }

    public int getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(int recipeId) {
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
        dest.writeInt(recipeId);
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
    }

    @Entity
    public static class Ingredient implements Parcelable {
        @SuppressWarnings("unused")
        public static final Parcelable.Creator<Ingredient> CREATOR = new Parcelable.Creator<Ingredient>() {
            @Override
            public Ingredient createFromParcel(Parcel in) {
                return new Ingredient(in);
            }

            @Override
            public Ingredient[] newArray(int size) {
                return new Ingredient[size];
            }
        };
        Float quantity;
        String measure;
        String ingredient;

        public Ingredient(Float quantity, String measure, String ingredient) {
            this.quantity = quantity;
            this.measure = measure;
            this.ingredient = ingredient;
        }

        protected Ingredient(Parcel in) {
            quantity = in.readByte() == 0x00 ? null : in.readFloat();
            measure = in.readString();
            ingredient = in.readString();
        }

        public Float getQuantity() {
            return quantity;
        }

        public void setQuantity(Float quantity) {
            this.quantity = quantity;
        }

        public String getMeasure() {
            return measure;
        }

        public void setMeasure(String measure) {
            this.measure = measure;
        }

        public String getIngredient() {
            return ingredient;
        }

        public void setIngredient(String ingredient) {
            this.ingredient = ingredient;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            if (quantity == null) {
                dest.writeByte((byte) (0x00));
            } else {
                dest.writeByte((byte) (0x01));
                dest.writeFloat(quantity);
            }
            dest.writeString(measure);
            dest.writeString(ingredient);
        }
    }

    @Entity
    public static class RecipeStep implements Parcelable {
        @SuppressWarnings("unused")
        public static final Parcelable.Creator<RecipeStep> CREATOR = new Parcelable.Creator<RecipeStep>() {
            @Override
            public RecipeStep createFromParcel(Parcel in) {
                return new RecipeStep(in);
            }

            @Override
            public RecipeStep[] newArray(int size) {
                return new RecipeStep[size];
            }
        };
        @PrimaryKey
        Integer recipeStepId;
        String shortDesc;
        String description;
        String videoUrl;
        String thumbnailUrl;

        public RecipeStep(Integer recipeStepId, String shortDesc, String description, String videoUrl, String thumbnailUrl) {
            this.recipeStepId = recipeStepId;
            this.shortDesc = shortDesc;
            this.description = description;
            this.videoUrl = videoUrl;
            this.thumbnailUrl = thumbnailUrl;
        }

        protected RecipeStep(Parcel in) {
            recipeStepId = in.readByte() == 0x00 ? null : in.readInt();
            shortDesc = in.readString();
            description = in.readString();
            videoUrl = in.readString();
            thumbnailUrl = in.readString();
        }

        public Integer getRecipeStepId() {
            return recipeStepId;
        }

        public void setRecipeStepId(Integer recipeStepId) {
            this.recipeStepId = recipeStepId;
        }

        public String getShortDesc() {
            return shortDesc;
        }

        public void setShortDesc(String shortDesc) {
            this.shortDesc = shortDesc;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getVideoUrl() {
            return videoUrl;
        }

        public void setVideoUrl(String videoUrl) {
            this.videoUrl = videoUrl;
        }

        public String getThumbnailUrl() {
            return thumbnailUrl;
        }

        public void setThumbnailUrl(String thumbnailUrl) {
            this.thumbnailUrl = thumbnailUrl;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            if (recipeStepId == null) {
                dest.writeByte((byte) (0x00));
            } else {
                dest.writeByte((byte) (0x01));
                dest.writeInt(recipeStepId);
            }
            dest.writeString(shortDesc);
            dest.writeString(description);
            dest.writeString(videoUrl);
            dest.writeString(thumbnailUrl);
        }

    }
}
