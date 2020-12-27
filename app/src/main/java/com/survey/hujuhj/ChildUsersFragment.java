package com.survey.hujuhj;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.survey.hujuhj.Adapter.UserAdapter;
import com.survey.hujuhj.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class ChildUsersFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private UserAdapter mUserAdapter;
    private List<User> mUser;
    Toolbar mToolbar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_child_users, container, false);

        mRecyclerView = view.findViewById(R.id.MyRecyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mToolbar = view.findViewById(R.id.toolbar);

        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//

            }
        });


        mUser = new ArrayList<>();
        ReadUsers();





        return view;

    }

    private void ReadUsers() {

        final FirebaseUser mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mReference = FirebaseDatabase.getInstance().getReference("Users");

        SharedPreferences sharedpreferences = getActivity().getSharedPreferences("ChildApp", Context.MODE_PRIVATE);
        boolean mAreYouParent = sharedpreferences.getBoolean("areYouParent", false);
        String mParent = sharedpreferences.getString("parent", null);

        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                mUser.clear();

                for (DataSnapshot mSnapshot : snapshot.getChildren()) {
                    User user = mSnapshot.getValue(User.class);

                    if (user.getID().equals(mParent)) {
                        mUser.add(user);
                    }

                }

                mUserAdapter = new UserAdapter(getContext(), mUser, false);
                mRecyclerView.setAdapter(mUserAdapter);

            }




            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }
}
