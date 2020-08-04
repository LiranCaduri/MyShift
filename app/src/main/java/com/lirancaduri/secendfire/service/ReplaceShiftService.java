package com.lirancaduri.secendfire.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.lirancaduri.secendfire.activity.ListActivity;
import com.lirancaduri.secendfire.data.ReplaceShift;
import com.lirancaduri.secendfire.global.Global;
import com.lirancaduri.secendfire.sqlite.ShiftSQLite;
import com.lirancaduri.secendfire.thread.CheckingFirebaseThread;

import java.sql.Time;

public class ReplaceShiftService extends Service implements CheckingFirebaseThread.CheckingFirebaseListener<ReplaceShift> {

    public static final String STOP_THREAD = "STOP_THREAD";
    private CheckingFirebaseThread checkingFirebaseThread;
    private ShiftSQLite shiftSQLite;
    private int id;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (checkingFirebaseThread == null) {
            checkingFirebaseThread = new CheckingFirebaseThread(Global.REPLACE_SHIFT);
            checkingFirebaseThread.setCheckingFirebaseListener(this);
            checkingFirebaseThread.start();
        }
        if (shiftSQLite == null){
            shiftSQLite = new ShiftSQLite(this);
        }
        if (intent != null) {
            boolean stopThread = intent.getBooleanExtra(STOP_THREAD, false);
            if (stopThread) {
                checkingFirebaseThread.stopThread();
                checkingFirebaseThread = null;
                shiftSQLite.close();
                shiftSQLite = null;
                return START_NOT_STICKY;
            }
        }
        return START_STICKY;
    }



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void getDataSnapshot(DataSnapshot dataSnapshot) {
        ReplaceShift replaceShift = dataSnapshot.getValue(ReplaceShift.class);
        boolean isNotNull = replaceShift != null;
        if (isNotNull) {
            boolean isFromMe = replaceShift.getUidUser().equals(FirebaseAuth.getInstance().getUid());
            boolean seeBefore =  Global.getBooleanFromPreference(this, replaceShift.getUidUser() + replaceShift.getIdShift(), false);
            if (isFromMe && !seeBefore) {
                onFind(replaceShift);
            }else if(seeBefore){
                dataSnapshot.getRef().removeValue();
            }
        }
    }

    @Override
    public void onFind(ReplaceShift replaceShift) {
        if (checkingFirebaseThread != null) {
            Global.saveInSharedPreferences(this, replaceShift.getUidUser() + replaceShift.getIdShift(), true, true);

            Intent intent = new Intent(this, ListActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 123, intent, 0);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NotificationChannel.DEFAULT_CHANNEL_ID);
            builder.setSmallIcon(android.R.drawable.ic_lock_lock);
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            builder.setSound(defaultSoundUri);
            builder.setContentTitle("Your Friend get the shift");
            builder.setContentText("In date : " + replaceShift.getDate() + "\n" +
                                    "Start time : " + new Time(replaceShift.getStartTime()).toString());
            builder.setAutoCancel(true);
            builder.setContentIntent(pendingIntent);
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(++id, builder.build());
            shiftSQLite.deleteById(replaceShift.getIdShift());


            // ברגע שקרה מפעיל את הפעולה הנכונה וככה בעצם להכניס לליסט מתי שהוא נמצא בעמוד
            Intent intentBroadcast = new Intent(ListActivity.ACTION_REPLACE_SHIFT);
            intentBroadcast.putExtra(ListActivity.TAG_UPDATE_SHIFTS,replaceShift);
            intentBroadcast.putExtra(ListActivity.TAG_ACTION,ListActivity.ACTION_REMOVE);
            sendBroadcast(intentBroadcast);
            stopSelf();
        }
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        if (checkingFirebaseThread != null) {
            checkingFirebaseThread.stopThread();
            checkingFirebaseThread = null;
        }
        if (shiftSQLite != null){
            shiftSQLite.close();
            shiftSQLite = null;
        }
    }
}


