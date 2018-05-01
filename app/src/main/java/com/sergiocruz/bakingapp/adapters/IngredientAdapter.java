package com.sergiocruz.bakingapp.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sergiocruz.bakingapp.R;
import com.sergiocruz.bakingapp.model.Ingredient;
import com.sergiocruz.bakingapp.model.Recipe;

import java.util.List;

import static com.sergiocruz.bakingapp.utils.AndroidUtils.animateItemViewSlideFromBottom;
import static com.sergiocruz.bakingapp.utils.AndroidUtils.capitalize;

public class IngredientAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_INGREDIENT = 1;

    private IngredientClickListener mIngredientClickListener;
    private List<Ingredient> ingredientList;
    private String mRecipeName;
    private Integer mServingsNum;

    public IngredientAdapter(IngredientClickListener ingredientClickListener) {
        this.mIngredientClickListener = ingredientClickListener;
    }

    public void swapIngredientsData(Recipe recipe) {
        this.mRecipeName = recipe == null ? null : recipe.getRecipeName();
        this.mServingsNum = recipe == null ? null : recipe.getServings();
        this.ingredientList = recipe == null ? null : recipe.getIngredientsList();
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        } else {
            return TYPE_INGREDIENT;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = null;
        switch (viewType) {
            case TYPE_HEADER: {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.ingredient_header_view_layout, parent, false);
                holder = new IngredientAdapter.HeaderIngredientViewHolder(view);
                break;
            }
            case TYPE_INGREDIENT: {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.ingredient_item_row_layout, parent, false);
                holder = new IngredientAdapter.IngredientRowViewHolder(view);
                break;
            }
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        int itemViewType = getItemViewType(position);

        if (itemViewType == TYPE_HEADER) {
            HeaderIngredientViewHolder viewHolder = (HeaderIngredientViewHolder) holder;
            viewHolder.recipeNameTV.setText(mRecipeName);
            viewHolder.servingSizeTV.setText(mServingsNum + "");
            viewHolder.ingredientsNum.setText(ingredientList.size() + "");

        } else if (itemViewType == TYPE_INGREDIENT) {
            IngredientRowViewHolder viewHolder = (IngredientRowViewHolder) holder;
            Ingredient ingredient = ingredientList.get(position - 1); // deduce header index

            boolean checked = ingredient.getChecked() != null && ingredient.getChecked() == 1;
            viewHolder.checkBox.setChecked(checked);
            viewHolder.ingredientName.setText(capitalize(ingredient.getIngredient()));
            viewHolder.quantity.setText(ingredient.getQuantity() + " " + ingredient.getMeasure());
        }

        animateItemViewSlideFromBottom(holder.itemView, 50 * position);

    }



    @Override
    public int getItemCount() {
        return (ingredientList == null ? 0 : ingredientList.size() + 1);
    }

    public interface IngredientClickListener {
        void onIngredientClicked(Ingredient ingredient, boolean isChecked);
    }

    class HeaderIngredientViewHolder extends RecyclerView.ViewHolder {
        final TextView recipeNameTV;
        final TextView servingSizeTV;
        final TextView ingredientsNum;

        public HeaderIngredientViewHolder(View itemView) {
            super(itemView);
            recipeNameTV = itemView.findViewById(R.id.recipe_name);
            servingSizeTV = itemView.findViewById(R.id.servings_num);
            ingredientsNum = itemView.findViewById(R.id.ingredients_num);
        }
    }

    public class IngredientRowViewHolder extends RecyclerView.ViewHolder {
        final TextView ingredientName;
        final TextView quantity;
        final android.support.v7.widget.AppCompatCheckBox checkBox;

        public IngredientRowViewHolder(View itemView) {
            super(itemView);
            ingredientName = itemView.findViewById(R.id.ingredient_row);
            quantity = itemView.findViewById(R.id.quantity_row);
            checkBox = itemView.findViewById(R.id.ingredient_checkBox);

            checkBox.setOnClickListener(view -> {
                mIngredientClickListener.onIngredientClicked(
                        ingredientList.get(getAdapterPosition() - 1), // correct array index
                        checkBox.isChecked());
                //update the data inside the adapter
                ingredientList.get(getAdapterPosition() - 1 ).setChecked(checkBox.isChecked() ? 1 : 0);
            });

        }
    }
}