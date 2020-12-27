package com.survey.hujuhj;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.PendingIntent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private static final String TAG = "MapsActivity";

    private GoogleMap mMap;
    private GeofencingClient geofencingClient;
    private GeofenceHelper geofenceHelper;

    private float GEOFENCE_RADIUS = 100;
    private String GEOFENCE_ID = "SOME_GEOFENCE_ID";

    LatLng mLatng;

    private int FINE_LOCATION_ACCESS_REQUEST_CODE = 10001;
    private int BACKGROUND_LOCATION_ACCESS_REQUEST_CODE = 10002;

    FirebaseAuth Auth;
    DatabaseReference DBaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Auth = FirebaseAuth.getInstance();


        geofencingClient = LocationServices.getGeofencingClient(this);
        geofenceHelper = new GeofenceHelper(this);


    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng eiffel = new LatLng(48.8589, 2.29365);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(eiffel, 16));

        enableUserLocation();

        mMap.setOnMapLongClickListener(this);
    }

    private void enableUserLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            //Ask for permission
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                //We need to show user a dialog for displaying why the permission is needed and then ask for the permission...
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_ACCESS_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_ACCESS_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == FINE_LOCATION_ACCESS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //We have the permission
                mMap.setMyLocationEnabled(true);
            } else {
                //We do not have the permission..
            }
        }

        if (requestCode == BACKGROUND_LOCATION_ACCESS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //We have the permission
                Toast.makeText(this, "You can add geofences...", Toast.LENGTH_SHORT).show();
            } else {
                //We do not have the permission..
                Toast.makeText(this, "Background location access is neccessary for geofences to trigger...", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        if (Build.VERSION.SDK_INT >= 29) {
            //We need background permission
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                handleMapLongClick(latLng);
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                    //We show a dialog and ask for permission
                    ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_ACCESS_REQUEST_CODE);
                } else {
                    ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_ACCESS_REQUEST_CODE);
                }
            }

        } else {
            handleMapLongClick(latLng);
        }

    }

    private void handleMapLongClick(LatLng latLng) {
        mMap.clear();
        createALocationBottomSheetDialog();
        mLatng = latLng;
        addMarker(mLatng);
        addCircle(mLatng, GEOFENCE_RADIUS);
//        addMarker(latLng);
//        addCircle(latLng, GEOFENCE_RADIUS);
//        addGeofence(latLng, GEOFENCE_RADIUS);
    }

    private void addGeofence(LatLng latLng, float radius, HashMap<String, String> mLocationDetails) {

        Geofence geofence = geofenceHelper.getGeofence(GEOFENCE_ID, latLng, radius, Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_DWELL | Geofence.GEOFENCE_TRANSITION_EXIT);
        GeofencingRequest geofencingRequest = geofenceHelper.getGeofencingRequest(geofence);

        PendingIntent pendingIntent = geofenceHelper.getPendingIntent();
        geofencingClient.addGeofences(geofencingRequest, pendingIntent)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: Geofence Added...");

                        SharedPreferences sharedpreferences = getSharedPreferences("ChildApp", Context.MODE_PRIVATE);
                        boolean mParent = sharedpreferences.getBoolean("areYouParent", false);
                        String mParentId = sharedpreferences.getString("parent", null);

                        if(mParent){
                            DBaseReference = FirebaseDatabase.getInstance().getReference("GeoFences").child(mParentId);
                        }else{
                            FirebaseUser mFirebaseUser = Auth.getCurrentUser();
                            String UserID = mFirebaseUser.getUid();
                            DBaseReference = FirebaseDatabase.getInstance().getReference("GeoFences").child(UserID);
                        }


                        String pushID = DBaseReference.push().getKey();

//                        HashMap<String, String> hashMap = new HashMap<>();
                        mLocationDetails.put("lat", String.valueOf(latLng.latitude));
                        mLocationDetails.put("lng", String.valueOf(latLng.longitude));
                        mLocationDetails.put("ID", pushID);


                        DBaseReference.child(pushID).setValue(mLocationDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.d(TAG, "onComplete: Firebase LatLng Updated!");

                            }
                        });

                        // Setting the name of the location



                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String errorMessage = geofenceHelper.getErrorString(e);
                        Log.d(TAG, "onFailure: " + errorMessage);
                    }
                });
    }

    private void addMarker(LatLng latLng) {
        MarkerOptions markerOptions = new MarkerOptions().position(latLng);
        mMap.addMarker(markerOptions);
    }

    private void addCircle(LatLng latLng, float radius) {
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(latLng);
        circleOptions.radius(radius);
        circleOptions.strokeColor(Color.argb(255, 255, 0,0));
        circleOptions.fillColor(Color.argb(64, 255, 0,0));
        circleOptions.strokeWidth(4);
        mMap.addCircle(circleOptions);
    }

    void createALocationBottomSheetDialog() {

        BottomSheetDialog mBottomSheetDialog;
        mBottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);

        View mBottomSheetView = LayoutInflater.from(this).inflate(R.layout.name_the_location_you_want_to_create, this.findViewById(R.id.bottomSheetContainer));

        //BottomSheetBehavior.from(mBottomSheetView).setState(BottomSheetBehavior.STATE_EXPANDED);

        mBottomSheetDialog.setContentView(mBottomSheetView);
        mBottomSheetDialog.show();

        Button mCreateALocation = mBottomSheetDialog.findViewById(R.id.create_a_location);
        EditText mNameOfLocation = mBottomSheetDialog.findViewById(R.id.location_name);
        EditText mStartTime = mBottomSheetDialog.findViewById(R.id.start_time);
        EditText mEndTime = mBottomSheetDialog.findViewById(R.id.end_time);




        HashMap<String, String> hashMap = new HashMap<>();



        mCreateALocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox mMonday = mBottomSheetDialog.findViewById(R.id.monday);
                CheckBox mTuesday = mBottomSheetDialog.findViewById(R.id.tuesday);
                CheckBox mWednesday = mBottomSheetDialog.findViewById(R.id.wednesday);
                CheckBox mThursday = mBottomSheetDialog.findViewById(R.id.thursday);
                CheckBox mFriday = mBottomSheetDialog.findViewById(R.id.friday);
                CheckBox mSaturday = mBottomSheetDialog.findViewById(R.id.saturday);
                CheckBox mSunday = mBottomSheetDialog.findViewById(R.id.sunday);

                String mName = mNameOfLocation.getText().toString();
                String mStart = mStartTime.getText().toString();
                String mEnd = mEndTime.getText().toString();

                if(mName.isEmpty()){

                    Toast.makeText(MapsActivity.this, "Please Specify Name Of The Location", Toast.LENGTH_SHORT).show();

                }else{

                    if(mStart.isEmpty() && mEnd.isEmpty() && !mMonday.isChecked() && !mTuesday.isChecked() && !mWednesday.isChecked()
                    && !mThursday.isChecked() && !mFriday.isChecked() && !mSaturday.isChecked() && !mSunday.isChecked()){

                        hashMap.put("locationName", mName);
                        hashMap.put("startTime", mStart);
                        hashMap.put("endTime", mEnd);

                        hashMap.put("monday", "");
                        hashMap.put("tuesday", "");

                        hashMap.put("wednesday", "");
                        hashMap.put("thursday", "");

                        hashMap.put("friday", "");
                        hashMap.put("saturday", "");
                        hashMap.put("sunday", "");

                        Toast.makeText(MapsActivity.this, mName+": Created Successfully!", Toast.LENGTH_SHORT).show();
                        addGeofence(mLatng, GEOFENCE_RADIUS, hashMap);
                        mBottomSheetDialog.dismiss();

                    }else if(!mStart.isEmpty() && mEnd.isEmpty() && !mMonday.isChecked() && !mTuesday.isChecked() && !mWednesday.isChecked()
                            && !mThursday.isChecked() && !mFriday.isChecked() && !mSaturday.isChecked() && !mSunday.isChecked()){

                           Toast.makeText(MapsActivity.this, "The End Time is Empty", Toast.LENGTH_SHORT).show();

                    }else if(mStart.isEmpty() && !mEnd.isEmpty() && !mMonday.isChecked() && !mTuesday.isChecked() && !mWednesday.isChecked()
                            && !mThursday.isChecked() && !mFriday.isChecked() && !mSaturday.isChecked() && !mSunday.isChecked()){
                           Toast.makeText(MapsActivity.this, "The Start Time is Empty", Toast.LENGTH_SHORT).show();

                    }else if(!mStart.isEmpty() && !mEnd.isEmpty() && !mMonday.isChecked() && !mTuesday.isChecked() && !mWednesday.isChecked()
                            && !mThursday.isChecked() && !mFriday.isChecked() && !mSaturday.isChecked() && !mSunday.isChecked()){

                        hashMap.put("locationName", mName);
                        hashMap.put("startTime", mStart);
                        hashMap.put("endTime", mEnd);

                        hashMap.put("monday", "");
                        hashMap.put("tuesday", "");

                        hashMap.put("wednesday", "");
                        hashMap.put("thursday", "");

                        hashMap.put("friday", "");
                        hashMap.put("saturday", "");
                        hashMap.put("sunday", "");

                        Toast.makeText(MapsActivity.this, mName+": Created Successfully!", Toast.LENGTH_SHORT).show();
                        addGeofence(mLatng, GEOFENCE_RADIUS, hashMap);
                        mBottomSheetDialog.dismiss();
                    }else{

                        String monday, tuesday, wednesday, thursday, friday, saturday, sunday;

                        if(mMonday.isChecked()){
                            monday = "yes";
                        }else{
                            monday = "noe";
                        }

                        if(mTuesday.isChecked()){
                            tuesday = "yes";
                        }else{
                            tuesday = "no";
                        }

                        if(mWednesday.isChecked()){
                            wednesday = "yes";
                        }else{
                            wednesday = "no";
                        }

                        if(mThursday.isChecked()){
                            thursday = "yes";
                        }else{
                            thursday = "no";
                        }

                        if(mFriday.isChecked()){
                            friday = "yes";
                        }else{
                            friday = "no";
                        }

                        if(mSaturday.isChecked()){
                            saturday = "yes";
                        }else{
                            saturday = "no";
                        }

                        if(mSunday.isChecked()){
                            sunday = "yes";
                        }else{
                            sunday = "no";
                        }

                        hashMap.put("locationName", mName);
                        hashMap.put("startTime", mStart);
                        hashMap.put("endTime", mEnd);

                        hashMap.put("monday", monday);
                        hashMap.put("tuesday", tuesday);

                        hashMap.put("wednesday", wednesday);
                        hashMap.put("thursday", thursday);

                        hashMap.put("friday", friday);
                        hashMap.put("saturday", saturday);
                        hashMap.put("sunday", sunday);

                        Toast.makeText(MapsActivity.this, mName+": Created Successfully!", Toast.LENGTH_SHORT).show();
                        addGeofence(mLatng, GEOFENCE_RADIUS, hashMap);
                        mBottomSheetDialog.dismiss();

                    }
                }
            }
        });


    }
}