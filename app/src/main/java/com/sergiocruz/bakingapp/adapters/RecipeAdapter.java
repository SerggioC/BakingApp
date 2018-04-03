package com.sergiocruz.bakingapp.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sergiocruz.bakingapp.R;
import com.sergiocruz.bakingapp.model.Recipe;

import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {
    private RecipeClickListener mRecipeClickListener;
    private List<Recipe> recipesList;

    public RecipeAdapter(RecipeClickListener mRecipeClickListener) {
        this.mRecipeClickListener = mRecipeClickListener;
    }

    public void swapRecipesData(List<Recipe> recipesList) {
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

        holder.recipeName.setText(recipe.getRecipeName());
        holder.numIngredients.setText(String.valueOf(recipe.getIngredientsList().size()));
        holder.numSteps.setText(String.valueOf(recipe.getStepsList().size()));
        holder.numServings.setText(String.valueOf(recipe.getServings()));
    }


    @Override
    public int getItemCount() {
        return recipesList == null ? 0 : recipesList.size();
    }

    public interface RecipeClickListener {
        void onRecipeClicked(Recipe recipe);
    }

    class RecipeViewHolder extends RecyclerView.ViewHolder {
        final TextView recipeName;
        final TextView numIngredients;
        final TextView numSteps;
        final TextView numServings;

        public RecipeViewHolder(View itemView) {
            super(itemView);
            recipeName = itemView.findViewById(R.id.recipe_name);
            numIngredients = itemView.findViewById(R.id.ingredients_num);
            numSteps = itemView.findViewById(R.id.steps_num);
            numServings = itemView.findViewById(R.id.servings_num);

            itemView.setOnClickListener(view ->
                    mRecipeClickListener.onRecipeClicked(recipesList.get(getAdapterPosition())));
        }
    }

}