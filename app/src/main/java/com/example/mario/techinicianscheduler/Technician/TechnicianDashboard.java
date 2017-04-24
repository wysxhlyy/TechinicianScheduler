package com.example.mario.techinicianscheduler.Technician;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextClock;
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
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import su.levenetc.android.textsurface.Text;
import su.levenetc.android.textsurface.TextBuilder;
import su.levenetc.android.textsurface.TextSurface;
import su.levenetc.android.textsurface.animations.Alpha;
import su.levenetc.android.textsurface.animations.ChangeColor;
import su.levenetc.android.textsurface.animations.Delay;
import su.levenetc.android.textsurface.animations.Parallel;
import su.levenetc.android.textsurface.animations.Rotate3D;
import su.levenetc.android.textsurface.animations.Sequential;
import su.levenetc.android.textsurface.animations.ShapeReveal;
import su.levenetc.android.textsurface.animations.SideCut;
import su.levenetc.android.textsurface.animations.Slide;
import su.levenetc.android.textsurface.animations.TransSurface;
import su.levenetc.android.textsurface.contants.Align;
import su.levenetc.android.textsurface.contants.Pivot;
import su.levenetc.android.textsurface.contants.Side;

import static com.example.mario.techinicianscheduler.R.drawable.schedule;

/**
 * Entrance of the technicians' account, the work houur of technican,the number of tasks has been assigned to him
 * and the estimate duration to finish these tasks has been shown on the activity.
 * A cool text animation will be shown when this activity start, after the animation ends, the lower half of the
 * page will show a clock which indicates current time.
 */

public class TechnicianDashboard extends AppCompatActivity implements View.OnClickListener {

    private TextView loggedTechUsername;
    private ImageButton techStartSideBar;
    private TextView techDate;
    private TextView techTaskNum;
    private TextView techWorkHour;
    private TextView taskEstimateTime;

    private RelativeLayout layout;


    private int estimateDuration=0;

    private Bundle techInfo;
    private String technicianId;

    private JSONObject jsonObject;
    private int retCode;
    private RequestQueue requestQueue;

    private ArrayList<Task> arrangedTasks;
    private ArrayList<LatLng> recordPos;
    private int taskSize;

    private ResideMenu resideMenu;

