package com.volcano.holsansys.ui.notifications;


import android.content.Context;
import android.os.AsyncTask;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.volcano.holsansys.MainActivity;
import com.volcano.holsansys.R;
import com.volcano.holsansys.tools.WebServiceAPI;

import java.util.LinkedList;

public class NotificationListAdapter extends BaseAdapter {

    private LinkedList<Notification> mData;
    private Context mContext;

    public NotificationListAdapter(LinkedList<Notification> mData, Context mContext) {
        this.mData = mData;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_notification,parent,false);
            holder = new ViewHolder();
            holder.time_notification = convertView.findViewById(R.id.time_notification);
            holder.remark_notification = convertView.findViewById(R.id.remark_notification);
            holder.switch_notification = convertView.findViewById(R.id.switch_notification);
            convertView.setTag(holder);   //将Holder存储到convertView中
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        holder.time_notification.setText(mData.get(position).getTime());
        holder.remark_notification.setText(mData.get(position).getRemark());
        if(mData.get(position).getSwitch().equals("1")){
            holder.switch_notification.setChecked(true);
        }else {
            holder.switch_notification.setChecked(false);
        }
        final ViewHolder finalHolder = holder;
        holder.switch_notification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!buttonView.isPressed()){
                    return;
                }
                String[] myParamsArr = {"ChangeNotificationSwitch", MainActivity.userID
                        , MainActivity.patientName,isChecked+""
                        , finalHolder.remark_notification.getText().toString()};
                VerifyTask myVerifyTask = new VerifyTask();
                myVerifyTask.execute(myParamsArr);
                MainActivity.refreshNotificationFlag=true;
                if(!MainActivity.mode){
                    if(MainActivity.textToSpeech!=null){
                        if(isChecked){
                            MainActivity.textToSpeech.setPitch(1.0f);
                            MainActivity.textToSpeech.setSpeechRate(1.0f);
                            MainActivity.textToSpeech.speak("开启闹钟"
                                    , TextToSpeech.QUEUE_FLUSH, null);
                        }else {
                            MainActivity.textToSpeech.setPitch(1.0f);
                            MainActivity.textToSpeech.setSpeechRate(1.0f);
                            MainActivity.textToSpeech.speak("取消闹钟"
                                    , TextToSpeech.QUEUE_FLUSH, null);
                        }
                    }
                    MainActivity.switchState=isChecked;
                    MainActivity.switchNotification=finalHolder.remark_notification.getText().toString();
                    MainActivity.switchChange=true;
                }
            }
        });
        return convertView;
    }

    static class ViewHolder{
        TextView time_notification;
        TextView remark_notification;
        Switch switch_notification;
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

        }
    }

}
