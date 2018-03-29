package com.sergiocruz.bakingapp;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Recipe implements Parcelable{
    private int recipeId;
    private String recipeName;
    private List<Ingredients> ingredientsList;
    private List<RecipeSteps> stepsList;
    private Integer servings;
    private String recipeImage;

    public Recipe(int recipeId, String recipeName, List<Ingredients> ingredientsList, List<RecipeSteps> stepsList, Integer servings, String recipeImage) {
        this.recipeId = recipeId;
        this.recipeName = recipeName;
        this.ingredientsList = ingredientsList;
        this.stepsList = stepsList;
        this.servings = servings;
        this.recipeImage = recipeImage;
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

    public List<Ingredients> getIngredientsList() {
        return ingredientsList;
    }

    public void setIngredientsList(List<Ingredients> ingredientsList) {
        this.ingredientsList = ingredientsList;
    }

    public List<RecipeSteps> getStepsList() {
        return stepsList;
    }

    public void setStepsList(List<RecipeSteps> stepsList) {
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

    protected Recipe(Parcel in) {
        recipeId = in.readInt();
        recipeName = in.readString();
        if (in.readByte() == 0x01) {
            ingredientsList = new ArrayList<Ingredients>();
            in.readList(ingredientsList, Ingredients.class.getClassLoader());
        } else {
            ingredientsList = null;
        }
        if (in.readByte() == 0x01) {
            stepsList = new ArrayList<RecipeSteps>();
            in.readList(stepsList, RecipeSteps.class.getClassLoader());
        } else {
            stepsList = null;
        }
        servings = in.readByte() == 0x00 ? null : in.readInt();
        recipeImage = in.readString();
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


    private static class Ingredients implements Parcelable{
        Float quantity;
        String measure;
        String ingredient;

        public Ingredients(Float quantity, String measure, String ingredient) {
            this.quantity = quantity;
            this.measure = measure;
            this.ingredient = ingredient;
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

        protected Ingredients(Parcel in) {
            quantity = in.readByte() == 0x00 ? null : in.readFloat();
            measure = in.readString();
            ingredient = in.readString();
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

        @SuppressWarnings("unused")
        public static final Parcelable.Creator<Ingredients> CREATOR = new Parcelable.Creator<Ingredients>() {
            @Override
            public Ingredients createFromParcel(Parcel in) {
                return new Ingredients(in);
            }

            @Override
            public Ingredients[] newArray(int size) {
                return new Ingredients[size];
            }
        };
    }

    private static class RecipeSteps implements Parcelable{
        Integer recipeStepId;
        String shortDesc;
        String description;
        String videoUrl;
        String thumbnailUrl;

        public RecipeSteps(Integer recipeStepId, String shortDesc, String description, String videoUrl, String thumbnailUrl) {
            this.recipeStepId = recipeStepId;
            this.shortDesc = shortDesc;
            this.description = description;
            this.videoUrl = videoUrl;
            this.thumbnailUrl = thumbnailUrl;
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


        protected RecipeSteps(Parcel in) {
            recipeStepId = in.readByte() == 0x00 ? null : in.readInt();
            shortDesc = in.readString();
            description = in.readString();
            videoUrl = in.readString();
            thumbnailUrl = in.readString();
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

        @SuppressWarnings("unused")
        public static final Parcelable.Creator<RecipeSteps> CREATOR = new Parcelable.Creator<RecipeSteps>() {
            @Override
            public RecipeSteps createFromParcel(Parcel in) {
                return new RecipeSteps(in);
            }

            @Override
            public RecipeSteps[] newArray(int size) {
                return new RecipeSteps[size];
            }
        };

    }
}
