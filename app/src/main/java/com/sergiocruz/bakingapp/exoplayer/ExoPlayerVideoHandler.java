package com.sergiocruz.bakingapp.exoplayer;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.SurfaceView;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.util.Util;
import com.sergiocruz.bakingapp.R;

public class ExoPlayerVideoHandler {
    private static ExoPlayerVideoHandler instance;
    private static SimpleExoPlayer mExoPlayer;
    private boolean isPlayerPlaying;

    private ExoPlayerVideoHandler() {
    }

    public static ExoPlayerVideoHandler getInstance() {
        if (instance == null) instance = new ExoPlayerVideoHandler();
        return instance;
    }

    public SimpleExoPlayer initExoPlayer(@NonNull Context context, @NonNull PlayerView exoPlayerView, @NonNull Player.EventListener listener) {

        if (mExoPlayer == null) {
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(context, new DefaultTrackSelector());
            if (listener != null) mExoPlayer.addListener(listener);
            isPlayerPlaying = true;
        }

        mExoPlayer.clearVideoSurface();
        mExoPlayer.setVideoSurfaceView((SurfaceView) exoPlayerView.getVideoSurfaceView());
        exoPlayerView.setDefaultArtwork(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher));
        mExoPlayer.seekTo(mExoPlayer.getCurrentPosition() + 1);
        exoPlayerView.setPlayer(mExoPlayer);

        return mExoPlayer;
    }

    /*
    * InitExoPlayer() First
    * */
    public static SimpleExoPlayer getExoPlayer() {
        return mExoPlayer;
    }

    public void loadVideo(Context context, Uri uri) {
        mExoPlayer.prepare(getMediaSource(context, uri));
        mExoPlayer.seekTo(mExoPlayer.getCurrentPosition() + 1);
        goToForeground();
    }

    @NonNull
    private MediaSource getMediaSource(Context context, Uri uri) {
        String userAgent = Util.getUserAgent(context, context.getString(R.string.app_name));
        return new ExtractorMediaSource(
                uri,
                new ExoCacheDataSourceFactory(context, userAgent),
                new DefaultExtractorsFactory(),
                null,
                null
        );
    }

    public void releaseVideoPlayer() {
        if (mExoPlayer != null) {
            mExoPlayer.release();
        }
        mExoPlayer = null;
    }

    public void goToBackground() {
        if (mExoPlayer != null) {
            isPlayerPlaying = mExoPlayer.getPlayWhenReady();
            mExoPlayer.setPlayWhenReady(false);
        }
    }

    public void goToForeground() {
        if (mExoPlayer != null) {
            mExoPlayer.setPlayWhenReady(isPlayerPlaying);
        }
    }

    public boolean isPlayerPlaying() {
        return isPlayerPlaying;
    }
}