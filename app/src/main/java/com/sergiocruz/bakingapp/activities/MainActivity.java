package com.sergiocruz.bakingapp.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.sergiocruz.bakingapp.R;
import com.sergiocruz.bakingapp.fragments.RecipeListFragment;
import com.sergiocruz.bakingapp.helpers.TimberImplementation;

import timber.log.Timber;

/**
 * An activity representing a list of recipes. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link RecipeDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            TimberImplementation.init();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.recipe_list_fragment_container, new RecipeListFragment())
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        int count = getFragmentManager().getBackStackEntryCount();
        Timber.i("Back Stack Count = " + count);
        if (count == 0) {
            super.onBackPressed();
        } else {
            getFragmentManager().popBackStack();
        }

    }
}
