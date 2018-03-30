package com.sergiocruz.bakingapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sergiocruz.bakingapp.R;
import com.sergiocruz.bakingapp.activities.MainActivity;
import com.sergiocruz.bakingapp.activities.RecipeDetailActivity;
import com.sergiocruz.bakingapp.fragments.RecipeDetailFragment;
import com.sergiocruz.bakingapp.model.Recipe;

import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {
    private RecipeClickListener mRecipeClickListener;
    private MainActivity mParentActivity;
    private List<Recipe> recipesList;
    private Boolean isTwoPane;
    private Boolean isFavorite;
    private Context mContext;

    public RecipeAdapter(Context context, RecipeClickListener mRecipeClickListener) {
        this.mRecipeClickListener = mRecipeClickListener;
        this.mContext = context;
    }

    public void swapRecipesData(List<Recipe> recipesList, Boolean isTwoPane, Boolean isFavorite) {
        this.recipesList = recipesList;
        this.isTwoPane = isTwoPane;
        this.isFavorite = isFavorite;
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

            itemView.setOnClickListener(recipeClickListener);
        }

        View.OnClickListener recipeClickListener = view -> {

            mRecipeClickListener.onRecipeClicked(recipesList.get(getAdapterPosition()), itemView);

            int recipeId = recipesList.get(getAdapterPosition()).getRecipeId();
            if (isTwoPane) {
                Bundle arguments = new Bundle();
                arguments.putInt(RecipeDetailFragment.ARG_RECIPE_ITEM_ID, recipeId);

                RecipeDetailFragment fragment = new RecipeDetailFragment();
                fragment.setArguments(arguments);

                mParentActivity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.recipe_step_fragment, fragment)
                        .commit();

            } else {
                Context context = view.getContext();
                Intent intent = new Intent(context, RecipeDetailActivity.class);
                intent.putExtra(RecipeDetailFragment.ARG_RECIPE_ITEM_ID, recipeId);

                context.startActivity(intent);
            }
        };
    }

    public interface RecipeClickListener {
        void onRecipeClicked(Recipe recipe, View itemView);
    }

}