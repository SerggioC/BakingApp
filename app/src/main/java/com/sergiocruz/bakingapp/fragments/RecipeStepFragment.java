package com.sergiocruz.bakingapp.fragments;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.util.Util;
import com.sergiocruz.bakingapp.R;
import com.sergiocruz.bakingapp.activities.FullScreenActivity;
import com.sergiocruz.bakingapp.activities.RecipeDetailActivity;
import com.sergiocruz.bakingapp.exoplayer.ExoCacheDataSourceFactory;
import com.sergiocruz.bakingapp.exoplayer.MediaSessionCallBacks;
import com.sergiocruz.bakingapp.model.ActivityViewModel;
import com.sergiocruz.bakingapp.model.Recipe;
import com.sergiocruz.bakingapp.model.RecipeStep;
import com.sergiocruz.bakingapp.utils.AndroidUtils;
import com.sergiocruz.bakingapp.utils.AndroidUtils.MimeType;
import com.sergiocruz.bakingapp.utils.NetworkUtils;

import java.util.List;

import timber.log.Timber;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.NOTIFICATION_SERVICE;
import static com.sergiocruz.bakingapp.activities.FullScreenActivity.PLAYER_PLAYING_KEY;
import static com.sergiocruz.bakingapp.activities.FullScreenActivity.PLAYER_POSITION_KEY;
import static com.sergiocruz.bakingapp.fragments.RecipeDetailFragment.RECIPE_STEP_POSITION;
import static com.sergiocruz.bakingapp.fragments.RecipeListFragment.FAVORITES;
import static com.sergiocruz.bakingapp.fragments.RecipeListFragment.ONLINE;
import static com.sergiocruz.bakingapp.utils.AndroidUtils.MimeType.VIDEO;

public class RecipeStepFragment extends Fragment implements Player.EventListener {
    public static final String MEDIA_SESSION_TAG = "lets_bake_media_session";
    public static final String NOTIFICATION_TAG = "lets_bake_notification_tag";
    public static final int NOTIFICATION_ID = 0x01;
    public static final Integer FULL_SCREEN_REQUEST_CODE = 0x02;
    public static final String PLAYER_URI_KEY = "key_exoplayer_uri";
    private static final String CHANNEL_ID = "lets_bake_notification_channel_id";
    public static final String ENTERING_FULL_SCREEN_KEY = "key_app_entering_full_screen";
    private static MediaSessionCompat mMediaSession;
    private Context mContext;
    private ActivityViewModel viewModel;
    private List<RecipeStep> stepsList;
    private Integer stepNumber;
    private TextView stepDetailTV;
    private PlayerView exoPlayerView;
    private ImageButton nextButton;
    private ImageButton previousButton;
    private PlaybackStateCompat.Builder mPlaybackState;
    private NotificationManager mNotificationManager;
    private SimpleExoPlayer mExoPlayer;
    private boolean isDetached = false;
    private Boolean hasSavedState = false;
    private Uri mExoPlayerUri;
    private Boolean savedIsExoPlaying;
    private Long savedExoPosition;
    private Boolean isParentFullScreen = false;
    private boolean isEnteringFullScreen = false;

