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
import com.sergiocruz.bakingapp.model.Recipe;
import com.sergiocruz.bakingapp.model.RecipeStep;
import com.sergiocruz.bakingapp.utils.AndroidUtils;

import java.util.List;

import timber.log.Timber;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
import static com.sergiocruz.bakingapp.utils.AndroidUtils.animateItemViewSlideFromBottom;

public class RecipeStepAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_STEP_HEADER = 0;
    private static final int TYPE_STEP = 1;

    private RecipeStepClickListener mRecipeStepClickListener;
    private Context context;
    private List<RecipeStep> recipeStepList;

    public RecipeStepAdapter(Context context, RecipeStepClickListener mRecipeStepClickListener) {
        this.context = context;
        this.mRecipeStepClickListener = mRecipeStepClickListener;
    }

    public void swapRecipeStepData(Recipe recipe) {
        this.recipeStepList = recipe == null ? null : recipe.getStepsList();
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_STEP_HEADER;
        } else {
            return TYPE_STEP;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = null;
        switch (viewType) {
            case TYPE_STEP_HEADER: {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.recipe_steps_headview_item, parent, false);
                holder = new HeaderStepViewHolder(view);
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

        if (itemViewType == TYPE_STEP_HEADER) {
            HeaderStepViewHolder viewHolder = (HeaderStepViewHolder) holder;
            int size = recipeStepList == null ? 0 : recipeStepList.size();
            viewHolder.stepsNum.setText(size + "");

        } else if (itemViewType == TYPE_STEP) {
            RecipeStepViewHolder viewHolder = (RecipeStepViewHolder) holder;

            RecipeStep recipeStep = recipeStepList.get(position - 1);

            String thumbnailUrl = recipeStep.getThumbnailUrl();
            AndroidUtils.MimeType mimeType = AndroidUtils.getMymeTypeFromString(thumbnailUrl);
            Timber.i("type from url= " + mimeType);

            switch (mimeType) {
                case IMAGE:
                    Glide.with(context)
                            .load(thumbnailUrl)
                            .transition(withCrossFade())
                            .apply(new RequestOptions().error(R.drawable.ic_chef_hat))
                            .into(viewHolder.recipeImageIcon);
                default:
                    Glide.with(context)
                            .load(R.drawable.ic_chef_hat)
                            .transition(withCrossFade())
                            .into(viewHolder.recipeImageIcon);
            }

            String shortDesc = recipeStep.getShortDesc();
            String description = TextUtils.isEmpty(shortDesc) ? context.getString(R.string.view_step) + " " + position : shortDesc;
            viewHolder.recipeStepResume.setText(description);
        }

        animateItemViewSlideFromBottom(holder.itemView, 50 * position);

    }

    @Override
    public int getItemCount() {
        return recipeStepList == null ? 0 : recipeStepList.size();
    }

    public interface RecipeStepClickListener {
        void onRecipeStepClicked(RecipeStep recipeStep, int stepClicked);
    }

    class HeaderStepViewHolder extends RecyclerView.ViewHolder {
        final TextView stepsNum;

        HeaderStepViewHolder(View itemView) {
            super(itemView);
            stepsNum = itemView.findViewById(R.id.steps_num);
        }
    }

    public class RecipeStepViewHolder extends RecyclerView.ViewHolder {
        final ImageView recipeImageIcon;
        final TextView recipeStepResume;

        RecipeStepViewHolder(View itemView) {
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