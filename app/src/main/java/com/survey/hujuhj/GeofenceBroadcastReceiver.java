package com.survey.hujuhj;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.iid.FirebaseInstanceId;
import com.survey.hujuhj.Model.GeoFences;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.survey.hujuhj.SendNotificationPack.APIService;
import com.survey.hujuhj.SendNotificationPack.Client;
import com.survey.hujuhj.SendNotificationPack.Data;
import com.survey.hujuhj.SendNotificationPack.MyResponse;
import com.survey.hujuhj.SendNotificationPack.NotificationSender;
import com.survey.hujuhj.SendNotificationPack.Token;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "GeofenceBroadcastReceiv";
    FirebaseAuth Auth;
    DatabaseReference DBaseReference, mDBReferenceForToken;
    private APIService apiService;
    String userToken = null;
    @Override

    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
//        Toast.makeText(context, "Geofence triggered...", Toast.LENGTH_SHORT).show();

        NotificationHelper notificationHelper = new NotificationHelper(context);
        Auth = FirebaseAuth.getInstance();
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        if (geofencingEvent.hasError()) {
            Log.d(TAG, "onReceive: Error receiving geofence event...");
            return;
        }

        List<Geofence> geofenceList = geofencingEvent.getTriggeringGeofences();
        for (Geofence geofence: geofenceList) {
            Log.d(TAG, "onReceive: " + geofence.getRequestId());
        }

        int transitionType = geofencingEvent.getGeofenceTransition();
        Location location;

        location = geofencingEvent.getTriggeringLocation();
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        Geofence geofence = geofenceList.get(0);

        SharedPreferences sharedpreferences = context.getSharedPreferences("ChildApp", Context.MODE_PRIVATE);
        boolean mParent = sharedpreferences.getBoolean("areYouParent", false);
        String mParentId = sharedpreferences.getString("parent", null);
        String mChildID = sharedpreferences.getString("childId", null);

        if(mParent){
            DBaseReference = FirebaseDatabase.getInstance().getReference("GeoFences").child(mParentId);
            mDBReferenceForToken = FirebaseDatabase.getInstance().getReference().child("Tokens").child(mParentId).child("token");
            mDBReferenceForToken.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    userToken=dataSnapshot.getValue(String.class);

                    DBaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot mSnapshot: snapshot.getChildren()){
                    GeoFences mGeofence = mSnapshot.getValue(GeoFences.class);

                    if(geofence.getRequestId().equals(mGeofence.getID())) {

                        String mLocationName, mStart, mEnd, mMonday, mTuesday, mWednesday, mThursday, mFriday,
                                mSaturday, mSunday;

                        mLocationName = mGeofence.getLocationName();
//                        mStart = mGeofence.getStartTime();
//                        mEnd = mGeofence.getEndTime();
//
//                        mMonday = mGeofence.getMonday();
//                        mTuesday = mGeofence.getTuesday();
//                        mWednesday = mGeofence.getWednesday();
//                        mThursday = mGeofence.getThursday();
//                        mFriday = mGeofence.getFriday();
//                        mSaturday = mGeofence.getSaturday();
//                        mSunday = mGeofence.getSunday();
//
//
//                        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
//
//                        Log.d(TAG, "onDataChange: Date "+ currentDate);
//
//                        String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
//
//                        Log.d(TAG, "onDataChange: time"+ currentTime );
//
//                        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
//                        Date d = new Date();
//                        String dayOfTheWeek = sdf.format(d);
//
//                        Log.d(TAG, "onDataChange: "+ dayOfTheWeek);

                        String trigger;

                        switch (transitionType) {
                            case Geofence.GEOFENCE_TRANSITION_ENTER:
                                trigger = "Enter";
                                String mNotifyMessage =  "Your kid is at "+mLocationName+" now";
                                String mParentsNotifyReached = "You Reached "+mLocationName;
                                Toast.makeText(context, mParentsNotifyReached, Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "onReceive: Enter");

                                addNotification(context, mParentsNotifyReached, trigger, userToken);
                                sendNotificationsOverFirebase(context, userToken, mNotifyMessage, "Click to view location!");
                                notificationHelper.sendHighPriorityNotification(mNotifyMessage, "View Location", MapsActivity.class);
                                break;
                            case Geofence.GEOFENCE_TRANSITION_DWELL:
                                trigger = "Dwell";
                                String mNotifyMessageDwell =  "Your kid is roaming in "+mLocationName+" now";
                                Log.d(TAG, "onReceive:  Dwell");

                                sendNotificationsOverFirebase(context, userToken, mNotifyMessageDwell, "Click to view location!");

                                Toast.makeText(context,  mNotifyMessageDwell, Toast.LENGTH_SHORT).show();
                                notificationHelper.sendHighPriorityNotification(mNotifyMessageDwell, "View Location", MapsActivity.class);
                                break;
                            case Geofence.GEOFENCE_TRANSITION_EXIT:
                                trigger = "Exit";
                                String mNotifyMessageDwellLeft =  "Your kid has left "+mLocationName+" now";
                                String mNotifyParentsLeft = "You Left "+ mLocationName;
                                Log.d(TAG,  "onReceive: Exit");
                                addNotification(context, mNotifyParentsLeft, trigger, userToken);

                                sendNotificationsOverFirebase(context, userToken, mNotifyMessageDwellLeft, "Click to view location!");
                                Toast.makeText(context, mNotifyMessageDwellLeft, Toast.LENGTH_SHORT).show();

                                notificationHelper.sendHighPriorityNotification(mNotifyMessageDwellLeft, "View Location", MapsActivity.class);
                                break;
                        }
                    }


                    }

                }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }else{
            FirebaseUser mFirebaseUser = Auth.getCurrentUser();
            String UserID = mFirebaseUser.getUid();
            DBaseReference = FirebaseDatabase.getInstance().getReference("GeoFences").child(UserID);
            mDBReferenceForToken = FirebaseDatabase.getInstance().getReference().child("Tokens").child(UserID).child("token");

            DBaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot mSnapshot: snapshot.getChildren()){
                    GeoFences mGeofence = mSnapshot.getValue(GeoFences.class);

                    if(geofence.getRequestId().equals(mGeofence.getID())) {

                        String mLocationName, mStart, mEnd, mMonday, mTuesday, mWednesday, mThursday, mFriday,
                                mSaturday, mSunday;

                        mLocationName = mGeofence.getLocationName();
//                        mStart = mGeofence.getStartTime();
//                        mEnd = mGeofence.getEndTime();
//
//                        mMonday = mGeofence.getMonday();
//                        mTuesday = mGeofence.getTuesday();
//                        mWednesday = mGeofence.getWednesday();
//                        mThursday = mGeofence.getThursday();
//                        mFriday = mGeofence.getFriday();
//                        mSaturday = mGeofence.getSaturday();
//                        mSunday = mGeofence.getSunday();
//
//
//                        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
//
//                        Log.d(TAG, "onDataChange: Date "+ currentDate);
//
//                        String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
//
//                        Log.d(TAG, "onDataChange: time"+ currentTime );
//
//                        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
//                        Date d = new Date();
//                        String dayOfTheWeek = sdf.format(d);
//
//                        Log.d(TAG, "onDataChange: "+ dayOfTheWeek);

                        String trigger;

                        switch (transitionType) {
                            case Geofence.GEOFENCE_TRANSITION_ENTER:
                                trigger = "Enter";
                                String mNotifyMessage =  "Your kid is in "+mLocationName+" now";
                                String mParentsNotifyReached = "You Reached The"+mLocationName;
                                Toast.makeText(context, mParentsNotifyReached, Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "onReceive: Enter");

                                addNotification(context, mParentsNotifyReached, trigger, userToken);
                                sendNotificationsOverFirebase(context, userToken, mNotifyMessage, "Click to view location!");
                                notificationHelper.sendHighPriorityNotification(mNotifyMessage, "View Location", MapsActivity.class);
                                break;
                            case Geofence.GEOFENCE_TRANSITION_DWELL:
                                trigger = "Dwell";
                                String mNotifyMessageDwell =  "Your kid is roaming in "+mLocationName+" now";
                                Log.d(TAG, "onReceive:  Dwell");

                                sendNotificationsOverFirebase(context, userToken, mNotifyMessageDwell, "Click to view location!");

                                Toast.makeText(context,  mNotifyMessageDwell, Toast.LENGTH_SHORT).show();
                                notificationHelper.sendHighPriorityNotification(mNotifyMessageDwell, "View Location", MapsActivity.class);
                                break;
                            case Geofence.GEOFENCE_TRANSITION_EXIT:
                                trigger = "Exit";
                                String mNotifyMessageDwellLeft =  "Your kid has left "+mLocationName+" now";
                                String mNotifyParentsLeft = "You Left The "+ mLocationName;
                                Log.d(TAG,  "onReceive: Exit");
                                addNotification(context, mNotifyParentsLeft, trigger, userToken);

                                sendNotificationsOverFirebase(context, userToken, mNotifyMessageDwellLeft, "Click to view location!");
                                Toast.makeText(context, mNotifyMessageDwellLeft, Toast.LENGTH_SHORT).show();

                                notificationHelper.sendHighPriorityNotification(mNotifyMessageDwellLeft, "View Location", MapsActivity.class);
                                break;
                        }
                    }


                    }

                }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        }

