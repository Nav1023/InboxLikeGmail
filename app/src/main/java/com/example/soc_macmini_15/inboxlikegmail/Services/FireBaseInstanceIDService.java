package com.example.soc_macmini_15.inboxlikegmail.Services;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;


/**
 *  Created By Navneet Singh
 *  Date :- 03/09/2018
 *
 */

public class FireBaseInstanceIDService extends FirebaseInstanceIdService {

    private String TAG = "MyFirebaseIDService";

    public FireBaseInstanceIDService() {
        super();
    }

    @Override
    public void onTokenRefresh() {
        // Getting Registration Token
         String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        Log.d(TAG, "onTokenRefresh: " + refreshedToken);
        super.onTokenRefresh();
    }
}
