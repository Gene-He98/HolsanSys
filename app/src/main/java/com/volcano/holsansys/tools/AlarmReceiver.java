package com.volcano.holsansys.tools;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.volcano.holsansys.drug.TimerActivity;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context,"闹钟响了",Toast.LENGTH_SHORT).show();
        Intent alarm = intent;
        alarm.setClass(context, TimerActivity.class);
        alarm.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(alarm);
    }
}
