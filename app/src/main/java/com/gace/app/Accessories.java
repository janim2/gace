package com.gace.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Accessories {

    Context context;
    private static final String SP_NAME = "appStore";
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    public Accessories(Context context){
        this.context = context;
        preferences = context.getSharedPreferences(SP_NAME, 0);
    }

    /*
    SHARED PREFERENCES START HERE:
     */

    //Clear User Data
    public boolean clearStore(){
        editor = preferences.edit();
        editor.clear();
        return editor.commit();
    }

    //Put String Values Into Store
    public void put(String key, String value){
        editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    //Retrieve String Values From Store
    public String getString(String key){
        return preferences.getString(key, "");
    }

    //Put Boolean Values Into Store
    public void put(String key, boolean value){
        editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    //Retrieve Boolean Values From Store
    public boolean getBoolean(String key){
        return preferences.getBoolean(key, false);
    }
    /*
    SHARED PREFERENCES END HERE:
     */
}