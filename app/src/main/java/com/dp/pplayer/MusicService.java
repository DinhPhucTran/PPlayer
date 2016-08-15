package com.dp.pplayer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.widget.Toast;

import java.io.IOException;

public class MusicService extends Service implements MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {
//        PrepareMusicRetrieverTask.MusicRetrieverPreparedListener {
    public static final String ACTION_TOGGLE_PLAYBACK = "com.dp.pplayer.TOGGLE_PLAYBACK";
    public static final String ACTION_PLAY = "com.dp.pplayer.PLAY";
    public static final String ACTION_PAUSE = "com.dp.pplayer.PAUSE";
    public static final String ACTION_NEXT = "com.dp.pplayer.NEXT";
    public static final String ACTION_PREV = "com.dp.pplayer.PREV";
    public static final String ACTION_STOP = "com.dp.pplayer.STOP";

    public static final String ACTION_ON_COMPLETION = "com.dp.pplayer.ON_COMPLETION";
    public static final String ACTION_ON_PREPARED_PLAYER = "com.dp.pplayer.ON_PREPARED";

    public static final String STATE_STOPPED = "com.dp.pplayer.STOPPED";
    public static final String STATE_PLAYING = "com.dp.pplayer.PLAYING";
    public static final String STATE_PAUSED = "com.dp.pplayer.PAUSED";

    public static boolean isShuffle;
    public static boolean isRepeat;
    public static int duration;

    private final int NOTIFICATION_ID = 1;

    public static MediaPlayer mPlayer;
    public static String mSongTitle;
    public static String mArtist;
    public static String mAlbum;
    public static long mAlbumId;
    private int mCurrentSongIndex;
//    private boolean mStartPlayingAfterRetrieve;

    enum State {
        Retrieving, Preparing, Stopped, Paused, Playing
    }

    private static State mState = State.Stopped;
//    MusicRetriever mRetriever;

    //AudioManager mAudioManager;
    NotificationManager mNotificationManager;

    Notification.Builder mNotificationBuilder = null;

    public void resetMediaPlayer() {
        if (mPlayer == null) {
            mPlayer = new MediaPlayer();
            mPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
            mPlayer.setOnPreparedListener(this);
            mPlayer.setOnCompletionListener(this);
            mPlayer.setOnErrorListener(this);
            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        } else {
            mPlayer.reset();
        }
    }

    @Override
    public void onCreate() {
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        mRetriever = new MusicRetriever(getContentResolver());
//        (new PrepareMusicRetrieverTask(mRetriever, this)).execute();
        mSongTitle = new String();
        mCurrentSongIndex = 0;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        if (action.equals(ACTION_TOGGLE_PLAYBACK))
            processTogglePlaybackRequest();
        else if (action.equals(ACTION_PLAY))
            processPlayRequest();
        else if (action.equals(ACTION_PAUSE))
            processPauseRequest();
        else if (action.equals(ACTION_NEXT))
            processPlayNextRequest();
        else if (action.equals(ACTION_PREV))
            processPlayPrevRequest();
        else if(action.equals(ACTION_STOP))
            stopPlayer(true);
        else {
            playSongIndex(Integer.parseInt(action));
            mCurrentSongIndex = Integer.parseInt(action);
        }

        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

//    @Override
//    public void onMusicRetrieverPrepared() {
//        mState = State.Stopped;
//        if (mStartPlayingAfterRetrieve) {
//            playSongIndex(mCurrentSongIndex);
//        }
//    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (!isShuffle) {
            mCurrentSongIndex++;
            if (mCurrentSongIndex >= MainActivity.songList.size()) {
                mCurrentSongIndex = 0;
                if (isRepeat)
                    playSongIndex(mCurrentSongIndex);
                else {
                    stopPlayer(true);
                }
                return;
            } else {
                playSongIndex(mCurrentSongIndex);
            }
        } else {

        }

