package com.example.mario.techinicianscheduler.Manager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mario.techinicianscheduler.DBHelper;
import com.example.mario.techinicianscheduler.MyListAdapter;
import com.example.mario.techinicianscheduler.R;
import com.example.mario.techinicianscheduler.Task;
import com.example.mario.techinicianscheduler.TechnicianInfo;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ScheduleStep3 extends AppCompatActivity implements View.OnClickListener, MyListAdapter.CheckedAllListener {

//    private CheckBox cb1;
//    private CheckBox cb2;
//    private CheckBox cb3;
//    private CheckBox cb4;
//    private CheckBox cb5;

    private Bundle planInfo;
    private Button editTech;
    private Button generate;

    private CheckBox cbButtonAll;
    private SparseBooleanArray isChecked;
    boolean flag;
    private MyListAdapter adapter;
    private ListView availableTechs;
    private SparseBooleanArray checkedTech;

    private int retCode;
    private static final String TAG = ManagerLogin.class.getSimpleName();
    private JSONObject jsonObject;
    private int techNum=0;
    private Map<String,String> technician=new HashMap<>();
    private ArrayList<TechnicianInfo> techs;
    private ArrayList<TechnicianInfo> chosenTechs;
    private ArrayList<Task> tasks;
    private int taskNum=0;

    private ArrayList<TechnicianInfo> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_step3);
        initialize();

        Bundle bundle=getIntent().getExtras();
        planInfo=getIntent().getExtras();

        taskNum=bundle.getInt("numOfTasks");
        for(int i=1;i<4;i++){
            String[] split=bundle.getString("task"+i).split(",");
            Log.d("string",bundle.getString("task"+i));
            Task t=new Task();
            t.setSkillRequirement(Integer.parseInt(split[0]));
            t.setStationId(split[1]);
            t.setDuration(Integer.parseInt(split[2]));
            t.setId(i);
            tasks.add(t);
        }


        RequestQueue requestQueue= Volley.newRequestQueue(ScheduleStep3.this);
        StringRequest stringRequest=new StringRequest(Request.Method.POST, DBHelper.DB_ADDRESS+"getAvailableTech.php",listener,errorListener){
            protected Map<String,String> getParams() throws AuthFailureError {
                Map<String,String> map=new HashMap<String, String>();
                map.put("managerId",getSharedPreferences("managerSession",MODE_PRIVATE).getString("managerId",null));
                map.put("taskNum",taskNum+"");
                for(int i=1;i<taskNum+1;i++){
                    map.put("stationId"+i,tasks.get(i-1).getStationId());
                }
                return map;
            }
        };

        requestQueue.add(stringRequest);


//        list = new ArrayList<>();
//        for (int i = 0; i < 4; i++)
//        {
//            TechnicianInfo test = new TechnicianInfo();
//            test.setFirstName("sister" + i);
//            list.add(test);
//        }
//
//        adapter = new MyListAdapter(list,this);

        adapter=new MyListAdapter(techs,this);
        adapter.setCheckedAllListener(this);
        availableTechs.setAdapter(adapter);



        editTech.setOnClickListener(this);
        generate.setOnClickListener(this);

    }

    private void initialize() {
        techs=new ArrayList<>();
        tasks=new ArrayList<>();
        chosenTechs=new ArrayList<>();

        cbButtonAll=(CheckBox)findViewById(R.id.cb_all_button);
        isChecked=new SparseBooleanArray();
        availableTechs=(ListView)findViewById(R.id.availableTechs);
        checkedTech=new SparseBooleanArray();


        editTech=(Button)findViewById(R.id.editTech);
        generate=(Button)findViewById(R.id.generate);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.editTech:
                Intent intent=new Intent(ScheduleStep3.this,ManageTechnicians.class);
                startActivity(intent);
                break;
            case R.id.generate:
                Intent intent2=new Intent(ScheduleStep3.this,ScheduleResult.class);
                for(int i=0;i<techs.size();i++){
                    if(checkedTech.valueAt(i)){
                        chosenTechs.add(techs.get(i));
                        techNum++;
                    }
                }

                planInfo.putInt("numOfTask",taskNum);
                planInfo.putInt("numOfChosenTech",techNum);

                planInfo.putParcelableArrayList("addedTask",tasks);
                planInfo.putParcelableArrayList("chosenTech",chosenTechs);

                intent2.putExtras(planInfo);
                startActivity(intent2);
                break;
        }
    }


    Response.Listener<String> listener=new Response.Listener<String>() {
        @Override
        public void onResponse(String s) {
            try {
                jsonObject=new JSONObject(s);
                retCode=jsonObject.getInt("success");
            }catch (JSONException e){
                e.printStackTrace();
            }

            if(retCode==1){
                try {
                    techNum=Integer.parseInt(jsonObject.getString("techNum"))-1;

                    for(int i=1;i<taskNum+1;i++){
                        tasks.get(i-1).setPosition(new LatLng(Double.parseDouble(jsonObject.getString("stationLat"+i)),Double.parseDouble(jsonObject.getString("stationLong"+i))));
                        tasks.get(i-1).setStationName(jsonObject.getString("stationName"+i));
                        Log.d("latitude"+i,jsonObject.getString("stationLat"+i));
                    }

                    if(techNum==0){
                        Toast.makeText(ScheduleStep3.this,"Cannot find available technicians",Toast.LENGTH_SHORT).show();
                    }
                    for(int i=1;i<techNum+1;i++){
                        TechnicianInfo t=new TechnicianInfo();
                        t.setId(i);
                        t.setFirstName(jsonObject.getString("techName"+i));
                        t.setSkillLevel(Integer.parseInt(jsonObject.getString("skillLevel"+i)));
                        t.setWorkHour(jsonObject.getInt("workHour"+i));
                        techs.add(t);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }else {
                Toast.makeText(ScheduleStep3.this,"Cannot find available technicians",Toast.LENGTH_SHORT).show();
            }
        }
    };

    Response.ErrorListener errorListener=new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError volleyError) {
            Toast.makeText(ScheduleStep3.this,"Database not connected",Toast.LENGTH_SHORT).show();
            Log.e(TAG,volleyError.getMessage(),volleyError);
        }
    };

    @Override
    public void CheckAll(SparseBooleanArray checkall) {

        if (checkall.indexOfValue(false) < 0) {
                if (!cbButtonAll.isChecked()) {
                    this.flag = false;
                    cbButtonAll.setChecked(true);
                }                                   //if all the checkbox is selected, the select all button should be set to true.
            } else if (checkall.indexOfValue(false) >= 0 && checkall.indexOfValue(true) >= 0) {
                if (cbButtonAll.isChecked()) {
                    this.flag = true;
                    cbButtonAll.setChecked(false);
                }                                   //if some of the checkbox is true, some is false, the selct all button will be set to false.
            }
        checkedTech=checkall;
    }

    /**
     * Handle the Select All checkbox
     * @param v
     */
    public void allSelect(View v){
        Log.d("click","clicked");
        if(cbButtonAll.isChecked()){
            flag=true;
        }else {
            flag=false;
        }

        if(flag){
            for(int i=0;i<techs.size();i++){
                isChecked.put(i,true);
                MyListAdapter.setIsSelected(isChecked);
            }
        }else{
            for(int i=0;i<techs.size();i++){
                isChecked.put(i,false);
                MyListAdapter.setIsSelected(isChecked);
            }
        }
        adapter.notifyDataSetChanged();
    }
}
