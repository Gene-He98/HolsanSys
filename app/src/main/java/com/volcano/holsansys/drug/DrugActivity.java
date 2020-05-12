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
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.volcano.holsansys.MainActivity;
import com.volcano.holsansys.R;
import com.volcano.holsansys.tools.WebServiceAPI;

import java.io.File;
import java.util.List;
import java.util.Map;

import static android.app.AlarmManager.INTERVAL_FIFTEEN_MINUTES;
import static android.app.PendingIntent.FLAG_CANCEL_CURRENT;
import static com.blankj.utilcode.util.BarUtils.transparentStatusBar;
import static com.blankj.utilcode.util.FileUtils.getFileName;
import static com.blankj.utilcode.util.UriUtils.file2Uri;

public class DrugActivity extends AppCompatActivity {

    private Button drugLater;
    private Button drugNow;
    private MediaPlayer mediaPlayer;
    private Intent intent;
    public static int delayTimes=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        transparentStatusBar(this);
        setContentView(R.layout.activity_drug);
        drugLater=findViewById(R.id.drug_later);
        drugNow=findViewById(R.id.drug_now);
        intent =getIntent();
        String[] myParamsArr={"NotificationDetail", MainActivity.userID
                ,MainActivity.patientName,intent.getStringExtra("NotificationName")};
        VerifyTask myVerifyTask = new VerifyTask();
        myVerifyTask.execute(myParamsArr);

        drugLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(delayTimes==3){
                    delayTimes=0;
                    String[] myParamsArr={"DrugRecord", MainActivity.userID
                            ,MainActivity.patientName,intent.getStringExtra("NotificationName")
                            ,"未服药"};
                    VerifyTask myVerifyTask = new VerifyTask();
                    myVerifyTask.execute(myParamsArr);
                }
                else {
                    Intent intentNew = new Intent(DrugActivity.this, TimerActivity.class);
                    intentNew.putExtra("NotificationName",intent.getStringExtra("NotificationName"));
                    intentNew.putExtra("TinkleSrc",intent.getStringExtra("TinkleSrc"));
                    PendingIntent pendingIntent = PendingIntent.getActivity(DrugActivity.this, 0, intent, FLAG_CANCEL_CURRENT);
                    AlarmManager am = (AlarmManager) DrugActivity.this.getSystemService(Context.ALARM_SERVICE);
                    am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+INTERVAL_FIFTEEN_MINUTES , pendingIntent);
                    String[] myParamsArr={"DrugRecord", MainActivity.userID
                            ,MainActivity.patientName,intent.getStringExtra("NotificationName")
                            ,"第"+delayTimes+"次延迟服药"};
                    VerifyTask myVerifyTask = new VerifyTask();
                    myVerifyTask.execute(myParamsArr);
                    delayTimes++;
                }
            }
        });

        drugNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delayTimes=0;
                //发送服药记录给服务器
                String[] myParamsArr={"DrugRecord", MainActivity.userID
                        ,MainActivity.patientName,intent.getStringExtra("NotificationName")
                        ,"服药成功"};
                VerifyTask myVerifyTask = new VerifyTask();
                myVerifyTask.execute(myParamsArr);
            }
        });

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
            }else {
                Gson myGson = new Gson();
                List<Map<String, String>> myList = myGson.fromJson(myResult, new TypeToken<List<Map<String, String>>>() {
                }.getType());
                try {
                    Map<String, String> myMap = myList.get(0);
                    String detailPictureSrc = myMap.get("PictureSrc");
                    String detailPictureText = myMap.get("PictureText");
                    String detailVoiceSrc = myMap.get("VoiceSrc");
                    String detailTextText = myMap.get("TextText");
                    String[] detailNotificationWay = myMap.get("NotificationWay").split(",");
                    for (int i = 0; i < detailNotificationWay.length; i++) {
                        switch (detailNotificationWay[i]) {
                            case "图文提醒":
                                ImageView pic_iv = findViewById(R.id.drug_pic_iv);
                                TextView pic_tv = findViewById(R.id.drug_pic_tv);
                                pic_iv.setVisibility(View.VISIBLE);
                                pic_iv.setImageURI(Uri.fromFile(
                                        new File(detailPictureSrc)));
                                pic_tv.setVisibility(View.VISIBLE);
                                pic_tv.setText(detailPictureText);
                                break;
                            case "语音提醒":
                                TextView voice_tv = findViewById(R.id.drug_voice);
                                voice_tv.setVisibility(View.VISIBLE);
                                Uri mVoiceUri = file2Uri(new File(detailVoiceSrc));
                                mediaPlayer = MediaPlayer.create(DrugActivity.this, mVoiceUri);
                                mediaPlayer.start();
                                voice_tv.setText("正在播放：" + getFileName(detailVoiceSrc));
                                voice_tv.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (mediaPlayer.isPlaying())
                                            mediaPlayer.pause();
                                        else
                                            mediaPlayer.start();
                                    }
                                });
                                break;
                            case "文字提醒":
                                TextView text_tv = findViewById(R.id.drug_tv);
                                text_tv.setVisibility(View.VISIBLE);
                                text_tv.setText(detailTextText);
                                break;
                        }
                    }
                } catch (Exception ex) {
                }
            }
        }
    }

    @Override
    public void finish(){
        if(mediaPlayer!=null)
            mediaPlayer.release();
        super.finish();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if( (keyCode== KeyEvent.KEYCODE_BACK || keyCode==KeyEvent.KEYCODE_HOME )&& event.getAction() == KeyEvent.ACTION_DOWN){
            if(delayTimes==3){
                delayTimes=0;
                String[] myParamsArr={"DrugRecord", MainActivity.userID
                        ,MainActivity.patientName,intent.getStringExtra("NotificationName")
                        ,"未服药"};
                VerifyTask myVerifyTask = new VerifyTask();
                myVerifyTask.execute(myParamsArr);
            }
            else {
                Intent intentNew = new Intent(DrugActivity.this, TimerActivity.class);
                intentNew.putExtra("NotificationName",intent.getStringExtra("NotificationName"));
                intentNew.putExtra("TinkleSrc",intent.getStringExtra("TinkleSrc"));
                PendingIntent pendingIntent = PendingIntent.getActivity(DrugActivity.this, 0, intent, FLAG_CANCEL_CURRENT);
                AlarmManager am = (AlarmManager) DrugActivity.this.getSystemService(Context.ALARM_SERVICE);
                am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+INTERVAL_FIFTEEN_MINUTES, pendingIntent);
                String[] myParamsArr={"DrugRecord", MainActivity.userID
                        ,MainActivity.patientName,intent.getStringExtra("NotificationName")
                        ,"第"+delayTimes+"次延迟服药"};
                VerifyTask myVerifyTask = new VerifyTask();
                myVerifyTask.execute(myParamsArr);
                delayTimes++;
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}
