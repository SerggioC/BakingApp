package com.sergiocruz.bakingapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sergiocruz.bakingapp.activities.MainActivity;
import com.sergiocruz.bakingapp.activities.RecipeDetailActivity;
import com.sergiocruz.bakingapp.dummy.DummyContent;
import com.sergiocruz.bakingapp.fragments.RecipeDetailFragment;
import com.sergiocruz.bakingapp.model.Recipe;

import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    private final MainActivity mParentActivity;
    private final List<Recipe> recipesList;
    private final boolean isTwoPane;

    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            DummyContent.DummyItem item = (DummyContent.DummyItem) view.getTag();
            if (isTwoPane) {
                Bundle arguments = new Bundle();
                arguments.putString(RecipeDetailFragment.ARG_ITEM_ID, item.id);
                RecipeDetailFragment fragment = new RecipeDetailFragment();
                fragment.setArguments(arguments);
                mParentActivity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.recipe_step_fragment, fragment)
                        .commit();
            } else {
                Context context = view.getContext();
                Intent intent = new Intent(context, RecipeDetailActivity.class);
                intent.putExtra(RecipeDetailFragment.ARG_ITEM_ID, item.id);

                context.startActivity(intent);
            }
        }
    };

    RecipeAdapter(MainActivity parent, List<Recipe> recipesList, boolean isTwoPane) {
        this.recipesList = recipesList;
        mParentActivity = parent;
        this.isTwoPane = isTwoPane;
    }

    @Override
    public RecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recipe_list_item, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecipeViewHolder holder, int position) {
        holder.recipeName.setText(recipesList.get(position).getRecipeName());
        holder.numIngredients.setText(recipesList.get(position).getIngredientsList().size());
        holder.numSteps.setText(recipesList.get(position).getStepsList().size());
        holder.numServings.setText(recipesList.get(position).getServings());

        holder.itemView.setTag(recipesList.get(position));
        holder.itemView.setOnClickListener(mOnClickListener);
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
        }
    }
}