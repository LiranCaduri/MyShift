package com.lirancaduri.secendfire.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.lirancaduri.secendfire.R;
import com.lirancaduri.secendfire.adapter.ShiftAdapter;
import com.lirancaduri.secendfire.data.ReplaceShift;
import com.lirancaduri.secendfire.data.Shift;
import com.lirancaduri.secendfire.dialog.DialogListShift;
import com.lirancaduri.secendfire.service.CheckingNewShiftsService;
import com.lirancaduri.secendfire.sqlite.ShiftSQLite;

import java.util.Collections;
import java.util.List;

public class ListActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener {


    public static final String TAG_UPDATE_SHIFTS = "TAG_UPDATE_SHIFTS";
    public static final String ACTION_REPLACE_SHIFT = "ReplaceShift";
    public static final int ACTION_ADD = 23;
    public static final String TAG_ACTION = "TAG_ACTION";
    public static final int ACTION_REMOVE = 65;
    private ListView listViewShifts;
    private List<Shift> shiftList;
    private ShiftAdapter shiftAdapter;
    private FloatingActionButton btnAdd;
    private ShiftSQLite shiftSQLite;
    private int position;
    private BroadcastReceiver updateShiftReplaceBroadcast;
    public static final String TAG_NEW_SHIFT = "TAG_NEW_SHIFT";




    // משנה את הטייטל , מאתחל VIEWS מקבל את כל הרשימה ומציג עם אדאפטר
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setTitle("List of shifts");
        }
        initViews();
        btnAdd.setOnClickListener(this);

        // בודק התחברות ושואל אם יש משמרות להחלפה
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            Intent intent = new Intent(this, CheckingNewShiftsService.class);
            startService(intent);
        }
        shiftSQLite = new ShiftSQLite(this);
        shiftList = shiftSQLite.getAllShifts();
        Collections.sort(shiftList);
        shiftAdapter = new ShiftAdapter(this, shiftList);
        listViewShifts.setAdapter(shiftAdapter);
        listViewShifts.setEmptyView(findViewById(R.id.tvEmptyList));
        listViewShifts.setOnItemLongClickListener(this);
        listViewShifts.setOnItemClickListener(this);

        //האזנה לפעולה שקרתה
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_REPLACE_SHIFT);
        initBroadcast();

        //מתחילים את ההאזנה
        registerReceiver(updateShiftReplaceBroadcast,intentFilter);


    }

    private void initBroadcast() {
        updateShiftReplaceBroadcast = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                //
                int action = intent.getIntExtra(TAG_ACTION, -1);
                if (action != -1) {
                    if (action == ACTION_REMOVE) {
                        ReplaceShift replaceShift = (ReplaceShift) intent.getSerializableExtra(TAG_UPDATE_SHIFTS);
                        for (Shift shift : shiftList) {
                            if (shift.getId() == replaceShift.getIdShift()) {
                                shiftList.remove(shift);
                                break;
                            }
                        }
                    } else if (action == ACTION_ADD) {
                        Shift newShift = (Shift) intent.getSerializableExtra(TAG_NEW_SHIFT);

                        shiftList.add(newShift);
                    }
                    Collections.sort(shiftList);
                    shiftAdapter.notifyDataSetChanged();
                }
            }
        };
    }


    //מפסיקים את האזנה
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(updateShiftReplaceBroadcast);
    }

    private void initViews() {
        listViewShifts = findViewById(R.id.listViewShifts);
        btnAdd = findViewById(R.id.btnAdd);
    }

    //קליק על הכפתור הוספה , פותח את האקטיביטי של ההוספה
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnAdd:
                startActivityForResult(new Intent(this, AddEditActivity.class), AddEditActivity.ACTION_ADD_SHIFT);
                break;
        }
    }


    //בלחיצה ארוכה פותח דיאלוג עם אפשרות מחיקה או לבקש החלפה
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int pos, long id) {
        position = pos;
        DialogListShift dialogListShift = new DialogListShift();
        dialogListShift.setBtnDeleteListener(new DialogListShift.DialogListShiftListener() {
            @Override
            public void onClick() {
                shiftSQLite.deleteById(shiftList.remove(position));
                shiftAdapter.notifyDataSetChanged();
            }
        });
        dialogListShift.setBtnAskForReplaceListener(new DialogListShift.DialogListShiftListener() {
            @Override
            public void onClick() {
                Intent intent = new Intent(ListActivity.this,ShiftSwitchActivity.class);
                intent.putExtra(ShiftSwitchActivity.TAG_SHIFT,shiftList.get(position));
                startActivity(intent);
            }
        });
        dialogListShift.show(getFragmentManager(),"dialogListShift");
        return true;
    }



    //בלחיצה על ITEM פותח אקטיביטי לעריכה
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, AddEditActivity.class);
        intent.putExtra(AddEditActivity.TAG_SHIFT,shiftList.get(position));
        intent.putExtra(AddEditActivity.TAG_ACTION, AddEditActivity.ACTION_EDIT_SHIFT);
        intent.putExtra(AddEditActivity.TAG_POSITION,position);
        startActivityForResult(intent, AddEditActivity.ACTION_EDIT_SHIFT);
    }


    //לפי סוג הפעולה עורך את האובייקט שנערך או להוסיף
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Shift shift = (Shift) data.getSerializableExtra(AddEditActivity.TAG_SHIFT);
            switch (requestCode) {
                case AddEditActivity.ACTION_ADD_SHIFT:
                    shiftList.add(shift);
                    break;
                case AddEditActivity.ACTION_EDIT_SHIFT:
                    position = data.getIntExtra(AddEditActivity.TAG_POSITION,-1);
                    if (position != -1){
                        shiftList.set(position,shift);
                    }
                    break;
            }
            Collections.sort(shiftList);
            shiftAdapter.notifyDataSetChanged();

        }
    }
}
