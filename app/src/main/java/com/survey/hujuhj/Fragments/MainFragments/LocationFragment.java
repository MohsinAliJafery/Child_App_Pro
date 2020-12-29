package com.survey.hujuhj.Fragments.MainFragments;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.firebase.iid.FirebaseInstanceId;
import com.survey.hujuhj.HistoryActivity;
import com.survey.hujuhj.KeywordActivity;
import com.survey.hujuhj.Model.GeoFences;
import com.survey.hujuhj.Model.User;
import com.survey.hujuhj.Model.location;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.survey.hujuhj.GeofenceHelper;
import com.survey.hujuhj.MapsActivity;
import com.survey.hujuhj.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.survey.hujuhj.SendNotificationPack.Token;
import com.survey.hujuhj.ServiceToCheckTheChildsLocation;
import com.survey.hujuhj.SignalActivity;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static android.content.Context.JOB_SCHEDULER_SERVICE;
import static android.content.Context.LOCATION_SERVICE;

public class LocationFragment extends Fragment implements OnMapReadyCallback, View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    private static final int GPS_REQUEST_CODE = 9003;
    private static final String TAG = "Location";
    private boolean LocationPermissionGranted;
    public static final int PERMISSION_REQUEST_CODE = 404;
    public static final int GOOGLE_PLAY_REQUEST_CODE = 405;
    public static final String P_TAG = "PermissionTag";
    String[] PermissionList = {Manifest.permission.ACCESS_FINE_LOCATION};
    private FusedLocationProviderClient mLocationClient;
    public GoogleMap mGoogleMap;
    FloatingActionButton Message, ChangeMapType;
    ConstraintLayout SelectMapType;

    public static DrawerLayout mNavigationDrawer;
    private NavigationView mNavigationView;

    View mcView;

    private LocationCallback mLocationCallback;

    ImageView mHybrid, mSatellite, mMap;
    FirebaseAuth Auth;
    DatabaseReference DBaseReference;
    private GeofenceHelper geofenceHelper;
    private GeofencingClient geofencingClient;
    String currentDate;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.navigation_drawer_layout, container, false);
        SharedPreferences sharedpreferences = getActivity().getSharedPreferences("ChildApp", Context.MODE_PRIVATE);
        boolean mParent = sharedpreferences.getBoolean("areYouParent", false);

        Toolbar mToolbar = view.findViewById(R.id.navigation_search_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);

        SelectMapType = view.findViewById(R.id.select_map_type);
        Message = view.findViewById(R.id.current_location);
        ChangeMapType = view.findViewById(R.id.change_map_type);
        Message.setOnClickListener(this);
        ChangeMapType.setOnClickListener(this);

        geofencingClient = LocationServices.getGeofencingClient(getActivity());
        geofenceHelper = new GeofenceHelper(getActivity());
        mLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());





//                        String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
//
//                        Log.d(TAG, "onDataChange: time"+ currentTime );

