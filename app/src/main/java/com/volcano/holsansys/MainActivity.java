package com.volcano.holsansys;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.volcano.holsansys.add.AddMedicineActivity;
import com.volcano.holsansys.add.AddNotificationActivity;
import com.volcano.holsansys.add.AddPatientActivity;
import com.volcano.holsansys.login.LoginActivity;
import com.volcano.holsansys.tools.AlarmReceiver;
import com.volcano.holsansys.tools.WebServiceAPI;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static android.Manifest.permission;
import static android.Manifest.permission_group.LOCATION;
import static android.Manifest.permission_group.PHONE;
import static android.Manifest.permission_group.SMS;
import static android.Manifest.permission_group.STORAGE;

public class MainActivity extends AppCompatActivity {

    public static Thread bgThread1;
    public static Thread bgThread2;
    public static Thread bgThread3;
    public static boolean admin_flag;
    public static String userID;
    public static String userName;
    public static String patientName = "";
    public static int currentView = 3;
    public static boolean mode = true;
    public static boolean refreshPatientFlag =false;
    public static boolean refreshNotificationFlag =false;
    public static boolean refreshManageFlag =false;
    private boolean kind;
    private Timer timer;
    private TimerTask alarmTask;

    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明定位回调监听器
    public AMapLocationListener mLocationListener;
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(checkSelfPermission(STORAGE)==PackageManager.PERMISSION_DENIED
                ||checkSelfPermission(LOCATION)==PackageManager.PERMISSION_DENIED
                ||checkSelfPermission(SMS)==PackageManager.PERMISSION_DENIED
                ||checkSelfPermission(PHONE)==PackageManager.PERMISSION_DENIED){
            requestPermissions(new String[]{permission.WRITE_EXTERNAL_STORAGE
                    ,permission.ACCESS_FINE_LOCATION,permission.ACCESS_BACKGROUND_LOCATION
                    ,permission.SEND_SMS,permission.CALL_PHONE}
                    , 1);
        }
        if (!admin_flag) {
            isAdmin();
        } else {
            setContentView(R.layout.activity_main);
            initial();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.setting_menu, menu);
        menu.findItem(R.id.guardian_item).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (!MainActivity.patientName.equals("") && mode){
                    menu.findItem(R.id.add_patient_item).setVisible(false);
                    menu.findItem(R.id.add_medicine_item).setVisible(false);
                    addAlarm();
                }else {
                    menu.findItem(R.id.add_patient_item).setVisible(true);
                    menu.findItem(R.id.add_medicine_item).setVisible(true);
                    deleteAlarm();
                }
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.guardian_item:
                if (patientName.equals("")) {
                    AlertDialog.Builder normalDialog = new AlertDialog.Builder(MainActivity.this);
                    normalDialog.setTitle("您尚未选择用药人");
                    normalDialog.setMessage("请选择一位用药人后进入日常模式！");
                    normalDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    normalDialog.setCancelable(false);
                    // 显示
                    normalDialog.show();
                } else {
                    if (mode) {
                        mode = false;
                        switch (currentView) {
                            case 1:
                                findViewById(R.id.add_bt).setVisibility(View.GONE);
                                findViewById(R.id.notification_bt_emer).setVisibility(View.VISIBLE);
                                break;
                            case 2:
                                findViewById(R.id.manage_bt_emer).setVisibility(View.VISIBLE);
                                break;
                            case 3:
                                findViewById(R.id.me_bt_emer).setVisibility(View.VISIBLE);
                                findViewById(R.id.edit_patient).setVisibility(View.GONE);
                                findViewById(R.id.delete_patient).setVisibility(View.GONE);
                                findViewById(R.id.back_main).setVisibility(View.GONE);
                                break;
                        }

                        timer=new Timer();
                        alarmTask=new AlarmTask();
                        //30分钟后每30分钟执行该任务一次
                        timer.schedule(alarmTask,30*60*1000,30*60*1000);
                        item.setTitle("切换至管理模式");
                    } else {
                        mode = true;
                        switch (currentView) {
                            case 1:
                                findViewById(R.id.add_bt).setVisibility(View.VISIBLE);
                                findViewById(R.id.notification_bt_emer).setVisibility(View.GONE);
                                break;
                            case 2:
                                findViewById(R.id.manage_bt_emer).setVisibility(View.GONE);
                                break;
                            case 3:
                                findViewById(R.id.me_bt_emer).setVisibility(View.GONE);
                                findViewById(R.id.edit_patient).setVisibility(View.VISIBLE);
                                findViewById(R.id.delete_patient).setVisibility(View.VISIBLE);
                                findViewById(R.id.back_main).setVisibility(View.VISIBLE);
                                break;
                        }
                        alarmTask.cancel();//取消定时任务
                        item.setTitle("切换至日常模式");
                    }
                }
                break;
            case R.id.add_patient_item:
                Intent intentPatient = new Intent(MainActivity.this, AddPatientActivity.class);
                intentPatient.putExtra("kind", "add");
                startActivity(intentPatient);
                break;
            case R.id.add_medicine_item:
                Intent intentMedicine = new Intent(MainActivity.this, AddMedicineActivity.class);
                intentMedicine.putExtra("kind", "add");
                startActivity(intentMedicine);
                break;
            case R.id.exit_item:
                System.exit(1);
                break;
            default:
        }
        return true;
    }

    private void initial() {
        //初始化底部导航栏
        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_user, R.id.navigation_manage, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this,
                R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this,
                navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }

    private void isAdmin() {
        AlertDialog.Builder normalDialog = new AlertDialog.Builder(MainActivity.this);
        normalDialog.setTitle("您暂未登录");
        normalDialog.setMessage("是否登录？");
        normalDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                Bundle bundle = new Bundle();
                intent.putExtras(bundle);
                startActivityForResult(intent, 200);
            }
        });
        normalDialog.setNegativeButton("关闭", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                System.exit(0);
            }
        });
        normalDialog.setCancelable(false);
        // 显示
        normalDialog.show();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 200 && resultCode == 30) {  //判断返回码是否是30
            userID = data.getStringExtra("userID");
            userName = data.getStringExtra("userName");
            setContentView(R.layout.activity_main);
            initial();
        } else if (requestCode == 200 && resultCode == 404) {
            this.finish();
        } else if (requestCode == 200 && resultCode == RESULT_CANCELED) {
            isAdmin();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void addNotificationBt(View view) {
        Intent intent = new Intent(MainActivity.this, AddNotificationActivity.class);
        intent.putExtra("kind", "add");
        startActivity(intent);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (!patientName.equals("") && currentView == 3 && mode) {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
                MainActivity.patientName = "";
                findViewById(R.id.user_information).setVisibility(View.VISIBLE);
                findViewById(R.id.patients_information).setVisibility(View.GONE);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    //紧急求救按钮
    public void emergencyCall(View view) {
        if(checkSelfPermission(LOCATION)==PackageManager.PERMISSION_DENIED
                ||checkSelfPermission(SMS)==PackageManager.PERMISSION_DENIED
                ||checkSelfPermission(PHONE)==PackageManager.PERMISSION_DENIED){
            requestPermissions(new String[]{permission.ACCESS_FINE_LOCATION
                            ,permission.ACCESS_BACKGROUND_LOCATION
                            ,permission.SEND_SMS,permission.CALL_PHONE}
                    , 2);
        }
        else{
            getAddress();
        }
    }

    //获取地址并拨打电话发送信息
    private void getAddress(){
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);
        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOption.setOnceLocation(true);
        mLocationOption.setOnceLocationLatest(true);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
        SmsManager sms = SmsManager.getDefault();
        PendingIntent pi = PendingIntent.
                getBroadcast(MainActivity.this,0,new Intent(),0);
        sms.sendTextMessage(userID,null,patientName
                +"遇到了紧急情况，位置是"+mLocationClient.getLastKnownLocation().getAddress()
                +"（火山药馆自动发送）",pi,null);
        Intent intent = new Intent();               //创建Intent对象
        intent.setAction(Intent.ACTION_CALL);      //设置动作为拨打电话
        intent.setData(Uri.parse("tel:" + userID));   // 设置要拨打的电话号码
        startActivity(intent);
        mLocationClient.stopLocation();
        mLocationClient.onDestroy();
    }

    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            //启动应用时请求权限
            case 1:
                if (grantResults.length <= 0
                        || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    AlertDialog.Builder normalDialog = new AlertDialog.Builder(MainActivity.this);
                    normalDialog.setTitle("权限申请");
                    normalDialog.setMessage("在权限中开启存储权限，以正常使用软件");
                    normalDialog.setPositiveButton("去开启", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                            intent.setData(uri);
                            startActivity(intent);
                        }
                    });
                    normalDialog.setNegativeButton("不了", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    normalDialog.setCancelable(false);
                    // 显示
                    normalDialog.show();
                }
                else if (grantResults[1] == PackageManager.PERMISSION_DENIED
                        || grantResults[3]== PackageManager.PERMISSION_DENIED
                        || grantResults[4]== PackageManager.PERMISSION_DENIED) {
                    AlertDialog.Builder normalDialog = new AlertDialog.Builder(MainActivity.this);
                    normalDialog.setTitle("权限申请");
                    normalDialog.setMessage("在权限中开启获取位置信息权限及电话短信权限，以保证一键求救功能等的正常使用");
                    normalDialog.setPositiveButton("去开启", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                            intent.setData(uri);
                            startActivity(intent);
                        }
                    });
                    normalDialog.setNegativeButton("不了", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    normalDialog.setCancelable(false);
                    // 显示
                    normalDialog.show();
                }
                break;
            //紧急求救时的权限申请
            case 2:
                if (grantResults.length <= 0
                    || grantResults[0] != PackageManager.PERMISSION_GRANTED
                        || grantResults[2] != PackageManager.PERMISSION_GRANTED){
                    AlertDialog.Builder normalDialog = new AlertDialog.Builder(MainActivity.this);
                    normalDialog.setTitle("权限申请");
                    normalDialog.setMessage("在权限中开启获取位置信息权限及电话短信权限，以保证一键求救功能等的正常使用");
                    normalDialog.setPositiveButton("去开启", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                            intent.setData(uri);
                            startActivity(intent);
                        }
                    });
                    normalDialog.setNegativeButton("不了", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    normalDialog.setCancelable(false);
                    // 显示
                    normalDialog.show();
                }else
                    getAddress();
                break;

        }
    }

    //添加提醒
    public void addAlarm(){
        kind=true;
        String[] myParamsArr = {"NotificationInfo", MainActivity.userID, MainActivity.patientName};
        VerifyTask myVerifyTask = new VerifyTask();
        myVerifyTask.execute(myParamsArr);
    }

    public void deleteAlarm(){
        kind=false;
        String[] myParamsArr = {"NotificationInfo", MainActivity.userID, MainActivity.patientName};
        VerifyTask myVerifyTask = new VerifyTask();
        myVerifyTask.execute(myParamsArr);
    }

    class VerifyTask extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... myParams)
        {
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
        protected void onPostExecute(String myResult)
        {
            if(!myResult.equals("[]") && !myResult.equals("{\"msg\":\"ok\"}")){
                Gson myGson = new Gson();
                List<Map<String,String>> myList=myGson.fromJson(myResult, new TypeToken<List<Map<String,String>>>(){}.getType());
                try {
                    for (int i=0;i<myList.size();i++){
                        Map<String,String> myMap=myList.get(i);
                        String dayNotification = myMap.get("DayNotification");
                        String notificationName = myMap.get("NotificationName");
                        String tinkleSrc =myMap.get("TinkleSrc");
                        String notificationVibrate=myMap.get("NotificationVibrate");

                        Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
                        intent.putExtra("NotificationName",notificationName);
                        intent.putExtra("TinkleSrc",tinkleSrc);
                        intent.putExtra("NotificationVibrate",notificationVibrate);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);
                        AlarmManager am = (AlarmManager) MainActivity.this.getSystemService(Context.ALARM_SERVICE);
                        if(kind){
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTimeInMillis(System.currentTimeMillis());
                            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(dayNotification.split(":")[0]));
                            calendar.set(Calendar.MINUTE, Integer.parseInt(dayNotification.split(":")[1]));
                            calendar.set(Calendar.SECOND, 0);
                            calendar.set(Calendar.MILLISECOND, 0);
                            //获取系统进程
                            am.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                            am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+10000, (24*60*60*1000), pendingIntent);
                        }else {
                            am.cancel(pendingIntent);
                        }
                    }
                }
                catch (Exception ex){}
            }
        }
    }

    class AlarmTask extends TimerTask {
        @Override
        public void run() {
            //初始化定位
            mLocationClient = new AMapLocationClient(getApplicationContext());
            //设置定位回调监听
            mLocationClient.setLocationListener(mLocationListener);
            //初始化AMapLocationClientOption对象
            mLocationOption = new AMapLocationClientOption();
            //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            mLocationOption.setOnceLocation(true);
            mLocationOption.setOnceLocationLatest(true);
            //设置是否返回地址信息（默认返回地址信息）
            mLocationOption.setNeedAddress(true);
            //给定位客户端对象设置定位参数
            mLocationClient.setLocationOption(mLocationOption);
            //启动定位
            mLocationClient.startLocation();
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
            String[] myParamsArr = {"UpdateLocation", MainActivity.userID, MainActivity.patientName
                    ,mLocationClient.getLastKnownLocation().getAddress()};
            (new WebServiceAPI()).ConnectingWebService(myParamsArr);
        }
    }
}

