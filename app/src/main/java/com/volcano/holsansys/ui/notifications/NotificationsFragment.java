package com.volcano.holsansys.ui.notifications;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.volcano.holsansys.MainActivity;
import com.volcano.holsansys.R;
import com.volcano.holsansys.add.AddNotificationActivity;
import com.volcano.holsansys.tools.WebServiceAPI;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class NotificationsFragment extends Fragment {

    private List<Notification> mData = null;
    private Context mContext;
    private NotificationListAdapter mAdapter = null;
    private ListView list_notification;

    public View onCreateView(@NonNull final LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_notifications, container, false);

        //提醒界面数据更新
        mContext =getActivity();
        list_notification = root.findViewById(R.id.listView_notification);

        list_notification.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent =new Intent(getContext(), AddNotificationActivity.class);
                intent.putExtra("kind","change");
                intent.putExtra("NotificationName",((TextView)view
                        .findViewById(R.id.remark_notification)).getText().toString());
                startActivity(intent);
            }
        });

        String[] myParamsArr ={"NotificationInfo", MainActivity.userID,MainActivity.patientName};
        VerifyTask myVerifyTask = new VerifyTask();
        myVerifyTask.execute(myParamsArr);
        return root;
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
            if(!myResult.equals("[]")){
                Gson myGson = new Gson();
                List<Map<String,String>> myList=myGson.fromJson(myResult, new TypeToken<List<Map<String,String>>>(){}.getType());
                mData = new LinkedList<>();
                try {
                    for (int i=0;i<myList.size();i++){

                        Map<String,String> myMap=myList.get(i);
                        String dayNotification = myMap.get("DayNotification");
                        String notificationName = myMap.get("NotificationName");
                        mData.add(new Notification(dayNotification, notificationName));
                    }

                    mAdapter = new NotificationListAdapter((LinkedList<Notification>) mData, mContext);
                    list_notification.setAdapter(mAdapter);
                }
                catch (Exception ex){}
            }
        }
    }
}