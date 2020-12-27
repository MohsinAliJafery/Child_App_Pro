package com.survey.hujuhj;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.survey.hujuhj.Adapter.ViewPagerAdapter;
import com.survey.hujuhj.Fragments.ChatsFragment;
import com.survey.hujuhj.Fragments.ProfileFragment;
import com.survey.hujuhj.Fragments.UsersFragment;
import com.survey.hujuhj.Model.User;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class  ChatActivity extends AppCompatActivity {


    CircleImageView ProfileImage;
    TextView Username;

    FirebaseUser mFirebaseUser;
    DatabaseReference mReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        ProfileImage = findViewById(R.id.profile_image);
        Username = findViewById(R.id.Username);

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mReference = FirebaseDatabase.getInstance().getReference().child("Users").child(mFirebaseUser.getUid());

        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                User mUser = snapshot.getValue(User.class);
                Username.setText(mUser.getUsername());

                if (mUser.getImageUrl().equals("default")) {
                    ProfileImage.setImageResource(R.mipmap.ic_launcher);
                }else{
                    Glide.with(getApplicationContext()).load(mUser.getImageUrl()).into(ProfileImage);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        TabLayout mTabLayout = findViewById(R.id.TabLayouts);
        ViewPager mViewPager = findViewById(R.id.ViewPagers);

        mViewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));

        mTabLayout.setupWithViewPager(mViewPager);

        ViewPagerAdapter mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        mViewPagerAdapter.addFragment(new ChatsFragment(), "Chats");
        mViewPagerAdapter.addFragment(new UsersFragment(), "Users");
        mViewPagerAdapter.addFragment(new ProfileFragment(), "Profile");

        mViewPager.setAdapter(mViewPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.Logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(ChatActivity.this, Login_or_Register.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                return true;

        }
        return false;
    }

    private void status(String status){

        mReference = FirebaseDatabase.getInstance().getReference("Users").child(mFirebaseUser.getUid());
        HashMap<String, Object> mHashmap = new HashMap<>();
        mHashmap.put("Status", status);
        mReference.updateChildren(mHashmap);

    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
    }


}