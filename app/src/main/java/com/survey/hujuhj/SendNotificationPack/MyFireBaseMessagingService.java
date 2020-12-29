package com.survey.hujuhj.SendNotificationPack;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.survey.hujuhj.Fragments.UsersFragment;
import com.survey.hujuhj.MainActivity;
import com.survey.hujuhj.MessageActivity;
import com.survey.hujuhj.R;
import com.survey.hujuhj.SignalActivity;
import com.survey.hujuhj.SosAlarmService;


public class MyFireBaseMessagingService extends FirebaseMessagingService {

    String title,message, location;
    Intent intent;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
            super.onMessageReceived(remoteMessage);
            title=remoteMessage.getData().get("Title");
            message=remoteMessage.getData().get("Message");
            location = remoteMessage.getData().get("Location");

            if("SOS".equals(title)){

                Intent intent;
                intent = new Intent(MyFireBaseMessagingService.this, SosAlarmService.class);
                startService(intent);

            }else if("Enter".equals(title)){

                title = "Your kid has reached " + location;
                intent = new Intent(MyFireBaseMessagingService.this, MainActivity.class);

                PendingIntent mPendingIntent = PendingIntent.getActivity(this, 102, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                NotificationCompat.Builder builder =
                        new NotificationCompat.Builder(getApplicationContext())
                                .setSmallIcon(R.drawable.ic_baseline_transit_enterexit_24)
                                .setContentTitle(title)
                                .setContentText(message)
                                .setContentIntent(mPendingIntent);
                NotificationManager manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                manager.notify(0, builder.build());



            }else if("Exit".equals(title)){
                title = "Your Exited "+ location;

                intent = new Intent(MyFireBaseMessagingService.this, MainActivity.class);

                PendingIntent mPendingIntent = PendingIntent.getActivity(this, 102, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                NotificationCompat.Builder builder =
                        new NotificationCompat.Builder(getApplicationContext())
                                .setSmallIcon(R.drawable.ic_baseline_transit_enterexit_24)
                                .setContentTitle(title)
                                .setContentText(message)
                                .setContentIntent(mPendingIntent);
                NotificationManager manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                manager.notify(0, builder.build());


            }
            else{
                NotificationCompat.Builder builder =
                        new NotificationCompat.Builder(getApplicationContext())
                                .setSmallIcon(R.drawable.ic_child_care_gray_24dp)
                                .setContentTitle(title)
                                .setContentText(message);
                NotificationManager manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                manager.notify(0, builder.build());
            }


    }

}
