package com.survey.hujuhj;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.text.NoCopySpan;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class SosAlarmService extends Service {

    private MediaPlayer mMediaPlayer;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mMediaPlayer.start();
        showNotification("SOS Alert!", "Hurry! Get in Touch with him");
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mMediaPlayer = MediaPlayer.create(this, R.raw.best_rington_in_2012);


        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mMediaPlayer.start();
            }
        });





    }

    @Override
    public void onDestroy() {
        mMediaPlayer.release();
        super.onDestroy();
    }


    private void showNotification(String title, String message) {
        Intent intent;
        intent = new Intent(this, MainActivity.class );
        intent.putExtra("mFrom", "SOS");
        PendingIntent mPendingIntent = PendingIntent.getActivity(this, 101, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(R.drawable.ic_send_plane)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setContentIntent(mPendingIntent);
//        NotificationManager manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
//        manager.notify(0, builder.build());

        Notification mNotification = builder.build();
        startForeground(123, mNotification);
    }
}
