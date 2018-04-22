package com.sergiocruz.bakingapp.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.util.Util;
import com.sergiocruz.bakingapp.R;
import com.sergiocruz.bakingapp.exoplayer.ExoCacheDataSourceFactory;

import timber.log.Timber;

import static com.sergiocruz.bakingapp.fragments.RecipeStepFragment.PLAYER_URI_KEY;

public class FullScreenActivity extends AppCompatActivity {
    public static final String PLAYER_PLAYING_KEY = "is_exo_player_playing_key";
    public static final String PLAYER_POSITION_KEY = "exo_player_position_key";
    private Boolean hasSavedState = false;
    private Boolean savedIsExoPlaying;
    private Long savedExoPosition;
    private Boolean stateManaged = false;
    private PlayerView exoPlayerView;
    private ImageView mExoFullScreenIcon;
    private SimpleExoPlayer mExoPlayer;
    private Uri mExoPlayerUri;

    private void bindViews() {
        exoPlayerView = findViewById(R.id.exoPlayerView);
        mExoFullScreenIcon = exoPlayerView.findViewById(R.id.exo_fullscreen_icon);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setWindowFlagsToFullScreen();
        setContentView(R.layout.activity_full_screen);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) { // get ExoPlayer status&position
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            mExoPlayerUri = savedInstanceState.getParcelable(PLAYER_URI_KEY);
            savedIsExoPlaying = savedInstanceState.getBoolean(PLAYER_PLAYING_KEY, false);
            savedExoPosition = savedInstanceState.getLong(PLAYER_POSITION_KEY, 0);
            hasSavedState = true;
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        bindViews();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mExoPlayerUri = extras.getParcelable(PLAYER_URI_KEY);
            savedIsExoPlaying = extras.getBoolean(PLAYER_PLAYING_KEY);
            savedExoPosition = extras.getLong(PLAYER_POSITION_KEY);
            hasSavedState = true;
            Timber.d("mExoPlayerUri= " + mExoPlayerUri +"\n" +
                    "savedIsExoPlaying= " + savedIsExoPlaying + "\n" +
                    "savedExoPosition= " + savedExoPosition);
        }

        setupExoPlayer();

        mExoFullScreenIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_fullscreen_exit_white_24dp));
        mExoFullScreenIcon.setOnClickListener(v -> {
            mExoFullScreenIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_fullscreen_white_24dp));
            manageExoPlayerState();
        });
    }

    private void setupExoPlayer() {
        //exoPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT); // on XML

        mExoPlayer = ExoPlayerFactory.newSimpleInstance(this, new DefaultTrackSelector());
        mExoPlayer.clearVideoSurface();
        mExoPlayer.setVideoSurfaceView((SurfaceView) exoPlayerView.getVideoSurfaceView());
        exoPlayerView.setDefaultArtwork(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        exoPlayerView.setPlayer(mExoPlayer);

        if (hasSavedState) {
            if (mExoPlayerUri != null) loadVideo(mExoPlayerUri);
            if (savedIsExoPlaying != null) mExoPlayer.setPlayWhenReady(savedIsExoPlaying);
            if (savedExoPosition != null) mExoPlayer.seekTo(savedExoPosition);
        }
    }

    @Override
    public void onBackPressed() {
        if (!stateManaged) manageExoPlayerState();
        super.onBackPressed();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(PLAYER_URI_KEY, mExoPlayerUri.toString());
        outState.putBoolean(PLAYER_PLAYING_KEY, mExoPlayer.getPlayWhenReady());
        outState.putLong(PLAYER_POSITION_KEY, mExoPlayer.getCurrentPosition());
    }

    private void setDataToParent() {
        Intent intent = getIntent();
        intent.putExtra(PLAYER_URI_KEY, mExoPlayerUri);
        intent.putExtra(PLAYER_PLAYING_KEY, mExoPlayer.getPlayWhenReady());
        intent.putExtra(PLAYER_POSITION_KEY, mExoPlayer.getCurrentPosition());
        setResult(RESULT_OK, intent);
    }

    private void manageExoPlayerState() {
        setDataToParent();
        goToBackground();
        stateManaged = true;
        finish();
    }

    private void loadVideo(Uri uri) {
        mExoPlayer.prepare(getMediaSource(uri));
        mExoPlayer.seekTo(mExoPlayer.getCurrentPosition() + 1);
        goToForeground();
    }

    @NonNull
    private MediaSource getMediaSource(Uri uri) {
        String userAgent = Util.getUserAgent(this, getString(R.string.app_name));
        return new ExtractorMediaSource(
                uri,
                new ExoCacheDataSourceFactory(this, userAgent),
                new DefaultExtractorsFactory(),
                null,
                null
        );
    }

    public void goToBackground() {
        if (mExoPlayer != null) {
            savedIsExoPlaying = mExoPlayer.getPlayWhenReady();
            mExoPlayer.setPlayWhenReady(false);
        }
    }

    public void goToForeground() {
        if (mExoPlayer != null) {
            mExoPlayer.setPlayWhenReady(savedIsExoPlaying);
        }
    }

    public void releaseVideoPlayer() {
        if (mExoPlayer != null) {
            mExoPlayer.stop();
            mExoPlayer.release();
            mExoPlayer = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseVideoPlayer();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
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
