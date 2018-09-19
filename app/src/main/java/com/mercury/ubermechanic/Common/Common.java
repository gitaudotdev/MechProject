package com.mercury.ubermechanic.Common;

import android.location.Location;

import com.mercury.ubermechanic.Model.User;
import com.mercury.ubermechanic.Remote.FCMClient;
import com.mercury.ubermechanic.Remote.IFCMService;
import com.mercury.ubermechanic.Remote.IGoogleApi;
import com.mercury.ubermechanic.Remote.RetrofitClient;

public class Common {


    public static final String mechanic_tbl="Mechanics";
    public static final String user_mechanic_tbl="MechanicsInformation";
    public static final String user_driver_tbl="DriversInformation";
    public static final String pickup_request_tbl="PickupRequest";
    public static final String token_tbl="Tokens";
    public static final String history_tbl="History";
    public static final String invoice_tbl = "Invoices";

    public  static User currentUser= new User();

    public static String JobId="";


    public static Location mLastLocation=null;

    public static final String newsUrl ="https://www.the-star.co.ke/classifieds/jobs/daily-nation-jobs.html";
    public static final String baseUrl="https://maps.googleapis.com";
    public static final String fcmUrl="https://fcm.googleapis.com/";
    public static  final String user_field="usr";
    public static  final String pwd_field="pwd";
    public static  final int PICK_IMAGE_REQUEST= 7777;



    public static IGoogleApi getGoogleApi(){
        return RetrofitClient.getClient(baseUrl).create(IGoogleApi.class);
    }
    public static IFCMService getFCMService(){
        return FCMClient.getClient(fcmUrl).create(IFCMService.class);
    }
}
