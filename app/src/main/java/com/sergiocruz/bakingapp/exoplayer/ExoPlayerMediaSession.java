package com.sergiocruz.bakingapp.exoplayer;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import com.google.android.exoplayer2.SimpleExoPlayer;

import static com.sergiocruz.bakingapp.fragments.RecipeStepFragment.MEDIA_SESSION_TAG;

public class ExoPlayerMediaSession {

    private static ExoPlayerMediaSession instance;
    private MediaSessionCompat mMediaSession;
    private PlaybackStateCompat.Builder mPlaybackState;

    private ExoPlayerMediaSession() {
    }

    public static ExoPlayerMediaSession getInstance() {
        if (instance == null) instance = new ExoPlayerMediaSession();
        return instance;
    }

    public MediaSessionCompat initializeMediaSession(Activity activity, Context context, SimpleExoPlayer exoPlayer) {
        activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);

        if (mMediaSession == null)
        mMediaSession = new MediaSessionCompat(context, MEDIA_SESSION_TAG);
        mMediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mMediaSession.setMediaButtonReceiver(null);
        mMediaSession.setPlaybackState(getPlayBackStateBuilder().build());
        mMediaSession.setCallback(new MediaSessionCallBacks(exoPlayer));
        mMediaSession.setActive(true);

        return mMediaSession;
    }

    public PlaybackStateCompat.Builder getPlayBackStateBuilder() {
        if (mPlaybackState == null)
        mPlaybackState = new PlaybackStateCompat.Builder().setActions(
                PlaybackStateCompat.ACTION_PLAY |
                        PlaybackStateCompat.ACTION_PAUSE |
                        PlaybackStateCompat.ACTION_PLAY_PAUSE |
                        PlaybackStateCompat.ACTION_FAST_FORWARD |
                        PlaybackStateCompat.ACTION_REWIND |
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
        );
        return mPlaybackState;
    }
}
