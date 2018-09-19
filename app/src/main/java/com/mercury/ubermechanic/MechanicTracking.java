package com.mercury.ubermechanic;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mercury.ubermechanic.Common.Common;
import com.mercury.ubermechanic.Helper.DirectionJSONParser;
import com.mercury.ubermechanic.Model.DataMessage;
import com.mercury.ubermechanic.Model.FCMResponse;
import com.mercury.ubermechanic.Model.Invoice;
import com.mercury.ubermechanic.Model.Token;
import com.mercury.ubermechanic.Remote.IFCMService;
import com.mercury.ubermechanic.Remote.IGoogleApi;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MechanicTracking extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener
{
    RelativeLayout layoutMechanicTracking;

    private GoogleMap mMap;

    String Driverlat, Driverlng;

    String customerId;
    String mechanicId;

    private Polyline direction;

    //Play Services

    private static final int PLAY_SERVICE_RES_REQUEST = 7001;

    private GoogleApiClient mGoogleApiClient;

    private LocationRequest mLocationRequest;

    private static int UPDATE_INTERVAL = 5000;
    private static int FASTEST_INTERVAL = 3000;
    private static int DISPLACEMENT = 10;

    private Circle DriverMarker;
    private Marker mechanicMarker;

    Button btnStartJob;

    IGoogleApi mService;
    IFCMService mFCMService;

    GeoFire geofire;

    FirebaseDatabase db;
    DatabaseReference invoice;
    DatabaseReference history;
    DatabaseReference mechanicInfo;
    DatabaseReference DriverInfo;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mechanic_tracking);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        db= FirebaseDatabase.getInstance();
        invoice= db.getReference(Common.invoice_tbl);
        history= db.getReference(Common.history_tbl);
        DriverInfo = db.getReference(Common.user_driver_tbl);
        mechanicInfo= db.getReference(Common.user_mechanic_tbl);


        layoutMechanicTracking = findViewById(R.id.layoutMechanicTracking);

        if(getIntent()!= null)
        {
            Driverlat = getIntent().getStringExtra("lat");
            Driverlng= getIntent().getStringExtra("lng");
            customerId=getIntent().getStringExtra("customerId");

        }
        mService= Common.getGoogleApi();
        mFCMService = Common.getFCMService();

        btnStartJob = findViewById(R.id.btnStartJob);
        btnStartJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btnStartJob.getText().equals("START JOB"))
                {
                    btnStartJob.setText("FINISH JOB");
                }
                else if(btnStartJob.getText().equals("FINISH JOB"))
                {
//                    RecordJob(Common.JobId);
                    showInvoiceDialog();

                }
            }
        });

        setUpLocation();
    }

    private void showInvoiceDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("INVOICE");

        LayoutInflater inflater = LayoutInflater.from(this);
        View payment_layout = inflater.inflate(R.layout.layout_payment, null);


        final MaterialEditText etAmount = payment_layout.findViewById(R.id.etAmount);
        final MaterialEditText etJob = payment_layout.findViewById(R.id.etJob);

        dialog.setView(payment_layout);

        dialog.setPositiveButton("FINISH", new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                btnStartJob.setEnabled(false);



                if (TextUtils.isEmpty(etAmount.getText().toString())) {
                    Snackbar.make(layoutMechanicTracking, "please enter Amount", Snackbar.LENGTH_SHORT)
                            .show();
                    return;
                }
                if (TextUtils.isEmpty(etJob.getText().toString())) {
                    Snackbar.make(layoutMechanicTracking, "please enter Job Description", Snackbar.LENGTH_SHORT)
                            .show();
                    return;
                }

                        Invoice inv = new Invoice();
                        inv.setAmount(etAmount.getText().toString());
                        inv.setDescription(etJob.getText().toString());

                        invoice.child(customerId)
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .setValue(inv)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        int amount=0;
                                        String description="";

                                        Map<String,Object> customerinvoice = new HashMap<>();
                                        customerinvoice.put("invoice",amount);
                                        customerinvoice.put("description",description);

                                        invoice.child(mechanicId)
                                                .updateChildren(customerinvoice)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {

                                                        sendJobFinishedNotification(customerId);
                                                    }
                                                });

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(MechanicTracking.this, "Could not Send Your Invoice Please Try Again Later!!!", Toast.LENGTH_SHORT).show();

                                    }
                                });
                    }







        });
        dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });
        dialog.show();

    }

