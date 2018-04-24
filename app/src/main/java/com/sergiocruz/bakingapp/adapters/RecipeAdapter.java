package com.sergiocruz.bakingapp.adapters;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sackcentury.shinebuttonlib.ShineButton;
import com.sergiocruz.bakingapp.R;
import com.sergiocruz.bakingapp.model.Ingredient;
import com.sergiocruz.bakingapp.model.Recipe;
import com.sergiocruz.bakingapp.model.RecipeStep;

import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {
    private RecipeClickListener mRecipeClickListener;
    private FavoriteClickListener mFavoriteClickListener;
    private FavoriteLongClickListener mFavoriteLongClickListener;
    private List<Recipe> recipesList;

    public RecipeAdapter(RecipeClickListener mRecipeClickListener,
                         FavoriteClickListener mFavoriteClickListener,
                         FavoriteLongClickListener mFavoriteLongClickListener) {

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
    public RecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recipe_list_item, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecipeViewHolder holder, int position) {
        if (position < 0) return;
        Recipe recipe = recipesList.get(position);

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
    }


    @Override
    public int getItemCount() {
        return recipesList == null ? 0 : recipesList.size();
    }

    public interface RecipeClickListener {
        void onRecipeClicked(Recipe recipe);
    }

    public interface FavoriteClickListener {
        void onFavoriteClicked(Recipe recipe, int position1);
    }

    public interface FavoriteLongClickListener {
        void onFavoriteLongClicked(Recipe recipe, int position);
    }

    class RecipeViewHolder extends RecyclerView.ViewHolder {
        final TextView recipeName;
        final TextView numIngredients;
        final TextView numSteps;
        final TextView numServings;
        final ShineButton shineButton;

        public RecipeViewHolder(View itemView) {
            super(itemView);
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