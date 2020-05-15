package com.volcano.holsansys.ui.manage;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.volcano.holsansys.MainActivity;
import com.volcano.holsansys.R;
import com.volcano.holsansys.add.AddMedicineActivity;
import com.volcano.holsansys.tools.WebServiceAPI;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ManageFragment extends Fragment {

    private List<DrugRecord> recordData = null;
    private List<Medicine> medicineData = null;
    private Context mContext;
    private RecordListAdapter recordAdapter = null;
    private MedicineListAdapter medicineAdapter = null;
    private ListView list_record;
    private ListView list_medicine;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        MainActivity.currentView=2;
        final View root = inflater.inflate(R.layout.fragment_manage, container, false);
        if (MainActivity.mode){
            if (MainActivity.patientName.equals("")) {
                mContext = getActivity();
                list_medicine = root.findViewById(R.id.listView_manage);
                list_medicine.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(getContext(), AddMedicineActivity.class);
                        intent.putExtra("kind", "change");
                        String medicineName = ((TextView) view
                                .findViewById(R.id.medicine_name)).getText().toString();
                        medicineName=medicineName.replace("【","");
                        medicineName=medicineName.replace("】","");
                        intent.putExtra("MedicineName",medicineName);
                        startActivity(intent);
                    }
                });
                String[] myParamsArr = {"MedicineInfo", MainActivity.userID};
                VerifyTask myVerifyTask = new VerifyTask();
                myVerifyTask.execute(myParamsArr);
            } else {
                mContext = getActivity();
                list_record = root.findViewById(R.id.listView_manage);
                String[] myParamsArr = {"DrugRecordInfo", MainActivity.userID, MainActivity.patientName};
                VerifyTask myVerifyTask = new VerifyTask();
                myVerifyTask.execute(myParamsArr);
            }
        }else {
            root.findViewById(R.id.manage_bt_emer).setVisibility(View.VISIBLE);
            mContext = getActivity();
            list_record = root.findViewById(R.id.listView_manage);
            String[] myParamsArr = {"DrugRecordInfo", MainActivity.userID, MainActivity.patientName};
            VerifyTask myVerifyTask = new VerifyTask();
            myVerifyTask.execute(myParamsArr);
        }
        if(MainActivity.bgThread2==null){
            MainActivity.bgThread2=new Thread() {
                @Override
                public void run() {
                    while (true){
                        if(MainActivity.refreshManageFlag){
                            if(MainActivity.patientName.equals("")){
                                mContext = getActivity();
                                list_medicine = root.findViewById(R.id.listView_manage);
                                String[] myParamsArr = {"MedicineInfo", MainActivity.userID};
                                VerifyTask myVerifyTask = new VerifyTask();
                                myVerifyTask.execute(myParamsArr);
                            }else {
                                String[] myParamsArr = {"DrugRecordInfo", MainActivity.userID, MainActivity.patientName};
                                VerifyTask myVerifyTask = new VerifyTask();
                                myVerifyTask.execute(myParamsArr);
                            }
                            MainActivity.refreshManageFlag =false;
                        }
                    }
                }
            };
            MainActivity.bgThread2.start();
        }

        return root;

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
                if(MainActivity.patientName.equals("")){
                    medicineData = new LinkedList<>();
                    try {
                        for (int i=0;i<myList.size();i++){

                            Map<String,String> myMap=myList.get(i);
                            String medicineName = myMap.get("MedicineName");
                            String medicineAnotherName = myMap.get("MedicineAnotherName");
                            String validity = myMap.get("Validity");
                            medicineData.add(new Medicine(medicineName, medicineAnotherName, validity));
                        }

                        medicineAdapter = new MedicineListAdapter((LinkedList<Medicine>) medicineData, mContext);
                        list_medicine.setAdapter(medicineAdapter);
                    }
                    catch (Exception ex){}
                }else {
                    recordData = new LinkedList<>();
                    try {
                        for (int i=0;i<myList.size();i++){

                            Map<String,String> myMap=myList.get(i);
                            String recordTime = myMap.get("DrugTime");
                            String recordName = myMap.get("NotificationName");
                            String recordIf = myMap.get("IfDrug");
                            recordData.add(new DrugRecord(recordTime, recordName, recordIf));
                        }

                        recordAdapter = new RecordListAdapter((LinkedList<DrugRecord>) recordData, mContext);
                        list_record.setAdapter(recordAdapter);
                    }
                    catch (Exception ex){}
                }
            }
        }
    }

}