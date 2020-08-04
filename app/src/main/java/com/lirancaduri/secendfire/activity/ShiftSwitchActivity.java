package com.lirancaduri.secendfire.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lirancaduri.secendfire.R;
import com.lirancaduri.secendfire.data.ReplaceShift;
import com.lirancaduri.secendfire.data.Shift;
import com.lirancaduri.secendfire.data.ShiftFirebase;
import com.lirancaduri.secendfire.global.Global;
import com.lirancaduri.secendfire.service.ReplaceShiftService;

import java.util.ArrayList;
import java.util.List;

import static com.lirancaduri.secendfire.global.Global.NEW_SHIFTS;

public class ShiftSwitchActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG_SHIFT = "TAG_SHIFT";
    private EditText etProblem;
    private DatabaseReference databaseReference;
    private ShiftFirebase shiftFirebase;
    private List<ShiftFirebase> shiftFirebases;


    // מקבל איזה משמרת לבקש החלפה עבורה
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shift_switch);
        etProblem = findViewById(R.id.etProblem);
        Shift shift = (Shift) getIntent().getSerializableExtra(TAG_SHIFT);
        shiftFirebase = new ShiftFirebase(shift);
        shiftFirebase.setUid(FirebaseAuth.getInstance().getUid());
        findViewById(R.id.btnSwitch).setOnClickListener(this);

    }


    // שקורה קליק אז מעלה לשרת אם יש אינטרנט
    @Override
    public void onClick(View v) {
        String problem = etProblem.getText().toString();
        if (!problem.equals("")){
            shiftFirebase.setProblem(problem);
            if (Global.isNetworkOk(this)) {
                databaseReference = FirebaseDatabase.getInstance().getReference().child(NEW_SHIFTS);
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshots) {
                        Iterable<DataSnapshot> children = dataSnapshots.getChildren();
                        shiftFirebases = new ArrayList<>();
                        shiftFirebases.add(shiftFirebase);
                        for (DataSnapshot dataSnapshot : children) {
                            ShiftFirebase shiftFirebase = dataSnapshot.getValue(ShiftFirebase.class);
                            if (shiftFirebase != null){
                                shiftFirebases.add(shiftFirebase);

                            }
                        }
                        databaseReference.removeValue(new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                databaseReference.setValue(shiftFirebases);
                                Global.makeAndShowSnackBar(ShiftSwitchActivity.this,"Send","Dismiss",false);
                                Intent intent = new Intent(ShiftSwitchActivity.this, ReplaceShiftService.class);
                                startService(intent);
                            }
                        });

                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }else{
                Global.makeAndShowSnackBar(ShiftSwitchActivity.this,"Internet invalid","Dismiss",false);

            }

        }else{
            Toast.makeText(this, "Problem cannot be empty", Toast.LENGTH_SHORT).show();
        }
    }
}
