package com.survey.hujuhj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.ContentLoadingProgressBar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.SecureRandom;
import java.util.HashMap;

public class EnterTheCodeInYourKidsGadget extends AppCompatActivity {

    ContentLoadingProgressBar mProgressBar;
    TextView mCodeForKidsGadget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_enter_the_code_in_your_kids_gadget);
        mProgressBar = findViewById(R.id.progress_bar);

        mCodeForKidsGadget = findViewById(R.id.code_for_kids_gadget);

        SecureRandom random = new SecureRandom();
        int num = random.nextInt(100000);
        String formatted = String.format("%05d", num);

        FirebaseUser mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mReference = FirebaseDatabase.getInstance().getReference("codes");

        String mUId = mFirebaseUser.getUid();
        
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("ID", mUId);
        hashMap.put("state", "off");
        hashMap.put("code", formatted);

        mReference.child(mUId).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                mCodeForKidsGadget.setText(""+formatted);
                mProgressBar.setVisibility(View.GONE);
                mCodeForKidsGadget.setVisibility(View.VISIBLE);
            }
        });

        mReference.child(mUId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot mSnapshot: snapshot.getChildren()){
                    if(snapshot.child("state").getValue(String.class).equals("on")){
                        Intent intent = new Intent(EnterTheCodeInYourKidsGadget.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}