    public RecipeStepFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = getContext();
        setTransitions();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        saveStateToBundle(outState);
    }

    private Bundle saveStateToBundle(@NonNull Bundle outState) {
        outState.putParcelable(PLAYER_URI_KEY, mExoPlayerUri);
        outState.putBoolean(PLAYER_PLAYING_KEY, mExoPlayer == null ? true : mExoPlayer.getPlayWhenReady());
        outState.putLong(PLAYER_POSITION_KEY, mExoPlayer == null ? 0 : mExoPlayer.getCurrentPosition());
        outState.putBoolean(ENTERING_FULL_SCREEN_KEY, isEnteringFullScreen);
        outState.putInt(RECIPE_STEP_POSITION, stepNumber);

        return outState;
    }

    private void bindViews(View rootView) {
        stepDetailTV = rootView.findViewById(R.id.recipe_step_detail_TextView);
        exoPlayerView = rootView.findViewById(R.id.exoPlayerView);
        ImageView mExoFullScreenIcon = exoPlayerView.findViewById(R.id.exo_fullscreen_icon);
        nextButton = rootView.findViewById(R.id.next_btn);
        previousButton = rootView.findViewById(R.id.previous_btn);

        mExoFullScreenIcon.setOnClickListener(v -> enterFullscreen());
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
        bindViews(rootView);

        setupExoPlayer();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        String resourceOnOff = prefs.getString(getString(R.string.pref_menu_key), ONLINE);
        Boolean getFavorites = resourceOnOff.equals(FAVORITES);
        Boolean hasInternet = NetworkUtils.hasActiveNetworkConnection(mContext);

        if (viewModel == null)
            viewModel = ActivityViewModel.getInstance(this, getFavorites, hasInternet);

        if (!hasSavedState)
            stepNumber = viewModel.getRecipeStepNumber().getValue();

        viewModel.getRecipe().observe(this, recipe -> stepsList = recipe.getStepsList());
        viewModel.getRecipeStep().observe(this, recipeStep -> {
            if (recipeStep == null) return;
            stepNumber = viewModel.getRecipeStepNumber().getValue();
            Timber.i("hasSavedState " + hasSavedState + "\n" +
                    "isParentFullScreen " + isParentFullScreen);
            if (!hasSavedState && !isParentFullScreen) {
                String videoUrl = recipeStep.getVideoUrl();
                Uri videoUri = Uri.parse("");
                MimeType videoMimeType = AndroidUtils.getMymeTypeFromString(videoUrl);
                if (videoMimeType == VIDEO) {
                    videoUri = Uri.parse(videoUrl);
                } else {
                    String thumbnailUrl = recipeStep.getThumbnailUrl();
                    MimeType thumbMimeType = AndroidUtils.getMymeTypeFromString(thumbnailUrl);
                    if (thumbMimeType == VIDEO) {
                        videoUri = Uri.parse(thumbnailUrl);
                    }
                }

                loadAndPlayVideo(mContext, videoUri, 0L, true);
                hasSavedState = false;
                isParentFullScreen = false;
            }

            updateFragmentUI(recipeStep);
        });

        setupFragmentUI(viewModel.getRecipeStep().getValue());

        return rootView;
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            mExoPlayerUri = savedInstanceState.getParcelable(PLAYER_URI_KEY);
            savedIsExoPlaying = savedInstanceState.getBoolean(PLAYER_PLAYING_KEY);
            savedExoPosition = savedInstanceState.getLong(PLAYER_POSITION_KEY);
            isEnteringFullScreen = savedInstanceState.getBoolean(ENTERING_FULL_SCREEN_KEY);
            stepNumber = savedInstanceState.getInt(RECIPE_STEP_POSITION, -1);

            hasSavedState = true;
            isParentFullScreen = false;
        } else {
            hasSavedState = false;
            isParentFullScreen = false;
        }

        Timber.d("mExoPlayerUri " + mExoPlayerUri + "\n" +
                "savedIsExoPlaying " + savedIsExoPlaying + "\n" +
                "savedExoPosition " + savedExoPosition + "\n");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FULL_SCREEN_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            mExoPlayerUri = data.getParcelableExtra(PLAYER_URI_KEY);
            savedIsExoPlaying = data.getBooleanExtra(PLAYER_PLAYING_KEY, false);
            savedExoPosition = data.getLongExtra(PLAYER_POSITION_KEY, 0);
            stepNumber = data.getIntExtra(RECIPE_STEP_POSITION, -1);

            hasSavedState = true;
            isParentFullScreen = true;
        } else {
            hasSavedState = false;
            isParentFullScreen = false;
        }

        Timber.d("mExoPlayerUri " + mExoPlayerUri + "\n" +
                "savedIsExoPlaying " + savedIsExoPlaying + "\n" +
                "savedExoPosition " + savedExoPosition);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (hasSavedState) {
            loadAndPlayVideo(mContext, mExoPlayerUri, savedExoPosition, savedIsExoPlaying);
            updateFragmentUI(viewModel.getRecipeStep().getValue());
            hasSavedState = false;
        }

        // Enter fullscreen with player if the device rotates to landscape layout mode and it's not a tablet
        // !isParentFullScreen to avoid entering fullscreen again when exiting from FullscreenActivity and device is still on landscape
        Boolean isTwoPane = getResources().getBoolean(R.bool.is_two_pane);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE && !isTwoPane && !isParentFullScreen) {
            enterFullscreen();
        } else {
            isEnteringFullScreen = false;
            isParentFullScreen = false;
        }


    }

    private void setupExoPlayer() {
        //exoPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT); // on XML

        mExoPlayer = ExoPlayerFactory.newSimpleInstance(mContext, new DefaultTrackSelector());
        mExoPlayer.addListener(this);
        mExoPlayer.clearVideoSurface();
        mExoPlayer.setVideoSurfaceView((SurfaceView) exoPlayerView.getVideoSurfaceView());
        exoPlayerView.setDefaultArtwork(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        exoPlayerView.setPlayer(mExoPlayer);

        initializeMediaSession();
    }

    public void loadAndPlayVideo(Context context, Uri uri, Long position, Boolean playWhenReady) {
        mExoPlayerUri = uri;
        if (mExoPlayer == null) setupExoPlayer();
        mExoPlayer.prepare(getMediaSource(context, uri));
        mExoPlayer.seekTo(position);
        mExoPlayer.setPlayWhenReady(playWhenReady);
    }

    private void enterFullscreen() {
        // Don't enter fullscreen if no video selected or empty Uri.
        if (TextUtils.isEmpty(mExoPlayerUri == null ? null : mExoPlayerUri.toString())) return;
        isEnteringFullScreen = true;

        Intent intent = new Intent(mContext, FullScreenActivity.class);
        intent.putExtras(saveStateToBundle(new Bundle()));

        stopNotifications();
        releaseVideoPlayer();

        startActivityForResult(intent, FULL_SCREEN_REQUEST_CODE);
    }

    private void setupFragmentUI(RecipeStep recipeStep) {

        updateFragmentUI(recipeStep);

        nextButton.setOnClickListener(v -> {
            int nextStepNumber = stepNumber + 1;
            if (nextStepNumber > stepsList.size() - 1) return;
            viewModel.setRecipeStepNumber(nextStepNumber);
            viewModel.setRecipeStep(stepsList.get(nextStepNumber));
            toggleNavigationButtons(nextStepNumber);
        });

        previousButton.setOnClickListener(v -> {
            int previousStepNumber = stepNumber - 1;
            if (previousStepNumber < 0) return;
            viewModel.setRecipeStepNumber(previousStepNumber);
            viewModel.setRecipeStep(stepsList.get(previousStepNumber));
            toggleNavigationButtons(previousStepNumber);
        });
    }

    private void toggleNavigationButtons(int stepNumber) {
        if (stepNumber > 0 && stepNumber < stepsList.size() - 1) {
            previousButton.setVisibility(View.VISIBLE);
            nextButton.setVisibility(View.VISIBLE);
        } else if (stepNumber == 0) {
            previousButton.setVisibility(View.INVISIBLE);
            nextButton.setVisibility(View.VISIBLE);
        } else if (stepNumber == stepsList.size() - 1) {
            previousButton.setVisibility(View.VISIBLE);
            nextButton.setVisibility(View.INVISIBLE);
        }
    }

    private void updateFragmentUI(RecipeStep recipeStep) {
        if (recipeStep == null || stepNumber < 0) {
            stepDetailTV.setText(R.string.select_step);
        } else {
            String shortDesc = recipeStep.getShortDesc();
            String description = recipeStep.getDescription();
            String text;
            if (stepNumber == 0) {
                text = shortDesc.equals(description) ? shortDesc : shortDesc + "\n" + description;
            } else {
                String stepNumberTextStr = getString(R.string.step_number);
                text = shortDesc.equals(description) ?
                        stepNumberTextStr + " " + stepNumber + "\n" + shortDesc :
                        stepNumberTextStr + " " + stepNumber + "\n" + shortDesc + "\n" +
                        description;
            }
            stepDetailTV.setText(text);
        }
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

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        if (mExoPlayer != null) {
            // pause on background? If entering full Screen keep playing
            mExoPlayer.setPlayWhenReady(isEnteringFullScreen);
        }
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        isDetached = true;
        stopNotifications();
        releaseVideoPlayer();
    }

    public void releaseVideoPlayer() {
        if (mExoPlayer != null) {
            mExoPlayer.removeListener(this);
            mExoPlayer.stop();
            mExoPlayer.release();
            mExoPlayer = null;
        }
    }

    private void stopNotifications() {
        if (mMediaSession != null) mMediaSession.setActive(false);
        if (mNotificationManager != null) mNotificationManager.cancel(NOTIFICATION_TAG, NOTIFICATION_ID);
    }

    /**
     * Media Notifications and controls callBacks
     */
    private void initializeMediaSession() {

        getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);

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
        if (viewModel == null) {
            return;
        }

        Recipe recipeValue = viewModel.getRecipe().getValue();
        RecipeStep stepDescValue = viewModel.getRecipeStep().getValue();
        if (isDetached || recipeValue == null || stepDescValue == null) {
            stopNotifications();
            return;
        }

        NotificationCompat.Builder notificationCompatBuilder = new NotificationCompat.Builder(mContext, CHANNEL_ID);

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

        notificationCompatBuilder
                .setContentTitle(recipeValue.getRecipeName())
                .setContentText(stepDescValue.getShortDesc())
                .setContentIntent(contentPendingIntent)
                .setSmallIcon(R.drawable.ic_muffin)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .addAction(playPauseAction) // action 0
                .setStyle(new android.support.v4.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(mMediaSession.getSessionToken())
                        .setShowActionsInCompactView(0)); // action indexes

        //.addAction(restartAction)   // action index 0

        mNotificationManager = (NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);
        mNotificationManager.notify(NOTIFICATION_TAG, NOTIFICATION_ID, notificationCompatBuilder.build());

    }

    private void setTransitions() {
        this.setSharedElementEnterTransition(new DetailsTransition());
        this.setEnterTransition(new DetailsTransition());
        this.setExitTransition(new DetailsTransition());
        this.setSharedElementReturnTransition(new DetailsTransition());
    }

    public static class MediaReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            MediaButtonReceiver.handleIntent(mMediaSession, intent);
            Timber.d("Intent = " + intent.getAction());
        }
    }

    public class DetailsTransition extends TransitionSet {
        DetailsTransition() {
            setOrdering(ORDERING_TOGETHER);
            addTransition(new ChangeBounds())
                    .addTransition(new ChangeTransform())
                    .addTransition(new ChangeImageTransform())
                    .addTransition(new ChangeClipBounds());
        }
    }


}