//            while(userToken == null){
//
//            }

//        DBaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for(DataSnapshot mSnapshot: snapshot.getChildren()){
//                    GeoFences mGeofence = mSnapshot.getValue(GeoFences.class);
//
//                    if(geofence.getRequestId().equals(mGeofence.getID())) {
//
//                        String mLocationName, mStart, mEnd, mMonday, mTuesday, mWednesday, mThursday, mFriday,
//                                mSaturday, mSunday;
//
//                        mLocationName = mGeofence.getLocationName();
//                        mStart = mGeofence.getStartTime();
//                        mEnd = mGeofence.getEndTime();
//
//                        mMonday = mGeofence.getMonday();
//                        mTuesday = mGeofence.getTuesday();
//                        mWednesday = mGeofence.getWednesday();
//                        mThursday = mGeofence.getThursday();
//                        mFriday = mGeofence.getFriday();
//                        mSaturday = mGeofence.getSaturday();
//                        mSunday = mGeofence.getSunday();
//
//
//                        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
//
//                        Log.d(TAG, "onDataChange: Date "+ currentDate);
//
//                        String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
//
//                        Log.d(TAG, "onDataChange: time"+ currentTime );
//
//                        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
//                        Date d = new Date();
//                        String dayOfTheWeek = sdf.format(d);
//
//                        Log.d(TAG, "onDataChange: "+ dayOfTheWeek);
//
//                        String trigger;
//
//                        switch (transitionType) {
//                            case Geofence.GEOFENCE_TRANSITION_ENTER:
//                                trigger = "Enter";
//                                String mNotifyMessage =  "Your kid is in "+mLocationName+" now";
//                                String mParentsNotifyReached = "You Reached The"+mLocationName;
//                                Toast.makeText(context, mParentsNotifyReached, Toast.LENGTH_SHORT).show();
//                                Log.d(TAG, "onReceive: Enter");
//
//                                addNotification(context, mParentsNotifyReached, trigger, userToken);
//                                sendNotificationsOverFirebase(context, userToken, mNotifyMessage, "Click to view location!");
//                                notificationHelper.sendHighPriorityNotification(mNotifyMessage, "View Location", MapsActivity.class);
//                                break;
//                            case Geofence.GEOFENCE_TRANSITION_DWELL:
//                                trigger = "Dwell";
//                                String mNotifyMessageDwell =  "Your kid is roaming in "+mLocationName+" now";
//                                Log.d(TAG, "onReceive:  Dwell");
//
//                                sendNotificationsOverFirebase(context, userToken, mNotifyMessageDwell, "Click to view location!");
//
//                                Toast.makeText(context,  mNotifyMessageDwell, Toast.LENGTH_SHORT).show();
//                                notificationHelper.sendHighPriorityNotification(mNotifyMessageDwell, "View Location", MapsActivity.class);
//                                break;
//                            case Geofence.GEOFENCE_TRANSITION_EXIT:
//                                trigger = "Exit";
//                                String mNotifyMessageDwellLeft =  "Your kid has left "+mLocationName+" now";
//                                String mNotifyParentsLeft = "You Left The "+ mLocationName;
//                                Log.d(TAG,  "onReceive: Exit");
//                                addNotification(context, mNotifyParentsLeft, trigger, userToken);
//
//                                sendNotificationsOverFirebase(context, userToken, mNotifyMessageDwellLeft, "Click to view location!");
//                                Toast.makeText(context, mNotifyMessageDwellLeft, Toast.LENGTH_SHORT).show();
//
//                                notificationHelper.sendHighPriorityNotification(mNotifyMessageDwellLeft, "View Location", MapsActivity.class);
//                                break;
//                        }
//                    }
//
//
//                    }
//
//                }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });


    }

    private void addNotification(Context mContext, String title, String trigger, String userToken) {

        String message = "Inform your parents about this";

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(mContext)
                        .setSmallIcon(R.drawable.ic_child_care_black_24dp)
                        .setContentTitle(title)
                        .setContentText("message");

        Intent notificationIntent = new Intent(mContext, InformYourParents.class);
        notificationIntent.putExtra("whatsUpOnYourMind", trigger);
        notificationIntent.putExtra("userToken", userToken);

        PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        NotificationManager manager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }


    public void sendNotificationsOverFirebase(Context mContext, String usertoken, String title, String message) {
        Data data = new Data(title, message, title);
        NotificationSender sender = new NotificationSender(data, usertoken);
        apiService.sendNotifcation(sender).enqueue(new Callback<MyResponse>() {
            @Override
            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                if (response.code() == 200) {
                    if (response.body().success != 1) {
                        Toast.makeText(mContext, "Failed ", Toast.LENGTH_LONG);
                    }
                }
            }

            @Override
            public void onFailure(Call<MyResponse> call, Throwable t) {

            }
        });
    }
    // For Tokens
    private void UpdateToken(){
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        String refreshToken= FirebaseInstanceId.getInstance().getToken();
        Token token= new Token(refreshToken);
        FirebaseDatabase.getInstance().getReference("Tokens").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(token);
    }

}
