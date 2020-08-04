package com.lirancaduri.secendfire.thread;


import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lirancaduri.secendfire.data.ShiftFirebase;

import static com.lirancaduri.secendfire.global.Global.NEW_SHIFTS;
import static com.lirancaduri.secendfire.global.Global.getBooleanFromPreference;

public class CheckingFirebaseThread extends Thread  {

    private boolean keepChecking = true;
    private CheckingFirebaseListener checkingFirebaseListener;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();


    // מקבל לאיזה ילד בפיירבס להאזין
    public CheckingFirebaseThread(String child){
        databaseReference = databaseReference.child(child);
    }



    // כל 4 שניות בודק אם יש חדש ושולח כל אובייקט שכזה למי שקרא לו על ידי ממשק ליסטנר וככה מתקשרים בניהם
    @Override
    public void run() {
        while (keepChecking){
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshots) {
                    Iterable<DataSnapshot> children = dataSnapshots.getChildren();
                    for (DataSnapshot dataSnapshot : children) {
                        checkingFirebaseListener.getDataSnapshot(dataSnapshot);
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            try {
                Thread.sleep(4000);
            } catch (InterruptedException ignore) {}
        }
    }

    public void setCheckingFirebaseListener(CheckingFirebaseListener checkingFirebaseListener) {
        this.checkingFirebaseListener = checkingFirebaseListener;
    }


    // מפסיק את הטרייד ובנוסף מעיר אותו אם הוא באמצע שינה
    public void stopThread(){
        keepChecking = false;
        interrupt();
    }


    // ממשק ההאזנה GENRY שככה אפשר להשתמש עם כל אובייקט
    public  interface CheckingFirebaseListener<T> {
         void getDataSnapshot(DataSnapshot dataSnapshot);
         void onFind (T t);
    }

}
