package com.volcano.holsansys.add;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.volcano.holsansys.MainActivity;
import com.volcano.holsansys.R;
import com.volcano.holsansys.tools.WebServiceAPI;

import java.io.File;
import java.util.List;
import java.util.Map;

import static com.blankj.utilcode.util.UriUtils.file2Uri;
import static com.blankj.utilcode.util.UriUtils.uri2File;

public class AddNotificationActivity extends AppCompatActivity {
    private Dialog dialog;
    private TextView tinkle;
    private ImageView addImage;
    private Uri mCameraUri;
    private Uri mAlbumUri;
    private CheckBox wayPic;
    private CheckBox wayVoice;
    private CheckBox wayText;
    private TimePicker dayTp;
    private boolean imageFlag = false;
    private Uri mVoiceUri;
    private Uri mTinkleUri;
    private Toast mToast = null;
    private EditText nameNotification;
    private TextView addImageText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent= getIntent();
        setContentView(R.layout.activity_add_notification);
        addImage =findViewById(R.id.add_image);
        wayPic=findViewById(R.id.way_pic);
        wayVoice=findViewById(R.id.way_voice);
        wayText=findViewById(R.id.way_text);
        tinkle=findViewById(R.id.tinkle_title);
        dayTp=findViewById(R.id.day_tp);
        nameNotification = findViewById(R.id.name_notification);
        addImageText = findViewById(R.id.add_image_text);

