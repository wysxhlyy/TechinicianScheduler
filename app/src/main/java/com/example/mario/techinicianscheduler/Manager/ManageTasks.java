package com.example.mario.techinicianscheduler.Manager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.mario.techinicianscheduler.Manager.HandleTask.CreateTask;
import com.example.mario.techinicianscheduler.Manager.HandleTask.EditTask;
import com.example.mario.techinicianscheduler.R;
import com.example.mario.techinicianscheduler.ResideMenu.ResideMenu;
import com.example.mario.techinicianscheduler.ResideMenu.ResideMenuItem;
import com.example.mario.techinicianscheduler.Task;
import com.example.mario.techinicianscheduler.Technician.TechnicianLogin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManageTasks extends AppCompatActivity implements View.OnClickListener{

    private ImageButton addNewTask;
    private ListView taskListView;
    private SimpleAdapter dataAdapter;
    private ArrayList<Task> existTasks;
    private Bundle managerInfo;
    private ImageButton menu;

    private ResideMenu resideMenu;

    private static final int ACTIVITY_CREATE_TASK=1;
    private static final int ACTIVITY_DELETE_TASK=2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_tasks);
        initialize();

        managerInfo=getIntent().getExtras();
        existTasks=managerInfo.getParcelableArrayList("availableTask");
        List<Map<String,Object>> list=new ArrayList<>();
        Map<String,Object> map=new HashMap<>();

        for(int i=0;i<existTasks.size();i++){
            map=new HashMap<>();
            map.put("name",existTasks.get(i).getName());
            map.put("requirement","Level "+existTasks.get(i).getSkillRequirement()+"");
            map.put("duration",existTasks.get(i).getDuration()+"");
            list.add(map);
        }
        dataAdapter=new SimpleAdapter(this,list,R.layout.manage_task_list,new String[]{"name","requirement","duration"},new int[]{R.id.task_name,R.id.task_skill,R.id.task_duration});
        taskListView.setAdapter(dataAdapter);

        editTask();

        addNewTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ManageTasks.this, CreateTask.class);
                intent.putExtras(managerInfo);
                startActivityForResult(intent,ACTIVITY_CREATE_TASK);
            }
        });

        handleResideMenu();
        menu.setOnClickListener(this);

    }

    private void initialize() {
        addNewTask=(ImageButton)findViewById(R.id.addNewTask);
        taskListView=(ListView)findViewById(R.id.taskListView);
        menu=(ImageButton)findViewById(R.id.manageTaskMenu);
    }

    /**
     * Handle the sidebar menu.
     */
    private void handleResideMenu(){
        resideMenu=new ResideMenu(this);
        resideMenu.setShadowVisible(true);
        resideMenu.attachToActivity(this);
        resideMenu.setScaleValue(0.6f);
        resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_RIGHT);


        String titles[]={"Home","Schedule","Manage Tasks","Manage Technicians","Settings","Log out"};
        int icon[]={R.drawable.home,R.drawable.schedule,R.drawable.tasks,R.drawable.technicians,R.drawable.settings,R.drawable.logout};

        for(int i=0;i<titles.length;i++){
            ResideMenuItem item=new ResideMenuItem(this,icon[i],titles[i]);
            item.setId(i);
            item.setOnClickListener(this);
            resideMenu.addMenuItem(item,ResideMenu.DIRECTION_LEFT);
        }

        resideMenu.setMenuListener(menuListener);
    }

    /**
     * set the sidebar background;
     */
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



    private void editTask() {
        taskListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int myItemInt, long l) {
                Intent intent=new Intent(ManageTasks.this, EditTask.class);
                managerInfo.putInt("selectedTask",myItemInt+1);
                intent.putExtras(managerInfo);
                startActivityForResult(intent,ACTIVITY_DELETE_TASK);
            }
        });
    }




    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        recreate();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case 0:
                Intent intentt=new Intent(ManageTasks.this,ManagerDashboard.class);
                intentt.putExtras(managerInfo);
                startActivity(intentt);
                finish();
                break;
            case 1:
                Intent intent=new Intent(ManageTasks.this,chooseTask.class);
                Bundle bundle= managerInfo;
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
                break;
            case 2:
                Intent intent1=new Intent(ManageTasks.this,ManageTasks.class);
                Bundle bundle1=managerInfo;
                intent1.putExtras(bundle1);
                startActivity(intent1);
                finish();
                break;
            case 3:
                Intent intent2=new Intent(ManageTasks.this,ManageTechnicians.class);
                Bundle bundle2=managerInfo;
                intent2.putExtras(bundle2);
                startActivity(intent2);
                finish();
                break;
            case 4:
                Intent intent3=new Intent(ManageTasks.this,ManagerSetting.class);
                intent3.putExtras(managerInfo);
                startActivity(intent3);
                finish();
                break;
            case 5:
                Intent intent4=new Intent(ManageTasks.this, TechnicianLogin.class);
                startActivity(intent4);
                finish();
                break;
            case R.id.manageTaskMenu:
                resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
                break;

        }
    }
}
