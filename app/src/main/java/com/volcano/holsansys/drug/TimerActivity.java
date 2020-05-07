package com.volcano.holsansys.drug;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.volcano.holsansys.MainActivity;
import com.volcano.holsansys.R;
import com.volcano.holsansys.tools.WebServiceAPI;

import java.io.File;

import static android.app.PendingIntent.FLAG_CANCEL_CURRENT;
import static com.blankj.utilcode.util.BarUtils.transparentStatusBar;
import static com.blankj.utilcode.util.UriUtils.file2Uri;

public class TimerActivity extends AppCompatActivity {

    private Button timerNow;
    private Button timerLater;
    private MediaPlayer mediaPlayer;
    private String notificationName;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        transparentStatusBar(this);
        setContentView(R.layout.activity_timer);
        timerLater=findViewById(R.id.timer_later);
        timerNow=findViewById(R.id.timer_now);
        intent =getIntent();

        notificationName = intent.getStringExtra("NotificationName");
        Uri mTinkleUri=file2Uri(new File(intent.getStringExtra("TinkleSrc")));
        ((TextView)findViewById(R.id.timer_name)).setText(notificationName);
        timerLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(DrugActivity.delayTimes==3){
                    DrugActivity.delayTimes=0;
                    String[] myParamsArr={"DrugRecord", MainActivity.userID
                            ,MainActivity.patientName,intent.getStringExtra("NotificationName")
                            ,"未服药"};
                    VerifyTask myVerifyTask = new VerifyTask();
                    myVerifyTask.execute(myParamsArr);
                }
                else {
                    Intent intentNew = new Intent(TimerActivity.this, TimerActivity.class);
                    intentNew.putExtra("NotificationName",notificationName);
                    intentNew.putExtra("TinkleSrc",intent.getStringExtra("TinkleSrc"));
                    PendingIntent pendingIntent = PendingIntent.getActivity(TimerActivity.this, 0, intent, FLAG_CANCEL_CURRENT);
                    AlarmManager am = (AlarmManager) TimerActivity.this.getSystemService(Context.ALARM_SERVICE);
                    am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+10000, (24*60*60*1000), pendingIntent);
                    String[] myParamsArr={"DrugRecord", MainActivity.userID
                            ,MainActivity.patientName,notificationName
                            ,"第"+DrugActivity.delayTimes+"次延迟服药"};
                    VerifyTask myVerifyTask = new VerifyTask();
                    myVerifyTask.execute(myParamsArr);
                    DrugActivity.delayTimes++;
                }
            }
        });

        timerNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentNew =new Intent(TimerActivity.this,DrugActivity.class);
                intentNew.putExtra("NotificationName",notificationName);
                intentNew.putExtra("TinkleSrc",intent.getStringExtra("TinkleSrc"));
                mediaPlayer.release();
                startActivity(intentNew);
                finish();
            }
        });


        mediaPlayer = MediaPlayer.create(this,mTinkleUri);
        mediaPlayer.start();

    }

    @Override
    public void finish(){
        mediaPlayer.release();
        super.finish();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if( (keyCode== KeyEvent.KEYCODE_BACK || keyCode==KeyEvent.KEYCODE_HOME )&& event.getAction() == KeyEvent.ACTION_DOWN){
            if(DrugActivity.delayTimes==3){
                DrugActivity.delayTimes=0;
                String[] myParamsArr={"DrugRecord", MainActivity.userID
                        ,MainActivity.patientName,intent.getStringExtra("NotificationName")
                        ,"未服药"};
                VerifyTask myVerifyTask = new VerifyTask();
                myVerifyTask.execute(myParamsArr);
            }
            else {
                Intent intentNew = new Intent(TimerActivity.this, TimerActivity.class);
                intentNew.putExtra("NotificationName",notificationName);
                intentNew.putExtra("TinkleSrc",intent.getStringExtra("TinkleSrc"));
                PendingIntent pendingIntent = PendingIntent.getActivity(TimerActivity.this, 0, intent, FLAG_CANCEL_CURRENT);
                AlarmManager am = (AlarmManager) TimerActivity.this.getSystemService(Context.ALARM_SERVICE);
                am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+10000, (24*60*60*1000), pendingIntent);
                String[] myParamsArr={"DrugRecord", MainActivity.userID
                        ,MainActivity.patientName,notificationName
                        ,"第"+DrugActivity.delayTimes+"次延迟服药"};
                VerifyTask myVerifyTask = new VerifyTask();
                myVerifyTask.execute(myParamsArr);
                DrugActivity.delayTimes++;
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    class VerifyTask extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... myParams) {
            String myResult="";
            try
            {
                myResult = (new WebServiceAPI()).ConnectingWebService(myParams);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return myResult;
        }

        @Override
        protected void onProgressUpdate(Integer... myValues)
        {
            super.onProgressUpdate(myValues);
        }

        @Override
        protected void onPostExecute(String myResult) {
            if(myResult.equals("[{\"msg\":\"ok\"}]")){
                finish();
            }
        }
    }


}
