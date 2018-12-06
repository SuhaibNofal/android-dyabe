package com.app.nevada.nevadaapp;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class getIDFromFireBase{
    //extends FirebaseInstanceIdService {
    private static final String TAG = "MyFirebaseIIDService";

    Context context ;
   /* @Override
    public void onTokenRefresh() {
//Getting registration token
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
//Displaying token on logcat
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        //Toast.makeText(context,refreshedToken,Toast.LENGTH_SHORT).show();

    }*/


}
