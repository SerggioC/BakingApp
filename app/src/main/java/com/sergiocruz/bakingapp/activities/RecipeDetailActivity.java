package com.sergiocruz.bakingapp.activities;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.sergiocruz.bakingapp.R;
import com.sergiocruz.bakingapp.fragments.RecipeDetailFragment;
import com.sergiocruz.bakingapp.fragments.RecipeStepFragment;

import timber.log.Timber;

/**
 * An activity representing a single recipe detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link MainActivity}.
 */
public class RecipeDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//
//        // if device is on layout mode and it's not a tablet, enter fullscreen with player
//        Boolean fullScreen = getResources().getConfiguration().orientation == ORIENTATION_LANDSCAPE
//                && !getResources().getBoolean(R.bool.is_two_pane);
//
//        if (fullScreen) {
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//
//        View decorView = getWindow().getDecorView();
//        decorView.setSystemUiVisibility(
//                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
//                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
//                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
//
//        }


        setContentView(R.layout.activity_recipe_detail);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }


        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        // http://developer.android.com/guide/components/fragments.html
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.

            RecipeDetailFragment recipeDetailFragment = new RecipeDetailFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

            Boolean isTwoPane = getResources().getBoolean(R.bool.is_two_pane);
            if (isTwoPane) {
                RecipeStepFragment recipeStepFragment = new RecipeStepFragment();
                fragmentTransaction
                        .replace(R.id.recipe_detail_fragment_container, recipeDetailFragment)
                        .replace(R.id.recipe_step_fragment_container, recipeStepFragment)
                        .commit();

            } else {
                fragmentTransaction
                        .replace(R.id.recipe_detail_fragment_container, recipeDetailFragment)
                        .commit();
            }

        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        int a = 1;
        a = a + 1;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            //navigateUpTo(new Intent(this, MainActivity.class));

            super.onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        int count = getSupportFragmentManager().getBackStackEntryCount();
        Timber.i("Back Stack Count = " + count);
        if (count == 0 || getResources().getBoolean(R.bool.is_two_pane)) {
            super.onBackPressed();
        } else {
            getSupportFragmentManager().popBackStack();
        }
    }
}
