package com.lirancaduri.secendfire.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.lirancaduri.secendfire.R;
import com.lirancaduri.secendfire.global.Global;

public class LoginRegisterActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG_ACTION = "TAG_ACTION";
    public static final int ACTION_LOGIN = 41;
    public static final int ACTION_REGISTER = 33;
    private int action;
    private String actionName;
    private TextInputEditText etEmail, etPassword;
    private FirebaseAuth firebaseAuth;
    private Snackbar snackbar;
    private OnFailureListener onFailureListener;


    // בודק איזה פעולה צריך התחברות או הרשמה ולפי זה כותב אם זה התחברות או הרשמה
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);
        initViews();
        firebaseAuth = FirebaseAuth.getInstance();
        Button btnAction = findViewById(R.id.btnAction);
        btnAction.setOnClickListener(this);


        action = getIntent().getIntExtra(TAG_ACTION, ACTION_LOGIN);
        if (action == ACTION_LOGIN) {
            actionName = getString(R.string.login);
            btnAction.setText(actionName);
        } else if (action == ACTION_REGISTER) {
            actionName = getString(R.string.register);
            btnAction.setText(actionName);
        }
        initOnFailureListener();
        //שליטה באנימציה של מעבר אקטיבטי
        overridePendingTransition(false);
    }



    private void initOnFailureListener() {
        // יצירת אובייקט ליסטנר שיאמר מה סוג השגיאה שקרתה
        onFailureListener = new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                if (exception instanceof FirebaseAuthWeakPasswordException) {
                    makeAndShowSnackBar("Weak password least 6 char");

                } else if (exception instanceof FirebaseAuthInvalidCredentialsException) {
                    makeAndShowSnackBar("Email invalid");

                } else if (exception instanceof FirebaseAuthUserCollisionException) {
                    makeAndShowSnackBar("Email exist");

                } else if (exception instanceof FirebaseNetworkException) {
                    makeAndShowSnackBar("No internet");
                } else {
                    makeAndShowSnackBar("Error try Again" + "\n" + exception.getMessage());
                }
                if (snackbar != null) {
                    snackbar.dismiss();
                    snackbar = null;
                }
            }
        };

    }


    //שליטה באנימציה של מעבר אקטיבטי
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(true);
    }


    private void overridePendingTransition(boolean toLeft) {
        if (toLeft) {
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        } else {
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }


    // מאתחל VIEWS
    private void initViews() {
        etPassword = findViewById(R.id.etPassword);
        etEmail = findViewById(R.id.etEmail);
    }


    // בלחיצה  מוריד את המקלדת ומפעיל בדיקה על הפלט שהתקבל
    @Override
    public void onClick(View view) {
        Global.disableKeyBoard(this);
        switch (view.getId()) {
            case R.id.btnAction:
                loginOrRegister();
                break;
        }
    }


    // בודקת שהאימייל תקין, שהסיסמא תקינה ולפחות 6 תווים , בודקת שיש חיבור אינטרנט טוב ולארח מכן לפי סוג הפעולה שרצינו היא מבצעת אם התחברות אם הרשמה
    private void loginOrRegister() {
        String email = etEmail.getText().toString();
        if (!email.isEmpty() && Global.isValidEmailAddress(email)) {
            String password = etPassword.getText().toString();
            if (password.length() > 6) {
                snackbar = Global.makeAndShowDialogProgress(this, actionName + "...", false);

                if (Global.isNetworkOk(this)) {
                    switch (action) {
                        case ACTION_LOGIN:
                            login(email, password);
                            break;
                        case ACTION_REGISTER:
                            register(email, password);
                            break;
                    }
                }else{
                    makeAndShowSnackBar("No internet");
                    snackbar.dismiss();
                }


            } else {
                makeAndShowSnackBar("password must be least 6 char");
            }
        } else {
            makeAndShowSnackBar("Email invalid");
        }
    }

    private void login(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginRegisterActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(LoginRegisterActivity.this, "Successfully Login", Toast.LENGTH_LONG).show();
                    setResult(RESULT_OK);
                    startActivity(new Intent(LoginRegisterActivity.this, ListActivity.class));
                    finish();
                }
                if (snackbar != null) {
                    snackbar.dismiss();
                    snackbar = null;
                }

            }
        }).addOnFailureListener(onFailureListener);
    }

    public void register(String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(LoginRegisterActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    makeAndShowSnackBar("Successfully registered");
                    setResult(RESULT_OK);
                    startActivity(new Intent(LoginRegisterActivity.this, ListActivity.class));
                    finish();
                }
                if (snackbar != null) {
                    snackbar.dismiss();
                    snackbar = null;
                }
            }
        }).addOnFailureListener(onFailureListener);
    }

    public void makeAndShowSnackBar(String message) {
        Global.makeAndShowSnackBar(this, message, "Dismiss", false);
    }


}





