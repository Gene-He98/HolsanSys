package com.volcano.holsansys;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.volcano.holsansys.add.AddNotificationActivity;
import com.volcano.holsansys.add.AddPatientActivity;
import com.volcano.holsansys.login.LoginActivity;

import static android.Manifest.permission;
import static android.Manifest.permission_group.LOCATION;
import static android.Manifest.permission_group.SMS;
import static android.Manifest.permission_group.STORAGE;

public class MainActivity extends AppCompatActivity {


    public static boolean admin_flag;
    public static String userID;
    public static String userName;
    public static String patientName = "";
    public static int currentView = 3;
    public static boolean mode = true;
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
                ||checkSelfPermission(LOCATION)==PackageManager.PERMISSION_DENIED){
            requestPermissions(new String[]{permission.WRITE_EXTERNAL_STORAGE
                    ,permission.ACCESS_FINE_LOCATION,permission.ACCESS_BACKGROUND_LOCATION}
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.setting_menu, menu);
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
                        item.setTitle("切换至日常模式");
                    }
                }
                break;
            case R.id.add_patient_item:
                Intent intent = new Intent(MainActivity.this, AddPatientActivity.class);
                intent.putExtra("kind", "add");
                startActivity(intent);
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

    public void emergencyCall(View view) {
        if(checkSelfPermission(LOCATION)==PackageManager.PERMISSION_DENIED
                ||checkSelfPermission(SMS)==PackageManager.PERMISSION_DENIED){
            requestPermissions(new String[]{permission.ACCESS_FINE_LOCATION
                            ,permission.ACCESS_BACKGROUND_LOCATION,permission.SEND_SMS}
                    , 2);
            getAddress();


        }
        else{
            getAddress();
        }
    }

    private void getAddress(){
        mLocationListener = new AMapLocationListener(){
            @Override
            public void onLocationChanged(AMapLocation amapLocation) {
                if (amapLocation != null) {
                    if (amapLocation.getErrorCode() == 0) {
                        /*amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                        amapLocation.getLatitude();//获取纬度
                        amapLocation.getLongitude();//获取经度
                        amapLocation.getAccuracy();//获取精度信息*/
                        SmsManager sms = SmsManager.getDefault();
                        PendingIntent pi = PendingIntent.
                                getBroadcast(MainActivity.this,0,new Intent(),0);
                        sms.sendTextMessage(userID,null,patientName+"遇到了紧急情况，我的位置是"+amapLocation.getAddress()+"（火山药馆自动发送）",pi,null);//分别发送每一条短信
                        mLocationClient.stopLocation();
                        mLocationClient.onDestroy();
                        /*amapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
                        amapLocation.getCountry();//国家信息
                        amapLocation.getProvince();//省信息
                        amapLocation.getCity();//城市信息
                        amapLocation.getDistrict();//城区信息
                        amapLocation.getStreet();//街道信息
                        amapLocation.getStreetNum();//街道门牌号信息
                        amapLocation.getCityCode();//城市编码
                        amapLocation.getAdCode();//地区编码
                        amapLocation.getAoiName();//获取当前定位点的AOI信息
                        amapLocation.getBuildingId();//获取当前室内定位的建筑物Id
                        amapLocation.getFloor();//获取当前室内定位的楼层
                        amapLocation.getGpsAccuracyStatus();//获取GPS的当前状态
                        //获取定位时间
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date date = new Date(amapLocation.getTime());
                        df.format(date);*/
                    }else {
                        //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                        Log.e("AmapError","location Error, ErrCode:"
                                + amapLocation.getErrorCode() + ", errInfo:"
                                + amapLocation.getErrorInfo());
                    }
                }
            }
        };
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
    }

    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
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
                            System.exit(0);
                        }
                    });
                    normalDialog.setCancelable(false);
                    // 显示
                    normalDialog.show();
                }
                else if (grantResults[1] != PackageManager.PERMISSION_GRANTED ) {
                    AlertDialog.Builder normalDialog = new AlertDialog.Builder(MainActivity.this);
                    normalDialog.setTitle("权限申请");
                    normalDialog.setMessage("在权限中开启获取位置信息权限，以保证一键求救功能等的正常使用");
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
                            System.exit(0);
                        }
                    });
                    normalDialog.setCancelable(false);
                    // 显示
                    normalDialog.show();
                }
                break;
            case 2:
                if (grantResults.length <= 0
                    || grantResults[0] != PackageManager.PERMISSION_GRANTED
                        || grantResults[3] != PackageManager.PERMISSION_GRANTED){
                    AlertDialog.Builder normalDialog = new AlertDialog.Builder(MainActivity.this);
                    normalDialog.setTitle("权限申请");
                    normalDialog.setMessage("在权限中开启获取位置信息权限及短信权限，以保证一键求救功能等的正常使用");
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
                            System.exit(0);
                        }
                    });
                    normalDialog.setCancelable(false);
                    // 显示
                    normalDialog.show();
                }
                else {

                }
                break;

        }
    }
}

