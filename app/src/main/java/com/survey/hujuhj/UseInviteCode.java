package com.survey.hujuhj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.ContentLoadingProgressBar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class UseInviteCode extends AppCompatActivity {

    FloatingActionButton mBackFloatingButton;
    private ContentLoadingProgressBar mProgressBar;
    EditText mName, mCode;
    Button mRegister;
    boolean mCodeOk;
    String mHashMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_use_invite_code);

        mName = findViewById(R.id.name);
        mCode = findViewById(R.id.code);
        mRegister = findViewById(R.id.register);
        mProgressBar = findViewById(R.id.progress_bar);

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mRegister.setVisibility(View.GONE);
                mProgressBar.setVisibility(View.VISIBLE);

                String mCodeString = mCode.getText().toString();

                FirebaseUser mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                DatabaseReference mChildsReference = FirebaseDatabase.getInstance().getReference("codes");


                mChildsReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot mSnapshot: snapshot.getChildren()){
                            if(mCodeString.equals(mSnapshot.child("code").getValue(String.class))){
                                mCodeOk = true;
                                mHashMap =  mSnapshot.child("ID").getValue(String.class);
                                mChildsReference.child(mHashMap).child("state").setValue("on");

                                DatabaseReference mUsersReference = FirebaseDatabase.getInstance().getReference("Users");
                                String mUserId = mUsersReference.push().getKey();

                                HashMap<String, String> hashMap = new HashMap<>();
                                hashMap.put("ID", mUserId);
                                hashMap.put("ParentID", mHashMap);
                                hashMap.put("Username", mName.getText().toString().toLowerCase());
                                hashMap.put("hasChild", "");
                                hashMap.put("ImageUrl", "default");
                                hashMap.put("Status", "offline");

                                SharedPreferences sharedpreferences = getSharedPreferences("ChildApp", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedpreferences.edit();
                                editor.putString("childId", mUserId);
                                editor.apply();
                                Toast.makeText(UseInviteCode.this, ""+mUserId, Toast.LENGTH_SHORT).show();

                                mUsersReference.child(mUserId).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Intent intent = new Intent(UseInviteCode.this, MainActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            SharedPreferences sharedpreferences = getSharedPreferences("ChildApp", Context.MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sharedpreferences.edit();
                                            editor.putString("parent", mHashMap);
                                            editor.putString("childId", mUserId);
                                            editor.putBoolean("areYouParent", true);
                                            editor.apply();

                                            // Gives error
//                                            DatabaseReference mUpdatedParents = FirebaseDatabase.getInstance().getReference("Users");
//                                            mUpdatedParents.child(mHashMap).child("hasChild").setValue("1").addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                @Override
//                                                public void onComplete(@NonNull Task<Void> task) {
//                                                    Toast.makeText(UseInviteCode.this, "Parents Updated!", Toast.LENGTH_SHORT).show();
//
//
//
//                                                }
//                                            });



                                        }else{
                                            mRegister.setVisibility(View.VISIBLE);
                                            mProgressBar.setVisibility(View.GONE);
                                        }
                                    }
                                });
                            }else{
                                mRegister.setVisibility(View.VISIBLE);
                                mProgressBar.setVisibility(View.GONE);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });


        mBackFloatingButton = findViewById(R.id.back_floating_button);

        mBackFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }
}
