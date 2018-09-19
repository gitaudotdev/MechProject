package com.mercury.ubermechanic;


import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.mercury.ubermechanic.Common.Common;
import com.mercury.ubermechanic.Model.DataMessage;
import com.mercury.ubermechanic.Model.FCMResponse;
import com.mercury.ubermechanic.Model.Token;
import com.mercury.ubermechanic.Remote.IFCMService;
import com.mercury.ubermechanic.Remote.IGoogleApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerCall extends AppCompatActivity {

    TextView txtDistance,txtAddress,txtDate,txtTime,txtIssue;
    Button btnAccept,btnDecline;

    MediaPlayer mediaPlayer;

    IGoogleApi mService;
    IFCMService mFCMService;

    String customerId;

    String lat,lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_call);

        mService = Common.getGoogleApi();
        mFCMService = Common.getFCMService();

        //InitView
        txtDistance = findViewById(R.id.txtDistance);
        txtAddress = findViewById(R.id.txtAddress);
        txtDate = findViewById(R.id.txtDate);
        txtIssue = findViewById(R.id.txtIssue);
        txtTime = findViewById(R.id.txtTime);


        btnAccept = findViewById(R.id.btnAccept);
        btnDecline= findViewById(R.id.btnDecline);

        btnDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(customerId))
                    cancelBooking(customerId);

            }
        });

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomerCall.this,MechanicTracking.class);
                //sending Customer location to new activity
                intent.putExtra("lat",lat);
                intent.putExtra("lng",lng);
                intent.putExtra("customerId",customerId);

                AcceptBooking(customerId);
                startActivity(intent);
                finish();
            }
        });

        mediaPlayer = MediaPlayer.create(this,R.raw.ringtone);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        if(getIntent() !=null)
        {
             lat = getIntent().getStringExtra("lat");
             lng = getIntent().getStringExtra("lng");
             customerId = getIntent().getStringExtra("customer");


            getDirection(lat,lng);
        }
    }

    private void AcceptBooking(String customerId) {
        Token token = new Token(customerId);

//        Notification notification = new Notification("Notice","Mechanic has Accepted your Booking");
//        Sender sender = new Sender(notification,token.getToken());


        Map<String,String>content = new HashMap<>();
        content.put("title","Cancel");
        content.put("message","Mechanic has Accepted your request");
        DataMessage dataMessage = new DataMessage(token.getToken(),content);


        mFCMService.sendMessage(dataMessage)
                .enqueue(new Callback<FCMResponse>() {
                    @Override
                    public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                        if(response.body().success==1)
                        {
                            Toast.makeText(CustomerCall.this, "Accepted", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<FCMResponse> call, Throwable t) {

                    }
                });

    }

    private void cancelBooking(String customerId) {
        Token token = new Token(customerId);
//
//        Notification notification = new Notification("Notice","Mechanic has cancelled your Booking");
//        Sender sender = new Sender(notification,token.getToken());
        Map<String,String>content = new HashMap<>();
        content.put("title","Cancel");
        content.put("message","Mechanic has Cancelled your request");
        DataMessage dataMessage = new DataMessage(token.getToken(),content);

        mFCMService.sendMessage(dataMessage)
                .enqueue(new Callback<FCMResponse>() {
                    @Override
                    public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                        if(response.body().success==1)
                        {
                            Toast.makeText(CustomerCall.this, "Cancelled", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<FCMResponse> call, Throwable t) {

                    }
                });
    }


    private void getDirection(String lat,String lng) {


        String requestApi = null;
        try {
            requestApi = "https://maps.googleapis.com/maps/api/directions/json?"+
                    "mode=driving&"+
                    "transit_routing_preferences=less__driving&"+
                    "origin="+ Common.mLastLocation.getLatitude()+","+Common.mLastLocation.getLongitude()+"&"+
                    "destination="+lat+","+lng+"&"+
                    "key="+getResources().getString(R.string.google_direction_api);

            Log.d("GITAU",requestApi); //print url for debug

            mService.getPath(requestApi)
                    .enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {

                            try {
                                JSONObject jsonObject = new JSONObject(response.body().toString());

                                JSONArray routes = jsonObject.getJSONArray("routes");

                                //after we get routes , we just take the first elements of routes
                                JSONObject object = routes.getJSONObject(0);

                                // then get array "legs"
                                JSONArray legs = object.getJSONArray("legs");

                                //take first element of legs array
                                JSONObject legsObject = legs.getJSONObject(0);

                                //getting Distance
                                JSONObject distance = legsObject.getJSONObject("distance");
                                txtDistance.setText(distance.getString("text"));

                                //get Time
                                JSONObject time = legsObject.getJSONObject("duration");
                                txtTime.setText(time.getString("text"));

                                //get Address
                                String address= legsObject.getString("end_address");
                                txtAddress.setText(address);










                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Toast.makeText(CustomerCall.this,""+t.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });



        }catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    @Override
    protected void onStop() {
        if(mediaPlayer.isPlaying())
            mediaPlayer.release();
        super.onStop();
    }

    @Override
    protected void onPause() {
        if(mediaPlayer.isPlaying())
            mediaPlayer.pause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mediaPlayer!= null &&!mediaPlayer.isPlaying())
            mediaPlayer.start();
    }
}
