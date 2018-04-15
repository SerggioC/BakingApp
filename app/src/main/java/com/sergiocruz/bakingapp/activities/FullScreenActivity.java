package com.sergiocruz.bakingapp.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.google.android.exoplayer2.ui.PlayerView;
import com.sergiocruz.bakingapp.R;
import com.sergiocruz.bakingapp.exoplayer.ExoPlayerVideoHandler;

import static com.sergiocruz.bakingapp.fragments.RecipeStepFragment.FULL_SCREEN_PARENT_EXTRA;

public class FullScreenActivity extends AppCompatActivity {
    private boolean destroyVideo = true;
    private PlayerView exoPlayerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setWindowFlagsToFullScreen();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_full_screen);

    }

    @Override
    protected void onResume() {
        super.onResume();
        exoPlayerView = findViewById(R.id.exoPlayerView);
        ImageView mExoFullScreenIcon = exoPlayerView.findViewById(R.id.exo_fullscreen_icon);
        mExoFullScreenIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_fullscreen_exit_white_24dp));

        ExoPlayerVideoHandler.getInstance().initExoPlayer(this, exoPlayerView, null);
        ExoPlayerVideoHandler.getInstance().goToForeground();

        findViewById(R.id.exo_fullscreen_button).setOnClickListener(v -> {
            mExoFullScreenIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_fullscreen_exit_white_24dp));
            destroyVideo = false;

            setDataToParent();

            finish();
        });
    }

    private void setDataToParent() {
        Intent intent = getIntent();
        intent.putExtra(FULL_SCREEN_PARENT_EXTRA, true);
        setResult(RESULT_OK, intent);
    }

    @Override
    protected void onPause(){
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        setDataToParent();
        ExoPlayerVideoHandler.getInstance().goToBackground();
        finish();
        super.onBackPressed();
    }

    /**
     * Hides the bottom window navigation buttons
     * and top status bar for Full screen pixel usage
     */
    private void setWindowFlagsToFullScreen() {
        Window window = this.getWindow();
        window.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        View decorView = window.getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }




}
