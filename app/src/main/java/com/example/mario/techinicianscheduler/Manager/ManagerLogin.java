package com.example.mario.techinicianscheduler.Manager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
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
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mario.techinicianscheduler.DBHelper;
import com.example.mario.techinicianscheduler.R;
import com.example.mario.techinicianscheduler.SignUp;
import com.example.mario.techinicianscheduler.Technician.TechnicianLogin;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ManagerLogin extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = ManagerLogin.class.getSimpleName();


    private Button login;
    private TextView technician;
    private ImageButton goTechnician;

    private EditText managerUsername;
    private EditText managerPassword;
    private SharedPreferences sharedPreferences;
    private int retCode;

    private String username,password;
    private JSONObject jsonObject;
    private RequestQueue requestQueue;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_login);

        initialize();
        login.setOnClickListener(this);
        login.setOnClickListener(this);
        goTechnician.setOnClickListener(this);
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
        technician=(TextView) findViewById(R.id.managerGoSignUp);
        managerUsername=(EditText)findViewById(R.id.managerUsername);
        managerPassword=(EditText)findViewById(R.id.managerPassword);
        goTechnician=(ImageButton)findViewById(R.id.goTechnician);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder().setDefaultFontPath("fonts/WorkSans-Light.otf").setFontAttrId(R.attr.fontPath).build());

    }

    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private class CostTimeTask extends AsyncTask<String,Integer,String> {
        private ProgressDialog dialog;


        public CostTimeTask(Context context){
            dialog=new ProgressDialog(context,0);

            dialog.setCancelable(true);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.show();
        }


        @Override
        protected String doInBackground(String... strings) {
            requestQueue= Volley.newRequestQueue(ManagerLogin.this);

            //Connect PHP File.
            StringRequest stringRequest=new StringRequest(Request.Method.POST, DBHelper.DB_ADDRESS+"managerLogin.php",listener,errorListener) {
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("username", username);
                    map.put("password", password);
                    return map;
                }
            };
            requestQueue.add(stringRequest);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            dialog.dismiss();
            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.putString("username",username);
            editor.putString("password",password);
            editor.commit();
        }
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

                ManagerLogin.CostTimeTask costTimeTask=new ManagerLogin.CostTimeTask(ManagerLogin.this);
                costTimeTask.execute();
                //Store the data to be used in next log in.

                break;
            case R.id.goTechnician:
                Intent intent=new Intent(ManagerLogin.this, TechnicianLogin.class);
                startActivity(intent);
                finish();
                break;
        }
    }

    /**
     * Use Volley.
     */
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
                Intent intent=new Intent(ManagerLogin.this,ManagerDashboard.class);
                Bundle bundle=new Bundle();
                try {
                    bundle.putString("managerName",jsonObject.getString("firstName"));   //Get the JSON data.
                    bundle.putString("managerEmail",jsonObject.getString("email"));
                    bundle.putString("managerPhone",jsonObject.getString("phone"));
                    bundle.putString("managerSurname",jsonObject.getString("surname"));
                    bundle.putString("managerPass",jsonObject.getString("password"));
                    bundle.putString("managerId",jsonObject.getString("m_id"));

                    SharedPreferences.Editor editor=getSharedPreferences("managerSession",MODE_PRIVATE).edit();
                    editor.putString("managerId",jsonObject.getString("m_id"));
                    editor.commit();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                intent.putExtras(bundle);
                startActivity(intent);
            }else {
                Toast.makeText(ManagerLogin.this,"Wrong username or password!",Toast.LENGTH_SHORT).show();
            }
        }
    };

    Response.ErrorListener errorListener=new Response.ErrorListener() {

        @Override
        public void onErrorResponse(VolleyError volleyError) {
            Toast.makeText(ManagerLogin.this,"Database not connected",Toast.LENGTH_SHORT).show();
            Log.e(TAG,volleyError.getMessage(),volleyError);
            NetworkResponse response = volleyError.networkResponse;
            if (volleyError instanceof ServerError && response != null) {
                try {
                    String res = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                    // Now you can use any deserializer to make sense of data
                    JSONObject obj = new JSONObject(res);
                } catch (UnsupportedEncodingException e1) {
                    // Couldn't properly decode data to string
                    e1.printStackTrace();
                } catch (JSONException e2) {
                    // returned data is not JSONObject?
                    e2.printStackTrace();
                }
            }
        }
    };

    /**
     * Handle the transfer between technician page and manager page.
     */
    public void spinnerHandle(){
        String prompt=technician.getText().toString();
        SpannableString spannedString=new SpannableString(prompt);
        spannedString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ManagerLogin.this, SignUp.class);
                startActivity(intent);
            }
        },0,prompt.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        technician.setText(spannedString);
        technician.setMovementMethod(LinkMovementMethod.getInstance());

    }

}
