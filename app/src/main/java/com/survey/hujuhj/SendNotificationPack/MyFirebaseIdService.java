package com.survey.hujuhj.SendNotificationPack;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;

public class MyFirebaseIdService extends FirebaseMessagingService {

    @Override
    public void onNewToken(String s)
    {
        super.onNewToken(s);

        SharedPreferences sharedpreferences = getSharedPreferences("ChildApp", Context.MODE_PRIVATE);
        boolean mParent = sharedpreferences.getBoolean("areYouParent", false);
        String mParentId = sharedpreferences.getString("parent", null);
        String mChildID = sharedpreferences.getString("childId", null);

        if(mParent){
            UpdateToken(mChildID);
        }else {
            FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
            if(firebaseUser!=null){
                UpdateToken(mParentId);
            }
        }


    }


    private void UpdateToken(String mID){
        String refreshToken= FirebaseInstanceId.getInstance().getToken();
        Token token= new Token(refreshToken);
        FirebaseDatabase.getInstance().getReference("Tokens").child(mID).setValue(token);
    }

}
