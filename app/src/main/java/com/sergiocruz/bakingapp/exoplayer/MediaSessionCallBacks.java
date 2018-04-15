package com.sergiocruz.bakingapp.exoplayer;

import android.support.v4.media.session.MediaSessionCompat;

import com.google.android.exoplayer2.SimpleExoPlayer;

import timber.log.Timber;

public class MediaSessionCallBacks extends MediaSessionCompat.Callback {
    SimpleExoPlayer mExoPlayer;

    public MediaSessionCallBacks(SimpleExoPlayer mExoPlayer) {
        this.mExoPlayer = mExoPlayer;
    }

    /**
     * Override to handle requests to begin playback.
     */
    @Override
    public void onPlay() {
        super.onPlay();
        mExoPlayer.setPlayWhenReady(true);
        Timber.i("Baking MediaSessionCallBacks onPlay");
    }

    /**
     * Override to handle requests to pause playback.
     */
    @Override
    public void onPause() {
        super.onPause();
        mExoPlayer.setPlayWhenReady(false);
        Timber.i("Baking MediaSessionCallBacks onPause");
    }

    /**
     * Override to handle requests to skip to the previous media item.
     */
    @Override
    public void onSkipToPrevious() {
        super.onSkipToPrevious();
        mExoPlayer.seekTo(0);
        Timber.i("Baking MediaSessionCallBacks onSkipToPrevious");

    }
}

