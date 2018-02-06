package com.example.hvpri.packagetracker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    String[] PERMISSIONS = {android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION};
    int PERMISSION_ALL = 1;

    Button AdminDriver;
    Button Customer;
    TextView Status;

    FirebaseDatabase database;
    GeoFire ownerGeoFireObject;


    private FusedLocationProviderClient mFusedLocationClient;
    private Location mLastKnownLocation;
    LocationRequest mLocationRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getLocationPermission();

        database = FirebaseDatabase.getInstance();

        ownerGeoFireObject = new GeoFire(database.getReference("/testexample"));

        AdminDriver = (Button) findViewById(R.id.AdminDriver);
        Customer = (Button) findViewById(R.id.Customer);
        Status = (TextView)findViewById(R.id.statustext);

        final Intent maps = new Intent(this, MapsActivity.class);

        AdminDriver.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View view) {
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                Status.setText("Location Casting");
            }
        });

        Customer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(maps);
            }
        });


        //Location
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(2000);
        mLocationRequest.setFastestInterval(2000);

    }

    // ALL THE PERMISSION CRAP ---------------------------------------------------------------------//

    private void getLocationPermission() {
        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (requestCode == PERMISSION_ALL) {
            if (grantResults.length == 3) {
                // We can now safely use the API we requested access to
//                Log.d(TAG, "onRequestPermissionsResult: if condition");
                //startActivity(intent);

            } else {
                // Permission was denied or request was cancelled
//                Log.d(TAG, "onRequestPermissionsResult: else");
            }
        }
    }
// ALL THE PERMISSION CRAP ---------------------------------------------------------------------//

    LocationCallback mLocationCallback = new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {
                mLastKnownLocation=location;
                ownerGeoFireObject.setLocation("Location", new GeoLocation(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()));
            }
        }
    };
}
