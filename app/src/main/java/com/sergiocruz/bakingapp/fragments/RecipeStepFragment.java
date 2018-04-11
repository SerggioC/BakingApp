package com.sergiocruz.bakingapp.fragments;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.transition.ChangeBounds;
import android.support.transition.ChangeClipBounds;
import android.support.transition.ChangeImageTransform;
import android.support.transition.ChangeTransform;
import android.support.transition.TransitionSet;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.sergiocruz.bakingapp.R;
import com.sergiocruz.bakingapp.activities.RecipeDetailActivity;
import com.sergiocruz.bakingapp.exoplayer.ExoCacheDataSourceFactory;
import com.sergiocruz.bakingapp.exoplayer.MediaSessionCallBacks;
import com.sergiocruz.bakingapp.model.ActivityViewModel;
import com.sergiocruz.bakingapp.model.RecipeStep;

import java.util.List;

import timber.log.Timber;

import static android.content.Context.NOTIFICATION_SERVICE;
import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;

public class RecipeStepFragment extends Fragment implements Player.EventListener {
    public static final String MEDIA_SESSION_TAG = "lets_bake_media_session";
    public static final String NOTIFICATION_TAG = "lets_bake_notification_tag";
    private static final String CHANNEL_ID = "lets_bake_notification_channel_id";
    public static final int NOTIFICATION_ID = 1;
    private static MediaSessionCompat mMediaSession;
    private ActivityViewModel viewModel;
    private List<RecipeStep> stepsList;
    private Integer stepNumber;
    private TextView stepDetailTV;
    private PlayerView exoPlayerView;
    private SimpleExoPlayer mExoPlayer;
    private PlaybackStateCompat.Builder mPlaybackState;
    private NotificationManager mNotificationManager;
    private Context mContext;
    private RecipeStep recipeStep;

    public RecipeStepFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
        setTransitions();
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     * This is optional, and non-graphical fragments can return null (which
     * is the default implementation).  This will be called between
     * {@link #onCreate(Bundle)} and {@link #onActivityCreated(Bundle)}.
     * <p>
     * <p>If you return a View from here, you will later be called in
     * {@link #onDestroyView} when the view is being released.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate
     *                           any views in the fragment,
     * @param container          If non-null, this is the parent view that the fragment's
     *                           UI should be attached to.  The fragment should not add the view itself,
     *                           but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recipe_step, container, false);

        viewModel = ActivityViewModel.getInstance(this);
        viewModel.getRecipeStep().observe(this, recipeStep -> {
            stepNumber = viewModel.getRecipeStepNumber().getValue();
            updateFragmentUI(recipeStep);
        });

        recipeStep = viewModel.getRecipeStep().getValue();
        stepNumber = viewModel.getRecipeStepNumber().getValue();
        if (stepNumber == null) stepNumber = -1;
        stepsList = viewModel.getRecipe().getValue().getStepsList();
        setupFragmentUI(rootView, recipeStep);

        return rootView;
    }

    private void setupFragmentUI(View rootView, RecipeStep recipeStep) {
        stepDetailTV = rootView.findViewById(R.id.recipe_step_detail_TextView);
        exoPlayerView = rootView.findViewById(R.id.exoPlayerView);
        //exoPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);

        updateFragmentUI(recipeStep);

        ImageButton next = rootView.findViewById(R.id.next_btn);
        next.setOnClickListener(v -> {
            int nextStepNumber = stepNumber + 1;
            if (nextStepNumber > stepsList.size() - 1) return;
            viewModel.setRecipeStepNumber(nextStepNumber);
            viewModel.setRecipeStep(stepsList.get(nextStepNumber));
        });

        ImageButton previous = rootView.findViewById(R.id.previous_btn);
        previous.setOnClickListener(v -> {
            int previousStepNumber = stepNumber - 1;
            if (previousStepNumber < 0) return;
            viewModel.setRecipeStepNumber(previousStepNumber);
            viewModel.setRecipeStep(stepsList.get(previousStepNumber));
        });

        initializeExoPlayer(Uri.parse(recipeStep.getVideoUrl()));

    }

    private void changeVideo(Uri uri) {
        Timber.i("Video Uri = " + uri);
        String userAgent = Util.getUserAgent(mContext, "Lets_Bake");
        MediaSource mediaSource = new ExtractorMediaSource(
                uri,
                new DefaultDataSourceFactory(mContext, userAgent),
                new DefaultExtractorsFactory(),
                null,
                null
        );

        if (mExoPlayer == null) initializeExoPlayer(uri);
        mExoPlayer.prepare(mediaSource);
    }

