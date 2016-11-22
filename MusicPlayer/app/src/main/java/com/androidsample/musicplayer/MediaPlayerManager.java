package com.androidsample.musicplayer;

import android.content.Context;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;

import java.io.IOException;

/**
 * Created by Keval on 22-Nov-16.
 *
 * @author {@link 'https://github.com/kevalpatel2106'}
 */

public class MediaPlayerManager {
    private static MediaPlayer mMediaPlayer;
    private static Uri mCurrentMusicUri;
    private static boolean isPlaying;

    private static int mPauseDuration = 0;

    public static void startNewTrack(Context context, Uri uri) throws IOException {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
        }

        mCurrentMusicUri = uri;

        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setDataSource(context, uri);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.prepare();
        mMediaPlayer.start();
        isPlaying = true;
    }

    public static void startPlaying(){
        if (mMediaPlayer == null || mCurrentMusicUri == null){
            throw new RuntimeException("Start player.");
        }

        mMediaPlayer.seekTo(mPauseDuration);
        mMediaPlayer.start();
        isPlaying = true;
    }

    public static boolean isInitialized(){
        return mMediaPlayer != null;
    }

    public static void pauseMusic() {
        if (mMediaPlayer == null) return;

        mPauseDuration = mMediaPlayer.getCurrentPosition();
        mMediaPlayer.pause();
        isPlaying = false;
    }

    public static void stopMusic() {
        mPauseDuration = 0;
        mCurrentMusicUri = null;
        if (mMediaPlayer == null) return;

        mMediaPlayer.stop();
        mMediaPlayer.release();
        mMediaPlayer = null;

        isPlaying = false;
    }

    public static boolean isPlaying() {
        return isInitialized() && isPlaying;
    }
}
