package com.survey.hujuhj.SendNotificationPack;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.survey.hujuhj.MainActivity;
import com.survey.hujuhj.R;
import com.survey.hujuhj.SignalActivity;
import com.survey.hujuhj.SosAlarmService;


public class MyFireBaseMessagingService extends FirebaseMessagingService {
    String title,message;
    Intent intent;
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
            super.onMessageReceived(remoteMessage);
            title=remoteMessage.getData().get("Title");
            message=remoteMessage.getData().get("Message");



            if("SOS".equals(title)){

                Intent intent;
                intent = new Intent(MyFireBaseMessagingService.this, SosAlarmService.class);
                startService(intent);

            }else{
                NotificationCompat.Builder builder =
                        new NotificationCompat.Builder(getApplicationContext())
                                .setSmallIcon(R.drawable.ic_send_plane)
                                .setContentTitle(title)
                                .setContentText(message);
                NotificationManager manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                manager.notify(0, builder.build());
            }


    }

}
