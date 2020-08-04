package com.lirancaduri.secendfire.service;

import android.app.AlarmManager;
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
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.lirancaduri.secendfire.activity.ConfigActivity;
import com.lirancaduri.secendfire.data.ShiftFirebase;
import com.lirancaduri.secendfire.global.Global;
import com.lirancaduri.secendfire.sqlite.ShiftSQLite;
import com.lirancaduri.secendfire.thread.CheckingFirebaseThread;


public class CheckingNewShiftsService extends Service implements CheckingFirebaseThread.CheckingFirebaseListener<ShiftFirebase> {

    public static final String STOP_THREAD = "STOP_THREAD";
    private CheckingFirebaseThread checkingFirebaseThread;
    private ShiftSQLite shiftSQLite;
    private int id;



    //בודק אם הTHEARD NULL אז מייצר, ואם הSQLITE  NULL אז מייצר , ואם רצו להפיסק את THREAD אז מפסיק ומשווה לNULL
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (checkingFirebaseThread == null) {
            checkingFirebaseThread = new CheckingFirebaseThread(Global.NEW_SHIFTS);
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



    // מה יקרה בכל אובייקט שיגיע מFIREBASE
    @Override
    public void getDataSnapshot(DataSnapshot dataSnapshot) {
        ShiftFirebase shiftFirebase = dataSnapshot.getValue(ShiftFirebase.class);
        boolean isNotNull = shiftFirebase != null;
        if (isNotNull) {
            boolean notFromMe = !shiftFirebase.getUid().equals(FirebaseAuth.getInstance().getUid());
            boolean notSeeBefore = !Global.getBooleanFromPreference(this, shiftFirebase.getUid() + shiftFirebase.getId(), false);
            boolean notWorkToday = !shiftSQLite.workInDay(shiftFirebase.getDate());
            if (notFromMe && notSeeBefore && notWorkToday) {
                onFind(shiftFirebase);
            }
        }
    }


    @Override
    public void onFind(ShiftFirebase shiftFirebase) {
        if (checkingFirebaseThread != null) {
            // שומר בתור שראיתי את זה כבר
            Global.saveInSharedPreferences(this, shiftFirebase.getUid() + shiftFirebase.getId(), true, true);

            // מה יקרה בלחיצה על הכפתור YES
            Intent intent = new Intent(this, ConfigActivity.class);
            intent.putExtra(ConfigActivity.TAG_SHIFT_FIREBASE, shiftFirebase);
            PendingIntent pendingIntentTrue = PendingIntent.getActivity(this, 123, intent, 0);


            // הגדרות של ההתראה
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NotificationChannel.DEFAULT_CHANNEL_ID);
            builder.setSmallIcon(android.R.drawable.ic_lock_lock);
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            builder.setSound(defaultSoundUri);
            builder.setContentTitle("New Shift for you");
            builder.setContentText("Problem : " + shiftFirebase.getProblem() +
                    " Date : " + shiftFirebase.getDate() +
                    " Time start : " + shiftFirebase.getStart() +
                    " Time end : " + shiftFirebase.getEnd() );
            builder.addAction(android.R.drawable.ic_lock_lock, "Yes", pendingIntentTrue);
            builder.setAutoCancel(true);

            // אובייקט MANAGER מקבלים על ידי קבוע של השם והמרה מOBJECT לסוג האובייקט שצריך
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(++id, builder.build());
        }
    }


    // שהסרוויס הופסק סוגרים את הSQLITE את אTHREADE
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