//                        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
//                        Date d = new Date();
//                        String dayOfTheWeek = sdf.format(d);
//
//                        Log.d(TAG, "onDataChange: "+ dayOfTheWeek);


        SharedPreferences mSharedpreferences = getActivity().getSharedPreferences("ChildApp", Context.MODE_PRIVATE);
        boolean parent = sharedpreferences.getBoolean("areYouParent", false);
        String mParentId = sharedpreferences.getString("parent", null);
        String mChildId = sharedpreferences.getString("childId", null);


        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                Location location = locationResult.getLastLocation();

                Toast.makeText(getContext(), location.getLatitude() + "\n" + location.getLongitude(), Toast.LENGTH_SHORT).show();

                gotoLocation(location.getLatitude(), location.getLongitude());
                updateChildsLocation(location.getLatitude(), location.getLongitude());
                showMarker(location.getLatitude(), location.getLongitude());



                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("History").child(mParentId);
                    currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

                    String mPushKey = mDatabase.child(currentDate).push().getKey();

                    HashMap<String, Double> hashMap = new HashMap<>();
                    hashMap.put("lat", location.getLatitude());
                    hashMap.put("lng", location.getLongitude());

                    mDatabase.child(currentDate).child(mPushKey).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.d(TAG, "onComplete: History has been updated!");
                        }
                    });

                    Log.d("locationsChild", location.getLatitude() + "\n" + location.getLongitude());

                }

        };

        if (parent) {
            DBaseReference = FirebaseDatabase.getInstance().getReference("GeoFences").child(mParentId);
            DBaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot mSnapshot : snapshot.getChildren()) {
                        GeoFences mGeoFences = mSnapshot.getValue(GeoFences.class);


                        double lat = Double.parseDouble(mGeoFences.getLat());
                        double lng = Double.parseDouble(mGeoFences.getLng());
                        String ID = mGeoFences.getID();
                        String name = mGeoFences.getLocationName();
                        LatLng mLatlng = new LatLng(lat, lng);
                        float radius = 30;

                        List<Geofence> mGeofenceList = new ArrayList<Geofence>();
//                        Geofence mGeofence = geofenceHelper.getGeofence(ID, mLatlng, radius, Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_DWELL | Geofence.GEOFENCE_TRANSITION_EXIT);

                        addGeofence(ID, mLatlng, radius, name);


                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        } else {


        }


        mcView = ChangeMapType;

        mNavigationDrawer = view.findViewById(R.id.drawer_layout);
        mNavigationView = view.findViewById(R.id.navigation_view);

        InitilizeGoogleMap();
        SupportMapFragment SupportMapFrag = SupportMapFragment.newInstance();

        getActivity().getSupportFragmentManager().beginTransaction()
                .addToBackStack("maps").add(R.id.GoogleMaps, SupportMapFrag).commit();
        SupportMapFrag.setRetainInstance(true);
        SupportMapFrag.getMapAsync((OnMapReadyCallback) this);



        ActionBarDrawerToggle mToggle = new ActionBarDrawerToggle(getActivity(), mNavigationDrawer, mToolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );

        mNavigationDrawer.addDrawerListener(mToggle);
        mToggle.syncState();


        mNavigationView.setNavigationItemSelectedListener(this);
        return view;
    }




    private void addGeofence(String ID, LatLng latLng, float radius, String mName) {

        Geofence geofence = geofenceHelper.getGeofence(ID, latLng, radius, Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_DWELL | Geofence.GEOFENCE_TRANSITION_EXIT);
        GeofencingRequest geofencingRequest = geofenceHelper.getGeofencingRequest(geofence);
        PendingIntent pendingIntent = geofenceHelper.getPendingIntent();

        geofencingClient.addGeofences(geofencingRequest, pendingIntent)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: Geofence Added...");


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


    private void updateChildsLocation(double lat, double lng) {
        SharedPreferences sharedpreferences = getActivity().getSharedPreferences("ChildApp", Context.MODE_PRIVATE);
        boolean mParent = sharedpreferences.getBoolean("areYouParent", false);
        String mChildsId = sharedpreferences.getString("childId", "");

        DatabaseReference mReferForParentId = FirebaseDatabase.getInstance().getReference("Users");

        mReferForParentId.child(mChildsId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                String mCurrentParentId = user.getParentID();

                DatabaseReference mReference = FirebaseDatabase.getInstance().getReference("ChildsLocations");

                HashMap<String, Double> hashMap = new HashMap<>();
                hashMap.put("lat", lat);
                hashMap.put("lng", lng);

                mReference.child(mCurrentParentId).child("location").setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
//                        Toast.makeText(getContext(), "Location Updated!", Toast.LENGTH_SHORT).show(); gives error
                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        mGoogleMap = googleMap;
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
        mGoogleMap.getUiSettings().setCompassEnabled(true);

        SharedPreferences sharedpreferences = getActivity().getSharedPreferences("ChildApp", Context.MODE_PRIVATE);
        boolean mParent = sharedpreferences.getBoolean("areYouParent", false);
        Toast.makeText(getActivity(), ""+mParent, Toast.LENGTH_SHORT).show();
        if(!mParent){
//            getGeoLocationFromLatLng();
        }else{
            getLocationUpdates();
        }


    }


    private boolean isGPSEnabled(){
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);

        boolean providerEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if(providerEnabled){
            return true;
        }else{

            AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                    .setTitle("GPS Permissions")
                    .setMessage("GPS is required for this app to work. Please enable GPS.")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(intent, GPS_REQUEST_CODE);
                    }).setCancelable(false).show();

        }
    return false;
    }


    private void gotoLocation(double lat, double lng){

        LatLng mLatLng = new LatLng(lat, lng);
        CameraUpdate mCameraUpdate = CameraUpdateFactory.newLatLngZoom(mLatLng, 15);
        mGoogleMap.animateCamera(mCameraUpdate);

    }

    private void InitilizeGoogleMap() {
        if(IsServicesOk()) {
            if (isGPSEnabled()) {
                if (CheckLocationPermission()) {
                    Toast.makeText(getActivity(), "Ready to Map", Toast.LENGTH_SHORT).show();
                } else {
                    requestLocationPermission();
                }
            }
        }
    }

    private boolean CheckLocationPermission() {
        return ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private Boolean IsServicesOk() {
        GoogleApiAvailability GoogleApi = GoogleApiAvailability.getInstance();

        int result = GoogleApi.isGooglePlayServicesAvailable(getActivity());
        if(result == ConnectionResult.SUCCESS){
            return true;
        }else if(GoogleApi.isUserResolvableError(result)) {
            Dialog GooglePlayDialog = GoogleApi.getErrorDialog(getActivity(), result, GOOGLE_PLAY_REQUEST_CODE, null);
        }else{
            Toast.makeText(getActivity(), "Play Services are required!", Toast.LENGTH_SHORT);
        }
        return false;
    }
    private void requestLocationPermission() {
        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
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

    @Override
    public void onClick(View view) {

//        View LocationFragmentLayout = view.findViewById(R.id.location_fragment_layout);
//        ((ViewGroup)LocationFragmentLayout.getParent()).removeView(LocationFragmentLayout);

        switch (view.getId()){
            case R.id.current_location:
                SharedPreferences sharedpreferences = getActivity().getSharedPreferences("ChildApp", Context.MODE_PRIVATE);
                boolean mParent = sharedpreferences.getBoolean("areYouParent", false);
                String mChildID = sharedpreferences.getString("childId", null);
                Toast.makeText(getActivity(), ""+mParent, Toast.LENGTH_SHORT).show();
                if(!mParent){
                   getGeoLocationFromLatLng();

                 }else{
                    getLocationUpdates();

                }

                break;

            case R.id.change_map_type:

                LayoutInflater layoutInflater = getActivity().getLayoutInflater();
                final View popupView = layoutInflater.inflate(R.layout.popup, null);
//                final PopupWindow popupWindow = new PopupWindow(
//                        popupView,
//                        LinearLayout.LayoutParams.WRAP_CONTENT,
//                        LinearLayout.LayoutParams.WRAP_CONTENT, true);

                int mWidth = 400;
                int mHeight = 500;
                final PopupWindow popupWindow = new PopupWindow(popupView, mWidth,LinearLayout.LayoutParams.WRAP_CONTENT, true);

                popupWindow.setOutsideTouchable(true);
                popupWindow.setFocusable(true);

                popupWindow.setBackgroundDrawable(new BitmapDrawable());

                View parent = view.getRootView();

                popupWindow.showAtLocation(mcView, Gravity.NO_GRAVITY, (int) (mcView.getPivotX()+250),
                        mcView.getBottom()-20);

                mHybrid = popupView.findViewById(R.id.hybrid);
                mSatellite = popupView.findViewById(R.id.satellite);
                mMap = popupView.findViewById(R.id.map);

                mSatellite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                        popupWindow.dismiss();
                    }
                });

                mHybrid.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                        popupWindow.dismiss();
                    }
                });


                mMap.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        popupWindow.dismiss();
                    }
                });

                break;

        }

    }

    private void getGeoLocationFromLatLng() {

        DatabaseReference mReference = FirebaseDatabase.getInstance().getReference("ChildsLocations");
        FirebaseUser mParentId = FirebaseAuth.getInstance().getCurrentUser();
        String mPID = mParentId.getUid();
        UpdateToken(mPID);
        mReference.child(mParentId.getUid()).child("location").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                location mlocation = snapshot.getValue(location.class);
                double lat = mlocation.getLat();
                double lng = mlocation.getLng();


                Geocoder mGeocoder = new Geocoder(getActivity(), Locale.getDefault());

                try {
                    List<Address> addressList = mGeocoder.getFromLocation(lat, lng, 3);
                    if(addressList.size() > 0){
                        gotoLocation(lat, lng);
                        showMarker(lat, lng);
                    }
                } catch (IOException e) {

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void getCurrentLocation() {
        mLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if(task.isSuccessful()){
                    Location location = task.getResult();
                    gotoLocation(location.getLatitude(), location.getLongitude());
                }else{
                    Log.d("getCurrentLocation", "Error"+task.getException().getMessage());
                }
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch (item.getItemId()){

            case R.id.nav_location:
                createALocationBottomSheetDialog();
                break;

            case R.id.nav_signal:
                intent = new Intent(getActivity(), SignalActivity.class);
                startActivity(intent);
                 break;

            case R.id.nav_history:
                intent = new Intent(getActivity(), HistoryActivity.class);
                startActivity(intent);
                break;

            case R.id.nav_keywordSms:
                intent = new Intent(getActivity(), KeywordActivity.class);
                startActivity(intent);
                break;
        }

        mNavigationDrawer.closeDrawer(GravityCompat.START);

        return true;
    }

    void createALocationBottomSheetDialog() {

        BottomSheetDialog mBottomSheetDialog;
        mBottomSheetDialog = new BottomSheetDialog(getActivity(), R.style.BottomSheetDialogTheme);

        View mBottomSheetView = LayoutInflater.from(getActivity()).inflate(R.layout.create_a_location_layout, getActivity().findViewById(R.id.bottomSheetContainer));

        //BottomSheetBehavior.from(mBottomSheetView).setState(BottomSheetBehavior.STATE_EXPANDED);

        mBottomSheetDialog.setContentView(mBottomSheetView);
        mBottomSheetDialog.show();


        Button CreateALocation = mBottomSheetView.findViewById(R.id.create_a_location);
        CreateALocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MapsActivity.class);
                startActivity(intent);
                mBottomSheetDialog.dismiss();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GPS_REQUEST_CODE){
            if(isGPSEnabled()){
                Toast.makeText(getActivity(), "GPS is enabled", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getActivity(), "GPS not enabled!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showMarker(double lat, double lng){
        MarkerOptions markerOptions = new MarkerOptions()
                .title("here")
                .position(new LatLng(lat, lng));
        mGoogleMap.addMarker(markerOptions);
    }

    private void getLocationUpdates(){
        SharedPreferences sharedpreferences = getActivity().getSharedPreferences("ChildApp", Context.MODE_PRIVATE);
        boolean mParent = sharedpreferences.getBoolean("areYouParent", false);
        String mChildID = sharedpreferences.getString("childId", null);

        UpdateToken(mChildID);
        LocationRequest locationRequest = LocationRequest.create();

        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY );
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);

        mLocationClient.requestLocationUpdates(locationRequest, mLocationCallback, Looper.getMainLooper());
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mLocationCallback!= null){
            mLocationClient.removeLocationUpdates(mLocationCallback);
        }
    }

    private void UpdateToken(String mID){
//      FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        String refreshToken= FirebaseInstanceId.getInstance().getToken();
        Token token= new Token(refreshToken);
        FirebaseDatabase.getInstance().getReference("Tokens").child(mID).setValue(token);
    }
}
