package com.sergiocruz.bakingapp.fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sergiocruz.bakingapp.R;
import com.sergiocruz.bakingapp.activities.MainActivity;
import com.sergiocruz.bakingapp.activities.RecipeDetailActivity;
import com.sergiocruz.bakingapp.model.Ingredient;
import com.sergiocruz.bakingapp.model.MainFragmentViewModel;
import com.sergiocruz.bakingapp.model.Recipe;

import java.util.List;

/**
 * A fragment representing a single recipe detail screen.
 * This fragment is either contained in a {@link MainActivity}
 * in two-pane mode (on tablets) or a {@link RecipeDetailActivity}
 * on handsets.
 */
public class RecipeDetailFragment extends Fragment {
    /**
     * The fragment argument representing the recipe item
     * that this fragment represents.
     */
    public static final String ARG_RECIPE_ITEM = "recipe_item";

    private Recipe recipe;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RecipeDetailFragment() {
    }

    // https://stackoverflow.com/questions/44272914/sharing-data-between-fragments-using-new-architecture-component-viewmodel
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewModelProviders.of(RecipeDetailFragment.this).get(MainFragmentViewModel.class).getAllRecipes().observe(this, new Observer<List<Recipe>>() {
            @Override
            public void onChanged(@Nullable List<Recipe> recipeList) {
                // TODO Do stuff
            }
        });

        if (savedInstanceState != null) {
            recipe = savedInstanceState.getParcelable(ARG_RECIPE_ITEM);
        }

//        if (getArguments().containsKey(ARG_RECIPE_ITEM)) {


        //recipe = getArguments().getParcelable(ARG_RECIPE_ITEM);

        //mItem = DummyContent.ITEM_MAP.get(getArguments().getString(ARG_RECIPE_ITEM));

//            Activity activity = this.getActivity();
//            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar);
//            if (appBarLayout != null) {
//                appBarLayout.setTitle(mItem.content);
//            }
    }
    //}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recipe_detail, container, false);

        TextView ingredientListTextView = rootView.findViewById(R.id.ingredient_list);

        List<Ingredient> ingredients = recipe.getIngredientsList();
        int size = ingredients.size();
        for (int i = 0; i < size; i++) {
            Ingredient ingredient = ingredients.get(i);

            StringBuilder string = new StringBuilder()
                    .append(ingredient.getQuantity()).append(" ")
                    .append(ingredient.getMeasure()).append(" ")
                    .append(ingredient.getIngredient()).append("\n");

            ingredientListTextView.append(string);
        }

//        TextView steps = rootView.findViewById(R.id.steps_list_simple);
//
//        List<Ingredient> ingredients = recipe.getIngredientsList();
//        int size = ingredients.size();
//        for (int i = 0; i < size; i++) {
//            Ingredient ingredient = ingredients.get(i);
//
//            StringBuilder string = new StringBuilder()
//                    .append(ingredient.getQuantity()).append(" ")
//                    .append(ingredient.getMeasure()).append(" ")
//                    .append(ingredient.getIngredient()).append("\n");
//
//            ingredientListTextView.append(string);
//        }


        return rootView;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    @Override
    public void onSaveInstanceState(Bundle currentState) {
        currentState.putParcelable(ARG_RECIPE_ITEM, recipe);
    }


}
