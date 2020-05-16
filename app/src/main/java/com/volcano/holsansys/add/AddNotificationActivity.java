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
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.blankj.utilcode.util.UriUtils.file2Uri;
import static com.blankj.utilcode.util.UriUtils.uri2File;

public class AddNotificationActivity extends AppCompatActivity {
    private Dialog dialog;
    private TextView tinkle;
    private Uri mCameraUri;
    private CheckBox wayPic;
    private CheckBox wayVoice;
    private CheckBox wayText;
    private TimePicker dayTp;
    private Uri mVoiceUri;
    private Uri mTinkleUri;
    private Toast mToast = null;
    private EditText nameNotification;
    private List<String> picSrc;
    private static final int IMG_COUNT = 5;
    private static final String IMG_ADD_TAG = "a";
    private GridView gridView;
    private GVAdapter adapter;
    private EditText picText1;
    private EditText picText2;
    private EditText picText3;
    private EditText picText4;
    private String oriNotificationName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent= getIntent();
        oriNotificationName=intent.getStringExtra("NotificationName");
        setContentView(R.layout.activity_add_notification);
        wayPic=findViewById(R.id.way_pic);
        wayVoice=findViewById(R.id.way_voice);
        wayText=findViewById(R.id.way_text);
        tinkle=findViewById(R.id.tinkle_title);
        dayTp=findViewById(R.id.day_tp);
        nameNotification = findViewById(R.id.name_notification);
        gridView=findViewById(R.id.pic_gv);
        picText1=findViewById(R.id.add_image_1);
        picText2=findViewById(R.id.add_image_2);
        picText3=findViewById(R.id.add_image_3);
        picText4=findViewById(R.id.add_image_4);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                addImage();
            }
        });

        dayTp.setIs24HourView(true);
        wayPic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    findViewById(R.id.way_pic_ll).setVisibility(View.VISIBLE);
                }else {
                    findViewById(R.id.way_pic_ll).setVisibility(View.GONE);
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
            setTitle("新增提醒计划");
            findViewById(R.id.add_notification).setVisibility(View.VISIBLE);
            findViewById(R.id.change_ll).setVisibility(View.GONE);
            if (picSrc == null) {
                picSrc = new ArrayList<>();
                picSrc.add(IMG_ADD_TAG);
            }
            adapter = new GVAdapter();
            gridView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }else if(intent.getStringExtra("kind").equals("change")){
            setTitle("编辑提醒计划");
            findViewById(R.id.add_notification).setVisibility(View.GONE);
            findViewById(R.id.change_ll).setVisibility(View.VISIBLE);
            String[] myParamsArr={"NotificationDetail", MainActivity.userID
                    ,MainActivity.patientName,oriNotificationName};
            VerifyTask myVerifyTask = new VerifyTask();
            myVerifyTask.execute(myParamsArr);
        }

    }

    public void addImage() {
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
        Intent intent = new Intent(Intent.ACTION_PICK
                , android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
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

    private void reDrawLayout(boolean flag){
        LinearLayout picLL = findViewById(R.id.pic_ll);
        if(flag){
            LinearLayout.LayoutParams timeParams =
                    new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,880);
            picLL.setLayoutParams(new LinearLayout.LayoutParams(timeParams));
        }else {
            LinearLayout.LayoutParams timeParams =
                    new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT
                            , ViewGroup.LayoutParams.WRAP_CONTENT);
            picLL.setLayoutParams(new LinearLayout.LayoutParams(timeParams));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode){
            case 1 :
                if(resultCode == RESULT_OK){
                    picSrc.remove(picSrc.size() - 1);
                    picSrc.add(uri2File(mCameraUri).toPath().toString());
                    if(picSrc.size()==2){
                        reDrawLayout(true);
                    }
                    if(picSrc.size()!=IMG_COUNT){
                        picSrc.add(IMG_ADD_TAG);
                    }
                    reDrawEdit();
                    dialog.dismiss();
                    adapter.notifyDataSetChanged();
                }
                break;
            case 2 :
                if (resultCode == RESULT_OK) {
                    //获取选中文件的定位符
                    picSrc.remove(picSrc.size() - 1);
                    picSrc.add(uri2File(data.getData()).toPath().toString());
                    if(picSrc.size()==2){
                        reDrawLayout(true);
                    }
                    if(picSrc.size()!=IMG_COUNT){
                        picSrc.add(IMG_ADD_TAG);
                    }
                    reDrawEdit();
                    dialog.dismiss();
                    adapter.notifyDataSetChanged();
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

    private void reDrawEdit() {
        switch (picSrc.size()-1){
            case 0:
                findViewById(R.id.add_image_1).setVisibility(View.GONE);
                findViewById(R.id.add_image_2).setVisibility(View.GONE);
                findViewById(R.id.add_image_3).setVisibility(View.GONE);
                findViewById(R.id.add_image_4).setVisibility(View.GONE);
                break;
            case 1:
                findViewById(R.id.add_image_1).setVisibility(View.VISIBLE);
                findViewById(R.id.add_image_2).setVisibility(View.GONE);
                findViewById(R.id.add_image_3).setVisibility(View.GONE);
                findViewById(R.id.add_image_4).setVisibility(View.GONE);
                break;
            case 2:
                findViewById(R.id.add_image_1).setVisibility(View.VISIBLE);
                findViewById(R.id.add_image_2).setVisibility(View.VISIBLE);
                findViewById(R.id.add_image_3).setVisibility(View.GONE);
                findViewById(R.id.add_image_4).setVisibility(View.GONE);
                break;
            case 3:
                findViewById(R.id.add_image_1).setVisibility(View.VISIBLE);
                findViewById(R.id.add_image_2).setVisibility(View.VISIBLE);
                findViewById(R.id.add_image_3).setVisibility(View.VISIBLE);
                findViewById(R.id.add_image_4).setVisibility(View.GONE);
                break;
            case 4:
                findViewById(R.id.add_image_1).setVisibility(View.VISIBLE);
                findViewById(R.id.add_image_2).setVisibility(View.VISIBLE);
                findViewById(R.id.add_image_3).setVisibility(View.VISIBLE);
                findViewById(R.id.add_image_4).setVisibility(View.VISIBLE);
                break;
        }
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
            insertOperation("add");
            MainActivity.refreshNotificationFlag =true;
            this.finish();
        }
    }

    public void changeNotification(View view) {
        if(isReady()){
            insertOperation("change");
            MainActivity.refreshNotificationFlag =true;
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
                MainActivity.refreshNotificationFlag =true;
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

    public void insertOperation(String kind){
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

        StringBuilder pictureSrc= new StringBuilder();
        StringBuilder pictureText= new StringBuilder();
        String voiceSrc="";
        String tinkleSrc="";

        for (int i=0;i<picSrc.size();i++){
            if(picSrc.get(i).equals(IMG_ADD_TAG)){
                break;
            }else {
                pictureSrc.append(picSrc.get(i)).append(";");
            }
        }
        switch (picSrc.size()-1){
            case 1:
                if(((EditText)findViewById(R.id.add_image_1)).getText().toString().equals("")){
                    pictureText.append("无文字提醒;");
                }else {
                    pictureText.append(((EditText)findViewById(R.id.add_image_1)).getText().toString()).append(";");
                }
                break;
            case 2:
                if(((EditText)findViewById(R.id.add_image_1)).getText().toString().equals("")){
                    pictureText.append("无文字提醒;");
                }else {
                    pictureText.append(((EditText)findViewById(R.id.add_image_1)).getText().toString()).append(";");
                }
                if(((EditText)findViewById(R.id.add_image_2)).getText().toString().equals("")){
                    pictureText.append("无文字提醒;");
                }else {
                    pictureText.append(((EditText)findViewById(R.id.add_image_2)).getText().toString()).append(";");
                }
                break;
            case 3:
                if(((EditText)findViewById(R.id.add_image_1)).getText().toString().equals("")){
                    pictureText.append("无文字提醒;");
                }else {
                    pictureText.append(((EditText)findViewById(R.id.add_image_1)).getText().toString()).append(";");
                }
                if(((EditText)findViewById(R.id.add_image_2)).getText().toString().equals("")){
                    pictureText.append("无文字提醒;");
                }else {
                    pictureText.append(((EditText)findViewById(R.id.add_image_2)).getText().toString()).append(";");
                }
                if(((EditText)findViewById(R.id.add_image_3)).getText().toString().equals("")){
                    pictureText.append("无文字提醒;");
                }else {
                    pictureText.append(((EditText)findViewById(R.id.add_image_3)).getText().toString()).append(";");
                }
                break;
            case 4:
                if(((EditText)findViewById(R.id.add_image_1)).getText().toString().equals("")){
                    pictureText.append("无文字提醒;");
                }else {
                    pictureText.append(((EditText)findViewById(R.id.add_image_1)).getText().toString()).append(";");
                }
                if(((EditText)findViewById(R.id.add_image_2)).getText().toString().equals("")){
                    pictureText.append("无文字提醒;");
                }else {
                    pictureText.append(((EditText)findViewById(R.id.add_image_2)).getText().toString()).append(";");
                }
                if(((EditText)findViewById(R.id.add_image_3)).getText().toString().equals("")){
                    pictureText.append("无文字提醒;");
                }else {
                    pictureText.append(((EditText)findViewById(R.id.add_image_3)).getText().toString()).append(";");
                }
                if(((EditText)findViewById(R.id.add_image_4)).getText().toString().equals("")){
                    pictureText.append("无文字提醒;");
                }else {
                    pictureText.append(((EditText)findViewById(R.id.add_image_4)).getText().toString()).append(";");
                }
                break;
        }

        if(mVoiceUri!=null)
            voiceSrc=uri2File(mVoiceUri).toPath().toString();

        if(mTinkleUri!=null)
            tinkleSrc=uri2File(mTinkleUri).toPath().toString();

        String notificationVibrate="0";
        if(((CheckBox)findViewById(R.id.vibrate_cb)).isChecked())
            notificationVibrate="1";

        String notificationDayTime;
        if(dayTp.getMinute()==0){
            notificationDayTime=dayTp.getHour()+":"+dayTp.getMinute()+"0";
        }else {
            notificationDayTime=dayTp.getHour()+":"+dayTp.getMinute();
        }

        if(kind.equals("add")){
            String[] myParamsArr={"AddNotification", MainActivity.userID
                    ,nameNotification.getText().toString(),notificationDayTime
                    ,weekNotification,notificationWay, pictureSrc.toString(),pictureText.toString()
                    ,voiceSrc,((EditText)findViewById(R.id.text_ed)).getText().toString()
                    ,tinkleSrc,notificationVibrate,MainActivity.patientName};
            VerifyTask myVerifyTask = new VerifyTask();
            myVerifyTask.execute(myParamsArr);
        }else {
            String[] myParamsArr={"ChangeNotification", MainActivity.userID
                    ,nameNotification.getText().toString(),notificationDayTime
                    ,weekNotification,notificationWay, pictureSrc.toString(),pictureText.toString()
                    ,voiceSrc,((EditText)findViewById(R.id.text_ed)).getText().toString()
                    ,tinkleSrc,notificationVibrate,MainActivity.patientName,oriNotificationName};
            VerifyTask myVerifyTask = new VerifyTask();
            myVerifyTask.execute(myParamsArr);
        }
    }

    private void deleteOperation() {
        String[] myParamsArr = {"DeleteNotification", MainActivity.userID, MainActivity.patientName
                , oriNotificationName};
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
        else if(wayPic.isChecked()&& (picSrc.size()==1) ){
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
            findViewById(R.id.progress_add_notification).setVisibility(View.VISIBLE);
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
                    picSrc = new ArrayList<>();
                    adapter = new GVAdapter();
                    Map<String,String> myMap=myList.get(0);
                    String detailNotificationName=myMap.get("NotificationName");
                    nameNotification.setText(detailNotificationName);
                    String detailDayNotification=myMap.get("DayNotification");
                    dayTp.setHour(Integer.parseInt(detailDayNotification.split(":")[0]));
                    dayTp.setMinute(Integer.parseInt(detailDayNotification.split(":")[1]));
                    String[] detailWeekNotification=myMap.get("WeekNotification").split(",");
                    for(int i=0;i<detailWeekNotification.length;i++){
                        switch (detailWeekNotification[i]){
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
                    String[] detailPictureSrc=myMap.get("PictureSrc").split(";");
                    if(myMap.get("PictureSrc").equals("")){
                        picSrc.add(IMG_ADD_TAG);
                    }else {
                        picSrc.addAll(Arrays.asList(detailPictureSrc));
                        picSrc.add(IMG_ADD_TAG);
                    }
                    if(picSrc.size()>2){
                        reDrawLayout(true);
                    }
                    gridView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    String[] detailPictureText=myMap.get("PictureText").split(";");
                    switch (detailPictureText.length){
                        case 1:
                            picText1.setText(detailPictureText[0]);
                            break;
                        case 2:
                            picText1.setText(detailPictureText[0]);
                            picText2.setText(detailPictureText[1]);
                            break;
                        case 3:
                            picText1.setText(detailPictureText[0]);
                            picText2.setText(detailPictureText[1]);
                            picText3.setText(detailPictureText[2]);
                            break;
                        case 4:
                            picText1.setText(detailPictureText[0]);
                            picText2.setText(detailPictureText[1]);
                            picText3.setText(detailPictureText[2]);
                            picText4.setText(detailPictureText[3]);
                            break;
                    }
                    reDrawEdit();
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
                    findViewById(R.id.progress_add_notification).setVisibility(View.GONE);
                }
                catch (Exception ex){}
            }
            findViewById(R.id.progress_add_notification).setVisibility(View.GONE);
        }
    }

    private class GVAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return picSrc.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getApplication()).inflate(R.layout.grid_item_add_pic, parent, false);
                holder = new ViewHolder();
                holder.imageView = convertView.findViewById(R.id.addImageImg);
                holder.checkBox = convertView.findViewById(R.id.main_gridView_item_cb);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            String s = picSrc.get(position);
            if (!s.equals(IMG_ADD_TAG)) {
                holder.checkBox.setVisibility(View.VISIBLE);
                holder.imageView.setImageURI(Uri.fromFile(
                        new File(s)));
            } else {
                holder.checkBox.setVisibility(View.GONE);
                holder.imageView.setImageResource(R.drawable.ic_photo_upload);
            }
            holder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(picSrc.size()==3){
                        reDrawLayout(false);
                    }
                    picSrc.remove(position);
                    switch (position+1){
                        case 1:
                            picText1.setText(picText2.getText());
                            picText2.setText(picText3.getText());
                            picText3.setText(picText4.getText());
                            picText4.setText("");
                            break;
                        case 2:
                            picText2.setText(picText3.getText());
                            picText3.setText(picText4.getText());
                            picText4.setText("");
                            break;
                        case 3:
                            picText3.setText(picText4.getText());
                            picText4.setText("");
                            break;
                        case 4:
                            picText4.setText("");
                            break;
                    }
                    reDrawEdit();
                    adapter.notifyDataSetChanged();
                }
            });
            return convertView;
        }

        private class ViewHolder {
            ImageView imageView;
            CheckBox checkBox;
        }

    }

}
