package com.example.protik.helpcircle;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class PanicActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnMyLocationButtonClickListener, LocationListener {

    private FirebaseFirestore mFireStore;
    private GoogleMap mMap;
    private Location myLocation;
    private boolean locationSaved = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panic);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.panic_fragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        } else {
            Toast.makeText(this, "Null map fragment", Toast.LENGTH_SHORT).show();
        }
        mFireStore = FirebaseFirestore.getInstance();
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500, 10, this);
        }
    }

    private void saveUserLocation(){
        locationSaved = true;
        UserLocationModel model = new UserLocationModel(myLocation.getLatitude(), myLocation.getLongitude());
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            mFireStore.collection("users_location").document(mAuth.getCurrentUser().getUid())
                    .set(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(PanicActivity.this, "Your location is sent to your friends for help", Toast.LENGTH_SHORT).show();
                    }else {
                        locationSaved = false;
                        Toast.makeText(PanicActivity.this, "Couldn't sent location for help", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else {
            Toast.makeText(this, "Couldn't detect user!", Toast.LENGTH_SHORT).show();
        }
    }

    private void moveCamera(){
        CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()));
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(11);
        mMap.clear();

        MarkerOptions mp = new MarkerOptions();

        mp.position(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()));

        mp.title("my position");

        mMap.addMarker(mp);
        mMap.moveCamera(center);
        mMap.animateCamera(zoom);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            myLocation = mMap.getMyLocation();
            if (myLocation != null) {
                moveCamera();
                if (!locationSaved) {
                    saveUserLocation();
                }
            }else {
                Toast.makeText(this, "Null location", Toast.LENGTH_SHORT).show();
            }
//            mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
//                @Override
//                public void onMyLocationChange(Location location) {
//                    CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
//                    CameraUpdate zoom = CameraUpdateFactory.zoomTo(11);
//                    mMap.clear();
//                    myLocation = location;
//
//                    MarkerOptions mp = new MarkerOptions();
//
//                    mp.position(new LatLng(location.getLatitude(), location.getLongitude()));
//
//                    mp.title("my position");
//
//                    mMap.addMarker(mp);
//                    mMap.moveCamera(center);
//                    mMap.animateCamera(zoom);
//                }
//            });

            //mMap.setOnMyLocationButtonClickListener(this);
        } else {
            checkLocationPermission();
        }
    }

    public void checkLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {

            // Show an explanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.
            new AlertDialog.Builder(this)
                    .setTitle("Location permission")
                    .setMessage("Permission is needed to get your current location")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //Prompt the user once explanation has been shown
                            ActivityCompat.requestPermissions(PanicActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    999);
                        }
                    })
                    .create()
                    .show();


        } else {
            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    999);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case 999: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    startActivity(new Intent(this, MapActivity.class));

                } else {
                    Toast.makeText(this, "Permission denied! Couldn't get your location", Toast.LENGTH_SHORT).show();
                }
            }

        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    @Override
    public void onLocationChanged(Location location) {
        myLocation = location;
        if (!locationSaved) {
            saveUserLocation();
        }
        moveCamera();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
