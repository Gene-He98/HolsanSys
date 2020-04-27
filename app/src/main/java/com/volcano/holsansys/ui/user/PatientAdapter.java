package com.volcano.holsansys.ui.user;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.volcano.holsansys.R;

import java.util.LinkedList;

public class PatientAdapter extends BaseAdapter {
    private LinkedList<Patient> mData;
    private Context mContext;

    public PatientAdapter(LinkedList<Patient> mData, Context mContext) {
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
        PatientAdapter.ViewHolder holder = null;
        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_user,parent,false);
            holder = new PatientAdapter.ViewHolder();
            holder.name_patient = convertView.findViewById(R.id.patientName);
            holder.situation_patient = convertView.findViewById(R.id.situation_patient);
            holder.location_patient = convertView.findViewById(R.id.location_patient);
            convertView.setTag(holder);   //将Holder存储到convertView中
        }else{
            holder = (PatientAdapter.ViewHolder) convertView.getTag();
        }
        holder.name_patient.setText(mData.get(position).getName());
        holder.situation_patient.setText(mData.get(position).getSituation());
        holder.location_patient.setText(mData.get(position).getLocation());
        return convertView;
    }

    static class ViewHolder{
        TextView name_patient;
        TextView situation_patient;
        TextView location_patient;
    }
}
