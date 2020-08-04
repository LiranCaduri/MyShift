package com.lirancaduri.secendfire.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.firebase.auth.FirebaseAuth;
import com.lirancaduri.secendfire.service.CheckingNewShiftsService;


public class BootBroadcast extends BroadcastReceiver {



    // מאזין לפעולת BOOT שהמכשיר נדלק ומפעיל SERVICE במקרה שהמשתמש מחובר
    @Override
    public void onReceive(Context context, Intent intent) {
        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            Intent intentService = new Intent(context, CheckingNewShiftsService.class);
            context.startService(intentService);
        }

    }
}
