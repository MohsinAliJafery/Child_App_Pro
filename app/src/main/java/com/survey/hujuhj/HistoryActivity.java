package com.survey.hujuhj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.survey.hujuhj.Adapter.HistoryAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class HistoryActivity extends AppCompatActivity implements HistoryAdapter.ItemClickListener{


    private static final String TAG = "KISS";
    DatabaseReference mDBReference;
    HistoryAdapter adapter;
    List<String> myList;
    Map<String, String> SpecifiedDates;

    FirebaseUser mUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

       myList = new ArrayList<>();

        SpecifiedDates = new HashMap<String, String>();

        SharedPreferences mSharedpreferences = getSharedPreferences("ChildApp", Context.MODE_PRIVATE);
        boolean parent = mSharedpreferences.getBoolean("areYouParent", false);
        String mParentId = mSharedpreferences.getString("parent", null);
        String mChildId = mSharedpreferences.getString("childId", null);


        if(parent){
            mDBReference = FirebaseDatabase.getInstance().getReference("History").child(mParentId);

        }else{
            mUser = FirebaseAuth.getInstance().getCurrentUser();
            String UserId = mUser.getUid();
            mDBReference = FirebaseDatabase.getInstance().getReference("History").child(UserId);
        }


        mDBReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot mSnap: snapshot.getChildren()){
                    String mkey = mSnap.getKey();
                    Log.d(TAG, "onDataChange: " + mkey);
                    myList.add(mkey);
                    populate(myList);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void populate(List<String> myList) {
        RecyclerView recyclerView = findViewById(R.id.history_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new HistoryAdapter(this, myList);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(View view, int position) {

        Intent intent = new Intent(HistoryActivity.this, MapViewOfHistory.class);
        intent.putExtra("mFrom", adapter.getItem(position));
        startActivity(intent);

    }



}