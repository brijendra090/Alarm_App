package com.crackcode.ashu.alarmapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

import static android.os.Build.VERSION.SDK_INT;

public class MainActivity extends AppCompatActivity {

    private static final int ALARM_REQUEST_CODE = 0;

    private TimePicker timePicker;
    private Button buttonSetAlarm, buttonCancelAlarm;
    private TextView textViewTv;

    private PendingIntent curAlarmIntent;
    private AlarmManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timePicker = findViewById(R.id.timePicker);
        buttonSetAlarm = findViewById(R.id.buttonSetAlarm);
        buttonCancelAlarm = findViewById(R.id.buttonCancelAlarm);
        textViewTv = findViewById(R.id.tvAlarmInfo);

        manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        timePicker.setIs24HourView(true);

        buttonSetAlarm.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                curAlarmIntent = setAlarm(timePicker.getHour(), timePicker.getMinute());
                buttonSetAlarm.setVisibility(View.GONE);
                buttonCancelAlarm.setVisibility(View.VISIBLE);
            }
        });

        buttonCancelAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (curAlarmIntent != null){
                    manager.cancel(curAlarmIntent);
                    Toast.makeText(MainActivity.this, "Alarm Canceled", Toast.LENGTH_SHORT).show();
                    buttonCancelAlarm.setVisibility(View.GONE);
                    buttonSetAlarm.setVisibility(View.VISIBLE);
                    textViewTv.setText("No Alarm is set");
                }
            }
        });
    }

    private PendingIntent setAlarm(int hour, int minute){
        Intent intent = new Intent(this, AlarmReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, ALARM_REQUEST_CODE,
                intent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_ONE_SHOT);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        if (SDK_INT < Build.VERSION_CODES.KITKAT){
            manager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        } else if (Build.VERSION_CODES.KITKAT <= SDK_INT && SDK_INT < Build.VERSION_CODES.M) {
            manager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        } else if (SDK_INT >= Build.VERSION_CODES.M) {
            manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }

        textViewTv.setText("Alarm is set at "+ hour +":" + minute +":00");
        return pendingIntent;
    }

}