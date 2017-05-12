package com.example.mario.techinicianscheduler.Technician;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.example.mario.techinicianscheduler.DBHelper;
import com.example.mario.techinicianscheduler.Manager.HandleTask.DisplayTask;
import com.example.mario.techinicianscheduler.Manager.ManagerLogin;
import com.example.mario.techinicianscheduler.R;
import com.example.mario.techinicianscheduler.ResideMenu.ResideMenu;
import com.example.mario.techinicianscheduler.ResideMenu.ResideMenuItem;
import com.example.mario.techinicianscheduler.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The technician could view the tasks assigned to him.
 * All the arrangement is decided by his bound manager.
 * After the technician finish a task, he could swipe it to set the task as finished. But this is only for
 * looks better, the status of tasks will not be inserted to the database.
 */
public class TechnicianTasks extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = ManagerLogin.class.getSimpleName();

    private SwipeMenuListView taskList;
    private Bundle techInfo;

    private ImageButton callManager;
    private SimpleAdapter dataAdapter;
    private ArrayList<Task> arrangedTasks;
    private ImageButton workArrangementMenu;
    private ResideMenu resideMenu;

    private Map<String,Object> map=new HashMap<String,Object>();

    private int retCode;
    private JSONObject jsonObject;


    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_technician_tasks);

        initialize();
        arrangedTasks=techInfo.getParcelableArrayList("arrangedTasks");

        setListview();

        callManager.setOnClickListener(this);
        workArrangementMenu.setOnClickListener(this);
        handleResideMenu();

        handleSwipe();

        displayTask();
    }

    private void handleSwipe() {
        SwipeMenuCreator creator=new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem finishItem = new SwipeMenuItem(
                        getApplicationContext());
                finishItem.setBackground(new ColorDrawable(Color.parseColor("#50D2C2")));
                finishItem.setWidth(600);
                finishItem.setTitle("Finish");
                finishItem.setTitleSize(18);
                finishItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(finishItem);
            }
        };
        taskList.setMenuCreator(creator);


        taskList.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index){
                    case 0:
                        arrangedTasks.get(position).setFinished("true");
                        setFinishInDB(arrangedTasks.get(position));
                        arrangedTasks.remove(position);
                        setListview();
                        break;
                }
                return  false;
            }
        });
    }

    private void setFinishInDB(final Task task) {
        RequestQueue requestQueue = Volley.newRequestQueue(TechnicianTasks.this);

        //Create a string request, set the POST method
        //DB_ADDRESS defines the address of the host,and the php file is used to connect the database.
        //listener and errorlistener is used to receive the returned result.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, DBHelper.DB_ADDRESS + "setFinished.php", listener, errorListener) {
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("taskId", task.getId()+"");
                map.put("status","true");
                return map;
            }
        };
        requestQueue.add(stringRequest);

    }

    Response.Listener<String> listener = new Response.Listener<String>() {
        @Override
        public void onResponse(String s) {
            try {
                jsonObject = new JSONObject(s);
                retCode = jsonObject.getInt("success");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (retCode == 1) {
                Toast.makeText(TechnicianTasks.this, "Task has been set as finished successfully", Toast.LENGTH_SHORT).show();
            } else {
            }
        }
    };

    //If the database is failed to connect, a prompt will be sent to inform the technician.
    Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError volleyError) {
            Toast.makeText(TechnicianTasks.this, "Database not connected", Toast.LENGTH_SHORT).show();
            Log.e(TAG, volleyError.getMessage(), volleyError);
        }
    };


    private void displayTask() {
        taskList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int myItemInt, long l) {
                Intent intent=new Intent(TechnicianTasks.this, DisplayTask.class);
                techInfo.putInt("selectedTask",myItemInt+1);
                intent.putExtras(techInfo);
                startActivity(intent);
            }
        });
    }

    /**
     *
     */
    private void setListview() {
        List<Map<String,Object>> list=new ArrayList<Map<String,Object>>();

        for(int i=0;i<arrangedTasks.size();i++){
            if(arrangedTasks.get(i).getFinished().equals("false")){
                map=new HashMap<String,Object>();
                map.put("taskName",arrangedTasks.get(i).getName());
                map.put("skillLevel","Level "+arrangedTasks.get(i).getSkillRequirement()+"");
                map.put("estimateDur",arrangedTasks.get(i).getDuration()+"");
                list.add(map);
            }
        }

        dataAdapter=new SimpleAdapter(TechnicianTasks.this,list,R.layout.manage_task_list,new String[]{"taskName","skillLevel","estimateDur"},new int[]{R.id.task_name,R.id.task_skill,R.id.task_duration});
        taskList.setAdapter(dataAdapter);
    }


    /**
     * Initialize the components.
     */
    private void initialize() {
        taskList=(SwipeMenuListView) findViewById(R.id.taskArrangement);
        callManager=(ImageButton)findViewById(R.id.callManager);
        techInfo=getIntent().getExtras();
        workArrangementMenu=(ImageButton)findViewById(R.id.workArrangementMenu);
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
        resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_LEFT);



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


    /**
     * Handle the click event.
     * @param view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case 0:
                Intent intent0=new Intent(TechnicianTasks.this,TechnicianDashboard.class);
                intent0.putExtras(techInfo);
                techInfo.putParcelableArrayList("arrangedTasks",arrangedTasks);
                startActivity(intent0);
                break;
            case 1:
                Intent intent=new Intent(TechnicianTasks.this,TechnicianTasks.class);
                intent.putExtras(techInfo);
                techInfo.putParcelableArrayList("arrangedTasks",arrangedTasks);
                startActivity(intent);
                finish();
                break;
            case 2:
                Intent intent1=new Intent(TechnicianTasks.this,TechnicianRoute.class);
                intent1.putExtras(techInfo);
                techInfo.putParcelableArrayList("arrangedTasks",arrangedTasks);
                startActivity(intent1);
                finish();
                break;
            case 3:
                Intent intent20=new Intent(TechnicianTasks.this,TechnicianSetting.class);
                intent20.putExtras(techInfo);
                techInfo.putParcelableArrayList("arrangedTasks",arrangedTasks);
                startActivity(intent20);
                break;
            case 4:
                Intent intent3=new Intent(TechnicianTasks.this, TechnicianLogin.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent3);
                break;
            case R.id.callManager:
                Intent intent2=new Intent(Intent.ACTION_DIAL,null);
                intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent2);
                break;
            case R.id.workArrangementMenu:
                resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
                break;

        }
    }
}

