package com.doubleclick.uberappsimcoder.Customer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;

import com.doubleclick.uberappsimcoder.History.HistoryActivity;
import com.doubleclick.uberappsimcoder.MainActivity;
import com.doubleclick.uberappsimcoder.R;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomerMapsActivity extends FragmentActivity implements OnMapReadyCallback
//        ,GoogleApiClient.ConnectionCallbacks,
//        com.google.android.gms.location.LocationListener,
//        GoogleApiClient.OnConnectionFailedListener
{
////////////////////////////////////////////////////////////////////////////////////////
//    private GoogleMap mMap;
//    private GoogleApiClient mGoogleApiClient;
//    private Location mLastLocation;
//    private LocationRequest mLocationRequest;
//    private String User_id;
//    private Button LogOut, mRequest, Setting;
//    private LatLng PickUpLocation;
//    private int radius = 1;
//    private boolean foundDriver = false;
//    private String DriverFoundId;
//    private Marker mDriverMarker, PickUpMarker;
//    private Boolean RequestBool = false;
//    private GeoQuery geoQueryAvailableDriver;
//    private DatabaseReference db_DriverLocationRef, db_refCustomerRequest;
//    private ValueEventListener DriverLocationRefListener;
//    private Boolean isLogOut = false;
//    public static String CustomerRideId = "CustomerRideId";
//    private String distination = "cairo";
//    private int CountUpdate = 1;
//    private static int AUTOCOMPLETE_REQUEST_CODE = 1;
//    private TextView txtNameDriver, txtCarDriver, txtPhoneDriver;
//    private ImageView DriverImage;
//    private LinearLayout DriverInfo;
//////////////////////////////////////////////////////////////////////////////////////////

    private GoogleMap mMap;
    Location mLastLocation;
    LocationRequest mLocationRequest;

    private FusedLocationProviderClient mFusedLocationClient;

    private Button mLogout, mRequest, mSettings, mHistory;

    private LatLng pickupLocation;

    private Boolean requestBol = false;

    private Marker pickupMarker;

    private SupportMapFragment mapFragment;

    private String destination, requestService;

    private LatLng destinationLatLng;

    private LinearLayout mDriverInfo;

    private ImageView mDriverProfileImage;

    private TextView mDriverName, mDriverPhone, mDriverCar;

    private RadioGroup mRadioGroup;

    private RatingBar mRatingBar;
    private EditText Search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//        LogOut = findViewById(R.id.logOut);
//        mRequest = findViewById(R.id.Request);
//        Setting = findViewById(R.id.Setting);
//        User_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        txtNameDriver = findViewById(R.id.txtDriverName);
//        txtCarDriver = findViewById(R.id.txtDriverCar);
//        txtPhoneDriver = findViewById(R.id.txtDriverPhone);
//        DriverImage = findViewById(R.id.DriverImageView);
//        DriverInfo = findViewById(R.id.DriverInfo);
//
//
//        Setting.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent SettingIntent = new Intent(CustomerMapsActivity.this, SettingCustomerActivity.class);
//                startActivity(SettingIntent);
//            }
//        });
//        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
//                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
//
//        // Set the fields to specify which types of place data to
//        // return after the user has made a selection.
//        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);
//
//        // Start the autocomplete intent.
////        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
////                .build(this);
////        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
//        Places.initialize(CustomerMapsActivity.this, getString(R.string.mykey));
//
//        // Specify the types of place data to return.
//        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));
//
//        // Set up a PlaceSelectionListener to handle the response.
//        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
//            @Override
//            public void onPlaceSelected(@NotNull Place place) {
//                // TODO: Get info about the selected place.
////                try {
//                distination = place.getName();
////                }catch (Exception e){
//                Toast.makeText(CustomerMapsActivity.this, "" + distination, Toast.LENGTH_LONG).show();
////                }
//
////                Log.i("TAG", "Place: " + place.getName() + ", " + place.getId());
//            }
//
//            @Override
//            public void onError(@NotNull Status status) {
//                // TODO: Handle the error.
//                Toast.makeText(CustomerMapsActivity.this, "" + status, Toast.LENGTH_LONG).show();
//                Log.i("TAG", "An error occurred: " + status);
//            }
//        });
//        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//        LogOut.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                isLogOut = true;
//                Removelocation();
//                FirebaseAuth.getInstance().signOut();
//                Intent intent = new Intent(CustomerMapsActivity.this, MainActivity.class);
//                startActivity(intent);
//                finish();
//
//            }
//        });
//        mRequest.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (RequestBool) {
//                    DriverInfo.setVisibility(View.INVISIBLE);
//                    RequestBool = false;
//                    CountUpdate++;
//                    if (DriverFoundId != null) {
//                        geoQueryAvailableDriver.removeAllListeners();
//                    }
////                    db_DriverLocationRef.removeEventListener(DriverLocationRefListener);
//
//                    if (DriverFoundId != null) {
//                        DatabaseReference DriverRef = FirebaseDatabase.getInstance().getReference().child("User")
//                                .child("Drivers").child(DriverFoundId).child("CustomerRequest");//.child("CustomerRequest");
//                        DriverRef.child("CustomerRideId").setValue("");
//                        DriverRef.child("Distination").setValue("");
////                        DriverFoundId = "";
//                    }
//                    radius = 1;
////                    User_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
//                    db_refCustomerRequest = FirebaseDatabase.getInstance().getReference("CustomerRequest").child(User_id);
////                    GeoFire geoFire = new GeoFire(db_refCustomerRequest);
////                    geoFire.removeLocation(User_id);
//                    db_refCustomerRequest.removeValue();
//                    if (PickUpMarker != null) {
//                        PickUpMarker.remove();
//                        mDriverMarker.remove();
//                    }
//                    mRequest.setText("Cancel Uper");
//                } else {
//                    RequestBool = true;
//                    if (!(mLastLocation == null)) {
//                        DriverInfo.setVisibility(View.VISIBLE);
////                        User_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
//                        db_refCustomerRequest = FirebaseDatabase.getInstance().getReference("CustomerRequest");
//                        GeoFire geoFire = new GeoFire(db_refCustomerRequest);
//                        geoFire.setLocation(User_id, new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()), new GeoFire.CompletionListener() {
//                            @Override
//                            public void onComplete(String key, DatabaseError error) {
//
//                            }
//                        });
//                        if (CountUpdate > 1) {
//                            updateChildren();
////                            Handler handler = new Handler();
////                            Runnable r = new Runnable() {
////                                public void run() {
////                                    GettingCloseDriver();
////                                }
////                            };
////                            handler.postDelayed(r, 1000);
//                        }
//                        PickUpLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
//                        PickUpMarker = mMap.addMarker(new MarkerOptions().position(PickUpLocation).title("Pickup Customer Here")
//                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.person)));
//                        mRequest.setText("Getting your car" + " \n" + " " + DriverFoundId);
//                        GettingCloseDriver();
//                    }
//                }
//            }
//        });
//
//    }
//
//    private void getDriverInfo() {
//        DatabaseReference mCustomerDataBase = FirebaseDatabase.getInstance().getReference().child("User").child("Drivers").child(DriverFoundId);
//        mCustomerDataBase.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.exists() && snapshot.getChildrenCount() > 0) {
//                    Map<String, Object> map = (Map<String, Object>) snapshot.getValue();
//                    if (map.get("Name") != null) {
//
////                        mName = map.get("Name").toString();
//                        txtNameDriver.setText("" + map.get("Name").toString());
//                    }
//                    if (map.get("Car") != null) {
//
////                        mName = map.get("Name").toString();
//                        txtCarDriver.setText("" + map.get("Car").toString());
//                    }
//                    if (map.get("Phone") != null) {
////                        mPhone = map.get("Phone").toString();
//                        txtPhoneDriver.setText("" + map.get("Phone").toString());
//                    }
//                    if (map.get("ProfileImage") != null) {
////                        mProfileUri = map.get("ProfileImage").toString();
//                        Glide.with(getApplication()).load(map.get("ProfileImage").toString()).centerCrop().into(DriverImage);
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
//    ////////////////////////////////////////////////////////////////////////////////////////////
////    @Override
////    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
////        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
////            if (resultCode == RESULT_OK) {
////                Place place = Autocomplete.getPlaceFromIntent(data);
////                Log.i("TAG", "Place: " + place.getName() + ", " + place.getId());
////            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
////                // TODO: Handle the error.
////                Status status = Autocomplete.getStatusFromIntent(data);
////                Log.i("TAG", status.getStatusMessage());
////            } else if (resultCode == RESULT_CANCELED) {
////                // The user canceled the operation.
////            }
////            return;
////        }
////        super.onActivityResult(requestCode, resultCode, data);
////    }
//////////////////////////////////////////////////////////////////////////////////////////////
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
//    }
//
//    @Override
//    public void onLocationChanged(Location location) {
//        mLastLocation = location;
//        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
//
////        User_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
////        dbRef = FirebaseDatabase.getInstance().getReference("CustomerRequest");
////        GeoFire geoFire = new GeoFire(dbRef);
////        geoFire.setLocation(User_id, new GeoLocation(location.getLatitude(), location.getLongitude()),
////                new GeoFire.CompletionListener() {
////                    @Override
////                    public void onComplete(String key, DatabaseError error) {
////                        Toast.makeText(CustomerMapsActivity.this,""+key,Toast.LENGTH_SHORT).show();
////                    }
////                });
//
//
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
////        Removelocation();
////        GeoFire geoFire = new GeoFire(dbRef);
////        geoFire.removeLocation(User_id);
////    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if (!isLogOut) {
//            Removelocation();
//        }
////        String User_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
////        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("DriverAvailble");
////        dbRef.child(User_id).removeValue();
//    }
//
//    //    @Override
////    public void onBackPressed() {
////        super.onBackPressed();
////        String User_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
////        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("DriverAvailble");
////        dbRef.child(User_id).removeValue();
//////        GeoFire geoFire = new GeoFire(dbRef);
//////        geoFire.removeLocation(User_id);
////    }
//    public void Removelocation() {
//        if (DriverFoundId != null && User_id != null) {
//            DatabaseReference dbRefCustomerRideId = FirebaseDatabase.getInstance().getReference("User").child("Drivers")
//                    .child(DriverFoundId).child("CustomerRequest").child("CustomerRideId");
//            dbRefCustomerRideId.setValue("");
//            DatabaseReference dbRefCustomerRequest = FirebaseDatabase.getInstance().getReference("CustomerRequest");
//            dbRefCustomerRequest.child(User_id).removeValue();
//        }
//    }
//
//
//    public void GettingCloseDriver() {
//        DatabaseReference db_AvailableDriver = FirebaseDatabase.getInstance().getReference().child("DriverAvailable");
//        GeoFire geoFireAvailableDriver = new GeoFire(db_AvailableDriver);
//        geoQueryAvailableDriver = geoFireAvailableDriver.queryAtLocation(new GeoLocation(PickUpLocation.latitude, PickUpLocation.longitude), radius);
//        geoQueryAvailableDriver.removeAllListeners();
//        geoQueryAvailableDriver.addGeoQueryEventListener(new GeoQueryEventListener() {
//            @Override
//            public void onKeyEntered(String key, GeoLocation location) {
//                if (!foundDriver && RequestBool) {
//                    foundDriver = true;
//                    DriverFoundId = key;
//                    updateChildren();
////                    db_driverRef.child(CustomerRideId).setValue(User_id);
//                    gettingDriverLocation();
//
//                }
//
//            }
//
//            @Override
//            public void onKeyExited(String key) {
//
//            }
//
//            @Override
//            public void onKeyMoved(String key, GeoLocation location) {
//
//            }
//
//            @Override
//            public void onGeoQueryReady() {
//                if (!foundDriver) {
//                    radius++;
//                    GettingCloseDriver();
//                }
//            }
//
//            @Override
//            public void onGeoQueryError(DatabaseError error) {
//            }
//        });
//
//    }
//
//
//    private void updateChildren() {
//        DatabaseReference db_driverRef = FirebaseDatabase.getInstance().getReference().child("User")
//                .child("Drivers").child(DriverFoundId).child("CustomerRequest");
//        User_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        HashMap map = new HashMap();
//        map.put("CustomerRideId", User_id);
//        map.put("Distination", distination);
//        db_driverRef.updateChildren(map);
//    }
//
//
//    private void gettingDriverLocation() {
//        db_DriverLocationRef = FirebaseDatabase.getInstance().getReference().child("DriverAvailable").child(DriverFoundId).child("l");
//        DriverLocationRefListener = db_DriverLocationRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.exists() && RequestBool) {
//                    List<Object> map = (List<Object>) snapshot.getValue();
//                    double LocationLat = 0;
//                    double LocationLng = 0;
//                    mRequest.setText("Location Found");
//                    Toast.makeText(CustomerMapsActivity.this, "" + DriverFoundId + "====== " + radius, Toast.LENGTH_LONG).show();
//                    if (map.get(0) != null) {
//                        LocationLat = Double.parseDouble(map.get(0).toString());
//                    }
//                    if (map.get(1) != null) {
//                        LocationLng = Double.parseDouble(map.get(1).toString());
//                    }
//                    LatLng DriverLatLang = new LatLng(LocationLat, LocationLng);
//                    if (mDriverMarker != null) {
//                        mDriverMarker.remove();
//                    }
//
//                    Location location1 = new Location("");
//                    location1.setLatitude(PickUpLocation.latitude);
//                    location1.setLongitude(PickUpLocation.longitude);
//
//                    Location location2 = new Location("");
//                    location2.setLatitude(DriverLatLang.latitude);
//                    location2.setLongitude(DriverLatLang.longitude);
//
//                    float distance = location1.distanceTo(location2);
//                    if (distance < 100) {
//                        mRequest.setText("Your Driver is here ");
//                    } else {
//                        mRequest.setText("DriverFound " + distance);
//                        getDriverInfo();
//                        mDriverMarker = mMap.addMarker(new MarkerOptions().position(DriverLatLang).title("Your Driver is Here .")
//                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.car)));
//
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        destinationLatLng = new LatLng(0.0,0.0);

        mDriverInfo =  findViewById(R.id.driverInfo);

        mDriverProfileImage =  findViewById(R.id.driverProfileImage);

        mDriverName =  findViewById(R.id.driverName);
        mDriverPhone =  findViewById(R.id.driverPhone);
        mDriverCar =  findViewById(R.id.driverCar);

        mRatingBar =  findViewById(R.id.ratingBar);

        mRadioGroup =  findViewById(R.id.radioGroup);
        mRadioGroup.check(R.id.UberX);

        mLogout =  findViewById(R.id.logout);
        mRequest =  findViewById(R.id.request);
        mSettings = findViewById(R.id.settings);
        mHistory = findViewById(R.id.history);
        Search = findViewById(R.id.Search);


        Places.initialize(getApplicationContext(), String.valueOf(R.string.google_maps_key));


        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(CustomerMapsActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });

        mRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (requestBol){
                    endRide();


                }else{
                    int selectId = mRadioGroup.getCheckedRadioButtonId();

                    final RadioButton radioButton =  findViewById(selectId);

                    if (radioButton.getText() == null){
                        return;
                    }

                    requestService = radioButton.getText().toString();

                    requestBol = true;

                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("customerRequest");
                    GeoFire geoFire = new GeoFire(ref);
                    geoFire.setLocation(userId, new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()));

                    pickupLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                    pickupMarker = mMap.addMarker(new MarkerOptions().position(pickupLocation).title("Pickup Here").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_pickup)));

                    mRequest.setText("Getting your Driver....");

                    getClosestDriver();
                }
            }
        });
        mSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomerMapsActivity.this, SettingCustomerActivity.class);
                startActivity(intent);
                return;
            }
        });

        mHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomerMapsActivity.this, HistoryActivity.class);
                intent.putExtra("customerOrDriver", "Customers");
                startActivity(intent);
                return;
            }
        });
        Search.setFocusable(false);
        Search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS,
                        Place.Field.LAT_LNG,
                        Place.Field.NAME);

                // Start the autocomplete intent.
                Intent intent = new Autocomplete.IntentBuilder(

                        //AutocompleteActivityMode.FULLSCREEN
                        AutocompleteActivityMode.OVERLAY
                        , fieldList)
                        .build(CustomerMapsActivity.this);
                startActivityForResult(intent, 20);
            }
        });





