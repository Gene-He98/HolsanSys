package com.volcano.holsansys.ui.notifications;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
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
    private boolean ifVisible;

    public View onCreateView(@NonNull final LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        MainActivity.currentView=1;
        final View root = inflater.inflate(R.layout.fragment_notifications, container, false);
        mContext = getActivity();
        ifVisible=true;
        if(MainActivity.mode){
            if (MainActivity.patientName.equals("")) {
                root.findViewById(R.id.if_notification).setVisibility(View.VISIBLE);
                (root.findViewById(R.id.add_bt)).setVisibility(View.GONE);
                (root.findViewById(R.id.listView_notification)).setVisibility(View.GONE);
            } else {
                list_notification = root.findViewById(R.id.listView_notification);
                list_notification.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(getContext(), AddNotificationActivity.class);
                        intent.putExtra("kind", "change");
                        intent.putExtra("NotificationName", ((TextView) view
                                .findViewById(R.id.remark_notification)).getText().toString());
                        startActivity(intent);
                    }
                });
                String[] myParamsArr = {"NotificationInfo", MainActivity.userID, MainActivity.patientName};
                VerifyTask myVerifyTask = new VerifyTask();
                myVerifyTask.execute(myParamsArr);
            }
        }
        else {
            //语音播报
            if (MainActivity.textToSpeech != null) {
                MainActivity.textToSpeech.setPitch(1.0f);
                MainActivity.textToSpeech.setSpeechRate(1.0f);
                MainActivity.textToSpeech.speak("提醒计划界面"
                        , TextToSpeech.QUEUE_FLUSH, null);
            }
            //绘制界面
            root.findViewById(R.id.add_bt).setVisibility(View.GONE);
            root.findViewById(R.id.notification_bt_emer).setVisibility(View.VISIBLE);
            mContext = getActivity();
            list_notification = root.findViewById(R.id.listView_notification);
            final String[] myParamsArr = {"NotificationInfo", MainActivity.userID, MainActivity.patientName};
            VerifyTask myVerifyTask = new VerifyTask();
            myVerifyTask.execute(myParamsArr);
        }
        //UI刷新线程
        Thread refresh=new Thread() {
            @Override
            public void run() {
                while (ifVisible){
                    if (MainActivity.refreshNotificationFlag){
                        String[] myParamsArr = {"NotificationInfo", MainActivity.userID, MainActivity.patientName};
                        VerifyTask myVerifyTask = new VerifyTask();
                        myVerifyTask.execute(myParamsArr);
                        MainActivity.refreshNotificationFlag =false;
                    }
                    if(MainActivity.mode){
                        list_notification.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Intent intent = new Intent(getContext(), AddNotificationActivity.class);
                                intent.putExtra("kind", "change");
                                intent.putExtra("NotificationName", ((TextView) view
                                        .findViewById(R.id.remark_notification)).getText().toString());
                                startActivity(intent);
                            }
                        });
                    }else {
                        list_notification.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            }
                        });
                    }
                }
            }
        };
        refresh.start();
        return root;

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ifVisible =false;
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
                        String switchNotification = myMap.get("Switch");
                        mData.add(new Notification(dayNotification, notificationName, switchNotification));
                    }

                    mAdapter = new NotificationListAdapter((LinkedList<Notification>) mData, mContext);
                    list_notification.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                }
                catch (Exception ex){}
            }
        }
    }
}