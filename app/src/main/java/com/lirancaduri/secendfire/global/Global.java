package com.lirancaduri.secendfire.global;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;

import java.net.HttpURLConnection;
import java.net.URL;

import static android.content.Context.MODE_PRIVATE;

public class Global {
    public static final String NEW_SHIFTS = "NewShifts";
    private static final String KEY_PREFERENCES = "KEY_PREFERENCES";
    public static final String REPLACE_SHIFT = "REPLACE_SHIFT";




    // שמירה SharedPreferences
    public static void saveInSharedPreferences(Context context, String tag, Object value, boolean inBackgroundProcess) {
        SharedPreferences sharedPref = context.getSharedPreferences(Global.KEY_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(tag, String.valueOf(value));
        if (inBackgroundProcess) {
            editor.apply();
        }else{
            editor.commit();
        }
    }


    public static String getFromSharedPreferences(Context context, String tag) {
        return context.getSharedPreferences(Global.KEY_PREFERENCES, MODE_PRIVATE).getString(tag, null);
    }

    public static String getFromSharedPreferences(Context context, String tag, Object valueDefault) {
        return context.getSharedPreferences(Global.KEY_PREFERENCES, MODE_PRIVATE).getString(tag, String.valueOf(valueDefault));
    }


    public static Integer getIntegerFromPreference(Context context, String tag, Integer valueDefault){
        return Integer.valueOf(getFromSharedPreferences(context, tag,valueDefault));
    }

    public static Boolean getBooleanFromPreference(Context context, String tag, Boolean valueDefault){
        return Boolean.valueOf(getFromSharedPreferences(context, tag,valueDefault));
    }




    // יוצר SNACKBAR עם דיאלוג פרוגסס
    public static Snackbar makeAndShowDialogProgress(Activity activity, String message, boolean isRTL) {
        Snackbar snackDialogProgress = Snackbar.make(activity.findViewById(android.R.id.content), message, Snackbar.LENGTH_INDEFINITE);
        ViewGroup contentLay = (ViewGroup) snackDialogProgress.getView().findViewById(android.support.design.R.id.snackbar_text).getParent();
        ProgressBar item = new ProgressBar(activity);
        contentLay.addView(item);
        snackDialogProgress.show();
        return snackDialogProgress;
    }

    public static void makeAndShowSnackBar(Activity activity, String message, String dismissMessage, boolean isRTL) {
        makeAndShowSnackBar(activity, message, dismissMessage, isRTL, null);
    }


    public static boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static void makeAndShowSnackBar(Activity activity, String message, String dismissMessage, boolean isRTL, final View.OnClickListener onClickListener) {
        final Snackbar snackBar = Snackbar.make(activity.findViewById(android.R.id.content), message, Snackbar.LENGTH_INDEFINITE);
        snackBar.setAction(dismissMessage, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onClickListener != null) {
                    onClickListener.onClick(view);
                }
                snackBar.dismiss();
            }
        });
        snackBar.show();
    }

    // מקבל אקטיבטי בודק איזה VIES בפוקוס אם יש כזה ומוריד ממנו את המקלדת
    public static void disableKeyBoard(Activity activity){
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }




    public static boolean isNetworkOk(Context context){
        // כמעט לכל אובייקט MANAGER של אנדראויד נקבל בצורה כזאת עם איזשהו קבוע
        // בודק אם יש אינטרנט
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
