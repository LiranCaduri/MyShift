package com.lirancaduri.secendfire.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lirancaduri.secendfire.R;
import com.lirancaduri.secendfire.data.ReplaceShift;
import com.lirancaduri.secendfire.data.ShiftFirebase;
import com.lirancaduri.secendfire.sqlite.ShiftSQLite;

import java.util.ArrayList;
import java.util.List;

import static com.lirancaduri.secendfire.global.Global.NEW_SHIFTS;
import static com.lirancaduri.secendfire.global.Global.REPLACE_SHIFT;


public class ConfigActivity extends AppCompatActivity {


    public static final String TAG_SHIFT_FIREBASE = "TAG_ShiftFirebase";

    private DatabaseReference referenceNewShift, referenceReplaceShift;
    private ShiftFirebase currentShiftFirebase;
    private TextView tvText;
    private ShiftSQLite shiftSQLite;
    private List<ShiftFirebase> shiftFirebases;
    private List<ReplaceShift> replaceShifts;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        tvText = findViewById(R.id.tvText);
        shiftSQLite = new ShiftSQLite(this);
        currentShiftFirebase = (ShiftFirebase) getIntent().getSerializableExtra(TAG_SHIFT_FIREBASE);
        referenceNewShift = FirebaseDatabase.getInstance().getReference().child(NEW_SHIFTS);
        referenceReplaceShift = FirebaseDatabase.getInstance().getReference().child(REPLACE_SHIFT);

        // אנחנו בודקים שאנחנו לקחנו את המשמרת בנוסף מעדכנים את FIREBASE ואת הSQLITE
        referenceNewShift.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshots) {
                Iterable<DataSnapshot> children = dataSnapshots.getChildren();
                shiftFirebases = new ArrayList<>();
                boolean isGetByMe = false;
                for (DataSnapshot dataSnapshot : children) {
                    ShiftFirebase shiftFirebase = dataSnapshot.getValue(ShiftFirebase.class);
                    if (shiftFirebase != null){
                       if (!shiftFirebase.equals(currentShiftFirebase)){
                           shiftFirebases.add(shiftFirebase);
                       }else{
                               isGetByMe = true;
                       }
                    }
                }
                if (isGetByMe) {

                    referenceNewShift.removeValue(new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            referenceNewShift.setValue(shiftFirebases);
                            tvText.setText("The shift save");
                            shiftSQLite.insert(currentShiftFirebase);
                            Intent intent = new Intent(ListActivity.TAG_UPDATE_SHIFTS);
                            intent.putExtra(ListActivity.TAG_NEW_SHIFT,currentShiftFirebase);
                            sendBroadcast(intent);
                        }
                    });


                    //מודיעים שלקחנו את המשמרת אלינו
                    referenceReplaceShift.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshots) {
                            Iterable<DataSnapshot> children = dataSnapshots.getChildren();
                            replaceShifts = new ArrayList<>();
                            replaceShifts.add(new ReplaceShift(currentShiftFirebase.getUid(),currentShiftFirebase.getId(),currentShiftFirebase.getStart(),currentShiftFirebase.getDate()));
                            for (DataSnapshot dataSnapshot : children) {
                                ReplaceShift replaceShift = dataSnapshot.getValue(ReplaceShift.class);
                                if (replaceShift != null){

                                    replaceShifts.add(replaceShift);
                                }
                            }

                            referenceReplaceShift.removeValue(new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    referenceReplaceShift.setValue(replaceShifts);
                                }
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }else{
                    tvText.setText("Other take this shift");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
