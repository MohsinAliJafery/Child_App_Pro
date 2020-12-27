package com.survey.hujuhj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignalActivity extends AppCompatActivity {

    Button SOS;
    private APIService apiService;
    DatabaseReference mDBReferenceForToken;
    String userToken;
    FirebaseAuth Auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signal);

        Toolbar mToolbar = findViewById(R.id.navigation_search_toolbar);
        setSupportActionBar(mToolbar);
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        SOS = findViewById(R.id.sos_button);
        Auth = FirebaseAuth.getInstance();

        SharedPreferences sharedpreferences = getSharedPreferences("ChildApp", Context.MODE_PRIVATE);
        boolean mParent = sharedpreferences.getBoolean("areYouParent", false);
        String mParentId = sharedpreferences.getString("parent", null);
        String mChildID = sharedpreferences.getString("childId", null);

        if(mParent){
            mDBReferenceForToken = FirebaseDatabase.getInstance().getReference().child("Tokens").child(mParentId).child("token");

        }else{

            mDBReferenceForToken = FirebaseDatabase.getInstance().getReference().child("Tokens").child(mChildID).child("token");
        }

        mDBReferenceForToken.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userToken=dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        SOS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                sendNotificationsOverFirebase(SignalActivity.this, userToken,"SOS", "Get in touch with your kid immediately");



            }
        });


    }

    public void sendNotificationsOverFirebase(Context mContext, String usertoken, String title, String message) {
        Data data = new Data(title, message);
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
}