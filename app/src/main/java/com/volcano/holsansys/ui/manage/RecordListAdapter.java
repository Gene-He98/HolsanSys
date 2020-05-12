package com.volcano.holsansys.ui.manage;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.volcano.holsansys.R;

import java.util.LinkedList;

public class RecordListAdapter extends BaseAdapter {

    private LinkedList<DrugRecord> mData;
    private Context mContext;

    public RecordListAdapter(LinkedList<DrugRecord> mData, Context mContext) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_record,parent,false);
            holder = new ViewHolder();
            holder.recordTime = convertView.findViewById(R.id.record_time);
            holder.recordName = convertView.findViewById(R.id.record_name);
            holder.recordIf = convertView.findViewById(R.id.record_if);
            convertView.setTag(holder);   //将Holder存储到convertView中
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        holder.recordTime.setText(mData.get(position).getRecordTime());
        holder.recordName.setText(mData.get(position).getRecordName());
        holder.recordIf.setText(mData.get(position).getRecordIf());
        return convertView;
    }

    static class ViewHolder{
        TextView recordTime;
        TextView recordName;
        TextView recordIf;
    }

}
