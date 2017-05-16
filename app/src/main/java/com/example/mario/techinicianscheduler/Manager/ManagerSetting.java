package com.example.mario.techinicianscheduler.Manager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Handle the change of personal information for managers.
 * After decide to update the information, the app will communication with database.
 */
public class ManagerSetting extends AppCompatActivity implements View.OnClickListener {

    private EditText password;
    private EditText email;
    private EditText phone;
    private ImageButton menu;
    private ResideMenu resideMenu;

    private Button update;
    private Bundle managerInfo;
    private JSONObject jsonObject;
    private int retCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_setting);
        initialize();

        managerInfo = getIntent().getExtras();
        password.setText(managerInfo.getString("managerPass"));
        email.setText(managerInfo.getString("managerEmail"));
        phone.setText(managerInfo.getString("managerPhone"));

        //Click the update button can insert all the information into database.
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (password.getText().toString().equals("") || !email.getText().toString().matches("^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$") || !phone.getText().toString().matches("\\d+")) {
                    Toast.makeText(ManagerSetting.this, "Please input valid information", Toast.LENGTH_SHORT).show();
                    return;
                }
                RequestQueue requestQueue = Volley.newRequestQueue(ManagerSetting.this);

                StringRequest stringRequest = new StringRequest(Request.Method.POST, DBHelper.DB_ADDRESS + "updateManager.php", listener, errorListener) {
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("password", password.getText().toString());
                        map.put("email", email.getText().toString());
                        map.put("phone", phone.getText().toString());
                        map.put("managerId", managerInfo.getString("managerId"));
                        return map;
                    }
                };

                requestQueue.add(stringRequest);

                Intent intent = new Intent(ManagerSetting.this, ManagerLogin.class);
                startActivity(intent);
            }
        });

        menu.setOnClickListener(this);
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


    /**
     * Initial the components.
     */
    private void initialize() {
        password = (EditText) findViewById(R.id.managerSettingPass);
        email = (EditText) findViewById(R.id.managerSettingEmail);
        phone = (EditText) findViewById(R.id.managerSettingPhone);
        update = (Button) findViewById(R.id.managerSettingUpdate);
        menu = (ImageButton) findViewById(R.id.settingMenu);
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
                Toast.makeText(ManagerSetting.this, "Update Successfully", Toast.LENGTH_SHORT).show();

            }
        }
    };

    Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError volleyError) {
            Toast.makeText(ManagerSetting.this, "Database not connected", Toast.LENGTH_SHORT).show();

        }
    };

    /**
     * Handle the click event of the button in side menu.
     * @param view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case 0:
                Intent intentt = new Intent(ManagerSetting.this, ManagerDashboard.class);
                intentt.putExtras(managerInfo);
                startActivity(intentt);
                finish();
                break;
            case 1:
                Intent intent = new Intent(ManagerSetting.this, ChooseTask.class);
                Bundle bundle = managerInfo;
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
                break;
            case 2:
                Intent intent1 = new Intent(ManagerSetting.this, ManageTasks.class);
                Bundle bundle1 = managerInfo;
                intent1.putExtras(bundle1);
                startActivity(intent1);
                finish();
                break;
            case 3:
                Intent intent2 = new Intent(ManagerSetting.this, ManageTechnicians.class);
                Bundle bundle2 = managerInfo;
                intent2.putExtras(bundle2);
                startActivity(intent2);
                finish();
                break;
            case 4:
                Intent intent3 = new Intent(ManagerSetting.this, ManagerSetting.class);
                intent3.putExtras(managerInfo);
                startActivity(intent3);
                finish();
                break;
            case 5:
                Intent intent4 = new Intent(ManagerSetting.this, ManagerLogin.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent4);
                break;
            case R.id.settingMenu:
                resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
                break;

        }
    }
}
