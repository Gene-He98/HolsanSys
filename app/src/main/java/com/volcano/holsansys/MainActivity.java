package com.volcano.holsansys;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.volcano.holsansys.ui.notifications.AddNotificationActivity;

public class MainActivity extends AppCompatActivity {


    public static boolean admin_flag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //检测本地数据库账号密码自动连接是否成功

        // 检查是否登录
        if(!admin_flag){
            isAdmin();
        }else {
            setContentView(R.layout.activity_main);
            initial();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.setting_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.guardian_item:
                Toast.makeText(this,"切换管理员账户",Toast.LENGTH_SHORT).show();
                break;
            case R.id.exit_item:
                System.exit(1);
                break;
            default:
        }
        return true;
    }

    private void initial(){
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

    private void isAdmin(){
        AlertDialog.Builder normalDialog = new AlertDialog.Builder(MainActivity.this);
        normalDialog.setTitle("您暂未登录");
        normalDialog.setMessage("是否登录？");
        normalDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent =new Intent(MainActivity.this,LoginActivity.class);
                Bundle bundle = new Bundle();
                intent.putExtras(bundle);
                startActivityForResult(intent, 200); //两个参数：第一个是意图对象，第二个是请求码requestCode
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
        if(requestCode == 200 && resultCode == 30){  //判断返回码是否是30
            setContentView(R.layout.activity_main);
            initial();
        }
        if(requestCode == 200 && resultCode == RESULT_CANCELED){
            isAdmin();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void Add_Notification(View view) {
        Intent intent =new Intent(MainActivity.this, AddNotificationActivity.class);
        Bundle bundle = new Bundle();
        intent.putExtras(bundle);
        startActivityForResult(intent, 100); //两个参数：第一个是意图对象，第二个是请求码requestCode
    }
}
