package com.example.mario.techinicianscheduler.Technician;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.mario.techinicianscheduler.R;
import com.example.mario.techinicianscheduler.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TechnicianTasks extends AppCompatActivity implements View.OnClickListener{

    private TextView taskWelcome;
    private ListView taskList;
    private Bundle techInfo;

    private Button goRoute;
    private Button callManager;

    private SimpleAdapter dataAdapter;

    private ArrayList<Task> arrangedTasks;


    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_technician_tasks);

        initialize();
        arrangedTasks=techInfo.getParcelableArrayList("arrangedTasks");
        taskWelcome.setText("Hello "+techInfo.getString("techName")+",you have "+arrangedTasks.size()+" tasks today:");

        setListview();

        goRoute.setOnClickListener(this);
        callManager.setOnClickListener(this);
    }

    private void setListview() {
        List<Map<String,Object>> list=new ArrayList<Map<String,Object>>();
        Map<String,Object> map=new HashMap<String,Object>();

        map.put("taskName","Task name");
        map.put("skillLevel","Skill level");
        map.put("estimateDur","Estimate Duration");
        list.add(map);

        map=new HashMap<String,Object>();
        for(int i=0;i<arrangedTasks.size();i++){
            map=new HashMap<String,Object>();
            map.put("taskName",arrangedTasks.get(i).getName());
            map.put("skillLevel",arrangedTasks.get(i).getSkillRequirement()+"");
            map.put("estimateDur",arrangedTasks.get(i).getDuration()+"");
            list.add(map);
        }

        dataAdapter=new SimpleAdapter(TechnicianTasks.this,list,R.layout.db_item_layout,new String[]{"taskName","skillLevel","estimateDur"},new int[]{R.id.value1,R.id.value2,R.id.value3});
        taskList.setAdapter(dataAdapter);
    }


    private void initialize() {
        taskWelcome=(TextView)findViewById(R.id.taskWelcome);
        taskList=(ListView)findViewById(R.id.taskArrangement);
        goRoute=(Button)findViewById(R.id.goRoute);
        callManager=(Button)findViewById(R.id.callManager);
        techInfo=getIntent().getExtras();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.goRoute:
                Intent intent=new Intent(TechnicianTasks.this,TechnicianRoute.class);
                intent.putExtras(techInfo);
                startActivity(intent);
                break;
            case R.id.callManager:
                Intent intent2=new Intent(Intent.ACTION_DIAL,null);
                intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent2);
                break;
        }
    }






}

