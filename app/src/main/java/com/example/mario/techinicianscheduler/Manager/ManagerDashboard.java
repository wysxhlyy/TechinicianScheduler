package com.example.mario.techinicianscheduler.Manager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import com.example.mario.techinicianscheduler.TechnicianInfo;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ManagerDashboard extends AppCompatActivity implements View.OnClickListener {

    private TextView username;
    private Bundle managerInfo;

    private Button schedule;
    private Button manageTech;
    private Button manageTask;
    private Button Settings;


    private int retCode;
    private static final String TAG = ManagerLogin.class.getSimpleName();
    private JSONObject jsonObject;
    private int techNum=0;

    private ArrayList<TechnicianInfo> techs;
    private ArrayList<Task> tasks;
    private int taskNum=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_dashboard);

        initialize();

        username.setText("Welcome Back: "+ managerInfo.getString("managerName"));
        schedule.setOnClickListener(this);


        RequestQueue requestQueue= Volley.newRequestQueue(ManagerDashboard.this);
        StringRequest stringRequest=new StringRequest(Request.Method.POST, DBHelper.DB_ADDRESS+"getAvailableTech.php",listener,errorListener){
            protected Map<String,String> getParams() throws AuthFailureError {
                Map<String,String> map=new HashMap<String, String>();
                map.put("managerId",getSharedPreferences("managerSession",MODE_PRIVATE).getString("managerId",null));
                return map;
            }
        };

        requestQueue.add(stringRequest);



    }

    private void initialize() {
        username=(TextView)findViewById(R.id.loggedManagerUsername);
        schedule=(Button)findViewById(R.id.schedule);
        manageTech=(Button)findViewById(R.id.manageTech);
        manageTask=(Button)findViewById(R.id.manageTask);
        Settings=(Button)findViewById(R.id.managerSettings);
        managerInfo=getIntent().getExtras();

        techs=new ArrayList<>();
        tasks=new ArrayList<>();

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.schedule:
                Intent intent=new Intent(ManagerDashboard.this,chooseTask.class);
                Bundle bundle=new Bundle();
                bundle.putParcelableArrayList("availableTechnician",techs);
                bundle.putParcelableArrayList("availableTask",tasks);
                intent.putExtras(bundle);
                startActivity(intent);
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

                    taskNum=Integer.parseInt(jsonObject.getString("taskNum"));
                    if(taskNum!=0){
                        for(int i=1;i<taskNum+1;i++){
                            Task task=new Task();
                            task.setId(i);
                            task.setSkillRequirement(Integer.parseInt(jsonObject.getString("taskSkill"+i)));
                            task.setDuration(Integer.parseInt(jsonObject.getString("taskDuration"+i)));
                            if(jsonObject.getString("taskStatus"+i).equals("false")){
                                task.setFinished(false);
                            }else {
                                task.setFinished(true);
                            }
                            task.setStationName(jsonObject.getString("stationName"+i));
                            task.setPosition(new LatLng(Double.parseDouble(jsonObject.getString("stationLat"+i)),Double.parseDouble(jsonObject.getString("stationLong"+i))));
                            tasks.add(task);
                        }
                    }



                    techNum=Integer.parseInt(jsonObject.getString("techNum"));
                    if(techNum!=0){
                        for(int i=1;i<techNum+1;i++){
                            TechnicianInfo t=new TechnicianInfo();
                            t.setId(i);
                            t.setFirstName(jsonObject.getString("techName"+i));
                            t.setSkillLevel(Integer.parseInt(jsonObject.getString("skillLevel"+i)));
                            t.setWorkHour(jsonObject.getInt("workHour"+i));
                            techs.add(t);
                        }
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }else {
                Toast.makeText(ManagerDashboard.this,"Cannot find available technicians",Toast.LENGTH_SHORT).show();
            }
        }
    };

    Response.ErrorListener errorListener=new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError volleyError) {
            Toast.makeText(ManagerDashboard.this,"Database not connected",Toast.LENGTH_SHORT).show();
            Log.e(TAG,volleyError.getMessage(),volleyError);
        }
    };
}
