package com.example.mario.techinicianscheduler.Technician;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mario.techinicianscheduler.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TechnicianTasks extends AppCompatActivity implements View.OnClickListener{

    private TextView taskWelcome;
    private String technicianId;
    private ListView listview;
    private JSONObject jsonObject;
    private int retCode;
    private RequestQueue requestQueue;
    private SimpleAdapter dataAdapter;
    private int taskSize;

    private Button goRoute;
    private Button callManager;



    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_technician_tasks);

        initialize();

        requestQueue= Volley.newRequestQueue(TechnicianTasks.this);

        StringRequest stringRequest=new StringRequest(Request.Method.POST,"http://10.132.201.46/technicianScheduler/getWorkArrangement.php",listener,errorListener){
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("technicianId", technicianId);
                return map;
            }
        };
        requestQueue.add(stringRequest);

        goRoute.setOnClickListener(this);
        callManager.setOnClickListener(this);
    }


    private void initialize() {
        taskWelcome=(TextView)findViewById(R.id.taskWelcome);
        listview=(ListView)findViewById(R.id.taskArrangement);
        technicianId=getIntent().getExtras().getString("technicianId");
        goRoute=(Button)findViewById(R.id.goRoute);
        callManager=(Button)findViewById(R.id.callManager);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.goRoute:
                Intent intent=new Intent(TechnicianTasks.this,TechnicianRoute.class);
                startActivity(intent);
                break;
            case R.id.callManager:
                //Handle call manager
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

                List<Map<String,Object>> list=new ArrayList<Map<String,Object>>();
                Map<String,Object> map=new HashMap<String,Object>();

                map.put("id","ID");
                map.put("skillLevel","Skill level");
                map.put("stationName","Station");
                list.add(map);
                //assign the data to list view.
                try {
                    taskSize=Integer.parseInt(jsonObject.getString("taskSize"))-1;
                    taskWelcome.setText("Hello "+getIntent().getExtras().getString("technicianName")+",you have "+taskSize+" tasks today:");

                    for(int i=1;i<taskSize+1;i++){
                        map=new HashMap<String,Object>();
                        map.put("id",i+"");
                        map.put("skillLevel",jsonObject.getString("skill_level"+i));
                        map.put("stationName",jsonObject.getString("stationName"+i));
                        list.add(map);
                    }
                    dataAdapter=new SimpleAdapter(TechnicianTasks.this,list,R.layout.db_item_layout,new String[]{"id","skillLevel","stationName"},new int[]{R.id.value1,R.id.value2,R.id.value3});
                    listview.setAdapter(dataAdapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }else {
                Toast.makeText(TechnicianTasks.this,"No work today!",Toast.LENGTH_SHORT).show();
            }
        }
    };

    Response.ErrorListener errorListener=new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError volleyError) {
            Toast.makeText(TechnicianTasks.this,"Database not connected",Toast.LENGTH_SHORT).show();
        }
    };


}

