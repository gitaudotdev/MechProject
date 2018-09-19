package com.mercury.ubermechanic;

import android.Manifest;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.multidex.MultiDex;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.github.glomadrian.materialanimatedswitch.MaterialAnimatedSwitch;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.mercury.ubermechanic.Common.Common;
import com.mercury.ubermechanic.Model.Token;
import com.mercury.ubermechanic.Remote.IGoogleApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Maps extends FragmentActivity implements OnMapReadyCallback

{


    private GoogleMap mMap;

    FusedLocationProviderClient fusedLocationProviderClient;
    com.google.android.gms.location.LocationCallback locationCallback;

    //Play Services
    private static final int MY_PERMISSION_REQUEST_CODE = 7000;
    private static final int PLAY_SERVICE_RES_REQUEST = 7001;

    private GoogleApiClient mGoogleApiClient;

    private LocationRequest mLocationRequest;

    private static int UPDATE_INTERVAL = 5000;
    private static int FASTEST_INTERVAL = 3000;
    private static int DISPLACEMENT = 10;


    DatabaseReference mechanics;
    GeoFire geoFire;

    Marker mCurrent;


    MaterialAnimatedSwitch location_switch;
    SupportMapFragment mapFragment;

    //Car animation
    private List<LatLng> polyLineList;
    private Marker mechanicMarker;
    private float v;
    private double lat,lng;
    private Handler handler;
    private LatLng startPosition,endPosition,currentPosition;
    private  int index,next;
//    private Button btnGo;
    private PlaceAutocompleteFragment places;
    private String destination;
    private PolylineOptions polylineOptions,blackpolylineOptions;
    private Polyline blackPolyline,greyPolyline;

    private IGoogleApi mService;

    //presence system
    DatabaseReference onlineRef,currentUserRef;

    Runnable drawPathRunnable= new Runnable() {
        @Override
        public void run() {
            if(index<polyLineList.size()-1)
            {
                index++;
                next = index+1;

            }
            if(index<polyLineList.size()-1)
            {
                startPosition =polyLineList.get(index);
                endPosition =polyLineList.get(next);
            }
            ValueAnimator animator = ValueAnimator.ofFloat(0,1);
            animator.setDuration(10000);
            animator.setInterpolator(new LinearInterpolator());
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    v = animation.getAnimatedFraction();
                    lng = v*endPosition.longitude+(1-v)*startPosition.longitude;
                    lat = v*endPosition.longitude+(1-v)*startPosition.latitude;
                    LatLng newpos = new LatLng(lat,lng);
                    mechanicMarker.setPosition(newpos);
                    mechanicMarker.setAnchor(0.5f,0.5f);
                    mechanicMarker.setRotation(getBearing(startPosition,newpos));
                    mMap.moveCamera(CameraUpdateFactory.newCameraPosition(
                            new CameraPosition.Builder()
                            .target(newpos)
                            .zoom(15.5f)
                            .build()
                    ));

                }
            });
            animator.start();
            handler.postDelayed(this,5000);



        }
    };

    private float getBearing(LatLng startPosition, LatLng endPosition) {
        double lat = Math.abs(startPosition.latitude - endPosition.latitude);
        double lng = Math.abs(startPosition.longitude-endPosition.longitude);

        if(startPosition.latitude< endPosition.latitude && startPosition.longitude< endPosition.longitude )
            return (float) (Math.toDegrees(Math.atan(lng/lat)));
        else   if(startPosition.latitude>= endPosition.latitude && startPosition.longitude< endPosition.longitude )
            return (float) ((90-Math.toDegrees(Math.atan(lng/lat)))+90);
        else if(startPosition.latitude >= endPosition.latitude && startPosition.longitude >= endPosition.longitude )
            return (float) (Math.toDegrees(Math.atan(lng/lat))+180);
        else if(startPosition.latitude< endPosition.latitude && startPosition.longitude >= endPosition.longitude )
            return (float) ((90-Math.toDegrees(Math.atan(lng/lat)))+270);
      return -1;
    }
