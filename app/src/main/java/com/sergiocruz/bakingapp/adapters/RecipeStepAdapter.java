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
import com.sergiocruz.bakingapp.R;
import com.sergiocruz.bakingapp.model.Ingredient;
import com.sergiocruz.bakingapp.model.RecipeStep;

import java.util.List;

import timber.log.Timber;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class RecipeStepAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_STEP = 1;

    private RecipeStepClickListener mRecipeStepClickListener;
    private List<RecipeStep> recipeStepList;
    private List<Ingredient> ingredientList;
    private Context context;

    public RecipeStepAdapter(Context context, RecipeStepClickListener mRecipeStepClickListener) {
        this.context = context;
        this.mRecipeStepClickListener = mRecipeStepClickListener;
    }

    public void swapRecipeStepData(List<RecipeStep> recipeStepList, List<Ingredient> ingredientList) {
        this.recipeStepList = recipeStepList;
        this.ingredientList = ingredientList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        } else {
            return TYPE_STEP;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = null;
        switch (viewType) {
            case TYPE_HEADER: {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.recipe_steps_headview_item, parent, false);
                holder = new RecipeStepAdapter.HeaderStepViewHolder(view);
                break;
            }
            case TYPE_STEP: {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.recipe_step_list_item, parent, false);
                holder = new RecipeStepViewHolder(view);
                break;
            }
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        int itemViewType = getItemViewType(position);
        if (itemViewType == TYPE_HEADER) {
            HeaderStepViewHolder viewHolder = (HeaderStepViewHolder) holder;

            int size = ingredientList.size();
            viewHolder.ingredientsNum.setText(size + "");

            viewHolder.stepsNum.setText(recipeStepList.size() + "");

            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < size; i++) {
                Ingredient ingredient = ingredientList.get(i);
                stringBuilder.append(ingredient.getQuantity()).append(" ")
                        .append(ingredient.getMeasure()).append(" ")
                        .append(ingredient.getIngredient()).append("\n");
            }
            viewHolder.ingredientListTextView.append(stringBuilder);

        } else if (itemViewType == TYPE_STEP) {
            RecipeStepViewHolder viewHolder = (RecipeStepViewHolder) holder;

            Timber.i("position = " + (position - 1));

            RecipeStep recipeStep = recipeStepList.get(position - 1);

            Glide.with(context)
                    .load(recipeStep.getThumbnailUrl())
                    .transition(withCrossFade())
                    .apply(new RequestOptions().error(R.drawable.ic_chef))
                    .into(viewHolder.recipeImageIcon);

            String shortDesc = recipeStep.getShortDesc();
            String description = TextUtils.isEmpty(shortDesc) ? "View Recipe Step " + position : shortDesc;
            viewHolder.recipeStepResume.setText(description);
        }

    }

    @Override
    public int getItemCount() {
        return recipeStepList == null ? 0 : recipeStepList.size() + 1; // + 1 from header
    }

    public interface RecipeStepClickListener {
        void onRecipeStepClicked(RecipeStep recipeStep, int stepClicked);
    }

    class HeaderStepViewHolder extends RecyclerView.ViewHolder {
        final TextView ingredientListTextView;
        final TextView ingredientsNum;
        final TextView stepsNum;

        public HeaderStepViewHolder(View itemView) {
            super(itemView);
            ingredientsNum = itemView.findViewById(R.id.ingredients_num);
            stepsNum = itemView.findViewById(R.id.steps_num);
            ingredientListTextView = itemView.findViewById(R.id.ingredient_list);
        }
    }

    class RecipeStepViewHolder extends RecyclerView.ViewHolder {
        final ImageView recipeImageIcon;
        final TextView recipeStepResume;

        public RecipeStepViewHolder(View itemView) {
            super(itemView);
            recipeImageIcon = itemView.findViewById(R.id.step_image);
            recipeStepResume = itemView.findViewById(R.id.step_resume);

            itemView.setOnClickListener(view -> {
                int stepClicked = getAdapterPosition() - 1;
                mRecipeStepClickListener.onRecipeStepClicked(recipeStepList.get(stepClicked), stepClicked); // correct array index
            });
        }
    }

}