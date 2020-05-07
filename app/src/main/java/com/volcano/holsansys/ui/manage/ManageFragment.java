package com.volcano.holsansys.ui.manage;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.volcano.holsansys.MainActivity;
import com.volcano.holsansys.R;
import com.volcano.holsansys.tools.WebServiceAPI;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ManageFragment extends Fragment {

    private List<Manage> mData = null;
    private Context mContext;
    private ManageListAdapter mAdapter = null;
    private ListView list_manage;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        MainActivity.currentView=2;
        View root = inflater.inflate(R.layout.fragment_manage, container, false);
        if (MainActivity.mode){
            if (MainActivity.patientName.equals("")) {
                root.findViewById(R.id.if_manage).setVisibility(View.VISIBLE);
                (root.findViewById(R.id.listView_manage)).setVisibility(View.GONE);
                return root;
            } else {
                mContext = getActivity();
                list_manage = root.findViewById(R.id.listView_manage);
                String[] myParamsArr = {"DrugRecordInfo", MainActivity.userID, MainActivity.patientName};
                VerifyTask myVerifyTask = new VerifyTask();
                myVerifyTask.execute(myParamsArr);
                return root;
            }
        }else {
            root.findViewById(R.id.manage_bt_emer).setVisibility(View.VISIBLE);
            mContext = getActivity();
            list_manage = root.findViewById(R.id.listView_manage);
            String[] myParamsArr = {"DrugRecordInfo", MainActivity.userID, MainActivity.patientName};
            VerifyTask myVerifyTask = new VerifyTask();
            myVerifyTask.execute(myParamsArr);
            return root;
        }

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
            if(!myResult.equals("[]")){
                Gson myGson = new Gson();
                List<Map<String,String>> myList=myGson.fromJson(myResult, new TypeToken<List<Map<String,String>>>(){}.getType());
                mData = new LinkedList<>();
                try {
                    for (int i=0;i<myList.size();i++){

                        Map<String,String> myMap=myList.get(i);
                        String recordTime = myMap.get("DrugTime");
                        String recordName = myMap.get("NotificationName");
                        String recordIf = myMap.get("IfDrug");
                        mData.add(new Manage(recordTime, recordName, recordIf));
                    }

                    mAdapter = new ManageListAdapter((LinkedList<Manage>) mData, mContext);
                    list_manage.setAdapter(mAdapter);
                }
                catch (Exception ex){}
            }
        }
    }

}