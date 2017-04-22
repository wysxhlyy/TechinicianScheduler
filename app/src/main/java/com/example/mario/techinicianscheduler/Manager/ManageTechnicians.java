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

import com.example.mario.techinicianscheduler.Manager.HandleTechnician.AddTechnician;
import com.example.mario.techinicianscheduler.Manager.HandleTechnician.DisplayTechnician;
import com.example.mario.techinicianscheduler.R;
import com.example.mario.techinicianscheduler.ResideMenu.ResideMenu;
import com.example.mario.techinicianscheduler.ResideMenu.ResideMenuItem;
import com.example.mario.techinicianscheduler.Technician.TechnicianLogin;
import com.example.mario.techinicianscheduler.TechnicianInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manage the technician managed by this manager.The technician will be shown by a list.
 * The entrence to bind technican or view the technician's information.
 */
public class ManageTechnicians extends AppCompatActivity implements View.OnClickListener {

    private ListView techListView;
    private ImageButton addNewTech;
    private ImageButton menu;
    private SimpleAdapter dataAdapter;
    private ArrayList<TechnicianInfo> existTechs;
    private Bundle managerInfo;

    private ResideMenu resideMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_technicians);
        initialize();

        managerInfo = getIntent().getExtras();
        existTechs = managerInfo.getParcelableArrayList("availableTechnician");
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();

        for (int i = 0; i < existTechs.size(); i++) {
            map = new HashMap<>();
            map.put("workHour", existTechs.get(i).getWorkHour());
            map.put("name", upperFirstChar(existTechs.get(i).getFirstName()) + " " + upperFirstChar(existTechs.get(i).getSurname()));
            map.put("skillLevel", "Level " + existTechs.get(i).getSkillLevel() + "");
            list.add(map);
        }
        dataAdapter = new SimpleAdapter(this, list, R.layout.manage_tech_list, new String[]{"workHour", "name", "skillLevel"}, new int[]{R.id.tech_workHour, R.id.tech_name, R.id.tech_skill});
        techListView.setAdapter(dataAdapter);

        menu.setOnClickListener(this);
        addNewTech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ManageTechnicians.this, AddTechnician.class);
                intent.putExtras(managerInfo);
                startActivity(intent);
            }
        });

        displayTech();

        handleResideMenu();
    }

    /**
     * Handle the sidebar menu.
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
    }

    /**
     * set the sidebar background;
     */
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


    private void displayTech() {
        techListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int myItemInt, long l) {
                Intent intent = new Intent(ManageTechnicians.this, DisplayTechnician.class);
                managerInfo.putInt("selectedTech", myItemInt);
                intent.putExtras(managerInfo);
                startActivity(intent);
            }
        });
    }

    private void initialize() {
        techListView = (ListView) findViewById(R.id.techListView);
        addNewTech = (ImageButton) findViewById(R.id.addNewTech);
        menu = (ImageButton) findViewById(R.id.manageTechMenu);
    }


    private String upperFirstChar(String str) {
        return str.replaceFirst(str.substring(0, 1), str.substring(0, 1).toUpperCase());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case 0:
                Intent intentt = new Intent(ManageTechnicians.this, ManagerDashboard.class);
                intentt.putExtras(managerInfo);
                startActivity(intentt);
                finish();
                break;
            case 1:
                Intent intent = new Intent(ManageTechnicians.this, ChooseTask.class);
                Bundle bundle = managerInfo;
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
                break;
            case 2:
                Intent intent1 = new Intent(ManageTechnicians.this, ManageTasks.class);
                Bundle bundle1 = managerInfo;
                intent1.putExtras(bundle1);
                startActivity(intent1);
                finish();
                break;
            case 3:
                Intent intent2 = new Intent(ManageTechnicians.this, ManageTechnicians.class);
                Bundle bundle2 = managerInfo;
                intent2.putExtras(bundle2);
                startActivity(intent2);
                finish();
                break;
            case 4:
                Intent intent3 = new Intent(ManageTechnicians.this, ManagerSetting.class);
                intent3.putExtras(managerInfo);
                startActivity(intent3);
                finish();
                break;
            case 5:
                Intent intent4 = new Intent(ManageTechnicians.this, TechnicianLogin.class);
                startActivity(intent4);
                finish();
                break;
            case R.id.manageTechMenu:
                resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
                break;

        }
    }
}