    private void updateFragmentUI(RecipeStep recipeStep) {
        if (stepNumber == -1 || recipeStep == null) {
            stepDetailTV.setText(R.string.select_step);
        } else {
            stepDetailTV.setText(getString(R.string.step_number) + " " + stepNumber + "\n" + recipeStep.getDescription());
        }

        exoPlayerView.setDefaultArtwork(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));

        changeVideo(Uri.parse(recipeStep.getVideoUrl()));

    }

    private void initializeExoPlayer(Uri uri) {

        // Create an instance of the ExoPlayer.
        if (mExoPlayer == null) {

            TrackSelector trackSelector = new DefaultTrackSelector();
            //LoadControl loadControl = new DefaultLoadControl(); // Controls Buffering of media
            //mExoPlayer = ExoPlayerFactory.newSimpleInstance(mContext, trackSelector, loadControl);
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(mContext, trackSelector);
            exoPlayerView.setPlayer(mExoPlayer);

            // Prepare the MediaSource.
            String userAgent = Util.getUserAgent(mContext, "Lets_Bake");
//            MediaSource mediaSource = new ExtractorMediaSource(
//                    uri,
//                    new DefaultDataSourceFactory(mContext, userAgent),
//                    new DefaultExtractorsFactory(),
//                    null,
//                    null
//            );

            MediaSource mediaSource = new ExtractorMediaSource(
                    uri,
                    new ExoCacheDataSourceFactory(mContext, 100 * 1024 * 1024, 5 * 1024 * 1024),
                    new DefaultExtractorsFactory(),
                    null,
                    null
            );

            mExoPlayer.prepare(mediaSource);
            mExoPlayer.setPlayWhenReady(true);
            mExoPlayer.addListener(this);
            getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);



            initializeMediaSession();

        }
    }

    private void initializeMediaSession() {
        mMediaSession = new MediaSessionCompat(mContext, MEDIA_SESSION_TAG);
        mMediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mMediaSession.setMediaButtonReceiver(null);

        mPlaybackState = new PlaybackStateCompat.Builder().setActions(
                PlaybackStateCompat.ACTION_PLAY |
                        PlaybackStateCompat.ACTION_PAUSE |
                        PlaybackStateCompat.ACTION_PLAY_PAUSE |
                        PlaybackStateCompat.ACTION_FAST_FORWARD |
                        PlaybackStateCompat.ACTION_REWIND |
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
        );

        mMediaSession.setPlaybackState(mPlaybackState.build());
        mMediaSession.setCallback(new MediaSessionCallBacks(mExoPlayer));
        mMediaSession.setActive(true);
    }

    /**
     * Called when the timeline and/or manifest has been refreshed.
     * <p>
     * Note that if the timeline has changed then a position discontinuity may also have occurred.
     * For example, the current period index may have changed as a result of periods being added or
     * removed from the timeline. This will <em>not</em> be reported via a separate call to
     * {@link #onPositionDiscontinuity(int)}.
     *
     * @param timeline The latest timeline. Never null, but may be empty.
     * @param manifest The latest manifest. May be null.
     * @param reason   The {@link Player.TimelineChangeReason} responsible for this timeline change.
     */
    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

    }

    /**
     * Called when the available or selected tracks change.
     *
     * @param trackGroups     The available tracks. Never null, but may be of length zero.
     * @param trackSelections The track selections for each renderer. Never null and always of
     *                        length {link getRendererCount()}, but may contain null elements.
     */
    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    /**
     * Called when the player starts or stops loading the source.
     *
     * @param isLoading Whether the source is currently being loaded.
     */
    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    /**
     * Called when the value returned from either {link #getPlayWhenReady()} or
     * {link #getPlaybackState()} changes.
     *
     * @param playWhenReady Whether playback will proceed when ready.
     * @param playbackState One of the {@code STATE} constants.
     */
    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

        if ((playbackState == Player.STATE_READY) & mExoPlayer.getPlayWhenReady()) {

            mPlaybackState.setState(PlaybackStateCompat.STATE_PLAYING, mExoPlayer.getContentPosition(), 1);

        } else if ((playbackState == Player.STATE_READY) && !mExoPlayer.getPlayWhenReady()) {

            mPlaybackState.setState(PlaybackStateCompat.STATE_PAUSED, mExoPlayer.getContentPosition(), 1);

        }

        mMediaSession.setPlaybackState(mPlaybackState.build());
        showPlaybackNotification(mPlaybackState.build());
    }

    /**
     * Called when the value of {link #getRepeatMode()} changes.
     *
     * @param repeatMode The {@link PlaybackStateCompat.RepeatMode} used for playback.
     */
    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    /**
     * Called when the value of {link #getShuffleModeEnabled()} changes.
     *
     * @param shuffleModeEnabled Whether shuffling of windows is enabled.
     */
    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

    }

    /**
     * Called when an error occurs. The playback state will transition to {link #STATE_IDLE}
     * immediately after this method is called. The player instance can still be used, and
     * {link #release()} must still be called on the player should it no longer be required.
     *
     * @param error The error.
     */
    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    /**
     * Called when a position discontinuity occurs without a change to the timeline. A position
     * discontinuity occurs when the current window or period index changes (as a result of playback
     * transitioning from one period in the timeline to the next), or when the playback position
     * jumps within the period currently being played (as a result of a seek being performed, or
     * when the source introduces a discontinuity internally).
     * <p>
     * When a position discontinuity occurs as a result of a change to the timeline this method is
     * <em>not</em> called. {@link #onTimelineChanged(Timeline, Object, int)} is called in this
     * case.
     *
     * @param reason The {@link Player.DiscontinuityReason} responsible for the discontinuity.
     */
    @Override
    public void onPositionDiscontinuity(int reason) {

    }

    /**
     * Called when the current playback parameters change. The playback parameters may change due to
     * a call to {link #setPlaybackParameters(PlaybackParameters)}, or the player itself may change
     * them (for example, if audio playback switches to passthrough mode, where speed adjustment is
     * no longer possible).
     *
     * @param playbackParameters The playback parameters.
     */
    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    /**
     * Called when all pending seek requests have been processed by the player. This is guaranteed
     * to happen after any necessary changes to the player state were reported to
     * {@link #onPlayerStateChanged(boolean, int)}.
     */
    @Override
    public void onSeekProcessed() {

    }

    public void showPlaybackNotification(PlaybackStateCompat playbackState) {
        Timber.d("showPlaybackNotification init");
        NotificationCompat.Builder notificationCompatBuildert = new NotificationCompat.Builder(mContext, CHANNEL_ID);

        int icon;
        String playPause;
        if (playbackState.getState() == PlaybackStateCompat.STATE_PLAYING) {
            icon = R.drawable.exo_controls_pause;
            playPause = getString(R.string.exo_controls_pause_description);
        } else {
            icon = R.drawable.exo_controls_play;
            playPause = getString(R.string.exo_controls_play_description);
        }

        NotificationCompat.Action playPauseAction = new NotificationCompat.Action(
                icon, playPause,
                MediaButtonReceiver.buildMediaButtonPendingIntent(mContext, PlaybackStateCompat.ACTION_PLAY_PAUSE));

//        NotificationCompat.Action restartAction = new NotificationCompat.Action(
//                R.drawable.exo_controls_previous, getString(R.string.exo_controls_previous_description),
//                MediaButtonReceiver.buildMediaButtonPendingIntent(mContext, PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS));

        PendingIntent contentPendingIntent = PendingIntent.getActivity(mContext, 0, new Intent(mContext, RecipeDetailActivity.class), 0);

        notificationCompatBuildert
                .setContentTitle(viewModel.getRecipe().getValue().getRecipeName())
                .setContentText(recipeStep.getShortDesc())
                .setContentIntent(contentPendingIntent)
                .setSmallIcon(R.drawable.ic_muffin)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .addAction(playPauseAction) // action 0
                .setStyle(new android.support.v4.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(mMediaSession.getSessionToken())
                        .setShowActionsInCompactView(0));

        //.addAction(restartAction)   // action 0

        mNotificationManager = (NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);
        mNotificationManager.notify(NOTIFICATION_TAG, NOTIFICATION_ID, notificationCompatBuildert.build());

    }

    private void setTransitions() {
        this.setSharedElementEnterTransition(new DetailsTransition());
        this.setEnterTransition(new DetailsTransition());
        this.setExitTransition(new DetailsTransition());
        this.setSharedElementReturnTransition(new DetailsTransition());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mExoPlayer.stop();
        mExoPlayer.release();
        mMediaSession.setActive(false);
        mNotificationManager.cancel(NOTIFICATION_TAG, NOTIFICATION_ID);

        Timber.w("destroying recipe step fragment");

    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // if phone is on layout mode enter fullscreen with player
        if (getResources().getConfiguration().orientation == ORIENTATION_LANDSCAPE
                && !getResources().getBoolean(R.bool.is_two_pane)) {

        }
    }

    public static class MediaReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            MediaButtonReceiver.handleIntent(mMediaSession, intent);
            Timber.d("Intent = " + intent.getAction());
        }
    }

    public class DetailsTransition extends TransitionSet {
        public DetailsTransition() {
            setOrdering(ORDERING_TOGETHER);
            addTransition(new ChangeBounds())
                    .addTransition(new ChangeTransform())
                    .addTransition(new ChangeImageTransform())
                    .addTransition(new ChangeClipBounds());
        }
    }


}
