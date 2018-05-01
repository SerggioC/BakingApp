package com.sergiocruz.bakingapp;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.sergiocruz.bakingapp.activities.RecipeDetailActivity;

import org.hamcrest.CoreMatchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class RecipeListFragmentTest {

    @Rule
    public ActivityTestRule<RecipeDetailActivity> recipeDetailActivityTestRule =
            new ActivityTestRule<>(RecipeDetailActivity.class);

    @Test
    public void onClickRecipeStep() {
        Espresso.onData(CoreMatchers.is(CoreMatchers.containsString("Serving Size")));

    }



    @Test
    public void scrollToItemBelowFold_checkItsText() {
        // First scroll to the position that needs to be matched and click on it.
        onView(ViewMatchers.withId(R.id.recipe_ingredients_recyclerview))
                .perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));

        // Match the text in an item below the fold and check that it's displayed.
        String itemElementText = recipeDetailActivityTestRule.getActivity().getResources().getString(
                R.string.serving_size) + String.valueOf(1);
        onView(withText(itemElementText)).check(matches(isDisplayed()));
    }

}
