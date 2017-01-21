package com.example.mario.techinicianscheduler.Manager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mario.techinicianscheduler.R;
import com.example.mario.techinicianscheduler.SignUp;
import com.example.mario.techinicianscheduler.Technician.TechnicianLogin;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ManagerLogin extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = ManagerLogin.class.getSimpleName();


    private Button login;
    private Button signup;
    private TextView technician;

    private EditText managerUsername;
    private EditText managerPassword;
    private SharedPreferences sharedPreferences;
    private int retCode;

    private String username,password;

    Response.Listener<String> listener=new Response.Listener<String>() {
        @Override
        public void onResponse(String s) {
            try {
                JSONObject jsonObject=new JSONObject(s);
                retCode=jsonObject.getInt("success");
            }catch (JSONException e){
                e.printStackTrace();
            }

            if(retCode==1){
                Intent intent=new Intent(ManagerLogin.this,ManagerDashboard.class);
                startActivity(intent);
            }else {
                Toast.makeText(ManagerLogin.this,"Wrong username or password!",Toast.LENGTH_SHORT).show();
            }
        }
    };

    Response.ErrorListener errorListener=new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError volleyError) {
            Log.e(TAG,volleyError.getMessage(),volleyError);
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_login);

        initialize();
        login.setOnClickListener(this);
        signup.setOnClickListener(this);
        login.setOnClickListener(this);
        spinnerHandle();

        //show the stored username and password
        sharedPreferences=getSharedPreferences("managerLogin",MODE_PRIVATE);
        String getName=sharedPreferences.getString("username",null);
        if(getName==null){
            Toast.makeText(ManagerLogin.this,"No Stored Information,Input Again!",Toast.LENGTH_SHORT).show();
        }else {
            managerUsername.setText(sharedPreferences.getString("username",null));
            managerPassword.setText(sharedPreferences.getString("password",null));
        }






    }

    private void initialize() {
        login=(Button)findViewById(R.id.managerLogIn);
        signup=(Button)findViewById(R.id.managerSignUp);
        technician=(TextView) findViewById(R.id.goTechnician);
        managerUsername=(EditText)findViewById(R.id.managerUsername);
        managerPassword=(EditText)findViewById(R.id.managerPassword);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.managerLogIn:
                username=managerUsername.getText().toString();
                password=managerPassword.getText().toString();

                if(username.equals("")||password.equals("")){
                    Toast.makeText(ManagerLogin.this,"Missing username or password",Toast.LENGTH_SHORT).show();
                    return;
                }

                RequestQueue requestQueue= Volley.newRequestQueue(ManagerLogin.this);

                StringRequest stringRequest=new StringRequest(Request.Method.POST,"http://10.132.201.46/connectDB.php",listener,errorListener){
                    protected Map<String,String> getParams() throws AuthFailureError{
                        Map<String,String> map=new HashMap<String, String>();
                        map.put("username",username);
                        map.put("password",password);
                        return map;
                    }
                };

                requestQueue.add(stringRequest);
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putString("username",username);
                editor.putString("password",password);
                editor.commit();


                break;
            case R.id.managerSignUp:
                Intent intent=new Intent(ManagerLogin.this, SignUp.class);
                startActivity(intent);
                finish();
                break;
        }
    }

    public void spinnerHandle(){
        String prompt=technician.getText().toString();
        SpannableString spannedString=new SpannableString(prompt);
        spannedString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ManagerLogin.this, TechnicianLogin.class);
                startActivity(intent);
                finish();
            }
        },0,prompt.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        technician.setText(spannedString);
        technician.setMovementMethod(LinkMovementMethod.getInstance());

    }

}
