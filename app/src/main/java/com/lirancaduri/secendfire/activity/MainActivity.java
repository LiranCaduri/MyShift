package com.lirancaduri.secendfire.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.lirancaduri.secendfire.R;
import com.lirancaduri.secendfire.data.Shift;

import java.io.File;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    public static final int REQUEST_CODE_LOGIN = 123;
    public static final int REQUEST_CODE_REGISTER = 23;



    //מאתחלת את הכפתורים ובודקת אם המשתמש מחובר ישר עובר לרשימת המשמרות

    /**
     * בדיקה אם משתמש מחובר
     * אם המשתמש מחובר הולך לליסט אקטיביטי ונסגר
     * אם המשתמש לא מחובר הולך למסך login register activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (FirebaseAuth.getInstance().getCurrentUser() == null){
            findViewById(R.id.btnRegister).setOnClickListener(this);
            findViewById(R.id.btnLogin).setOnClickListener(this);
        }else {
            startActivity(new Intent(this,ListActivity.class));
            finish();
        }

    }


    /**
     *   לפי הכפתור שנלחץ פותח בתתאם את האקטיבטי
     */
    @Override
    public void onClick(View view) {
        int requestCode = 0,action = 0;
        switch (view.getId()){
            case R.id.btnRegister:
                requestCode = REQUEST_CODE_REGISTER;
                action = LoginRegisterActivity.ACTION_REGISTER;
                break;
            case R.id.btnLogin:
                requestCode = REQUEST_CODE_LOGIN;
                action = LoginRegisterActivity.ACTION_LOGIN;
                break;
        }
        Intent intent = new Intent(this,LoginRegisterActivity.class);
        intent.putExtra(LoginRegisterActivity.TAG_ACTION,action);
        startActivityForResult(intent, requestCode);

    }


    /**
     *  אם ההרשמה / התחברות היו תקינים סוגר את המאיין
     */


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK){
            finish();
        }
    }
}
