package com.example.mario.techinicianscheduler.Technician;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
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
import com.example.mario.techinicianscheduler.ResideMenu.ResideMenu;
import com.example.mario.techinicianscheduler.ResideMenu.ResideMenuItem;
import com.example.mario.techinicianscheduler.Task;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class TechnicianDashboard extends AppCompatActivity implements View.OnClickListener {

    private TextView loggedTechUsername;
    private ImageButton techStartSideBar;
    private TextView techDate;
    private TextView techTaskNum;
    private TextView techWorkHour;
    private TextView taskEstimateTime;


    private int estimateDuration=0;

    private Bundle techInfo;
    private String technicianId;

    private JSONObject jsonObject;
    private int retCode;
    private RequestQueue requestQueue;

    private ArrayList<Task> arrangedTasks;
    private ArrayList<LatLng> recordPos;
    private int taskSize;

    private ResideMenu resideMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_technician_dashboard);
        initialize();

        technicianId=techInfo.getInt("techId")+"";
        loggedTechUsername.setText(techInfo.getString("firstName"));
        Calendar c=Calendar.getInstance();
        int year=c.get(Calendar.YEAR);
        int month=c.get(Calendar.MONTH)+1;
        int day=c.get(Calendar.DAY_OF_MONTH);
        techDate.setText(year + "/" + month + "/" + day);


        getWorkArrangement();

        techStartSideBar.setOnClickListener(this);

        handleResideMenu();
    }

    private void handleResideMenu(){
        resideMenu=new ResideMenu(this);
        resideMenu.setShadowVisible(true);
        resideMenu.attachToActivity(this);
        resideMenu.setScaleValue(0.6f);
        resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_RIGHT);




        String titles[]={"Home","Work Arrangement","Route","Settings","Log out"};
        int icon[]={R.drawable.home,R.drawable.schedule,R.drawable.route,R.drawable.settings,R.drawable.logout};

        for(int i=0;i<titles.length;i++){
            ResideMenuItem item=new ResideMenuItem(this,icon[i],titles[i]);
            item.setId(i);
            item.setOnClickListener(this);
            resideMenu.addMenuItem(item,ResideMenu.DIRECTION_LEFT);
        }

        resideMenu.setMenuListener(menuListener);
    }

    private ResideMenu.OnMenuListener menuListener=new ResideMenu.OnMenuListener() {
        @Override
        public void openMenu() {
            resideMenu.setBackground(R.drawable.bg);
        }
        @Override
        public void closeMenu() {
            resideMenu.setBackground(R.drawable.white);
        }
    };

    public boolean dispatchTouchEvent(MotionEvent ev){
        return resideMenu.dispatchTouchEvent(ev);
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
        techInfo=getIntent().getExtras();
        arrangedTasks=new ArrayList<>();
        recordPos=new ArrayList<>();
        techStartSideBar=(ImageButton)findViewById(R.id.techStartSideBar);
        techDate=(TextView)findViewById(R.id.techDate);
        techWorkHour=(TextView)findViewById(R.id.techWorkHour);
        techTaskNum=(TextView)findViewById(R.id.techTaskNum);
        taskEstimateTime=(TextView)findViewById(R.id.techEstimateTaskDur);


    }


    @Override
    public void onClick(View view) {
        techInfo.putParcelableArrayList("arrangedTasks",arrangedTasks);
        techInfo.putParcelableArrayList("recordPos",recordPos);
        switch (view.getId()){
            case 0:
                Intent intent0=new Intent(TechnicianDashboard.this,TechnicianDashboard.class);
                intent0.putExtras(techInfo);
                startActivity(intent0);
                finish();
                break;
            case 1:
                Intent intent=new Intent(TechnicianDashboard.this,TechnicianTasks.class);
                intent.putExtras(techInfo);
                startActivity(intent);
                break;
            case 2:
                Intent intent1=new Intent(TechnicianDashboard.this,TechnicianRoute.class);
                intent1.putExtras(techInfo);
                startActivity(intent1);
                break;
            case 3:
                Intent intent2=new Intent(TechnicianDashboard.this,TechnicianSetting.class);
                intent2.putExtras(techInfo);
                startActivity(intent2);
                break;
            case 4:
                Intent intent3=new Intent(TechnicianDashboard.this, TechnicianLogin.class);
                startActivity(intent3);
                break;
            case R.id.techStartSideBar:
                resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
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
                        estimateDuration+=task.getDuration();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                techTaskNum.setText(taskSize+"");
                techWorkHour.setText(techInfo.getString("workHour"));
                taskEstimateTime.setText(estimateDuration+"");

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
