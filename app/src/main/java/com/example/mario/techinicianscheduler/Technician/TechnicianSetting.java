package com.example.mario.techinicianscheduler.Technician;

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

public class TechnicianSetting extends AppCompatActivity {

    private EditText password;
    private EditText email;
    private EditText phone;

    private Button update;
    private Bundle techInfo;
    private JSONObject jsonObject;
    private int retCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_technician_setting);

        initialize();

        techInfo=getIntent().getExtras();

        password.setText(techInfo.getString("password"));
        email.setText(techInfo.getString("email"));
        phone.setText(techInfo.getString("phone"));

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(password.equals("")||email.equals("")||phone.equals("")){
                    Toast.makeText(TechnicianSetting.this,"Please fill in all the information",Toast.LENGTH_SHORT).show();
                    return;
                }

                RequestQueue requestQueue= Volley.newRequestQueue(TechnicianSetting.this);

                StringRequest stringRequest=new StringRequest(Request.Method.POST, DBHelper.DB_ADDRESS+ "updateTechnician.php",listener,errorListener){
                    protected Map<String,String> getParams() throws AuthFailureError {
                        Map<String,String> map=new HashMap<String, String>();
                        map.put("password",password.getText().toString());
                        map.put("email",email.getText().toString());
                        map.put("phone",phone.getText().toString());
                        map.put("techId",techInfo.getInt("techId")+"");
                        return map;
                    }
                };

                requestQueue.add(stringRequest);

                Intent intent=new Intent(TechnicianSetting.this,TechnicianLogin.class);
                startActivity(intent);
            }
        });
    }
    private void initialize() {
        password=(EditText)findViewById(R.id.techSettingPass);
        email=(EditText)findViewById(R.id.techSettingEmail);
        phone=(EditText)findViewById(R.id.techSettingPhone);
        update=(Button)findViewById(R.id.techSettingUpdate);
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
                Toast.makeText(TechnicianSetting.this,"Update Successfully",Toast.LENGTH_SHORT).show();

            }
        }
    };

    Response.ErrorListener errorListener=new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError volleyError) {
            Toast.makeText(TechnicianSetting.this,"Database not connected",Toast.LENGTH_SHORT).show();

        }
    };

}
