package com.example.mario.techinicianscheduler.Manager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import com.example.mario.techinicianscheduler.TechnicianInfo;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.relex.circleindicator.CircleIndicator;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * The entrance of manager's account.
 * The most important function is to get the tasks and technicians information related to this manager.
 * The receive of tasks and technicians information will only happen here to reduce the times of connecting database.
 */
public class ManagerDashboard extends AppCompatActivity implements View.OnClickListener {

    private TextView username;
    private TextView managerDashNumTask;
    private TextView manageTaskNum;
    private TextView manageTechNum;
    private Bundle managerInfo;

    private int retCode;
    private static final String TAG = ManagerLogin.class.getSimpleName();
    private JSONObject jsonObject;
    private int techNum = 0;

    private ArrayList<TechnicianInfo> techs;
    private ArrayList<Task> tasks;
    private int taskNum = 0;

    private ResideMenu resideMenu;
    private ImageButton menu;

    private static int ACTIVITY_MANAGER_SETTING = 1;
    private ViewPager viewPager;
    private CircleIndicator indicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_dashboard);

        initialize();

        if (getIntent().getExtras() != null) {
            managerInfo = getIntent().getExtras();
            username.setText(managerInfo.getString("managerName"));
        }


        //Use another thread to handle the commnunication with database while it costs long time sometimes.
        ManagerDashboard.CostTimeTask costTimeTask = new ManagerDashboard.CostTimeTask(ManagerDashboard.this);
        costTimeTask.execute();

        menu.setOnClickListener(this);

        handleResideMenu(); //Handle the side menu.
        handleIndicator();  //Handle the shuffling figure.

    }

    private void handleIndicator() {

        List<Fragment> list = new ArrayList<>();
        MyFragment image1 = new MyFragment();
        MyFragment image2 = new MyFragment();
        MyFragment image3 = new MyFragment();
        MyFragment image4 = new MyFragment();
        MyFragment image5 = new MyFragment();

        image1.setRes(R.drawable.bay1);
        image2.setRes(R.drawable.bay2);
        image3.setRes(R.drawable.bay3);
        image4.setRes(R.drawable.bay4);
        image5.setRes(R.drawable.bay5);

        list.add(image1);
        list.add(image2);
        list.add(image3);
        list.add(image4);
        list.add(image5);
        MyPageAdapter pageAdapter = new MyPageAdapter(getSupportFragmentManager(), list);


        viewPager.setAdapter(pageAdapter);
        indicator.setViewPager(viewPager);
    }


    /**
     * The menu can be seen as some buttons placed in a listview.
     */
    private void handleResideMenu() {
        resideMenu = new ResideMenu(this);
        resideMenu.setShadowVisible(true);
        resideMenu.attachToActivity(this);
        resideMenu.setScaleValue(0.6f);
        resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_RIGHT);


        String titles[] = {"Home", "Schedule", "Manage Tasks", "Manage Technicians", "Settings", "Log out"};
        int icon[] = {R.drawable.home, R.drawable.schedule, R.drawable.tasks, R.drawable.technicians, R.drawable.settings, R.drawable.logout};

        for (int i = 0; i < titles.length; i++) {
            ResideMenuItem item = new ResideMenuItem(this, icon[i], titles[i]);
            item.setId(i);
            item.setOnClickListener(this);
            resideMenu.addMenuItem(item, ResideMenu.DIRECTION_LEFT);
        }

        resideMenu.setMenuListener(menuListener);
        resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_LEFT);

    }

    private ResideMenu.OnMenuListener menuListener = new ResideMenu.OnMenuListener() {
        @Override
        public void openMenu() {
            resideMenu.setBackground(R.drawable.bg);
        }

        @Override
        public void closeMenu() {
            resideMenu.setBackground(R.drawable.white);
        }
    };

    public boolean dispatchTouchEvent(MotionEvent ev) {
        return resideMenu.dispatchTouchEvent(ev);
    }


    /**
     * Initial the components.
     */
    private void initialize() {
        username = (TextView) findViewById(R.id.loggedManagerUsername);
        managerDashNumTask = (TextView) findViewById(R.id.managerDashNumTask);
        manageTaskNum = (TextView) findViewById(R.id.manageTaskNum);
        manageTechNum = (TextView) findViewById(R.id.manageTechNum);
        menu = (ImageButton) findViewById(R.id.startSideBar);
        indicator = (CircleIndicator) findViewById(R.id.indicator);
        viewPager = (ViewPager) findViewById(R.id.viewPager);

        techs = new ArrayList<>();
        tasks = new ArrayList<>();
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder().setDefaultFontPath("fonts/WorkSans-Light.otf").setFontAttrId(R.attr.fontPath).build());

    }

    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private class CostTimeTask extends AsyncTask<String, Integer, String> {
        private ProgressDialog dialog;

        public CostTimeTask(Context context) {
            dialog = new ProgressDialog(context, 0);

            dialog.setMessage("Calculating...");
            dialog.setCancelable(true);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.show();
        }


        //commnucation with database.
        @Override
        protected String doInBackground(String... strings) {
            RequestQueue requestQueue = Volley.newRequestQueue(ManagerDashboard.this);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, DBHelper.DB_ADDRESS + "getAvailableTech.php", listener, errorListener) {
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> map = new HashMap<String, String>();
                    Log.d("test",managerInfo.getString("managerId"));
                    map.put("managerId", managerInfo.getString("managerId"));
                    return map;
                }
            };

            requestQueue.add(stringRequest);
            return null;
        }

        /**
         * After the communication over, the dialog will dismiss automatically.
         * @param s
         */
        @Override
        protected void onPostExecute(String s) {
            dialog.dismiss();

        }
    }


    /**
     * Handle the button in side menu.
     * @param view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case 0: //go home
                Intent intentt = new Intent(ManagerDashboard.this, ManagerDashboard.class);
                intentt.putExtras(managerInfo);
                startActivity(intentt);
                finish();
                break;
            case 1: //go schedule
                if(techs.size()==0){
                    Toast.makeText(this,"No available technicains",Toast.LENGTH_SHORT).show();
                }else if(tasks.size()==0){
                    Toast.makeText(this,"No available tasks",Toast.LENGTH_SHORT).show();
                }else{
                    Intent intent = new Intent(ManagerDashboard.this, ChooseTask.class);
                    Bundle bundle = managerInfo;
                    bundle.putParcelableArrayList("availableTechnician", techs);
                    bundle.putParcelableArrayList("availableTask", tasks);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                break;
            case 2: //go manage task
                Intent intent1 = new Intent(ManagerDashboard.this, ManageTasks.class);
                Bundle bundle1 = managerInfo;
                bundle1.putParcelableArrayList("availableTechnician", techs);
                bundle1.putParcelableArrayList("availableTask", tasks);
                intent1.putExtras(bundle1);
                startActivity(intent1);
                break;
            case 3: //go manage technician
                Intent intent2 = new Intent(ManagerDashboard.this, ManageTechnicians.class);
                Bundle bundle2 = managerInfo;
                bundle2.putParcelableArrayList("availableTechnician", techs);
                bundle2.putParcelableArrayList("availableTask", tasks);
                intent2.putExtras(bundle2);
                startActivity(intent2);
                break;
            case 4: //go setting part
                Intent intent3 = new Intent(ManagerDashboard.this, ManagerSetting.class);
                Bundle bundle3 = managerInfo;
                bundle3.putParcelableArrayList("availableTechnician", techs);
                bundle3.putParcelableArrayList("availableTask", tasks);
                intent3.putExtras(bundle3);
                startActivityForResult(intent3, ACTIVITY_MANAGER_SETTING);
                break;
            case 5:
                Intent intent4 = new Intent(ManagerDashboard.this, ManagerLogin.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent4);
                break;
            case R.id.startSideBar:
                resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ACTIVITY_MANAGER_SETTING) {
            recreate();
        }
    }

    /**
     * Receive and handle all the information about the manager, including the tasks and technicians he managed.
     * All the information of tasks and technicians will be collected here and only here.
     */
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
                try {
                    taskNum = Integer.parseInt(jsonObject.getString("taskNum"));
                    if (taskNum != 0) {
                        for (int i = 1; i < taskNum + 1; i++) {
                            Task task = new Task();
                            task.setId(Integer.parseInt(jsonObject.getString("taskId" + i)));
                            task.setName(jsonObject.getString("taskName" + i));
                            task.setDescription(jsonObject.getString("taskDescription" + i));
                            task.setSkillRequirement(Integer.parseInt(jsonObject.getString("taskSkill" + i)));
                            task.setDuration(Integer.parseInt(jsonObject.getString("taskDuration" + i)));
                            task.setFinished(jsonObject.getString("taskStatus" + i));
                            task.setStationName(jsonObject.getString("stationName" + i));
                            task.setPosition(new LatLng(Double.parseDouble(jsonObject.getString("stationLat" + i)), Double.parseDouble(jsonObject.getString("stationLong" + i))));

                            Log.d("task Name", task.getStationName());
                            tasks.add(task);
                        }
                    }
                    techNum = Integer.parseInt(jsonObject.getString("techNum"));
                    if (techNum != 0) {
                        for (int i = 1; i < techNum + 1; i++) {
                            TechnicianInfo t = new TechnicianInfo();
                            t.setId(Integer.parseInt(jsonObject.getString("techId" + i)));
                            t.setFirstName(jsonObject.getString("techName" + i));
                            t.setSkillLevel(Integer.parseInt(jsonObject.getString("skillLevel" + i)));
                            t.setWorkHour(jsonObject.getInt("workHour" + i));
                            t.setSurname(jsonObject.getString("surname" + i));
                            techs.add(t);
                        }
                    }

                    managerDashNumTask.setText("You have " + tasks.size() + " tasks to manage today.");
                    manageTaskNum.setText(taskNum + "");
                    manageTechNum.setText(techNum + "");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(ManagerDashboard.this, "Cannot find relate information !", Toast.LENGTH_SHORT).show();
            }
        }
    };

    Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError volleyError) {
            Toast.makeText(ManagerDashboard.this, "Database not connected", Toast.LENGTH_SHORT).show();
            Log.e(TAG, volleyError.getMessage(), volleyError);
        }
    };
}
