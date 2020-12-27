package com.survey.hujuhj;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.survey.hujuhj.Adapter.MessageAdapter;
import com.survey.hujuhj.Fragments.ApiServices;
import com.survey.hujuhj.Model.Chat;
import com.survey.hujuhj.Model.User;
//import com.childcare33.hujuhj.Notifications.Client;
//import com.childcare33.hujuhj.Notifications.Data;
//import com.childcare33.hujuhj.Notifications.MyResponse;
//import com.childcare33.hujuhj.Notifications.Sender;
//import com.childcare33.hujuhj.Notifications.Token;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageActivity extends AppCompatActivity {

    CircleImageView ProfileImage;
    TextView Username;

    Intent mIntent;

    Button SendMessage;
    EditText TypeAMessage;

    MessageAdapter mMessageAdapter;
    List<Chat> mChat;

    RecyclerView mRecyclerView;

    FirebaseUser mFirebaseUser;
    DatabaseReference mDatabaseReference;

    ValueEventListener mSeenListener;
    private String UserId;

    ApiServices mApiService;

    boolean notify = false;

    @SuppressLint("WrongViewCast")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Toolbar mToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(MessageActivity.this, ChatActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
//            }
//        });

//        mApiService = Client.getClient("https://fom.googleapis.com/").create(ApiServices.class);

        SendMessage = findViewById(R.id.send_message);
        TypeAMessage = findViewById(R.id.type_a_message);

        ProfileImage = findViewById(R.id.profile_image);
        Username = findViewById(R.id.Username);

        mIntent = getIntent();
        
        UserId = mIntent.getStringExtra("UserId");

        mRecyclerView = findViewById(R.id.recyclerview_message_activity);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLinearLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        Log.d("Tag", UserId+";");

        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(UserId);

        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                Username.setText(user.getUsername());
                if(user.getImageUrl().equals("default")){
                    ProfileImage.setImageResource(R.mipmap.ic_launcher);
                }else{
                    Glide.with(getApplicationContext()).load(user.getImageUrl()).into(ProfileImage);
                }

                readMessage(mFirebaseUser.getUid(), UserId, user.getImageUrl());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        seenMessage(UserId);

        SendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify = true;
                String typeAMessage = TypeAMessage.getText().toString();
                if(!typeAMessage.equals("")){
                    sendMessage(mFirebaseUser.getUid(), UserId, typeAMessage);
                }else{
                    Toast.makeText(MessageActivity.this, "You can't send empty messages", Toast.LENGTH_SHORT).show();
                }
                TypeAMessage.setText("");
            }
        });




    }

    private void seenMessage(final String UserID){
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Chats");
        mSeenListener = mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot mSnapshot: snapshot.getChildren()){
                    Chat chat = mSnapshot.getValue(Chat.class);
                    if(chat.getReceiver().equals(mFirebaseUser.getUid()) && chat.getSender().equals(UserID)){

                        HashMap<String, Object> mHashmap = new HashMap<>();
                        mHashmap.put("seen", true);
                        mSnapshot.getRef().updateChildren(mHashmap);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void sendMessage(String Sender, final String Receiver, String Message){

        DatabaseReference mReference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> mHashmap = new HashMap<>();
        mHashmap.put("sender", Sender);
        mHashmap.put("receiver", Receiver);
        mHashmap.put("message", Message);
        mHashmap.put("seen", false);

        mReference.child("Chats").push().setValue(mHashmap);

        final DatabaseReference ChatReference = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(mFirebaseUser.getUid()).child(UserId);

        ChatReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    ChatReference.child("id").setValue(UserId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        final String msg = Message;

        mReference = FirebaseDatabase.getInstance().getReference("Users").child(mFirebaseUser.getUid());

        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
               if(notify){
//                   sendNotification(Receiver, user.getUsername(), msg);
               }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }

//    private void sendNotification(final String receiver, final String username, final String msg) {
//        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
//        Query mQuery = tokens.orderByKey().equalTo(receiver);
//
//        mQuery.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for(DataSnapshot mSnapshot: snapshot.getChildren()){
//                    Token token = mSnapshot.getValue(Token.class);
//
//                    Data mData = new Data(mFirebaseUser.getUid(), R.mipmap.ic_launcher, username+": "+msg, "New Message", UserId);
//
//                    Sender mSender = new Sender(mData, token.getToken());
//
//                    mApiService.sendNotification(mSender)
//                            .enqueue(new Callback<MyResponse>() {
//                                @Override
//                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
//                                    if(response.code() == 200){
//                                        if(response.body().success != 1){
//                                            Toast.makeText(MessageActivity.this, "Failed", Toast.LENGTH_SHORT).show();
//                                        }
//                                    }
//                                }
//
//                                @Override
//                                public void onFailure(Call<MyResponse> call, Throwable t) {
//
//                                }
//                            });
//
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }
//

    private void readMessage(final String MyID, final String UserID, final String ImageUrl){

        mChat = new ArrayList<>();

        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Chats");
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mChat.clear();
                for(DataSnapshot mSnapshot: snapshot.getChildren()){
                    Chat chat = mSnapshot.getValue(Chat.class);
                    if(chat.getReceiver().equals(MyID) && chat.getSender().equals(UserID)
                    || chat.getReceiver().equals(UserID) && chat.getSender().equals(MyID)){
                        mChat.add(chat);
                    }

                    mMessageAdapter = new MessageAdapter(MessageActivity.this, mChat, ImageUrl);
                    mRecyclerView.setAdapter(mMessageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void status(String status){

        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(mFirebaseUser.getUid());
        HashMap<String, Object> mHashmap = new HashMap<>();
        mHashmap.put("Status", status);
        mDatabaseReference.updateChildren(mHashmap);

    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        mDatabaseReference.removeEventListener(mSeenListener);
        status("offline");
    }
}