    private TextClock timeShow;
    private Typeface typeface;
    private TextSurface textSurface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_technician_dashboard);
        initialize();

        technicianId = techInfo.getInt("techId") + "";
        loggedTechUsername.setText(techInfo.getString("firstName"));
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DAY_OF_MONTH);
        techDate.setText(year + "/" + month + "/" + day);


        getWorkArrangement();

        techStartSideBar.setOnClickListener(this);

        handleResideMenu();

        dynamicLayout();

        //set the style of textclock
        timeShow.setFormat24Hour("hh:mm:ss");
        Typeface tf=Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/Satellite.ttf");
        timeShow.setTypeface(tf);

    }

    private void dynamicLayout(){
        final Handler h = new Handler();

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                h.post(new Runnable() {
                    @Override
                    public void run() {
                        layout.removeView(textSurface);
                        layout.addView(timeShow);
                        h.removeCallbacks(this);
                    }
                });
            }
        }).start();
    }


    /**
     * Create the text animation when enter the dashboard.
     */
    private void handleTextAnimation() {

        typeface=Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/Roboto-Black.ttf");

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTypeface(typeface);
        String[] weeks = {"Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"};
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(new Date());

        Text t1 = TextBuilder
                .create("Hello")
                .setPaint(paint)
                .setSize(66)
                .setAlpha(0)
                .setColor(Color.DKGRAY)
                .setPosition(Align.SURFACE_CENTER).build();

        Text t2 = TextBuilder
                .create(techInfo.getString("firstName"))
                .setPaint(paint)
                .setSize(40)
                .setAlpha(0)
                .setColor(Color.parseColor("#50D2C2"))
                .setPosition(Align.BOTTOM_OF, t1).build();

        Text t3 = TextBuilder
                .create("     Today is "+weeks[calendar.get(Calendar.DAY_OF_WEEK)-1])
                .setPaint(paint)
                .setSize(40)
                .setAlpha(0)
                .setColor(Color.DKGRAY)
                .setPosition(Align.RIGHT_OF, t2).build();

        Text t4 = TextBuilder
                .create("you have "+taskSize+" tasks")
                .setPaint(paint)
                .setSize(35)
                .setAlpha(0)
                .setColor(Color.parseColor("#50D2C2"))
                .setPosition(Align.BOTTOM_OF, t3).build();

        Text t5 = TextBuilder
                .create("Estimate Work Hour: ")
                .setPaint(paint)
                .setSize(20)
                .setAlpha(0)
                .setColor(Color.BLACK)
                .setPosition(Align.BOTTOM_OF | Align.CENTER_OF, t4).build();

        Text t6 = TextBuilder
                .create(estimateDuration+" min")
                .setPaint(paint)
                .setSize(20)
                .setAlpha(0)
                .setColor(Color.BLACK)
                .setPosition(Align.RIGHT_OF, t5).build();

        Text t7 = TextBuilder
                .create("Just Do it!!!")
                .setPaint(paint)
                .setSize(60)
                .setAlpha(0)
                .setColor(Color.BLACK)
                .setPosition(Align.CENTER_OF, t6).build();



        textSurface.play(
                new Sequential(
                        ShapeReveal.create(t1, 750, SideCut.show(Side.LEFT), false),
                        new Parallel(ShapeReveal.create(t1, 600, SideCut.hide(Side.LEFT), false), new Sequential(Delay.duration(300), ShapeReveal.create(t1, 600, SideCut.show(Side.LEFT), false))),
                        new Parallel(new TransSurface(500, t2, Pivot.CENTER), ShapeReveal.create(t2, 1300, SideCut.show(Side.LEFT), false)),
                        Delay.duration(500),
                        new Parallel(new TransSurface(600, t3, Pivot.CENTER), Slide.showFrom(Side.LEFT, t3, 600), ChangeColor.to(t3, 750, Color.BLACK)),
                        Delay.duration(400),
                        new Parallel(TransSurface.toCenter(t4, 800), Rotate3D.showFromSide(t4, 750, Pivot.TOP)),
                        Delay.duration(500),
                        new Parallel(TransSurface.toCenter(t5, 800), Slide.showFrom(Side.TOP, t5, 500)),
                        new Parallel(TransSurface.toCenter(t6, 800), Slide.showFrom(Side.LEFT, t6, 500)),
                        Delay.duration(100),
                        new Parallel(Alpha.hide(t1,1500),Alpha.hide(t2,1500),Alpha.hide(t3, 1500),Alpha.hide(t4,1500),Alpha.hide(t5,1500),Alpha.hide(t6, 1500)),
                        new Parallel(TransSurface.toCenter(t7, 500), Rotate3D.showFromSide(t7, 750, Pivot.LEFT)),
                        new Parallel(Alpha.hide(t7, 1500))
                )
        );


    }


    /**
     * Handle the side menu.
     */
    private void handleResideMenu(){
        resideMenu=new ResideMenu(this);
        resideMenu.setShadowVisible(true);
        resideMenu.attachToActivity(this);
        resideMenu.setScaleValue(0.6f);
        resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_RIGHT);




        String titles[]={"Home","Work Arrangement","Route","Settings","Log out"};
        int icon[]={R.drawable.home, schedule,R.drawable.route,R.drawable.settings,R.drawable.logout};

        for(int i=0;i<titles.length;i++){
            ResideMenuItem item=new ResideMenuItem(this,icon[i],titles[i]);
            item.setId(i);
            item.setOnClickListener(this);
            resideMenu.addMenuItem(item,ResideMenu.DIRECTION_LEFT);
        }

        resideMenu.setMenuListener(menuListener);
    }

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
     * Communicate with database to get the tasks assigned to the technician.
     */
    private void getWorkArrangement() {
        requestQueue= Volley.newRequestQueue(TechnicianDashboard.this);

        StringRequest stringRequest=new StringRequest(Request.Method.POST, DBHelper.DB_ADDRESS+"getWorkArrangement.php",listener,errorListener){
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("technicianId", technicianId);
                return map;
            }
        };
        requestQueue.add(stringRequest);
    }

    /**
     * Initial the components.
     */
    private void initialize() {
        loggedTechUsername=(TextView)findViewById(R.id.loggedTechUsername);
        techInfo=getIntent().getExtras();
        arrangedTasks=new ArrayList<>();
        recordPos=new ArrayList<>();
        techStartSideBar=(ImageButton)findViewById(R.id.techStartSideBar);
        techDate=(TextView)findViewById(R.id.techDate);
        techWorkHour=(TextView)findViewById(R.id.techWorkHour);
        techTaskNum=(TextView)findViewById(R.id.techTaskNum);
        taskEstimateTime=(TextView)findViewById(R.id.techEstimateTaskDur);
        textSurface=(TextSurface) findViewById(R.id.techTextSurface);
        layout=(RelativeLayout)findViewById(R.id.activity_technician_dashboard);
        timeShow=(TextClock) findViewById(R.id.timeShow);
        layout.removeView(timeShow);
        //timeShow.setVisibility(View.INVISIBLE);
    }


    @Override
    public void onClick(View view) {
        techInfo.putParcelableArrayList("arrangedTasks",arrangedTasks);
        techInfo.putParcelableArrayList("recordPos",recordPos);
        switch (view.getId()){
            case 0:
                Intent intent0=new Intent(TechnicianDashboard.this,TechnicianDashboard.class);
                intent0.putExtras(techInfo);
                startActivity(intent0);
                finish();
                break;
            case 1:
                Intent intent=new Intent(TechnicianDashboard.this,TechnicianTasks.class);
                intent.putExtras(techInfo);
                startActivity(intent);
                break;
            case 2:
                Intent intent1=new Intent(TechnicianDashboard.this,TechnicianRoute.class);
                intent1.putExtras(techInfo);
                startActivity(intent1);
                break;
            case 3:
                Intent intent2=new Intent(TechnicianDashboard.this,TechnicianSetting.class);
                intent2.putExtras(techInfo);
                startActivity(intent2);
                break;
            case 4:
                Intent intent3=new Intent(TechnicianDashboard.this, TechnicianLogin.class);
                startActivity(intent3);
                break;
            case R.id.techStartSideBar:
                resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
                break;

        }
    }


    /**
     * Get all the tasks information when the technician log in.
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
                try {
                    taskSize=Integer.parseInt(jsonObject.getString("taskSize"))-1;
                    for(int i=1;i<taskSize+1;i++){
                        Task task=new Task();
                        task.setName(jsonObject.getString("taskName"+i));
                        task.setSkillRequirement(Integer.parseInt(jsonObject.getString("skill_level"+i)));
                        task.setStationId(jsonObject.getString("stationId"+i));
                        task.setDuration(jsonObject.getInt("duration"+i));
                        task.setDescription(jsonObject.getString("description"+i));
                        task.setStationName(jsonObject.getString("stationName"+i));
                        task.setPosition(new LatLng(jsonObject.getDouble("latitude"+i),jsonObject.getDouble("longitude"+i)));
                        task.setFinished(jsonObject.getString("status"+i));
                        recordPos.add(task.getPosition());
                        arrangedTasks.add(task);
                        estimateDuration+=task.getDuration();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                techTaskNum.setText(taskSize+"");
                techWorkHour.setText(techInfo.getString("workHour"));
                estimateDuration=estimateDuration/Integer.parseInt(techInfo.getString("skillLevel"));
                taskEstimateTime.setText(estimateDuration+"");
                handleTextAnimation();

            }else {

            }
        }
    };

    Response.ErrorListener errorListener=new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError volleyError) {
            Toast.makeText(TechnicianDashboard.this,"Database not connected",Toast.LENGTH_SHORT).show();
        }
    };


}
