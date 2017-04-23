package com.example.mario.techinicianscheduler.Technician;

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
 * The technician could change its personal information here.
 */

public class TechnicianSetting extends AppCompatActivity implements View.OnClickListener {

    private EditText password;
    private EditText email;
    private EditText phone;

    private Button update;
    private Bundle techInfo;
    private JSONObject jsonObject;
    private int retCode;

    private ResideMenu resideMenu;
    private ImageButton techSettingMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_technician_setting);

        initialize();

        techInfo = getIntent().getExtras();

        password.setText(techInfo.getString("password"));
        email.setText(techInfo.getString("email"));
        phone.setText(techInfo.getString("phone"));

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (password.equals("") || email.equals("") || phone.equals("")) {
                    Toast.makeText(TechnicianSetting.this, "Please fill in all the information", Toast.LENGTH_SHORT).show();
                    return;
                }

                RequestQueue requestQueue = Volley.newRequestQueue(TechnicianSetting.this);

                StringRequest stringRequest = new StringRequest(Request.Method.POST, DBHelper.DB_ADDRESS + "updateTechnician.php", listener, errorListener) {
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("password", password.getText().toString());
                        map.put("email", email.getText().toString());
                        map.put("phone", phone.getText().toString());
                        map.put("techId", techInfo.getInt("techId") + "");
                        return map;
                    }
                };

                requestQueue.add(stringRequest);

                Intent intent = new Intent(TechnicianSetting.this, TechnicianLogin.class);
                startActivity(intent);
            }
        });

        handleResideMenu();
        techSettingMenu.setOnClickListener(this);
    }


    private void handleResideMenu() {
        resideMenu = new ResideMenu(this);
        resideMenu.setShadowVisible(true);
        resideMenu.attachToActivity(this);
        resideMenu.setScaleValue(0.6f);
        resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_RIGHT);


        String titles[] = {"Home","Work Arrangement", "Route", "Settings", "Log out"};
        int icon[] = {R.drawable.home,R.drawable.schedule, R.drawable.route, R.drawable.settings, R.drawable.logout};

        for (int i = 0; i < titles.length; i++) {
            ResideMenuItem item = new ResideMenuItem(this, icon[i], titles[i]);
            item.setId(i);
            item.setOnClickListener(this);
            resideMenu.addMenuItem(item, ResideMenu.DIRECTION_LEFT);
        }

        resideMenu.setMenuListener(menuListener);
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
        password = (EditText) findViewById(R.id.techSettingPass);
        email = (EditText) findViewById(R.id.techSettingEmail);
        phone = (EditText) findViewById(R.id.techSettingPhone);
        update = (Button) findViewById(R.id.techSettingUpdate);
        techSettingMenu = (ImageButton) findViewById(R.id.techSettingMenu);
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
                Toast.makeText(TechnicianSetting.this, "Update Successfully", Toast.LENGTH_SHORT).show();

            }
        }
    };

    Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError volleyError) {
            Toast.makeText(TechnicianSetting.this, "Database not connected", Toast.LENGTH_SHORT).show();

        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case 0:
                Intent intent0 = new Intent(TechnicianSetting.this, TechnicianDashboard.class);
                intent0.putExtras(techInfo);
                startActivity(intent0);
                break;
            case 1:
                Intent intent = new Intent(TechnicianSetting.this, TechnicianTasks.class);
                intent.putExtras(techInfo);
                startActivity(intent);
                break;
            case 2:
                Intent intent1 = new Intent(TechnicianSetting.this, TechnicianRoute.class);
                intent1.putExtras(techInfo);
                startActivity(intent1);
                break;
            case 3:
                Intent intent2 = new Intent(TechnicianSetting.this, TechnicianSetting.class);
                intent2.putExtras(techInfo);
                startActivity(intent2);
                finish();
                break;
            case 4:
                Intent intent3 = new Intent(TechnicianSetting.this, TechnicianLogin.class);
                startActivity(intent3);
                break;
            case R.id.techSettingMenu:
                resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
                break;
        }
    }
}