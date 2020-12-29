package com.survey.hujuhj;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.survey.hujuhj.SendNotificationPack.APIService;
import com.survey.hujuhj.SendNotificationPack.Client;
import com.survey.hujuhj.SendNotificationPack.Data;
import com.survey.hujuhj.SendNotificationPack.MyResponse;
import com.survey.hujuhj.SendNotificationPack.NotificationSender;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InformYourParents extends AppCompatActivity {

    TextView mQuestion;
    EditText mLocation;
    Button mInform;
    private APIService apiService;
    String mTrigger;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inform_your_parents);

        mQuestion = findViewById(R.id.name);
        mLocation = findViewById(R.id.location);
        mInform = findViewById(R.id.inform);
        
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);



        Intent intent = getIntent();
        String WhatsUp = intent.getStringExtra("whatsUpOnYourMind");
        String userToken = intent.getStringExtra("userToken");

        Toast.makeText(this, ""+userToken, Toast.LENGTH_SHORT).show();
        String mTrigger;

        String mName = mLocation.getText().toString();

        if(WhatsUp.equals("Enter")){

            mQuestion.setText("Where have you reached?");
            mTrigger = "Enter";

        }else{

            mQuestion.setText("Where are you going?");
            mTrigger = "Exit";

        }

        mInform.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              
                sendNotificationsOverFirebase(InformYourParents.this, userToken, mTrigger, "Tap to send a caring response!", mName);
                Intent intent = new Intent(InformYourParents.this, ChildUsersFragment.class);
                startActivity(intent);
            }
        });


    }

    public void sendNotificationsOverFirebase(Context mContext, String usertoken, String title, String message, String location) {
        Data data = new Data(title, message, location);
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