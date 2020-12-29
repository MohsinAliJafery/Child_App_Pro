package com.survey.hujuhj;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.survey.hujuhj.Model.location;

public class MapViewOfHistory extends AppCompatActivity implements OnMapReadyCallback{
    private static final int GPS_REQUEST_CODE = 9003;
    private static final String TAG = "Location";
    private boolean LocationPermissionGranted;
    public static final int PERMISSION_REQUEST_CODE = 404;
    public static final int GOOGLE_PLAY_REQUEST_CODE = 405;
    public static final String P_TAG = "PermissionTag";
    String[] PermissionList = {Manifest.permission.ACCESS_FINE_LOCATION};
    public GoogleMap mGoogleMap;

    String mDate;

    FirebaseUser mUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view_of_history);


        InitilizeGoogleMap();
        SupportMapFragment SupportMapFrag = SupportMapFragment.newInstance();

        getSupportFragmentManager().beginTransaction()
                .add(R.id.history_google_maps, SupportMapFrag).commit();
        SupportMapFrag.setRetainInstance(true);
        SupportMapFrag.getMapAsync((OnMapReadyCallback) this);



        Intent intent = getIntent();
        mDate = intent.getStringExtra("mFrom");




    }
    private void gotoLocation(double lat, double lng){

        LatLng mLatLng = new LatLng(lat, lng);
        CameraUpdate mCameraUpdate = CameraUpdateFactory.newLatLngZoom(mLatLng, 15);
        mGoogleMap.animateCamera(mCameraUpdate);

    }
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mGoogleMap = googleMap;
//        if (ActivityCompat.checkSelfPermission(MapViewOfHistory.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
        mGoogleMap.getUiSettings().setCompassEnabled(true);

        SharedPreferences mSharedpreferences = getSharedPreferences("ChildApp", Context.MODE_PRIVATE);
        boolean parent = mSharedpreferences.getBoolean("areYouParent", false);
        String mParentId = mSharedpreferences.getString("parent", null);
        String mChildId = mSharedpreferences.getString("childId", null);
        DatabaseReference mDatabase;
        if(parent){
            mDatabase = FirebaseDatabase.getInstance().getReference("History").child(mParentId);

        }else{
            mUser = FirebaseAuth.getInstance().getCurrentUser();
            String UserId = mUser.getUid();
            mDatabase = FirebaseDatabase.getInstance().getReference("History").child(UserId);
        }

        mDatabase.child(mDate).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot mSnap: snapshot.getChildren()){
                    location mLocation  = mSnap.getValue(location.class);
                    double lat = mLocation.getLat();
                    double lng = mLocation.getLng();

                    showMarker(lat, lng);
                    gotoLocation(lat, lng);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private boolean isGPSEnabled(){
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        boolean providerEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if(providerEnabled){
            return true;
        }else{

            AlertDialog alertDialog = new AlertDialog.Builder(MapViewOfHistory.this)
                    .setTitle("GPS Permissions")
                    .setMessage("GPS is required for this app to work. Please enable GPS.")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(intent, GPS_REQUEST_CODE);
                    }).setCancelable(false).show();

        }
        return false;
    }

    private void InitilizeGoogleMap() {
        if(IsServicesOk()) {
            if (isGPSEnabled()) {
                if (CheckLocationPermission()) {
                    Toast.makeText(MapViewOfHistory.this, "Ready to Map", Toast.LENGTH_SHORT).show();
                } else {
                    requestLocationPermission();
                }
            }
        }
    }

    private boolean CheckLocationPermission() {
        return ContextCompat.checkSelfPermission(MapViewOfHistory.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private Boolean IsServicesOk() {
        GoogleApiAvailability GoogleApi = GoogleApiAvailability.getInstance();

        int result = GoogleApi.isGooglePlayServicesAvailable(MapViewOfHistory.this);
        if(result == ConnectionResult.SUCCESS){
            return true;
        }else if(GoogleApi.isUserResolvableError(result)) {
            Dialog GooglePlayDialog = GoogleApi.getErrorDialog(MapViewOfHistory.this, result, GOOGLE_PLAY_REQUEST_CODE, null);
        }else{
            Toast.makeText(MapViewOfHistory.this, "Play Services are required!", Toast.LENGTH_SHORT);
        }
        return false;
    }
    private void requestLocationPermission() {
        if(ContextCompat.checkSelfPermission(MapViewOfHistory.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(PermissionList, PERMISSION_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == PERMISSION_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            LocationPermissionGranted = true;
            Log.d(P_TAG, "Permission has been Granted");
        }else{
            Log.d(P_TAG, "Permission has not been Granted");
        }
    }

    private void showMarker(double lat, double lng){
        MarkerOptions markerOptions = new MarkerOptions()
                .title("here")
                .position(new LatLng(lat, lng));
        mGoogleMap.addMarker(markerOptions);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GPS_REQUEST_CODE){
            if(isGPSEnabled()){
                Toast.makeText(MapViewOfHistory.this, "GPS is enabled", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(MapViewOfHistory.this, "GPS not enabled!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}