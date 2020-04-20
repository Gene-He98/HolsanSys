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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_notification,parent,false);
            TextView time_notification = convertView.findViewById(R.id.time_notification);
            TextView remark_notification = convertView.findViewById(R.id.remark_notification);
            Switch switch_notification = convertView.findViewById(R.id.switch_notification);
            time_notification.setText(mData.get(position).getTime());
            remark_notification.setText(mData.get(position).getRemark());
            switch_notification.setChecked(mData.get(position).getSwitch());
            return convertView;
        }

}
