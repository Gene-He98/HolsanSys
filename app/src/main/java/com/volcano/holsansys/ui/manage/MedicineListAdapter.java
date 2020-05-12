package com.volcano.holsansys.ui.manage;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.volcano.holsansys.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

import static com.blankj.utilcode.util.TimeUtils.getNowDate;

public class MedicineListAdapter extends BaseAdapter {

    private LinkedList<Medicine> mData;
    private Context mContext;

    public MedicineListAdapter(LinkedList<Medicine> mData, Context mContext) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_medicine,parent,false);
            holder = new ViewHolder();
            holder.medicine_name = convertView.findViewById(R.id.medicine_name);
            holder.medicine_another_name = convertView.findViewById(R.id.medicine_another_name);
            holder.validity = convertView.findViewById(R.id.validity);
            convertView.setTag(holder);   //将Holder存储到convertView中
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        holder.medicine_name.setText("【"+mData.get(position).getMedicineName()+"】");
        holder.medicine_another_name.setText("(别名:"+mData.get(position).getMedicineAnotherName()+")");
        holder.validity.setText(mData.get(position).getValidity());
        Date nowDate=getNowDate();
        SimpleDateFormat formatter = new   SimpleDateFormat   ("yyyy-MM-dd");
        Date validityDate= null;
        try {
            validityDate = new Date(formatter.parse(mData.get(position).getValidity()).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(nowDate.after(validityDate)){
            convertView.findViewById(R.id.if_overdue).setVisibility(View.VISIBLE);
        }
        return convertView;
    }

    static class ViewHolder{
        TextView medicine_name;
        TextView medicine_another_name;
        TextView validity;
    }

}
