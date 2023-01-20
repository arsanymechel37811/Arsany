package com.example.reminderapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.LoaderManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.getbase.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.xml.transform.sax.SAXResult;

import kotlin.jvm.internal.Intrinsics;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private FloatingActionButton mAddReminderButton;
    private Toolbar mToolbar;
    AlarmCursorAdapter mCursorAdapter;
    AlarmReminderDbHelper alarmReminderDbHelper = new AlarmReminderDbHelper(this);
    ListView reminderListView;
    ProgressDialog prgDialog;
    private ActivityResultLauncher permissionlauncher;
    private boolean isnot;
    private boolean issch;
    private boolean isalarm;
    private boolean isexact;
    CountDownTimer countDownTimer3;
    CountDownTimer countDownTimer4;
    CountDownTimer countDownTimer5;
    CountDownTimer countDownTimer6;
    CountDownTimer countDownTimer7;
    private Calendar mCalendar;
    private int mHour, mMinute,msec;
    private static final int VEHICLE_LOADER = 0;
    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityResultLauncher var10001 = this.registerForActivityResult((ActivityResultContract)(
                new ActivityResultContracts.RequestMultiplePermissions()),
                (ActivityResultCallback)(new ActivityResultCallback() {
            public void onActivityResult(Object var1) {this.onActivityResult((Map)var1);}
            public final void onActivityResult(Map permissions) {
                MainActivity var10000 = MainActivity.this;
                Boolean var10001 = (Boolean)permissions.get("android.permission.SET_ALARM");
                var10000.isalarm = var10001 != null ? var10001 : MainActivity.this.isalarm;
                var10000 = MainActivity.this;
                var10001 = (Boolean)permissions.get("android.permission.USE_EXACT_ALARM");
                var10000.isexact = var10001 != null ? var10001 : MainActivity.this.isexact;
                var10000 = MainActivity.this;
                var10001 = (Boolean)permissions.get("android.permission.POST_NOTIFICATIONS");
                var10000.isnot = var10001 != null ? var10001 : MainActivity.this.isnot;
                var10000 = MainActivity.this;
                var10001 = (Boolean)permissions.get("android.permission.SCHEDULE_EXACT_ALARM");
                var10000.issch = var10001 != null ? var10001 : MainActivity.this.issch;}}));
        this.permissionlauncher = var10001;
        this.requestpermission();
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setTitle(R.string.app_name);
        reminderListView = (ListView) findViewById(R.id.list);
        View emptyView = findViewById(R.id.empty_view);
        reminderListView.setEmptyView(emptyView);
        mCursorAdapter = new AlarmCursorAdapter(this, null);
        reminderListView.setAdapter(mCursorAdapter);
        reminderListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, AddReminderActivity.class);
                Uri currentVehicleUri = ContentUris.withAppendedId(AlarmReminderContract
                        .AlarmReminderEntry.CONTENT_URI, id);
                // Set the URI on the data field of the intent
                intent.setData(currentVehicleUri);
                startActivity(intent);}});
        mAddReminderButton = (FloatingActionButton) findViewById(R.id.fab);
        mAddReminderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), AddReminderActivity.class);
                startActivity(intent);}});
        getLoaderManager().initLoader(VEHICLE_LOADER, null, this);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("my not","my not",NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }
    @RequiresApi(33)
    private final void requestpermission() {
        this.issch = ContextCompat.checkSelfPermission((Context)this, "android.permission.SCHEDULE_EXACT_ALARM") == 0;
        this.isnot = ContextCompat.checkSelfPermission((Context)this, "android.permission.POST_NOTIFICATIONS") == 0;
        this.isexact = ContextCompat.checkSelfPermission((Context)this, "android.permission.USE_EXACT_ALARM") == 0;
        this.isalarm = ContextCompat.checkSelfPermission((Context)this, "android.permission.SET_ALARM") == 0;
        List permissionrequest = (List)(new ArrayList());
        if (!this.isexact) {
            permissionrequest.add("android.permission.USE_EXACT_ALARM");
        }

        if (!this.isalarm) {
            permissionrequest.add("android.permission.SET_ALARM");
        }

        if (!this.isnot) {
            permissionrequest.add("android.permission.POST_NOTIFICATIONS");
        }

        if (!this.issch) {
            permissionrequest.add("android.permission.SCHEDULE_EXACT_ALARM");
        }

        if (!((Collection)permissionrequest).isEmpty()) {
            ActivityResultLauncher var10000 = this.permissionlauncher;
            if (var10000 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("permissionlauncher");
            }
            Object[] var10001 = ((Collection)permissionrequest).toArray(new String[0]);
            Intrinsics.checkNotNull(var10001,
                    "null cannot be cast to non-null type kotlin.Array<T of kotlin.collections.ArraysKt__ArraysJVMKt.toTypedArray>");
            var10000.launch(var10001);
        }

    }
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                AlarmReminderContract.AlarmReminderEntry._ID,
                AlarmReminderContract.AlarmReminderEntry.KEY_TITLE,
                AlarmReminderContract.AlarmReminderEntry.KEY_DATE,
                AlarmReminderContract.AlarmReminderEntry.KEY_TIME,
                AlarmReminderContract.AlarmReminderEntry.KEY_REPEAT,
                AlarmReminderContract.AlarmReminderEntry.KEY_REPEAT_NO,
                AlarmReminderContract.AlarmReminderEntry.KEY_REPEAT_TYPE,
                AlarmReminderContract.AlarmReminderEntry.KEY_ACTIVE

        };

        return new CursorLoader(this,   // Parent activity context
                AlarmReminderContract.AlarmReminderEntry.CONTENT_URI,   // Provider content URI to query
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mCursorAdapter.swapCursor(cursor);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);

    }
    public void not(View view) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this,"my not");
        builder.setContentTitle("ReminderApp");
        builder.setContentText("Your Date is Now");
        builder.setSmallIcon(R.drawable.clock);
        builder.setAutoCancel(true);
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(MainActivity.this);
        managerCompat.notify(1,builder.build());
    }
    

}
