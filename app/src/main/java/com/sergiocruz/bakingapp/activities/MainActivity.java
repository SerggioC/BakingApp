package com.sergiocruz.bakingapp.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.sergiocruz.bakingapp.R;
import com.sergiocruz.bakingapp.RecipeAdapter;
import com.sergiocruz.bakingapp.dummy.DummyContent;
import com.sergiocruz.bakingapp.utils.NetworkUtils;

/**
 * An activity representing a list of recipes. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link RecipeDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class MainActivity extends AppCompatActivity {

    private boolean mIsTwoPane;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
        ((TextView) toolbar.findViewById(R.id.toolbar_text)).setShadowLayer(10, 4, 4, R.color.cardview_dark_background);

        mIsTwoPane = getResources().getBoolean(R.bool.is_two_pane);

        View recyclerView = findViewById(R.id.recipe_list_recyclerview);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        getDataFromInternet();
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new RecipeAdapter(this, DummyContent.ITEMS, mIsTwoPane));
        if (mIsTwoPane) {
            recyclerView.setLayoutManager(new GridLayoutManager(mContext, 3, GridLayoutManager.VERTICAL, false));
        } else {
            recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        }
    }

    private void getDataFromInternet() {
        NetworkUtils.getJSONDataFromAPI();
    }


}
