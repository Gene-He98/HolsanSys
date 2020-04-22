package com.volcano.holsansys.ui.notifications;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Switch;
import android.widget.TextView;
import com.volcano.holsansys.R;

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
            holder.switch_notification.setChecked(mData.get(position).getSwitch());
            return convertView;
        }

    static class ViewHolder{
        TextView time_notification;
        TextView remark_notification;
        Switch switch_notification;
    }

}
