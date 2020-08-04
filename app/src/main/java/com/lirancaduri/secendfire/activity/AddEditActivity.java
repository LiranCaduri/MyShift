package com.lirancaduri.secendfire.activity;

import android.animation.ObjectAnimator;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.lirancaduri.secendfire.R;
import com.lirancaduri.secendfire.data.Shift;
import com.lirancaduri.secendfire.global.Global;
import com.lirancaduri.secendfire.sqlite.ShiftSQLite;

import java.sql.Time;
import java.util.Calendar;

public class AddEditActivity extends AppCompatActivity implements View.OnClickListener {


    public static final String TAG_SHIFT = "TAG_SHIFT";//
    public static final String TAG_ACTION = "TAG_ACTION";
    public static final int ACTION_ADD_SHIFT = 21;
    public static final int ACTION_EDIT_SHIFT = 13;
    public static final String TAG_POSITION = "position";

    private Button btnStart, btnEnd, btnSave, btnDate;
    private TextInputEditText etTip, etSalary;
    private TextView tvTitle;
    private long start, end;
    private ShiftSQLite shiftSQLite;
    private boolean toAdd = true;
    private int id = -1;



    //מאתחל VIEWS מייצר ההאזנה ללחיצות, בודק מה הפעולה לערוך או ליצור חדש, אם לערוך אז לוקח את הפרטים הקודמים ושומר
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);
        initViews();
        listeners();
        ActionBar actionBar = getSupportActionBar();
        if (getIntent() != null) {
            Intent data = getIntent();
            int action = data.getIntExtra(TAG_ACTION, ACTION_ADD_SHIFT);
            if (action == ACTION_EDIT_SHIFT) {
                Shift shift = (Shift) data.getSerializableExtra(TAG_SHIFT);
                if (shift != null) {
                    toAdd = false;
                    id = shift.getId();
                    btnDate.setText(shift.getDate());
                    String startTime = "Time is : " + new Time(shift.getStart());
                    btnStart.setText(startTime);
                    String endTime = "Time is : " + new Time(shift.getEnd());
                    btnEnd.setText(endTime);
                    etSalary.setText(String.valueOf(shift.getSalary()));
                    etTip.setText(String.valueOf(shift.getTip()));
                }
            }
        }
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            if (toAdd) {
                tvTitle.setText("Add shift");
                actionBar.setTitle("Adding page");
            } else {
                tvTitle.setText("Edit shift");
                actionBar.setTitle("Edit page");
            }
        }
        shiftSQLite = new ShiftSQLite(this);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }


    //מה קורה בלחיצה על הכפתור אחורה בACTION BAR
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    // אחראי על האנימציה של העברת מסך
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }




    //יוצר האזנות
    private void listeners() {
        btnSave.setOnClickListener(this);
        btnDate.setOnClickListener(this);
        btnStart.setOnClickListener(this);
        btnEnd.setOnClickListener(this);
    }


    // מאתחל VIEWS
    private void initViews() {
        btnSave = findViewById(R.id.btnSave);
        btnStart = findViewById(R.id.btnStart);
        btnEnd = findViewById(R.id.btnEnd);
        btnDate = findViewById(R.id.btnDate);
        etSalary = findViewById(R.id.etSalary);
        etTip = findViewById(R.id.etTip);
        tvTitle = findViewById(R.id.tvTitle);
    }


    // בלחיצה בודק מה נלחץ אם נלחץ על SAVE לפי הפעולה שרצינו מחזיר את RESULT או פותח דיאלוג לבחירת תאריך/ שעה
    @Override
    public void onClick(View view) {
        Calendar systemCalendar;
        int hour, minute;
        TimePickerDialog timePickerDialog;
        switch (view.getId()) {
            case R.id.btnSave:
                Global.disableKeyBoard(this);
                try {

                    // אם התנאי שהוכנס הוא שוה FALSE אז זורק שגיאה ותופס
                    ifConditionFalseThrow(!btnStart.getText().toString().equals("start"), "Start time missing");
                    ifConditionFalseThrow(!btnEnd.getText().toString().equals("end"), "End time missing");
                    ifConditionFalseThrow(!btnDate.getText().toString().equals("date"), "Date missing");

                    String salaryString = etSalary.getText().toString();
                    int salary = -1;
                    if (!salaryString.isEmpty()) {
                        salary = Integer.valueOf(salaryString);
                    }
                    String tipString = etTip.getText().toString();
                    int tip = -1;
                    if (!tipString.isEmpty()){
                        tip = Integer.valueOf(tipString);

                    }
                    Shift shift = new Shift(btnDate.getText().toString(), start, end, salary, tip, id != -1 ? id : shiftSQLite.getMaxId() + 1);
                    if (toAdd) {
                        shiftSQLite.insert(shift);
                    } else {
                        shiftSQLite.update(shift);
                    }
                    Intent intent = new Intent();
                    intent.putExtra(TAG_SHIFT, shift);
                    setResult(RESULT_OK, intent);
                    finish();
                } catch (ExceptionConditionFalse exceptionConditionFalse) {
                    // וכאן אומר מה סוג השגיאה שקרתה
                    Global.makeAndShowSnackBar(this, exceptionConditionFalse.getMessage(), "Dismiss", false);
                }
                break;
            case R.id.btnDate:
                //date dialog
                systemCalendar = Calendar.getInstance();
                int year = systemCalendar.get(Calendar.YEAR);
                int month = systemCalendar.get(Calendar.MONTH);
                int day = systemCalendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(this, new SetDate(), year, month, day);
                // משפיע על הסטייל, במקרה הזה איך הדי
                datePickerDialog.getWindow().getAttributes().windowAnimations = R.style.SimpleStyleDialogShow;
                datePickerDialog.show();
                break;
            case R.id.btnStart:
                systemCalendar = Calendar.getInstance();
                hour = systemCalendar.get(Calendar.HOUR_OF_DAY);
                minute = systemCalendar.get(Calendar.MINUTE);
                timePickerDialog = new TimePickerDialog(this, new SetYourStart(), hour, minute, true);
                timePickerDialog.getWindow().getAttributes().windowAnimations = R.style.SimpleStyleDialogShow;
                timePickerDialog.show();
                break;
            case R.id.btnEnd:
                systemCalendar = Calendar.getInstance();
                hour = systemCalendar.get(Calendar.HOUR_OF_DAY);
                minute = systemCalendar.get(Calendar.MINUTE);
                timePickerDialog = new TimePickerDialog(this, new SetYourEnd(), hour, minute, true);
                timePickerDialog.getWindow().getAttributes().windowAnimations = R.style.SimpleStyleDialogShow;
                timePickerDialog.show();
                break;
        }
    }

    protected void ifConditionFalseThrow(boolean condition, String message) throws ExceptionConditionFalse {
        if (!condition) {
            throw new ExceptionConditionFalse(message);
        }
    }

    protected class ExceptionConditionFalse extends Exception {

        protected ExceptionConditionFalse(String message) {
            super(message);
        }
    }


    public class SetDate implements DatePickerDialog.OnDateSetListener {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            monthOfYear = monthOfYear + 1;
            String str = dayOfMonth + "/" + monthOfYear + "/" + year;
            Toast.makeText(AddEditActivity.this, str, Toast.LENGTH_LONG).show();
            btnDate.setText(str);
        }
    }

    public class SetYourStart implements TimePickerDialog.OnTimeSetListener {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            Time time = new Time(hourOfDay, minute, 0);
            start = time.getTime();
            String str = "Time is : " + time.toString();
            btnStart.setText(str);

        }
    }


    public class SetYourEnd implements TimePickerDialog.OnTimeSetListener {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            Time time = new Time(hourOfDay, minute, 0);
            end = time.getTime();
            String str = "Time is : " + time.toString();
            btnEnd.setText(str);

        }

    }
}


