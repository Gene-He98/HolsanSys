package com.volcano.holsansys.ui.notifications;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.volcano.holsansys.R;

import java.util.LinkedList;
import java.util.List;


public class NotificationsFragment extends Fragment {

    private List<Notification> mData = null;
    private Context mContext;
    private NotificationListAdapter mAdapter = null;
    private ListView list_notification;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_notifications, container, false);
        /*root.findViewById(R.id.add_bt).setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Navigation.findNavController(root).navigate(R.id.addNotificationFragment);
            }
        });*/

        //提醒界面数据更新
        mContext =getActivity();
        list_notification = root.findViewById(R.id.listView_notification);
        mData = new LinkedList<>();
        mData.add(new Notification("07:00", "早饭后服药", true));
        mData.add(new Notification("鸭说", "你是鸭么?", true));
        mData.add(new Notification("鱼说", "你是鱼么?", false));
        mData.add(new Notification("马说", "你是马么?", false));
        mData.add(new Notification("鱼说", "你是鱼么?", false));
        mData.add(new Notification("马说", "你是马么?", false));
        mData.add(new Notification("鱼说", "你是鱼么?", false));
        mData.add(new Notification("马说", "你是马么?", false));
        mAdapter = new NotificationListAdapter((LinkedList<Notification>) mData, mContext);
        list_notification.setAdapter(mAdapter);

        return root;
    }
}