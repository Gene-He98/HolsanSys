package com.volcano.holsansys.ui.user;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.volcano.holsansys.R;
import com.volcano.holsansys.ui.notifications.Notification;
import com.volcano.holsansys.ui.notifications.NotificationListAdapter;

import java.util.LinkedList;
import java.util.List;

public class UserFragment extends Fragment {

    private List<Patient> mData = null;
    private Context mContext;
    private PatientAdapter mAdapter = null;
    private ListView list_user;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_user, container, false);

        //提醒界面数据更新
        mContext =getActivity();
        list_user = root.findViewById(R.id.listView_user);
        mData = new LinkedList<>();
        mData.add(new Patient("父亲", "按时服药","街头镇道蓬岩村"));
        mData.add(new Patient("母亲", "21号中午未服药", "街头镇道蓬岩村"));
        mAdapter = new PatientAdapter((LinkedList<Patient>) mData, mContext);


        list_user.setAdapter(mAdapter);

        return root;
    }
}