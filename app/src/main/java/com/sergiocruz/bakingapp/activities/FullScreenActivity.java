package com.sergiocruz.bakingapp.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.sergiocruz.bakingapp.R;
import com.sergiocruz.bakingapp.exoplayer.ExoPlayerVideoHandler;

public class FullScreenActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setWindowFlagsToFullScreen();
        setContentView(R.layout.activity_full_screen);

        ExoPlayerVideoHandler exoPlayerVideoHandler = ExoPlayerVideoHandler.getInstance();

    }



    /**
     * Hides the bottom window navigation buttons
     * and top status bar for Full screen pixel usage
     * */
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
