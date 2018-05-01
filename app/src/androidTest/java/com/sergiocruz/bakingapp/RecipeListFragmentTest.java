package com.sergiocruz.bakingapp;

import android.content.Context;
import android.content.res.Resources;
import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.sergiocruz.bakingapp.activities.MainActivity;
import com.sergiocruz.bakingapp.fragments.RecipeListFragment;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class RecipeListFragmentTest {

    @Rule
    public ActivityTestRule<MainActivity> mainActivityTestRule =
            new ActivityTestRule<>(MainActivity.class);

    private IdlingResource mIdlingResource;
    private String ingredientString;
    private String servingsString;
    private String stepsString;

    @Before
    public void getStrings() {
        Context context = getInstrumentation().getTargetContext();
        Resources resources = context.getResources();
        ingredientString = resources.getString(R.string.ingredients);
        servingsString = resources.getString(R.string.serving_size);
        stepsString = resources.getString(R.string.steps);
    }

    @Before
    public void initFragment_RegisterIdlingResource(){
        RecipeListFragment recipeListFragment = new RecipeListFragment();
        mIdlingResource = recipeListFragment.getSimpleIdlingResource();
        IdlingRegistry.getInstance().register(mIdlingResource);
    }

    @Test
    public void onClickRecipe() {

        // Click the first element
        onView(withId(R.id.recipe_list_recyclerview)).
                perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        // Checks if the recipe_name is displayed
        onView(withId(R.id.recipe_name)).check(matches(isDisplayed()));

        // Checks that the Recipe has the correct strings displayed
        onView(withId(R.id.ingredient)).check(matches(withText(ingredientString)));
        onView(withId(R.id.servings)).check(matches(withText(servingsString)));
        onView(withId(R.id.steps)).check(matches(withText(stepsString)));

    }

    // Unregister resources when not needed to avoid malfunction
    @After
    public void unregisterIdlingResource() {
        if (mIdlingResource != null) {
            IdlingRegistry.getInstance().unregister(mIdlingResource);
        }
    }


}
