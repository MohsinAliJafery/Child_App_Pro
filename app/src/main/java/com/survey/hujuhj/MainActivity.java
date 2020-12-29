package com.survey.hujuhj;

import android.Manifest;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.survey.hujuhj.Fragments.MainFragments.EventsFragment;
import com.survey.hujuhj.Fragments.MainFragments.LocationFragment;
import com.survey.hujuhj.Fragments.MainFragments.MicrophoneFragment;
import com.survey.hujuhj.Fragments.UsersFragment;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity{


    private boolean LocationPermissionGranted;
    public static final int PERMISSION_REQUEST_CODE = 404;
    public static final int GOOGLE_PLAY_REQUEST_CODE = 405;
    public static final String P_TAG = "PermissionTag";
    String [] PermissionList = {Manifest.permission.ACCESS_FINE_LOCATION};
    public GoogleMap mGoogleMap;
    Fragment SelectedFragment = null;
    private DrawerLayout mNavigationDrawer;
    boolean areYouFromChat;
    int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mNavigationDrawer = findViewById(R.id.drawer_layout);
//
//        Intent intent = getIntent();
//
//            String mFrom = intent.getExtras().getString("mFrom");
//            Intent mServiceintent = new Intent(this, SosAlarmService.class);
//
//            if ("SOS".equals(mFrom)) {
//                stopService(mServiceintent);
//
//            }
        Intent mServiceintent = new Intent(this, SosAlarmService.class);
        stopService(mServiceintent);
        BottomNavigationView mBottomNavigation = findViewById(R.id.bottom_navigation);
        mBottomNavigation.setOnNavigationItemSelectedListener(mBottomNavigationListener);
        mBottomNavigation.setSelectedItemId(R.id.location);

        CheckForChildsLocation();

        LayoutInflater layoutInflater
                = (LayoutInflater) getBaseContext()
                .getSystemService(LAYOUT_INFLATER_SERVICE);

    }

        private BottomNavigationView.OnNavigationItemSelectedListener mBottomNavigationListener =
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                        switch (item.getItemId()){
                            case R.id.location:
                                SelectedFragment = new LocationFragment();
                                break;

                            case R.id.events:
                                SelectedFragment = new EventsFragment();
                                break;

                            case R.id.microphone:
                                SelectedFragment = new MicrophoneFragment();
                                break;

                            case R.id.chat:
                                SharedPreferences sharedpreferences = getSharedPreferences("ChildApp", Context.MODE_PRIVATE);
                                boolean mParent = sharedpreferences.getBoolean("areYouParent", false);
                                if(mParent){
                                    SelectedFragment = new ChildUsersFragment();
                                    break;
                                }else{
                                    SelectedFragment = new UsersFragment();
                                    break;
                                }
                        }

                        if(areYouFromChat) {
                            count = 1;
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragments_container, SelectedFragment).addToBackStack(String.valueOf(SelectedFragment)).commit();
                        }else{
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragments_container, SelectedFragment).commit();

                        }
                        return true;

                    }
                };
    public void CheckForChildsLocation(){
        JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);

        ComponentName mComponent = new ComponentName(this, ServiceToCheckTheChildsLocation.class);

        JobInfo jobInfo = new JobInfo.Builder(101, mComponent)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPeriodic(5000)
                .build();

        int result = jobScheduler.schedule(jobInfo);
        if(result == JobScheduler.RESULT_SUCCESS){
            Log.d("myTag", "CheckForChildsLocation: Success");
        }else{
            Log.d("myTag", "CheckForChildsLocation: Failure");
        }
    }
    @Override
    public void onBackPressed() {

        LocationFragment mf = new LocationFragment();

        if(mf.mNavigationDrawer.isDrawerOpen(GravityCompat.START)){
            mf.mNavigationDrawer.closeDrawer(GravityCompat.START);
        }else if(count == 0) {
            super.onBackPressed();
            //additional code
        }else{
            getSupportFragmentManager().popBackStack();
            count = 0;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.geofence:
                Toast.makeText(getApplicationContext(),"Item 1 Selected",Toast.LENGTH_LONG).show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