        Intent completionIntent = new Intent();
        completionIntent.setAction(ACTION_ON_COMPLETION);
        sendBroadcast(completionIntent);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mState = State.Stopped;
        releaseMediaPlayer();
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mState = State.Playing;
        updateNotification(mSongTitle);
        if (!mPlayer.isPlaying())
            mPlayer.start();

        Intent preparedIntent = new Intent();
        preparedIntent.setAction(ACTION_ON_PREPARED_PLAYER);
        sendBroadcast(preparedIntent);
    }

    @Override
    public void onDestroy() {
        mState = State.Stopped;
        stopForeground(true);
        releaseMediaPlayer();
    }

    //-----------Process requests----------
    public void processTogglePlaybackRequest() {
        if (mState == State.Paused || mState == State.Stopped) {
            processPlayRequest();
        } else {
            processPauseRequest();
        }
    }

    public void processPlayRequest() {
//        if (mState == State.Retrieving) {
//            mStartPlayingAfterRetrieve = true;
//            return;
//        }
        if (mState == State.Stopped) {
            playSongIndex(mCurrentSongIndex);
        } else if (mState == State.Paused) {
            mState = State.Playing;
            setUpAsForeground(mSongTitle);
            if (!mPlayer.isPlaying())
                mPlayer.start();
        }
    }

    public void processPauseRequest() {
//        if (mState == State.Retrieving) {
//            mStartPlayingAfterRetrieve = false;
//            return;
//        }
        if (mState == State.Playing) {
            mState = State.Paused;
            mPlayer.pause();
            //stopForeground(true);
        }
    }

    public void processPlayNextRequest() {
        if (!isShuffle) {
            mCurrentSongIndex++;
            if (mCurrentSongIndex >= MainActivity.songList.size())
                mCurrentSongIndex = 0;
            playSongIndex(mCurrentSongIndex);
        } else {

        }
    }

    public void processPlayPrevRequest() {
        if (!isShuffle) {
            mCurrentSongIndex--;
            if (mCurrentSongIndex < 0)
                mCurrentSongIndex = MainActivity.songList.size() - 1;
            playSongIndex(mCurrentSongIndex);
        } else {

        }
    }
    //-------------------------------------

    public void playSongIndex(int index) {
        resetMediaPlayer();
        mState = State.Stopped;
        Song playingSong = MainActivity.musicRetriever.getSong(index);
        if (playingSong == null) {
            Toast.makeText(getApplicationContext(), "Can't play song", Toast.LENGTH_SHORT).show();
            stopPlayer(true);
            return;
        }
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mPlayer.setDataSource(getApplicationContext(), playingSong.getURI());
            mSongTitle = playingSong.getTitle();
            mArtist = playingSong.getArtist();
            mAlbum = playingSong.getAlbum();
            mAlbumId = playingSong.getAlbumId();
            mState = State.Preparing;
            setUpAsForeground(mSongTitle);
            mPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopPlayer(boolean force) {
        if (mState == State.Playing || mState == State.Paused || force) {
            mState = State.Stopped;
            stopForeground(true);
            releaseMediaPlayer();
            stopSelf();
        }

    }

    public void updateNotification(String text) {
        PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0,
                new Intent(getApplicationContext(), MainActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT);
        mNotificationBuilder.setContentText(text)
                .setContentIntent(pi);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mNotificationManager.notify(NOTIFICATION_ID, mNotificationBuilder.build());
        }
    }

    public void setUpAsForeground(String text) {
        PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0,
                new Intent(getApplicationContext(), MainActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT);

        // Build the notification object.
        mNotificationBuilder = new Notification.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.ic)
                .setTicker(text)
                .setWhen(System.currentTimeMillis())
                .setContentTitle("Pplayer")
                .setContentText(text)
                .setContentIntent(pi)
                .setOngoing(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            startForeground(NOTIFICATION_ID, mNotificationBuilder.build());
        }
    }

    public void releaseMediaPlayer() {
        if (mPlayer != null) {
            mPlayer.reset();
            mPlayer.release();
            mPlayer = null;
        }
    }

    public static String getState() {
        return mState.toString();
    }

    public static int getDuration() {
        return duration;
    }
}
