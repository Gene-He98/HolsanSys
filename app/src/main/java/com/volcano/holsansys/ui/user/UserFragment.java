package com.volcano.holsansys.ui.user;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.volcano.holsansys.R;

import java.util.LinkedList;
import java.util.List;

public class UserFragment extends Fragment {

    private List<Patient> mData = null;
    private Context mContext;
    private PatientAdapter mAdapter = null;
    private ListView list_user;
    private LinearLayout foot_view;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_user, container, false);

        //提醒界面数据更新
        mContext =getActivity();
        list_user = root.findViewById(R.id.listView_user);
        foot_view = (LinearLayout) inflater.inflate(R.layout.footview_listview_user, null);//得到尾部的布局
        list_user.addFooterView(foot_view);//添加尾部
        mData = new LinkedList<>();
        mData.add(new Patient("父亲", "按时服药","街头镇道蓬岩村"));
        mData.add(new Patient("母亲", "21号中午未服药", "街头镇道蓬岩村"));
        mAdapter = new PatientAdapter((LinkedList<Patient>) mData, mContext);


        list_user.setAdapter(mAdapter);

        return root;
    }
}