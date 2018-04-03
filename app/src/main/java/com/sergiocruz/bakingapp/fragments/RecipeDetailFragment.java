package com.sergiocruz.bakingapp.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sergiocruz.bakingapp.R;
import com.sergiocruz.bakingapp.activities.MainActivity;
import com.sergiocruz.bakingapp.activities.RecipeDetailActivity;
import com.sergiocruz.bakingapp.model.Recipe;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        // Show the dummy content as text in a TextView.
        if (recipe != null) {
            ((TextView) rootView.findViewById(R.id.recipe_detail_TextView)).setText(recipe.getRecipeName() + " \n \n ya yaayyayaay");
        }

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