//        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
//                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
//        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
//            @Override
//            public void onPlaceSelected(Place place) {
//                // TODO: Get info about the selected place.
//                destination = place.getName().toString();
//                destinationLatLng = place.getLatLng();
//            }
//            @Override
//            public void onError(Status status) {
//                // TODO: Handle the error.
//            }
//        });


    }

    /**
     * Override the activity's onActivityResult(), check the request code, and
     * do something with the returned place data (in this example its place name and place ID).
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 20) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Search.setText(place.getAddress());
                Search.append(String.format("Locality Name : %s ",place.getName()));
                Log.i("CustomerMapsActivity ", "Place: " + place.getName() + ", " + place.getId());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Toast.makeText(getApplicationContext(),""+status.getStatusMessage(),Toast.LENGTH_LONG).show();
                Log.i("CustomerMapsActivity ", status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    private int radius = 1;
    private Boolean driverFound = false;
    private String driverFoundID;

    GeoQuery geoQuery;
    private void getClosestDriver(){
        DatabaseReference driverLocation = FirebaseDatabase.getInstance().getReference().child("driversAvailable");

        GeoFire geoFire = new GeoFire(driverLocation);
        geoQuery = geoFire.queryAtLocation(new GeoLocation(pickupLocation.latitude, pickupLocation.longitude), radius);
        geoQuery.removeAllListeners();

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                if (!driverFound && requestBol){
                    DatabaseReference mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(key);
                    mCustomerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                                Map<String, Object> driverMap = (Map<String, Object>) dataSnapshot.getValue();
                                if (driverFound){
                                    return;
                                }
                                if(driverMap.get("service").equals(requestService)){
                                    driverFound = true;
                                    driverFoundID = dataSnapshot.getKey();
                                    DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverFoundID).child("customerRequest");
                                    String customerId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                    HashMap map = new HashMap();
                                    map.put("customerRideId", customerId);
                                    map.put("destination", destination);
                                    map.put("destinationLat", destinationLatLng.latitude);
                                    map.put("destinationLng", destinationLatLng.longitude);
                                    driverRef.updateChildren(map);
                                    getDriverLocation();
                                    getDriverInfo();
                                    getHasRideEnded();
                                    mRequest.setText("Looking for Driver Location....");
                                }
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                if (!driverFound)
                {
                    radius++;
                    getClosestDriver();
                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }
    /*-------------------------------------------- Map specific functions -----
    |  Function(s) getDriverLocation
    |
    |  Purpose:  Get's most updated driver location and it's always checking for movements.
    |
    |  Note:
    |	   Even tho we used geofire to push the location of the driver we can use a normal
    |      Listener to get it's location with no problem.
    |
    |      0 -> Latitude
    |      1 -> Longitudde
    |
    *-------------------------------------------------------------------*/
    private Marker mDriverMarker;
    private DatabaseReference driverLocationRef;
    private ValueEventListener driverLocationRefListener;
    private void getDriverLocation(){
        driverLocationRef = FirebaseDatabase.getInstance().getReference().child("driversWorking").child(driverFoundID).child("l");
        driverLocationRefListener = driverLocationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && requestBol){
                    List<Object> map = (List<Object>) dataSnapshot.getValue();
                    double locationLat = 0;
                    double locationLng = 0;
                    if(map.get(0) != null){
                        locationLat = Double.parseDouble(map.get(0).toString());
                    }
                    if(map.get(1) != null){
                        locationLng = Double.parseDouble(map.get(1).toString());
                    }
                    LatLng driverLatLng = new LatLng(locationLat,locationLng);
                    if(mDriverMarker != null){
                        mDriverMarker.remove();
                    }
                    Location loc1 = new Location("");
                    loc1.setLatitude(pickupLocation.latitude);
                    loc1.setLongitude(pickupLocation.longitude);

                    Location loc2 = new Location("");
                    loc2.setLatitude(driverLatLng.latitude);
                    loc2.setLongitude(driverLatLng.longitude);

                    float distance = loc1.distanceTo(loc2);

                    if (distance<100){
                        mRequest.setText("Driver's Here");
                    }else{
                        mRequest.setText("Driver Found: " + String.valueOf(distance));
                    }



                    mDriverMarker = mMap.addMarker(new MarkerOptions().position(driverLatLng).title("your driver").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car)));
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    /*-------------------------------------------- getDriverInfo -----
    |  Function(s) getDriverInfo
    |
    |  Purpose:  Get all the user information that we can get from the user's database.
    |
    |  Note: --
    |
    *-------------------------------------------------------------------*/
    private void getDriverInfo(){
        mDriverInfo.setVisibility(View.VISIBLE);
        DatabaseReference mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverFoundID);
        mCustomerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    if(dataSnapshot.child("name")!=null){
                        mDriverName.setText(dataSnapshot.child("name").getValue().toString());
                    }
                    if(dataSnapshot.child("phone")!=null){
                        mDriverPhone.setText(dataSnapshot.child("phone").getValue().toString());
                    }
                    if(dataSnapshot.child("car")!=null){
                        mDriverCar.setText(dataSnapshot.child("car").getValue().toString());
                    }
                    if(dataSnapshot.child("profileImageUrl").getValue()!=null){
                        Glide.with(getApplication()).load(dataSnapshot.child("profileImageUrl").getValue().toString()).into(mDriverProfileImage);
                    }

                    int ratingSum = 0;
                    float ratingsTotal = 0;
                    float ratingsAvg = 0;
                    for (DataSnapshot child : dataSnapshot.child("rating").getChildren()){
                        ratingSum = ratingSum + Integer.valueOf(child.getValue().toString());
                        ratingsTotal++;
                    }
                    if(ratingsTotal!= 0){
                        ratingsAvg = ratingSum/ratingsTotal;
                        mRatingBar.setRating(ratingsAvg);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private DatabaseReference driveHasEndedRef;
    private ValueEventListener driveHasEndedRefListener;
    private void getHasRideEnded(){
        driveHasEndedRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverFoundID).child("customerRequest").child("customerRideId");
        driveHasEndedRefListener = driveHasEndedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                }else{
                    endRide();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void endRide(){
        requestBol = false;
        geoQuery.removeAllListeners();
        driverLocationRef.removeEventListener(driverLocationRefListener);
        driveHasEndedRef.removeEventListener(driveHasEndedRefListener);

        if (driverFoundID != null){
            DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverFoundID).child("customerRequest");
            driverRef.removeValue();
            driverFoundID = null;

        }
        driverFound = false;
        radius = 1;
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("customerRequest");
        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(userId);

        if(pickupMarker != null){
            pickupMarker.remove();
        }
        if (mDriverMarker != null){
            mDriverMarker.remove();
        }
        mRequest.setText("call Uber");

        mDriverInfo.setVisibility(View.GONE);
        mDriverName.setText("");
        mDriverPhone.setText("");
        mDriverCar.setText("Destination: --");
        mDriverProfileImage.setImageResource(R.mipmap.ic_default_user);
    }

    /*-------------------------------------------- Map specific functions -----
    |  Function(s) onMapReady, buildGoogleApiClient, onLocationChanged, onConnected
    |
    |  Purpose:  Find and update user's location.
    |
    |  Note:
    |	   The update interval is set to 1000Ms and the accuracy is set to PRIORITY_HIGH_ACCURACY,
    |      If you're having trouble with battery draining too fast then change these to lower values
    |
    |
    *-------------------------------------------------------------------*/
    @SuppressLint("MissingPermission")
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
        mMap.setMyLocationEnabled(true);
