package com.example.mario.techinicianscheduler.Technician;

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
import com.example.mario.techinicianscheduler.Manager.ManagerLogin;
import com.example.mario.techinicianscheduler.R;
import com.example.mario.techinicianscheduler.SignUp;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class TechnicianLogin extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = ManagerLogin.class.getSimpleName();

    private Button login;
    private TextView technician;
    private Bundle techInfo;
    private ImageButton goManager;

    private EditText techUsername;
    private EditText techPassword;
    private SharedPreferences sharedPreferences;
    private int retCode;

    private JSONObject jsonObject;

    private String username,password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_technician_login);

        initialize();
        login.setOnClickListener(this);

        spinnerHandle();

        sharedPreferences=getSharedPreferences("techLogin",MODE_PRIVATE);
        String getName=sharedPreferences.getString("username",null);
        if(getName==null){
            Toast.makeText(TechnicianLogin.this,"No Stored Information,Input Again!",Toast.LENGTH_SHORT).show();
        }else {
            techUsername.setText(sharedPreferences.getString("username",null));
            techPassword.setText(sharedPreferences.getString("password",null));
        }

        goManager.setOnClickListener(this);
    }

    private void initialize() {
        login=(Button)findViewById(R.id.techLogIn);
        goManager=(ImageButton)findViewById(R.id.goManager);
        technician=(TextView) findViewById(R.id.goSignUp);
        techUsername=(EditText)findViewById(R.id.techUsername);
        techPassword=(EditText)findViewById(R.id.techPassword);
        techInfo=new Bundle();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.techLogIn:
                username=techUsername.getText().toString();
                password=techPassword.getText().toString();

                if(username.equals("")||password.equals("")){
                    Toast.makeText(TechnicianLogin.this,"Missing username or password",Toast.LENGTH_SHORT).show();
                    return;
                }

                RequestQueue requestQueue= Volley.newRequestQueue(TechnicianLogin.this);

                StringRequest stringRequest=new StringRequest(Request.Method.POST, DBHelper.DB_ADDRESS+ "technicianLogIn.php",listener,errorListener){
                    protected Map<String,String> getParams() throws AuthFailureError {
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
            case R.id.goManager:
                Intent intent=new Intent(TechnicianLogin.this,ManagerLogin.class);
                startActivity(intent);
                finish();
                break;
        }
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
                Intent intent=new Intent(TechnicianLogin.this,TechnicianDashboard.class);
                try {
                    techInfo.putInt("techId",jsonObject.getInt("technicianId"));
                    techInfo.putString("username",jsonObject.getString("username"));
                    techInfo.putString("password",jsonObject.getString("password"));
                    techInfo.putString("email",jsonObject.getString("email"));
                    techInfo.putString("phone",jsonObject.getString("phone"));
                    techInfo.putString("firstName",jsonObject.getString("firstName"));
                    techInfo.putString("surname",jsonObject.getString("surname"));
                    techInfo.putString("skillLevel",jsonObject.getString("skillLevel"));
                    techInfo.putString("workHour",jsonObject.getString("workHour"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                intent.putExtras(techInfo);
                startActivity(intent);
            }else {
                Toast.makeText(TechnicianLogin.this,"Wrong username or password!",Toast.LENGTH_SHORT).show();
            }
        }
    };

    Response.ErrorListener errorListener=new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError volleyError) {
            Toast.makeText(TechnicianLogin.this,"Database not connected",Toast.LENGTH_SHORT).show();
            Log.e(TAG,volleyError.getMessage(),volleyError);
        }
    };

    /**
     * Handle the transfer between technician page and manager page.
     */
    private void spinnerHandle() {
        String prompt=technician.getText().toString();

        SpannableString spannedString=new SpannableString(prompt);
        spannedString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(TechnicianLogin.this, SignUp.class);
                startActivity(intent);
                finish();
            }
        },0,prompt.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        technician.setText(spannedString);
        technician.setMovementMethod(LinkMovementMethod.getInstance());
    }


}

