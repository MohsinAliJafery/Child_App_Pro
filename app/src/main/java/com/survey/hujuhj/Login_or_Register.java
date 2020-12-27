package com.survey.hujuhj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.survey.hujuhj.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login_or_Register extends AppCompatActivity {

    private Button Login, Register;
    FirebaseUser mFirebaseUser;
    TextView mUseInviteCode;


    @Override
    protected void onStart() {
        super.onStart();

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mReference = FirebaseDatabase.getInstance().getReference("Users");

        if(mFirebaseUser!= null){
        mReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot mSnapshot: snapshot.getChildren()){
                    User user = mSnapshot.getValue(User.class);
                    if(user.getHasChild().equals("1")){
                        Intent mIntent = new Intent(Login_or_Register.this, MainActivity.class);
                        startActivity(mIntent);
                        finish();
                    }else{
                        Intent mIntent = new Intent(Login_or_Register.this, ConnectKidsPhone.class);
                        startActivity(mIntent);
                        finish();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        }else{
            SharedPreferences sharedpreferences = getSharedPreferences("ChildApp", Context.MODE_PRIVATE);
            String mParent = sharedpreferences.getString("parent", null);
            if(mParent != null){
                Intent mIntent = new Intent(Login_or_Register.this, MainActivity.class);
                startActivity(mIntent);
                finish();
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_or__register);

        Login = findViewById(R.id.Login);
        Register = findViewById(R.id.Register);
        mUseInviteCode = findViewById(R.id.use_invite_code);


        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login_or_Register.this, LoginActivity.class));

            }
        });

        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login_or_Register.this, RegisterActivity.class));

            }
        });


        mUseInviteCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login_or_Register.this, UseInviteCode.class));
            }
        });

    }
}