//
//    @Override
//    protected void attachBaseContext(Context newBase) {
//        super.attachBaseContext(newBase);
//        MultiDex.install(this);
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fusedLocationProviderClient =LocationServices.getFusedLocationProviderClient(this);

        //Presence System
        onlineRef = FirebaseDatabase.getInstance().getReference().child(".info/connected");

        currentUserRef = FirebaseDatabase.getInstance().getReference(Common.mechanic_tbl)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        onlineRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //removes driver tbl when driver diconnects
                currentUserRef.onDisconnect().removeValue();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        //initView
        location_switch = findViewById(R.id.location_switch);
        location_switch.setOnCheckedChangeListener(new MaterialAnimatedSwitch.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(boolean IsOnline) {

                if (IsOnline) {

                    FirebaseDatabase.getInstance().goOnline();//set when connected switch on

                    if (ActivityCompat.checkSelfPermission(Maps.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(Maps.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                        return;
                    }
                    buildLocationRequest();
                    buildLocationCallback();
                    fusedLocationProviderClient.requestLocationUpdates(mLocationRequest, locationCallback, Looper.myLooper());
                    displayLocation();
                    Snackbar.make(mapFragment.getView(), "You Are Online", Snackbar.LENGTH_SHORT)
                            .show();

                } else {
                    FirebaseDatabase.getInstance().goOffline();

                    fusedLocationProviderClient.removeLocationUpdates(locationCallback);

                    mCurrent.remove();
                    mMap.clear();
                    handler=new Handler();
                    handler.removeCallbacks(drawPathRunnable);
                    Snackbar.make(mapFragment.getView(), "You Are Offline", Snackbar.LENGTH_SHORT)
                            .show();

                }
            }
        });

        polyLineList = new ArrayList<>();


        //places api
        places =(PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        places.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                if(location_switch.isChecked())
                {
                    destination =place.getAddress().toString();
                    destination = destination.replace("","+");

                    getDirection();
                }
                else{
                    Toast.makeText(Maps.this, "Please Change your status to ONLINE", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Status status) {
                Toast.makeText(Maps.this, ""+status.toString(), Toast.LENGTH_SHORT).show();

            }
        });



        //GEo Fire
        mechanics = FirebaseDatabase.getInstance().getReference(Common.mechanic_tbl);
        geoFire = new GeoFire(mechanics);

        setupLocation();

        mService = Common.getGoogleApi();

        updateFirebaseToken();


    }

    private void updateFirebaseToken() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference tokens = db.getReference(Common.token_tbl);

        Token token = new Token(FirebaseInstanceId.getInstance().getToken());
        tokens.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .setValue(token);

    }

    private void getDirection() {
        currentPosition =new LatLng(Common.mLastLocation.getLatitude(),Common.mLastLocation.getLongitude());

        String requestApi = null;
        try {
            requestApi = "https://maps.googleapis.com/maps/api/directions/json?"+
                    "mode=driving&"+
                    "transit_routing_preferences=less__driving&"+
                    "origin="+currentPosition.latitude+","+currentPosition.longitude+"&"+
                    "destination="+destination+"&"+
                    "key="+getResources().getString(R.string.google_direction_api);

            Log.d("GITAU",requestApi); //print url for debug

            mService.getPath(requestApi)
                    .enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {

                            try {
                                JSONObject jsonObject = new JSONObject(response.body().toString());
                                JSONArray jsonArray = jsonObject.getJSONArray("routes");
                                for(int i=0;i<jsonArray.length();i++)
                                {
                                    JSONObject route = jsonArray.getJSONObject(i);
                                    JSONObject poly = route.getJSONObject("overview_polyline");
                                    String polyline = poly.getString("points");
                                    polyLineList =decodePoly(polyline);

                                }
                                //Adjusting Bounds
                                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                for(LatLng latLng:polyLineList)
                                    builder.include(latLng);
                                LatLngBounds bounds = builder.build();
                                CameraUpdate mCameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds,2);
                                mMap.animateCamera(mCameraUpdate);

                                polylineOptions = new PolylineOptions();
                                polylineOptions.color(Color.GRAY);
                                polylineOptions.width(5);
                                polylineOptions.startCap(new SquareCap());
                                polylineOptions.endCap(new SquareCap());
                                polylineOptions.jointType(JointType.ROUND);
                                polylineOptions.addAll(polyLineList);
                                greyPolyline =mMap.addPolyline(polylineOptions);


                                blackpolylineOptions = new PolylineOptions();
                                blackpolylineOptions.color(Color.BLACK);
                                blackpolylineOptions.width(5);
                                blackpolylineOptions.startCap(new SquareCap());
                                blackpolylineOptions.endCap(new SquareCap());
                                blackpolylineOptions.jointType(JointType.ROUND);
                                blackPolyline =mMap.addPolyline(blackpolylineOptions);


                                mMap.addMarker(new MarkerOptions()
                                        .position(polyLineList.get(polyLineList.size()-1))
                                        .title("Pickup Location"));

                                //Animation
                                ValueAnimator polyLineAnimator = ValueAnimator.ofInt(0,100);
                                polyLineAnimator.setDuration(10000);
                                polyLineAnimator.setInterpolator(new LinearInterpolator());
                                polyLineAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                    @Override
                                    public void onAnimationUpdate(ValueAnimator animation) {
                                        List<LatLng> points = greyPolyline.getPoints();
                                        int percentValue = (int)animation.getAnimatedValue();
                                        int size = points.size();
                                        int newPoints = (int)(size * (percentValue/100.0f));
                                        List<LatLng> p = points.subList(0,newPoints);
                                        blackPolyline.setPoints(p);

                                    }
                                });
                                polyLineAnimator.start();

                                mechanicMarker = mMap.addMarker(new MarkerOptions().position(currentPosition)
                                             .flat(true)
                                             .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_mechanic)));

                                handler = new Handler();
                                index=-1;
                                next=1;
                                handler.postDelayed(drawPathRunnable,5000);




                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Toast.makeText(Maps.this,""+t.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });



        }catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    private List decodePoly(String encoded) {

        List poly = new ArrayList();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }

    //ctrl+ O
    //because we requested runtime permissions we need to override onrequestpermission result method


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                   buildLocationCallback();
                   buildLocationRequest();
                        if (location_switch.isChecked())
                            displayLocation();

                    }
                }


    }

    private void setupLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            //request runtime permission
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, MY_PERMISSION_REQUEST_CODE);

        } else {

            buildLocationRequest();
            buildLocationCallback();
            if (location_switch.isChecked())
                displayLocation();

        }

    }

    private void buildLocationCallback() {
        locationCallback = new com.google.android.gms.location.LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    Common.mLastLocation = location;

                }
                displayLocation();
            }
        };

    }

    private void buildLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);

    }


    private void displayLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        Common.mLastLocation = location;
                        if (Common.mLastLocation != null) {
                            if (location_switch.isChecked()) {
                                final double latitude = Common.mLastLocation.getLatitude();
                                final double longitude = Common.mLastLocation.getLongitude();

                                //UPDATE TO FIREBASE
                                geoFire.setLocation(FirebaseAuth.getInstance().getCurrentUser().getUid(), new GeoLocation(latitude, longitude), new GeoFire.CompletionListener() {
                                    @Override
                                    public void onComplete(String key, DatabaseError error) {
                                        //ADD MARKER
                                        if (mCurrent != null) {
                                            mCurrent.remove();
                                        }
                                        // REMOVE ALREADY MARKER
                                        mCurrent = mMap.addMarker(new MarkerOptions()
                                              .position(new LatLng(latitude, longitude))
                                                .title("Your Location"));

                                        //MOVE CAMERA TO THIS POSITION
                                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 15.0f));

                                    }
                                });
                            }
                        } else {
                            Log.d("ERROR", "Cannot get your location");
                        }
                    }
                });


    }

//    private void rotateMarker(final Marker mCurrent, final float i, GoogleMap mMap) {
//        final Handler handler = new Handler();
//        final long start = SystemClock.uptimeMillis();
//        final float startRotation = mCurrent.getRotation();
//        final long duration = 1500;
//
//
//        final LinearInterpolator interpolator = new LinearInterpolator();
//
//        handler.post(new Runnable() {
//            @Override
//            public void run() {
//                long elapsed = SystemClock.uptimeMillis() - start;
//                float t = interpolator.getInterpolation((float) elapsed / duration);
//                float rot = t * i + (1 - t) * startRotation;
//                mCurrent.setRotation(-rot > 180 ? rot / 2 : rot);
//
//                if (t < 1.0) {
//                    handler.postDelayed(this, 16);
//                }
//
//
//            }
//        });
//
//
//    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setTrafficEnabled(false);
        mMap.setIndoorEnabled(false);
        mMap.setBuildingsEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(true);




//        buildGoogleApiClient();
//        mMap.setMyLocationEnabled(true);

        //update location
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        buildLocationRequest();
        buildLocationCallback();
        fusedLocationProviderClient.requestLocationUpdates(mLocationRequest, locationCallback, Looper.myLooper());



    }

}