        dayTp.setIs24HourView(true);
        wayPic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    findViewById(R.id.pic_ll).setVisibility(View.VISIBLE);
                }else {
                    findViewById(R.id.pic_ll).setVisibility(View.GONE);
                }
            }
        });

        wayVoice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    findViewById(R.id.voice_ll).setVisibility(View.VISIBLE);
                }else {
                    findViewById(R.id.voice_ll).setVisibility(View.GONE);
                }
            }
        });

        wayText.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    findViewById(R.id.text_ll).setVisibility(View.VISIBLE);
                }else {
                    findViewById(R.id.text_ll).setVisibility(View.GONE);
                }
            }
        });

        if(intent.getStringExtra("kind").equals("add")){
            findViewById(R.id.add_notification).setVisibility(View.VISIBLE);
            findViewById(R.id.change_ll).setVisibility(View.GONE);
        }else if(intent.getStringExtra("kind").equals("change")){
            findViewById(R.id.add_notification).setVisibility(View.GONE);
            findViewById(R.id.change_ll).setVisibility(View.VISIBLE);
            String[] myParamsArr={"NotificationDetail", MainActivity.userID
                    ,MainActivity.patientName,intent.getStringExtra("NotificationName")};
            VerifyTask myVerifyTask = new VerifyTask();
            myVerifyTask.execute(myParamsArr);
        }

    }

    public void addImage(View view) {
        dialog = new Dialog(this,R.style.DialogTheme);
        //填充对话框的布局
        View inflate = LayoutInflater.from(this).inflate(R.layout.bottom_dialog, null);
        //初始化控件
        TextView camera = (TextView) inflate.findViewById(R.id.camera);
        TextView pic = (TextView) inflate.findViewById(R.id.pic);
        TextView cancel = (TextView) inflate.findViewById(R.id.cancel);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSysCamera();
            }
        });
        pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSysAlbum();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        //将布局设置给Dialog
        dialog.setContentView(inflate);
        //获取当前Activity所在的窗体
        Window dialogWindow = dialog.getWindow();
        //设置Dialog从窗体底部弹出
        dialogWindow.setGravity( Gravity.BOTTOM);
        //获得窗体的属性
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.y = 20;//设置Dialog距离底部的距离
//       将属性设置给窗体
        dialogWindow.setAttributes(lp);
        dialog.show();//显示对话框
    }

    private void openSysCamera() {
        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 判断是否有相机
        if (captureIntent.resolveActivity(getPackageManager()) != null) {
            Uri photoUri;

            photoUri = createImageUri();

            mCameraUri = photoUri;
            if (photoUri != null) {
                captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                captureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                startActivityForResult(captureIntent, 1);
            }
        }
    }

    private void openSysAlbum() {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 2);
    }

    private Uri createImageUri() {
        String status = Environment.getExternalStorageState();
        // 判断是否有SD卡,优先使用SD卡存储,当没有SD卡时使用手机存储
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            return getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new ContentValues());
        } else {
            return getContentResolver().insert(MediaStore.Images.Media.INTERNAL_CONTENT_URI, new ContentValues());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode){
            case 1 :
                if(resultCode == RESULT_OK){
                    addImage.setImageURI(mCameraUri);
                    mAlbumUri=null;
                    dialog.dismiss();
                    imageFlag=true;
                }
                break;
            case 2 :
                if (resultCode == RESULT_OK) {
                    //获取选中文件的定位符
                    mAlbumUri = data.getData();
                    mCameraUri=null;
                    addImage.setImageURI(mAlbumUri);
                    dialog.dismiss();
                    imageFlag=true;
                }
                break;
            case 3 :
                if (resultCode == RESULT_OK) {
                    mTinkleUri=data.getData();
                    ((TextView) findViewById(R.id.tinkle_title)).setText(getVoiceTitle(mTinkleUri));
                }
                break;

            case 4 :
                if (resultCode == RESULT_OK) {
                    mVoiceUri =data.getData();
                    ((TextView) findViewById(R.id.voice_tv)).setText(getVoiceTitle(mVoiceUri));
                }
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void addVoice(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
        startActivityForResult(intent, 3);
    }

    public void addVoiceNotification(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
        startActivityForResult(intent, 4);
    }

    public String getVoiceTitle(Uri uri){
        final String scheme = uri.getScheme();
        String title = null;
        if ( scheme == null )
            title = uri.getPath();
        else if ( ContentResolver.SCHEME_FILE.equals( scheme ) ) {
            title = uri.getPath();
        } else if ( ContentResolver.SCHEME_CONTENT.equals( scheme ) ) {
            Cursor cursor = this.getContentResolver().query( uri, new String[] { OpenableColumns.DISPLAY_NAME }, null, null, null );
            if ( null != cursor ) {
                if ( cursor.moveToFirst() ) {
                    title = cursor.getString(0);
                }
                cursor.close();
            }
        }
        return title;

    }

    public void addNotification(View view) {
        if (isReady()){
            insertOperation();
            MainActivity.addNotification=true;
            this.finish();
        }
    }

    public void changeNotification(View view) {
        if(isReady()){
            deleteOperation();
            insertOperation();
            MainActivity.addNotification=true;
            this.finish();
        }
    }

    public void deleteNotification(View view) {
        AlertDialog.Builder normalDialog = new AlertDialog.Builder(AddNotificationActivity.this);
        normalDialog.setTitle("删除提醒计划");
        normalDialog.setMessage("是否确定删除？");
        normalDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteOperation();
                MainActivity.addNotification=true;
                AddNotificationActivity.this.finish();
            }
        });
        normalDialog.setNegativeButton("关闭", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        normalDialog.setCancelable(false);
        // 显示
        normalDialog.show();
    }

    public void insertOperation(){
        String weekNotification="";
        String notificationWay="";
        if(((CheckBox)findViewById(R.id.week_mon)).isChecked())
            weekNotification += "一,";
        if(((CheckBox)findViewById(R.id.week_tue)).isChecked())
            weekNotification += "二,";
        if(((CheckBox)findViewById(R.id.week_wed)).isChecked())
            weekNotification += "三,";
        if(((CheckBox)findViewById(R.id.week_thu)).isChecked())
            weekNotification += "四,";
        if(((CheckBox)findViewById(R.id.week_fri)).isChecked())
            weekNotification += "五,";
        if(((CheckBox)findViewById(R.id.week_sat)).isChecked())
            weekNotification += "六,";
        if(((CheckBox)findViewById(R.id.week_sun)).isChecked())
            weekNotification += "日";

        if(((CheckBox)findViewById(R.id.way_pic)).isChecked())
            notificationWay += "图文提醒,";
        else {
            mCameraUri=null;
            addImageText.setText("");
        }
        if(((CheckBox)findViewById(R.id.way_voice)).isChecked())
            notificationWay += "语音提醒,";
        else {
            mVoiceUri=null;
        }
        if(((CheckBox)findViewById(R.id.way_text)).isChecked())
            notificationWay += "文字提醒";
        else {
            ((EditText)findViewById(R.id.text_ed)).setText("");
        }

        String pictureSrc= "";
        String voiceSrc="";
        String tinkleSrc="";

        if(mCameraUri != null){
            pictureSrc=uri2File(mCameraUri).toPath().toString();
        }else if(mAlbumUri != null)
            pictureSrc=uri2File(mAlbumUri).toPath().toString();

        if(mVoiceUri!=null)
            voiceSrc=uri2File(mVoiceUri).toPath().toString();

        if(mTinkleUri!=null)
            tinkleSrc=uri2File(mTinkleUri).toPath().toString();

        String notificationVibrate="0";
        if(((CheckBox)findViewById(R.id.vibrate_cb)).isChecked())
            notificationVibrate="1";

        String[] myParamsArr={"AddNotification", MainActivity.userID
                ,nameNotification.getText().toString(),dayTp.getHour()+":"+dayTp.getMinute()
                ,weekNotification,notificationWay,pictureSrc,addImageText.getText().toString()
                ,voiceSrc,((EditText)findViewById(R.id.text_ed)).getText().toString()
                ,tinkleSrc,notificationVibrate,MainActivity.patientName};

        VerifyTask myVerifyTask = new VerifyTask();
        myVerifyTask.execute(myParamsArr);
    }

    private void deleteOperation() {
        String[] myParamsArr = {"DeleteNotification", MainActivity.userID, MainActivity.patientName
                , nameNotification.getText().toString()};
        VerifyTask myVerifyTask = new VerifyTask();
        myVerifyTask.execute(myParamsArr);
    }

    private boolean isReady(){
        if(nameNotification.getText().toString().equals("")){
            Toast toast=Toast.makeText(this, "请输入提醒计划名称！", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }
        else if(!wayPic.isChecked() &&!wayVoice.isChecked() &&!wayText.isChecked()){
            Toast toast=Toast.makeText(this, "请选择提醒方式！", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }
        else if(wayPic.isChecked()&& (!imageFlag || addImageText.getText().toString().equals("")) ){
            Toast toast=Toast.makeText(this, "请完善图文提醒！", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }
        else if(wayVoice.isChecked()&& ((TextView)findViewById(R.id.voice_tv)).getText().toString().equals("")){
            Toast toast=Toast.makeText(this, "请完善语音提醒！", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }
        else if(wayText.isChecked()&& ((EditText)findViewById(R.id.text_ed)).getText().toString().equals("")){
            Toast toast=Toast.makeText(this, "请完善文字提醒！", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }
        else if(tinkle.getText().equals("无")){
            Toast toast=Toast.makeText(this, "尚未设置铃声！", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }
        else return true;

        return false;
    }

    class VerifyTask extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            findViewById(R.id.progress_add).setVisibility(View.VISIBLE);
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
            //查询结果为成功，则跳转到主页面
            if(myResult.equals("[{\"msg\":\"ok\"}]")){
                //AddNotificationActivity.this.finish();
            }
            else if (myResult.equals("[{\"msg\":\"error\"}]")){
                if (mToast == null) {
                    mToast=Toast.makeText(AddNotificationActivity.this,
                            "添加失败，请稍后重试！",Toast.LENGTH_SHORT);
                    mToast.setGravity(Gravity.CENTER, 0, 0);
                    TextView v = (TextView) mToast.getView().findViewById(android.R.id.message);
                    v.setTextColor(Color.RED);     //设置字体颜色
                    v.setTextSize(20);
                }
                else {
                    mToast.setDuration(Toast.LENGTH_SHORT);
                }
                mToast.show();
            }
            else{
                Gson myGson = new Gson();
                List<Map<String,String>> myList=myGson.fromJson(myResult, new TypeToken<List<Map<String,String>>>(){}.getType());
                try {
                    Map<String,String> myMap=myList.get(0);
                    String detailNotificationName=myMap.get("NotificationName");
                    nameNotification.setText(detailNotificationName);
                    String detailDayNotification=myMap.get("DayNotification");
                    dayTp.setHour(Integer.parseInt(detailDayNotification.split(":")[0]));
                    dayTp.setMinute(Integer.parseInt(detailDayNotification.split(":")[1]));
                    String[] detailWeekNotificaction=myMap.get("WeekNotificaction").split(",");
                    for(int i=0;i<detailWeekNotificaction.length;i++){
                        switch (detailWeekNotificaction[i]){
                            case "一" :
                                ((CheckBox)findViewById(R.id.week_mon)).setChecked(true);
                                break;
                            case "二" :
                                ((CheckBox)findViewById(R.id.week_tue)).setChecked(true);
                                break;
                            case "三" :
                                ((CheckBox)findViewById(R.id.week_wed)).setChecked(true);
                                break;
                            case "四" :
                                ((CheckBox)findViewById(R.id.week_thu)).setChecked(true);
                                break;
                            case "五" :
                                ((CheckBox)findViewById(R.id.week_fri)).setChecked(true);
                                break;
                            case "六" :
                                ((CheckBox)findViewById(R.id.week_sat)).setChecked(true);
                                break;
                            case "日" :
                                ((CheckBox)findViewById(R.id.week_sun)).setChecked(true);
                                break;
                        }
                    }
                    String[] detailNotificationWay=myMap.get("NotificationWay").split(",");
                    for(int i=0;i<detailNotificationWay.length;i++){
                        switch (detailNotificationWay[i]){
                            case "图文提醒" :
                                wayPic.setChecked(true);
                                break;
                            case "语音提醒" :
                                wayVoice.setChecked(true);
                                break;
                            case "文字提醒" :
                                wayText.setChecked(true);
                                break;
                        }
                    }
                    String detailPictureSrc=myMap.get("PictureSrc");
                    String detailPictureText=myMap.get("PictureText");
                    if(!detailPictureSrc.equals("")){
                        mCameraUri=Uri.fromFile(
                                new File(detailPictureSrc));
                        addImage.setImageURI(mCameraUri);
                        addImageText.setText(detailPictureText);
                        imageFlag=true;
                    }
                    String detailVoiceSrc=myMap.get("VoiceSrc");
                    if(!detailVoiceSrc.equals("")){
                        mVoiceUri=file2Uri(new File(detailVoiceSrc));
                        ((TextView) findViewById(R.id.voice_tv))
                                .setText(getVoiceTitle(mVoiceUri));
                    }
                    String detailTextText=myMap.get("TextText");
                    if(!detailTextText.equals("")){
                        ((EditText)findViewById(R.id.text_ed)).setText(detailTextText);
                    }
                    String detailTinkleSrc=myMap.get("TinkleSrc");
                    mTinkleUri=file2Uri(new File(detailTinkleSrc));
                    ((TextView) findViewById(R.id.tinkle_title))
                            .setText(getVoiceTitle(mTinkleUri));
                    String detailNotificationVibrate=myMap.get("NotificationVibrate");
                    if(detailNotificationVibrate.equals("1"))
                        ((CheckBox)findViewById(R.id.vibrate_cb)).setChecked(true);
                    findViewById(R.id.progress_add).setVisibility(View.GONE);
                }
                catch (Exception ex){}
            }
        }
    }
}
