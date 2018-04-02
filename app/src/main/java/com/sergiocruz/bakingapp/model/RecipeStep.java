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
public class RecipeStep implements Parcelable {
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

    @Expose(serialize = false, deserialize = false)
    @PrimaryKey(autoGenerate = true)
    Integer columnId;

    @SerializedName("id")
    Integer recipeStepId;

    @SerializedName("shortDescription")
    String shortDesc;

    @SerializedName("description")
    String description;

    @SerializedName("videoURL")
    String videoUrl;

    @SerializedName("thumbnailURL")
    String thumbnailUrl;

    @Expose(serialize = false, deserialize = false)
    Integer recipeId;

    @Ignore //ignored from room, used for Retrofit GSON Serialization
    public RecipeStep(Integer recipeStepId, String shortDesc, String description, String videoUrl, String thumbnailUrl) {
        this.recipeStepId = recipeStepId;
        this.shortDesc = shortDesc;
        this.description = description;
        this.videoUrl = videoUrl;
        this.thumbnailUrl = thumbnailUrl;
    }

    public RecipeStep(Integer columnId, Integer recipeStepId, String shortDesc, String description, String videoUrl, String thumbnailUrl, Integer recipeId) {
        this.columnId = columnId;
        this.recipeStepId = recipeStepId;
        this.shortDesc = shortDesc;
        this.description = description;
        this.videoUrl = videoUrl;
        this.thumbnailUrl = thumbnailUrl;
        this.recipeId = recipeId;
    }

    protected RecipeStep(Parcel in) {
        columnId = in.readByte() == 0x00 ? null : in.readInt();
        recipeStepId = in.readByte() == 0x00 ? null : in.readInt();
        shortDesc = in.readString();
        description = in.readString();
        videoUrl = in.readString();
        thumbnailUrl = in.readString();
        recipeId = in.readByte() == 0x00 ? null : in.readInt();
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
        if (columnId == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(recipeStepId);
        }
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
        if (recipeId == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(recipeId);
        }
    }

}