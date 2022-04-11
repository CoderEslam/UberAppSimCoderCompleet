package com.doubleclick.uberappsimcoder.Driver;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.doubleclick.uberappsimcoder.History.HistoryActivity;
import com.doubleclick.uberappsimcoder.MainActivity;
import com.doubleclick.uberappsimcoder.R;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DriverMapsActivity extends FragmentActivity implements OnMapReadyCallback, RoutingListener {

    private GoogleMap mMap;
    Location mLastLocation;
    LocationRequest mLocationRequest;

    private FusedLocationProviderClient mFusedLocationClient;


    private Button mLogout, mSettings, mRideStatus, mHistory;

    private Switch mWorkingSwitch;

    private int status = 0;

    private String customerId = "", destination;
    private LatLng destinationLatLng, pickupLatLng;
    private float rideDistance;

    private Boolean isLoggingOut = false;

    private SupportMapFragment mapFragment;

    private LinearLayout mCustomerInfo;

    private ImageView mCustomerProfileImage;

    private TextView mCustomerName, mCustomerPhone, mCustomerDestination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        polylines = new ArrayList<>();


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



        mCustomerInfo = (LinearLayout) findViewById(R.id.customerInfo);

        mCustomerProfileImage = (ImageView) findViewById(R.id.customerProfileImage);

        mCustomerName = (TextView) findViewById(R.id.customerName);
        mCustomerPhone = (TextView) findViewById(R.id.customerPhone);
        mCustomerDestination = (TextView) findViewById(R.id.customerDestination);

        mWorkingSwitch = (Switch) findViewById(R.id.workingSwitch);
        mWorkingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    connectDriver();
                }else{
                    disconnectDriver();
                }
            }
        });

        mSettings = (Button) findViewById(R.id.settings);
        mLogout = (Button) findViewById(R.id.logout);
        mRideStatus = (Button) findViewById(R.id.rideStatus);
        mHistory = (Button) findViewById(R.id.history);
        mRideStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(status){
                    case 1:
                        status=2;
                        erasePolylines();
                        if(destinationLatLng.latitude!=0.0 && destinationLatLng.longitude!=0.0){
                            getRouteToMarker(destinationLatLng);
                        }
                        mRideStatus.setText("drive completed");

                        break;
                    case 2:
                        recordRide();
                        endRide();
                        break;
                }
            }
        });

        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isLoggingOut = true;

                disconnectDriver();

                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(DriverMapsActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });
        mSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DriverMapsActivity.this, DriverSettingActivity.class);
                startActivity(intent);
                return;
            }
        });
        mHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DriverMapsActivity.this, HistoryActivity.class);
                intent.putExtra("customerOrDriver", "Drivers");
                startActivity(intent);
                return;
            }
        });
        getAssignedCustomer();
    }

    private void getAssignedCustomer(){
        String driverId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference assignedCustomerRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverId).child("customerRequest").child("customerRideId");
        assignedCustomerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    status = 1;
                    customerId = dataSnapshot.getValue().toString();
                    getAssignedCustomerPickupLocation();
                    getAssignedCustomerDestination();
                    getAssignedCustomerInfo();
                }else{
                    endRide();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    Marker pickupMarker;
    private DatabaseReference assignedCustomerPickupLocationRef;
    private ValueEventListener assignedCustomerPickupLocationRefListener;
    private void getAssignedCustomerPickupLocation(){
        assignedCustomerPickupLocationRef = FirebaseDatabase.getInstance().getReference().child("customerRequest").child(customerId).child("l");
        assignedCustomerPickupLocationRefListener = assignedCustomerPickupLocationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && !customerId.equals("")){
                    List<Object> map = (List<Object>) dataSnapshot.getValue();
                    double locationLat = 0;
                    double locationLng = 0;
                    if(map.get(0) != null){
                        locationLat = Double.parseDouble(map.get(0).toString());
                    }
                    if(map.get(1) != null){
                        locationLng = Double.parseDouble(map.get(1).toString());
                    }
                    pickupLatLng = new LatLng(locationLat,locationLng);
                    pickupMarker = mMap.addMarker(new MarkerOptions().position(pickupLatLng).title("pickup location").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_pickup)));
                    getRouteToMarker(pickupLatLng);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void getRouteToMarker(LatLng pickupLatLng) {
        if (pickupLatLng != null && mLastLocation != null){
            Routing routing = new Routing.Builder()
                    .travelMode(AbstractRouting.TravelMode.DRIVING)
                    .withListener(this)
                    .alternativeRoutes(false)
                    .waypoints(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), pickupLatLng)
                    .build();
            routing.execute();
        }
    }

    private void getAssignedCustomerDestination(){
        String driverId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference assignedCustomerRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverId).child("customerRequest");
        assignedCustomerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if(map.get("destination")!=null){
                        destination = map.get("destination").toString();
                        mCustomerDestination.setText("Destination: " + destination);
                    }
                    else{
                        mCustomerDestination.setText("Destination: --");
                    }

                    Double destinationLat = 0.0;
                    Double destinationLng = 0.0;
                    if(map.get("destinationLat") != null){
                        destinationLat = Double.valueOf(map.get("destinationLat").toString());
                    }
                    if(map.get("destinationLng") != null){
                        destinationLng = Double.valueOf(map.get("destinationLng").toString());
                        destinationLatLng = new LatLng(destinationLat, destinationLng);
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


    private void getAssignedCustomerInfo(){
        mCustomerInfo.setVisibility(View.VISIBLE);
        DatabaseReference mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(customerId);
        mCustomerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if(map.get("name")!=null){
                        mCustomerName.setText(map.get("name").toString());
                    }
                    if(map.get("phone")!=null){
                        mCustomerPhone.setText(map.get("phone").toString());
                    }
                    if(map.get("profileImageUrl")!=null){
                        Glide.with(getApplication()).load(map.get("profileImageUrl").toString()).into(mCustomerProfileImage);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


    private void endRide(){
        mRideStatus.setText("picked customer");
        erasePolylines();

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(userId).child("customerRequest");
        driverRef.removeValue();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("customerRequest");
        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(customerId);
        customerId="";
        rideDistance = 0;

        if(pickupMarker != null){
            pickupMarker.remove();
        }
        if (assignedCustomerPickupLocationRefListener != null){
            assignedCustomerPickupLocationRef.removeEventListener(assignedCustomerPickupLocationRefListener);
        }
        mCustomerInfo.setVisibility(View.GONE);
        mCustomerName.setText("");
        mCustomerPhone.setText("");
        mCustomerDestination.setText("Destination: --");
        mCustomerProfileImage.setImageResource(R.mipmap.ic_default_user);
    }

    private void recordRide(){
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(userId).child("history");
        DatabaseReference customerRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(customerId).child("history");
        DatabaseReference historyRef = FirebaseDatabase.getInstance().getReference().child("history");
        String requestId = historyRef.push().getKey();
        driverRef.child(requestId).setValue(true);
        customerRef.child(requestId).setValue(true);

        HashMap map = new HashMap();
        map.put("driver", userId);
        map.put("customer", customerId);
        map.put("rating", 0);
        map.put("timestamp", getCurrentTimestamp());
        map.put("destination", destination);
        map.put("location/from/lat", pickupLatLng.latitude);
        map.put("location/from/lng", pickupLatLng.longitude);
        map.put("location/to/lat", destinationLatLng.latitude);
        map.put("location/to/lng", destinationLatLng.longitude);
        map.put("distance", rideDistance);
        historyRef.child(requestId).updateChildren(map);
    }

    private Long getCurrentTimestamp() {
        Long timestamp = System.currentTimeMillis()/1000;
        return timestamp;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

            }else{
                checkLocationPermission();
            }
        }
    }


    LocationCallback mLocationCallback = new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for(Location location : locationResult.getLocations()){
                if(getApplicationContext()!=null){

                    if(!customerId.equals("") && mLastLocation!=null && location != null){
                        rideDistance += mLastLocation.distanceTo(location)/1000;
                    }
                    mLastLocation = location;


                    LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    DatabaseReference refAvailable = FirebaseDatabase.getInstance().getReference("driversAvailable");
                    DatabaseReference refWorking = FirebaseDatabase.getInstance().getReference("driversWorking");
                    GeoFire geoFireAvailable = new GeoFire(refAvailable);
                    GeoFire geoFireWorking = new GeoFire(refWorking);

                    switch (customerId){
                        case "":
                            geoFireWorking.removeLocation(userId);
                            geoFireAvailable.setLocation(userId, new GeoLocation(location.getLatitude(), location.getLongitude()));
                            break;

                        default:
                            geoFireAvailable.removeLocation(userId);
                            geoFireWorking.setLocation(userId, new GeoLocation(location.getLatitude(), location.getLongitude()));
                            break;
                    }
                }
            }
        }
    };

    private void checkLocationPermission() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(this)
                        .setTitle("give permission")
                        .setMessage("give permission message")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(DriverMapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                            }
                        })
                        .create()
                        .show();
            }
            else{
                ActivityCompat.requestPermissions(DriverMapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode){
            case 1:{
                if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                        mMap.setMyLocationEnabled(true);
                    }
                } else{
                    Toast.makeText(getApplicationContext(), "Please provide the permission", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }





    private void connectDriver(){
        checkLocationPermission();
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
        mMap.setMyLocationEnabled(true);
    }

    private void disconnectDriver(){
        if(mFusedLocationClient != null){
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("driversAvailable");

        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(userId);
    }






    private List<Polyline> polylines;
    private static final int[] COLORS = new int[]{R.color.ripplecoloreffect};
    @Override
    public void onRoutingFailure(RouteException e) {
        if(e != null) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(this, "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onRoutingStart() {
    }
    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {
        if(polylines.size()>0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }

        polylines = new ArrayList<>();
        //add route(s) to the map.
        for (int i = 0; i <route.size(); i++) {

            //In case of more than 5 alternative routes
            int colorIndex = i % COLORS.length;

            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(getResources().getColor(COLORS[colorIndex]));
            polyOptions.width(10 + i * 3);
            polyOptions.addAll(route.get(i).getPoints());
            Polyline polyline = mMap.addPolyline(polyOptions);
            polylines.add(polyline);

            Toast.makeText(getApplicationContext(),"Route "+ (i+1) +": distance - "+ route.get(i).getDistanceValue()+": duration - "+ route.get(i).getDurationValue(),Toast.LENGTH_SHORT).show();
        }

    }
    @Override
    public void onRoutingCancelled() {
    }
    private void erasePolylines(){
        for(Polyline line : polylines){
            line.remove();
        }
        polylines.clear();
    }

}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//        implements OnMapReadyCallback,
//        GoogleApiClient.ConnectionCallbacks,
//        com.google.android.gms.location.LocationListener,
//        GoogleApiClient.OnConnectionFailedListener {
//
//    private GoogleMap mMap;
//    private GoogleApiClient mGoogleApiClient;
//    private Location mLastLocation;
//    private LocationRequest mLocationRequest;
//    private FirebaseAuth mAuth;
//    //    private DatabaseReference db_DriverAvailableRef,db_DriverWorkingRef;
//    private String User_id;
//    private Button LogOut,SettingDiver;
//    private String CustomerId = "";
//    private Marker PickUpCustomerMarker,DriverMarker;
//    private DatabaseReference assignedCustomerPickupLocationRef;
//    private ValueEventListener assignedCustomerPickupLocationRefListener;
//    private Boolean isLogOut = false;
//    private ImageView mCustomerImageView;
//    private TextView mCustomerName, mCustomerPhone,mCustomerDistination;
//    private LinearLayout CustomerInfo;
//    private LatLng latLng ;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_driver_maps);
//
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.Drivermap);
//        mapFragment.getMapAsync(this);
//        User_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        LogOut = findViewById(R.id.logOut);
//        mCustomerImageView = findViewById(R.id.CustomerImageView);
//        mCustomerName = findViewById(R.id.txtCustomerName);
//        mCustomerPhone = findViewById(R.id.txtCustomerPhone);
//        CustomerInfo = findViewById(R.id.CustomerInfo);
//        mCustomerDistination = findViewById(R.id.txtCustomerDistination);
//
//        SettingDiver = findViewById(R.id.Setting);
//
//        LogOut.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                isLogOut = true;
//                RemoveInfromation();
//                DisconnectedDriver();
//                FirebaseAuth.getInstance().signOut();
//                Intent intent = new Intent(DriverMapsActivity.this, MainActivity.class);
//                startActivity(intent);
////                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                finish();
//                return;
//            }
//        });
//        SettingDiver.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(DriverMapsActivity.this, DriverSettingActivity.class);
//                startActivity(intent);
//            }
//        });
//        getAssignedCustomer();
//    }
//    private void getAssignedCustomer() {
//        String driverId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        DatabaseReference assignedCustomerRef = FirebaseDatabase.getInstance().getReference().child("User").child("Drivers")
//                .child(driverId).child("CustomerRequest").child("CustomerRideId");
//        assignedCustomerRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.exists()) {
////                    Map<String,Object> map = (Map<String, Object>) snapshot.getValue();
////                    if (map.get("CustomerRideId") != null){
////                        CustomerId = map.get("CustomerRideId").toString(); //to get Key.
////                        getAssigendCustomerPickUpLocation();
////                    }
//                    CustomerId = snapshot.getValue().toString(); //to get Key
//                    getAssignedCustomerPickUpLocation();
//                    getAssignedCustomerDistination();
//                    getAssignedCustomerInfo();
//                } else {
////                    CustomerId = "";
//                    if (CustomerId == "") {
//                        if (PickUpCustomerMarker != null) {
//                            PickUpCustomerMarker.remove();
//                            CustomerInfo.setVisibility(View.GONE);
//                            mCustomerName.setText("");
//                            mCustomerPhone.setText("");
//                            mCustomerDistination.setText("");
//                            mCustomerImageView.setImageResource(R.drawable.profile);
//                        }
//                        if (assignedCustomerPickupLocationRef != null) {
//                            assignedCustomerPickupLocationRef.removeEventListener(assignedCustomerPickupLocationRefListener);
//                        }
//                    }
//                }
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) { }
//        });
//    }
//
//    private void getAssignedCustomerDistination() {
////        String driverId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        DatabaseReference assignedCustomerRef = FirebaseDatabase.getInstance().getReference().child("User").child("Drivers")
//                .child(User_id).child("CustomerRequest").child("Distination");
//        assignedCustomerRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.exists()) {
//                String destination = snapshot.getValue().toString(); //to get Key
//                    mCustomerDistination.setText("destination : "+destination);
//                } else {
//                    mCustomerDistination.setText("destination : --");
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) { }
//        });
//    }
//
//    private void getAssignedCustomerInfo() {
//        CustomerInfo.setVisibility(View.VISIBLE);
//        DatabaseReference mCustomerDataBase = FirebaseDatabase.getInstance().getReference().child("User").child("Customer").child(CustomerId);
//        mCustomerDataBase.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.exists() && snapshot.getChildrenCount() > 0) {
//                    Map<String, Object> map = (Map<String, Object>) snapshot.getValue();
//                    if (map.get("Name") != null) {
////                        mName = map.get("Name").toString();
//                        mCustomerName.setText("" + map.get("Name").toString());
//                    }
//                    if (map.get("Phone") != null) {
////                        mPhone = map.get("Phone").toString();
//                        mCustomerPhone.setText("" + map.get("Phone").toString());
//                    }
//                    if (map.get("ProfileImage") != null) {
////                        mProfileUri = map.get("ProfileImage").toString();
//                        Glide.with(getApplication()).load(map.get("ProfileImage").toString()).centerCrop().into(mCustomerImageView);
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }
//
//    private void getAssignedCustomerPickUpLocation() {
//        assignedCustomerPickupLocationRef = FirebaseDatabase.getInstance().getReference().child("CustomerRequest").child(CustomerId).child("l");
//        assignedCustomerPickupLocationRefListener = assignedCustomerPickupLocationRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.exists() && !CustomerId.equals("")) {
//                    List<Object> map = (List<Object>) snapshot.getValue();
//                    double LocationLat = 0;
//                    double LocationLng = 0;
//
//                    if (map.get(0) != null) {
//                        LocationLat = Double.parseDouble(map.get(0).toString());
//                    }
//                    if (map.get(1) != null) {
//                        LocationLng = Double.parseDouble(map.get(1).toString());
//                    }
//                    LatLng DriverLatLang = new LatLng(LocationLat, LocationLng);
//                    PickUpCustomerMarker = mMap.addMarker(new MarkerOptions().position(DriverLatLang).title("PickUp Customer Location")
//                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.person)));
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }
//
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        mMap = googleMap;
//        BulidGoogleApiClient();
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
//                &&
//                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
//        mMap.setMyLocationEnabled(true);
//        mMap.setPadding(50, 100, 50, 100);
//    }
//
//    @Override
//    public void onLocationChanged(final Location location) {
//        mLastLocation = location;
//        latLng = new LatLng(location.getLatitude(), location.getLongitude());
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
//
//
////        Thread thread = new Thread(new Runnable() {
////            @Override
////            public void run() {
//        if (location != null) {
//            if (!isLogOut) {
//                UpDateLocationDrivers();
//            }
//        }
////            }
////        });
////        thread.run();
//
//    }
//
//    private void UpDateLocationDrivers() {
//        DatabaseReference db_DriverAvailableRef = FirebaseDatabase.getInstance().getReference("DriverAvailable");
//        DatabaseReference db_DriverWorkingRef = FirebaseDatabase.getInstance().getReference("DriverWorking");
//        GeoFire geoFireDriverAvailable = new GeoFire(db_DriverAvailableRef);
//        GeoFire geoFireDriverWorking = new GeoFire(db_DriverWorkingRef);
//
//        DriverMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("Driver Location")
//                .icon(BitmapDescriptorFactory.fromResource(R.drawable.car)));
//
//        switch (CustomerId) {
////            case "off":
////                db_DriverWorkingRef.child(User_id).removeValue();
////                db_DriverAvailableRef.child(User_id).removeValue();
////                break;
//
//            case "":
////                geoFireDriverWorking.removeLocation(User_id);
//                //Remove Driver from working and put it to Driver Available if has no customer
//                db_DriverWorkingRef.child(User_id).removeValue();
//                geoFireDriverAvailable.setLocation(User_id, new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()),
//                        new GeoFire.CompletionListener() {
//                            @Override
//                            public void onComplete(String key, DatabaseError error) {
//                                Toast.makeText(DriverMapsActivity.this, "" + key, Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                if (PickUpCustomerMarker!=null){
//                    PickUpCustomerMarker.remove();
//                }
//                CustomerInfo.setVisibility(View.GONE);
//                break;
//            default:
//                if (CustomerId.length() > 3) {
////                geoFireDriverAvailable.removeLocation(User_id);
//                    // if not and has a customer remove Driver from available and put it in working Drivers
//                    db_DriverAvailableRef.child(User_id).removeValue();
//                    geoFireDriverWorking.setLocation(User_id, new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()),
//                            new GeoFire.CompletionListener() {
//                                @Override
//                                public void onComplete(String key, DatabaseError error) {
//                                    Toast.makeText(DriverMapsActivity.this, "" + key, Toast.LENGTH_SHORT).show();
//                                }
//                            });
//                }
//                break;
//        }
//    }
//
//    protected synchronized void BulidGoogleApiClient() {
//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .addApi(LocationServices.API)
//                .build();
//        mGoogleApiClient.connect();
//
//    }
//
//    @Override
//    public void onConnected(@Nullable Bundle bundle) {
//
//        mLocationRequest = new LocationRequest();
//        mLocationRequest.setInterval(1000);
//        mLocationRequest.setFastestInterval(1000);
//        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
//                &&
//                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
//        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
//    }
//
//    @Override
//    public void onConnectionSuspended(int i) {
//    }
//
//    @Override
//    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//
//    }
//
////    @Override
////    protected void onStop() {
////        super.onStop();
////        if (!isLogOut) {
////            DisconnectedDriver();
////        }
////    }
//
//    public void RemoveInfromation() {
//        if (User_id!=null) {
//            DatabaseReference dbRefDriverAvailable = FirebaseDatabase.getInstance().getReference("DriverAvailable");
//            DatabaseReference dbRefDriverWorking = FirebaseDatabase.getInstance().getReference("DriverWorking");
//            dbRefDriverAvailable.child(User_id).removeValue();
//            dbRefDriverWorking.child(User_id).removeValue();
////            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("User")
////                    .child("Drivers");
////            dbRef.child(User_id).child("CustomerRequest").child("CustomerRideId").setValue("");
//        }
//    }
//
//    private void DisconnectedDriver() {
//        DatabaseReference CustomerId = FirebaseDatabase.getInstance().getReference().child("User").child("Drivers")
//                .child(User_id).child("CustomerRequest").child("CustomerRideId");
//        CustomerId.setValue("off");
//    }
//
//    protected void onDestroy() {
//        super.onDestroy();
//        if (!isLogOut) {
//            DisconnectedDriver();
//            RemoveInfromation();
//
////            UpDateLocationDrivers();
//        }
//    }
//}