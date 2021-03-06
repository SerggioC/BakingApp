package com.sergiocruz.bakingapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.sackcentury.shinebuttonlib.ShineButton;
import com.sergiocruz.bakingapp.R;
import com.sergiocruz.bakingapp.model.Ingredient;
import com.sergiocruz.bakingapp.model.Recipe;
import com.sergiocruz.bakingapp.model.RecipeStep;
import com.sergiocruz.bakingapp.utils.AndroidUtils;

import java.util.List;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
import static com.sergiocruz.bakingapp.utils.AndroidUtils.animateItemViewSlideFromBottom;

public class RecipeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_EMPTY = -1;
    private static final int TYPE_RECIPE = 0;
    private RecipeClickListener mRecipeClickListener;
    private FavoriteClickListener mFavoriteClickListener;
    private FavoriteLongClickListener mFavoriteLongClickListener;
    private List<Recipe> recipesList;
    private Context mContext;

    public RecipeAdapter(Context context,
                         RecipeClickListener mRecipeClickListener,
                         FavoriteClickListener mFavoriteClickListener,
                         FavoriteLongClickListener mFavoriteLongClickListener) {

        this.mContext = context;
        this.mRecipeClickListener = mRecipeClickListener;
        this.mFavoriteClickListener = mFavoriteClickListener;
        this.mFavoriteLongClickListener = mFavoriteLongClickListener;
    }

    public void swapRecipesData(List<Recipe> recipesList) {
        if (this.recipesList == recipesList) return;
        this.recipesList = recipesList;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_EMPTY) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.empty_item, parent, false);
            return new EmptyViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recipe_list_item, parent, false);
            return new RecipeViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int position) {
        if (position < 0 || getItemViewType(position) == TYPE_EMPTY) return;

        Recipe recipe = recipesList.get(position);

        RecipeViewHolder holder = (RecipeViewHolder) viewHolder;

                String recipeImageUrl = recipe.getRecipeImage();
        AndroidUtils.MimeType mimeType = AndroidUtils.getMymeTypeFromString(recipeImageUrl);
        switch (mimeType) {
            case IMAGE:
                Glide.with(mContext)
                        .load(recipeImageUrl)
                        .transition(withCrossFade())
                        .apply(new RequestOptions().error(R.mipmap.ic_launcher))
                        .into(holder.recipeImage);
            default:
                Glide.with(mContext)
                        .load(R.mipmap.ic_launcher)
                        .transition(withCrossFade())
                        .into(holder.recipeImage);
        }

        String recipeName = recipe.getRecipeName();
        holder.recipeName.setText(TextUtils.isEmpty(recipeName) ? holder.itemView.getContext().getString(R.string.no_name_recipe) : recipeName);

        List<Ingredient> ingredientsList = recipe.getIngredientsList();
        holder.numIngredients.setText(String.valueOf(ingredientsList == null ? 0 : ingredientsList.size()));

        List<RecipeStep> stepsList = recipe.getStepsList();
        holder.numSteps.setText(String.valueOf(stepsList == null ? 0 : stepsList.size()));

        Integer servings = recipe.getServings();
        holder.numServings.setText(String.valueOf(servings == null ? 0 : servings));

        Integer isFavorite = recipe.getIsFavorite();
        holder.shineButton.setChecked(isFavorite != null && isFavorite == 1, true);

        animateItemViewSlideFromBottom(holder.itemView, 50 * position);
    }

    @Override
    public int getItemCount() {
        return recipesList == null ? 0 : recipesList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return getItemCount() == 0 ? TYPE_EMPTY : TYPE_RECIPE;
    }

    public interface RecipeClickListener {
        void onRecipeClicked(Recipe recipe);
    }

    public interface FavoriteClickListener {
        void onFavoriteClicked(Recipe recipe, int position);
    }

    public interface FavoriteLongClickListener {
        void onFavoriteLongClicked(Recipe recipe, int position);
    }

    class EmptyViewHolder extends RecyclerView.ViewHolder {
        final TextView infoTextView;

        EmptyViewHolder(View itemView) {
            super(itemView);
            infoTextView = itemView.findViewById(R.id.info_textView);
        }
    }

    class RecipeViewHolder extends RecyclerView.ViewHolder {
        final ImageView recipeImage;
        final TextView recipeName;
        final TextView numIngredients;
        final TextView numSteps;
        final TextView numServings;
        final ShineButton shineButton;

        RecipeViewHolder(View itemView) {
            super(itemView);
            recipeImage = itemView.findViewById(R.id.recipe_image);
            recipeName = itemView.findViewById(R.id.recipe_name);
            numIngredients = itemView.findViewById(R.id.ingredients_num);
            numSteps = itemView.findViewById(R.id.steps_num);
            numServings = itemView.findViewById(R.id.servings_num);
            shineButton = itemView.findViewById(R.id.favorite_button);

            shineButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                Recipe recipe = recipesList.get(position);
                mFavoriteClickListener.onFavoriteClicked(recipe, position);

                Integer isFavorite = recipe.getIsFavorite();
                if (isFavorite == null) {
                    recipe.setIsFavorite(1);
                    recipesList.set(position, recipe);
                } else {
                    if (isFavorite == 1) { // keep active
                        shineButton.setChecked(true, true);
                    } else if (isFavorite == 0) {
                        recipe.setIsFavorite(1);
                        recipesList.set(position, recipe);
                    }
                }
            });

            shineButton.setOnLongClickListener(v -> {
                int position = getAdapterPosition();
                Recipe recipe = recipesList.get(position);
                mFavoriteLongClickListener.onFavoriteLongClicked(recipe, position);

                Integer isFavorite = recipe.getIsFavorite();
                if (isFavorite != null) {
                    if (isFavorite == 1) {
                        recipe.setIsFavorite(0);
                        recipesList.set(position, recipe);
                    }
                }
                shineButton.setChecked(false, true);
                return true;
            });

            itemView.setOnClickListener(view ->
                    mRecipeClickListener.onRecipeClicked(recipesList.get(getAdapterPosition())));
        }
    }

}