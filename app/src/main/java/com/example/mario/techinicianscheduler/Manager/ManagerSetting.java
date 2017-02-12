package com.example.mario.techinicianscheduler.Manager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ManagerSetting extends AppCompatActivity {

    private EditText password;
    private EditText email;
    private EditText phone;

    private Button update;
    private Bundle managerInfo;
    private JSONObject jsonObject;
    private int retCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_setting);
        initialize();

        managerInfo=getIntent().getExtras();
        password.setText(managerInfo.getString("managerPass"));
        email.setText(managerInfo.getString("managerEmail"));
        phone.setText(managerInfo.getString("managerPhone"));

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(password.equals("")||email.equals("")||phone.equals("")){
                    Toast.makeText(ManagerSetting.this,"Please fill in all the information",Toast.LENGTH_SHORT).show();
                    return;
                }

                RequestQueue requestQueue= Volley.newRequestQueue(ManagerSetting.this);

                StringRequest stringRequest=new StringRequest(Request.Method.POST, DBHelper.DB_ADDRESS+ "updateManager.php",listener,errorListener){
                    protected Map<String,String> getParams() throws AuthFailureError {
                        Map<String,String> map=new HashMap<String, String>();
                        map.put("password",password.getText().toString());
                        map.put("email",email.getText().toString());
                        map.put("phone",phone.getText().toString());
                        map.put("managerId",managerInfo.getString("managerId"));
                        return map;
                    }
                };

                requestQueue.add(stringRequest);

                Intent intent=new Intent(ManagerSetting.this,ManagerLogin.class);
                startActivity(intent);
            }
        });

    }

    private void initialize() {
        password=(EditText)findViewById(R.id.managerSettingPass);
        email=(EditText)findViewById(R.id.managerSettingEmail);
        phone=(EditText)findViewById(R.id.managerSettingPhone);
        update=(Button)findViewById(R.id.managerSettingUpdate);
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
                Toast.makeText(ManagerSetting.this,"Update Successfully",Toast.LENGTH_SHORT).show();

            }
        }
    };

    Response.ErrorListener errorListener=new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError volleyError) {
            Toast.makeText(ManagerSetting.this,"Database not connected",Toast.LENGTH_SHORT).show();

        }
    };
}