//        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());

    }

    LocationCallback mLocationCallback = new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for(Location location : locationResult.getLocations()){
                if(getApplicationContext()!=null){
                    mLastLocation = location;
                    Toast.makeText(getApplicationContext(),""+location,Toast.LENGTH_LONG).show();

                    LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());

                    //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    //mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
                    if(!getDriversAroundStarted)
                        getDriversAround();
                }
            }
        }
    };

    /*-------------------------------------------- onRequestPermissionsResult -----
    |  Function onRequestPermissionsResult
    |
    |  Purpose:  Get permissions for our app if they didn't previously exist.
    |
    |  Note:
    |	requestCode: the nubmer assigned to the request that we've made. Each
    |                request has it's own unique request code.
    |
    *-------------------------------------------------------------------*/
    private void checkLocationPermission() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                new android.app.AlertDialog.Builder(this)
                        .setTitle("give permission")
                        .setMessage("give permission message")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(CustomerMapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                            }
                        })
                        .create()
                        .show();
            }
            else{
                ActivityCompat.requestPermissions(CustomerMapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }

    @SuppressLint("MissingPermission")
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




    boolean getDriversAroundStarted = false;
    List<Marker> markers = new ArrayList<Marker>();
    private void getDriversAround(){
        getDriversAroundStarted = true;
        DatabaseReference driverLocation = FirebaseDatabase.getInstance().getReference().child("driversAvailable");

        GeoFire geoFire = new GeoFire(driverLocation);
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(mLastLocation.getLongitude(), mLastLocation.getLatitude()), 999999999);

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {

                for(Marker markerIt : markers){
                    if(markerIt.getTag().equals(key))
                        return;
                }

                LatLng driverLocation = new LatLng(location.latitude, location.longitude);

                Marker mDriverMarker = mMap.addMarker(new MarkerOptions().position(driverLocation).title(key).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car)));
                mDriverMarker.setTag(key);

                markers.add(mDriverMarker);


            }

            @Override
            public void onKeyExited(String key) {
                for(Marker markerIt : markers){
                    if(markerIt.getTag().equals(key)){
                        markerIt.remove();
                    }
                }
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                for(Marker markerIt : markers){
                    if(markerIt.getTag().equals(key)){
                        markerIt.setPosition(new LatLng(location.latitude, location.longitude));
                    }
                }
            }

            @Override
            public void onGeoQueryReady() {
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });


    }

}