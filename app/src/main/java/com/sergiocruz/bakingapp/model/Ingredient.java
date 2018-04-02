package com.sergiocruz.bakingapp.model;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(foreignKeys = @ForeignKey(
        entity = Recipe.class,
        parentColumns = "columnId",
        childColumns = "recipeId",
        onDelete = CASCADE,
        onUpdate = CASCADE),
        indices = @Index(value = "recipeId"))
public class Ingredient implements Parcelable {
    @Ignore
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

    @Expose(serialize = false, deserialize = false)
    @PrimaryKey(autoGenerate = true)
    Integer ingredientId;

    @SerializedName("quantity")
    Float quantity;

    @SerializedName("measure")
    String measure;

    @SerializedName("ingredient")
    String ingredient;

    @Expose(serialize = false, deserialize = false)
    Integer recipeId;

    @Ignore //ignored from room, used for Retrofit GSON Serialization
    public Ingredient(Float quantity, String measure, String ingredient) {
        this.quantity = quantity;
        this.measure = measure;
        this.ingredient = ingredient;
    }

    public Ingredient(Integer ingredientId, Float quantity, String measure, String ingredient, Integer recipeId) {
        this.ingredientId = ingredientId;
        this.quantity = quantity;
        this.measure = measure;
        this.ingredient = ingredient;
        this.recipeId = recipeId;
    }

    protected Ingredient(Parcel in) {
        ingredientId = in.readByte() == 0x00 ? null : in.readInt();
        quantity = in.readByte() == 0x00 ? null : in.readFloat();
        measure = in.readString();
        ingredient = in.readString();
        recipeId = in.readByte() == 0x00 ? null : in.readInt();
    }

    public Integer getIngredientId() {
        return ingredientId;
    }

    public void setIngredientId(Integer ingredientId) {
        this.ingredientId = ingredientId;
    }

    public Integer getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(Integer recipeId) {
        this.recipeId = recipeId;
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
        if (ingredientId == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(ingredientId);
        }
        if (quantity == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeFloat(quantity);
        }
        dest.writeString(measure);
        dest.writeString(ingredient);
        if (recipeId == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(recipeId);
        }
    }
}
