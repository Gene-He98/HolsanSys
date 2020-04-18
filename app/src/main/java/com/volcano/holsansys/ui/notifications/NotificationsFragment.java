package com.volcano.holsansys.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.volcano.holsansys.R;

public class NotificationsFragment extends Fragment {
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_notifications, container, false);
        /*root.findViewById(R.id.add_bt).setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Navigation.findNavController(root).navigate(R.id.addNotificationFragment);
            }
        });*/

        return root;
    }
}