//    private void RecordJob(String jobId){
////        History hist = new History(jobId);
//
////      mechanicInfo.child(FirebaseAuth.getInstance().getCurrentUser().getUid());
////      DriverInfo.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(hist);
////      jobId = history.push().getKey();
//
//        History hist = new History(jobId);
//        hist.setJobId(String.valueOf(jobId));
//
//        history.child(jobId)
//                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
//                .push()
//                .setValue(hist)
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        history.child(mechanicId)
//                                .addListenerForSingleValueEvent(new ValueEventListener() {
//                                    @Override
//                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                        String jobId = "";
//                                        for(DataSnapshot snappy : dataSnapshot.getChildren())
//                                        {
//                                            History history = snappy.getValue(History.class);
//                                            history.getJobId();
//                                        }
//
//
//                                        Map<String,Object> historyupdate = new HashMap<>();
//                                        historyupdate.put("history",jobId);
//
//                                        mechanicInfo.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(historyupdate);
//                                        DriverInfo.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(historyupdate);
//                                    }
//
//                                    @Override
//                                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                    }
//                                });
//                    }
//                });
//
//
//
//
//
//
////      HashMap map = new HashMap();
////      map.put("mechanic",mechanicId);
////      map.put("Driver",customerId);
////      map.put("rating",0);
////      map.put("TimeStamp",getCurrentTime());
////
////      history.child(jobId)
////              .updateChildren(map);
//
//
//    }

    private Long getCurrentTime() {
        Long Timestamp = System.currentTimeMillis()/1000;
        return Timestamp;
    }

    private void setUpLocation() {
        if(checkPlayServices()){
            buildGoogleApiClient();
            createLocationRequest();
                displayLocation();

        }

    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);

    }

    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if(resultCode != ConnectionResult.SUCCESS){
            if(GooglePlayServicesUtil.isUserRecoverableError(resultCode))
                GooglePlayServicesUtil.getErrorDialog(resultCode,this,PLAY_SERVICE_RES_REQUEST).show();
            else{
                Toast.makeText(this,"This device is not Supported",Toast.LENGTH_SHORT).show();
                finish();
            }
            return false;
        }
        return true;

    }


    private void displayLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Common.mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if(Common.mLastLocation !=null){

            final double latitude = Common.mLastLocation.getLatitude();
            final double longitude = Common.mLastLocation.getLongitude();

            if(mechanicMarker !=null)
                mechanicMarker.remove();
            mechanicMarker= mMap.addMarker(new MarkerOptions().position(new LatLng(latitude,longitude))
            .title("You")
            .icon(BitmapDescriptorFactory.defaultMarker()));

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude,longitude),17.0f));

            if(direction !=null)

                direction.remove(); //remove old direction
            getDirection();





        }
        else{
            Log.d("Gitau","Cannot get your Location");
        }


    }

    private void getDirection() {
      LatLng  currentPosition =new LatLng(Common.mLastLocation.getLatitude(),Common.mLastLocation.getLongitude());

        String requestApi = null;
        try {
            requestApi = "https://maps.googleapis.com/maps/api/directions/json?"+
                    "mode=driving&"+
                    "transit_routing_preferences=less__driving&"+
                    "origin="+currentPosition.latitude+","+currentPosition.longitude+"&"+
                    "destination="+Driverlat+","+Driverlng+"&"+
                    "key="+getResources().getString(R.string.google_direction_api);

            Log.d("GITAU",requestApi); //print url for debug

            mService.getPath(requestApi)
                    .enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {

                            try {

                                new ParserTask().execute(response.body().toString());



                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Toast.makeText(MechanicTracking.this,""+t.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });



        }catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,mLocationRequest,this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        DriverMarker =mMap.addCircle(new CircleOptions()
        .center(new LatLng(Double.parseDouble(Driverlat),Double.parseDouble(Driverlng)))
        .radius(50) //50 => radius is 50m
        .strokeColor(Color.BLUE)
        .fillColor(0x220000FF)
        .strokeWidth(5.0f));

        //create geofence with radius as 50 m
        geofire = new GeoFire(FirebaseDatabase.getInstance().getReference(Common.mechanic_tbl));
        final GeoQuery geoQuery = geofire.queryAtLocation(new GeoLocation(Double.parseDouble(Driverlat),Double.parseDouble(Driverlng)),0.05f);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                if(customerId !=null){
                    sendArrivedNotification(customerId);
                    geofire.removeLocation(Common.mechanic_tbl);
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

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });


    }

    private void sendArrivedNotification(String customerId) {
        Token token = new Token(customerId);
        //we will send notification with title as Arrived
//            Notification notification = new Notification("Arrived",String.format("Your Mechanic %s  has Arrived at your Location",Common.currentUser.getName()));
//            Sender sender = new Sender(notification,token.getToken());

        Map<String,String>content = new HashMap<>();
        content.put("title","Cancel");
        content.put("message",String.format("Your Mechanic %s  has Arrived at your Location",Common.currentUser.getName()));
        DataMessage dataMessage = new DataMessage(token.getToken(),content);

            mFCMService.sendMessage(dataMessage).enqueue(new Callback<FCMResponse>() {
                @Override
                public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                    if(response.body().success !=1)
                    {
                        Toast.makeText(MechanicTracking.this, "Failed", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onFailure(Call<FCMResponse> call, Throwable t) {

                }
            });



    }


    private void sendJobFinishedNotification(String customerId){
        Token token = new Token(customerId);
        //we will send notification with title as Arrived
//        Notification notification = new Notification("Job Complete",customerId);
//        Sender sender = new Sender(notification,token.getToken());
        Map<String,String>content = new HashMap<>();
        content.put("title","Cancel");
        content.put("message",customerId);
        DataMessage dataMessage = new DataMessage(token.getToken(),content);


        mFCMService.sendMessage(dataMessage).enqueue(new Callback<FCMResponse>() {
            @Override
            public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                if(response.body().success !=1)
                {
                    Toast.makeText(MechanicTracking.this, "Failed", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<FCMResponse> call, Throwable t) {

            }
        });
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        displayLocation();
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        Common.mLastLocation = location;
        displayLocation();
    }


    private class ParserTask extends AsyncTask<String,Integer,List<List<HashMap<String,String>>>>
    {
        ProgressDialog mDialog = new ProgressDialog(MechanicTracking.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog.setMessage("Please Wait...");
            mDialog.show();
        }

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
          JSONObject jsonObject;
          List<List<HashMap<String, String>>> routes = null;
          try{
              jsonObject = new JSONObject(strings[0]);
              DirectionJSONParser parser = new DirectionJSONParser();
              routes = parser.parse(jsonObject);
          } catch (JSONException e) {
              e.printStackTrace();
          }
          return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
          mDialog.dismiss();

            ArrayList points = null;
            PolylineOptions polylineOptions = null;


            for(int i=0;i<lists.size();i++)
            {
                points = new ArrayList();
                polylineOptions = new PolylineOptions();

                List<HashMap<String,String>> path = lists.get(i);

                for(int j=0;j<path.size();j++)
                {
                    HashMap<String,String>point=path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));

                    LatLng position =new LatLng(lat,lng);

                    points.add(position);

                }
                polylineOptions.addAll(points);
                polylineOptions.width(10);
                polylineOptions.color(Color.BLUE);
                polylineOptions.geodesic(true);

            }
            direction=mMap.addPolyline(polylineOptions);
        }
    }
}
