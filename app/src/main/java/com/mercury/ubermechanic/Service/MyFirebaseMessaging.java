package com.mercury.ubermechanic.Service;

import android.content.Intent;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.mercury.ubermechanic.CustomerCall;

import java.util.HashMap;
import java.util.Map;

public class MyFirebaseMessaging extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
         //message from driver app will contain lat and long
        //message needs to be converted to latlng

        if(remoteMessage.getData() != null) {

            Map<String,String>data =remoteMessage.getData();
            String customer = data.get("customer");
            String lat = data.get("lat");
            String lng = data.get("lng");



            Intent intent = new Intent(getBaseContext(), CustomerCall.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("lat", lat);
            intent.putExtra("lng", lng);
            intent.putExtra("customer", customer);
            //add problem of driver

            startActivity(intent);
        }
    }
}
