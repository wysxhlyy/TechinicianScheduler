package com.example.mario.techinicianscheduler.Technician;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mario.techinicianscheduler.DBHelper;
import com.example.mario.techinicianscheduler.R;
import com.example.mario.techinicianscheduler.Task;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TechnicianDashboard extends AppCompatActivity implements View.OnClickListener {

    private TextView loggedTechUsername;

    private Button workArrangement;
    private Button route;
    private Button techSettings;
    private Button techQuit;

    private Bundle techInfo;
    private String technicianId;

    private JSONObject jsonObject;
    private int retCode;
    private RequestQueue requestQueue;

    private ArrayList<Task> arrangedTasks;
    private ArrayList<LatLng> recordPos;
    private int taskSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_technician_dashboard);
        initialize();

        technicianId=techInfo.getInt("techId")+"";
        loggedTechUsername.setText("Welcome Back: "+ techInfo.getString("firstName"));

        getWorkArrangement();

        workArrangement.setOnClickListener(this);
        route.setOnClickListener(this);
        techSettings.setOnClickListener(this);
        techQuit.setOnClickListener(this);
    }

    private void getWorkArrangement() {
        requestQueue= Volley.newRequestQueue(TechnicianDashboard.this);

        StringRequest stringRequest=new StringRequest(Request.Method.POST, DBHelper.DB_ADDRESS+"getWorkArrangement.php",listener,errorListener){
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("technicianId", technicianId);
                return map;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void initialize() {
        loggedTechUsername=(TextView)findViewById(R.id.loggedTechUsername);
        workArrangement=(Button)findViewById(R.id.workArrangement);
        route=(Button)findViewById(R.id.route);
        techSettings=(Button)findViewById(R.id.techSettings);
        techQuit=(Button)findViewById(R.id.techQuit);
        techInfo=getIntent().getExtras();
        arrangedTasks=new ArrayList<>();
        recordPos=new ArrayList<>();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.workArrangement:
                Intent intent=new Intent(TechnicianDashboard.this,TechnicianTasks.class);
                techInfo.putParcelableArrayList("arrangedTasks",arrangedTasks);
                techInfo.putParcelableArrayList("recordPos",recordPos);
                intent.putExtras(techInfo);
                startActivity(intent);
                break;
            case R.id.route:
                Intent intent1=new Intent(TechnicianDashboard.this,TechnicianRoute.class);
                techInfo.putParcelableArrayList("arrangedTasks",arrangedTasks);
                techInfo.putParcelableArrayList("recordPos",recordPos);
                intent1.putExtras(techInfo);
                startActivity(intent1);
                break;
            case R.id.techSettings:
                Intent intent2=new Intent(TechnicianDashboard.this,TechnicianSetting.class);
                intent2.putExtras(techInfo);
                startActivity(intent2);
                break;
            case R.id.techQuit:
                Intent intent3=new Intent(TechnicianDashboard.this, TechnicianLogin.class);
                startActivity(intent3);

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
                    taskSize=Integer.parseInt(jsonObject.getString("taskSize"))-1;
                    for(int i=1;i<taskSize+1;i++){
                        Task task=new Task();
                        task.setName(jsonObject.getString("taskName"+i));
                        task.setSkillRequirement(Integer.parseInt(jsonObject.getString("skill_level"+i)));
                        task.setStationId(jsonObject.getString("stationId"+i));
                        task.setDuration(jsonObject.getInt("duration"+i));
                        task.setDescription(jsonObject.getString("description"+i));
                        task.setStationName(jsonObject.getString("stationName"+i));
                        task.setPosition(new LatLng(jsonObject.getDouble("latitude"+i),jsonObject.getDouble("longitude"+i)));
                        recordPos.add(task.getPosition());
                        arrangedTasks.add(task);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }else {

            }
        }
    };

    Response.ErrorListener errorListener=new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError volleyError) {
            Toast.makeText(TechnicianDashboard.this,"Database not connected",Toast.LENGTH_SHORT).show();
        }
    };


}
