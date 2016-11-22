package com.androidsample.musicplayer;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by Keval on 22-Nov-16.
 *
 * @author {@link 'https://github.com/kevalpatel2106'}
 */

public class MusicPlayerService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }


}
