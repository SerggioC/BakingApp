package com.sergiocruz.bakingapp.exoplayer;

import android.app.Activity;
import android.content.Context;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;

public class ExoPlayerEventListeners implements Player.EventListener {

    private static ExoPlayerEventListeners instance;
    private MediaSessionCompat mMediaSession;
    private PlaybackStateCompat.Builder mPlaybackState;
    private SimpleExoPlayer mExoPlayer;

    private ExoPlayerEventListeners(Activity activity, Context context, SimpleExoPlayer mExoPlayer) {
        this.mMediaSession = ExoPlayerMediaSession.getInstance().initializeMediaSession(activity, context, mExoPlayer);
        this.mPlaybackState = ExoPlayerMediaSession.getInstance().getPlayBackStateBuilder();
        this.mExoPlayer = mExoPlayer;
    }

    public static ExoPlayerEventListeners getInstance(Activity activity, Context context, SimpleExoPlayer mExoPlayer) {
        if (instance == null) instance = new ExoPlayerEventListeners(activity, context, mExoPlayer);
        return instance;
    }


    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

        if ((playbackState == Player.STATE_READY) & mExoPlayer.getPlayWhenReady()) {

            mPlaybackState.setState(PlaybackStateCompat.STATE_PLAYING, mExoPlayer.getContentPosition(), 1);

        } else if ((playbackState == Player.STATE_READY) && !mExoPlayer.getPlayWhenReady()) {

            mPlaybackState.setState(PlaybackStateCompat.STATE_PAUSED, mExoPlayer.getContentPosition(), 1);

        }

        mMediaSession.setPlaybackState(mPlaybackState.build());
    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity(int reason) {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    @Override
    public void onSeekProcessed() {

    }
}
