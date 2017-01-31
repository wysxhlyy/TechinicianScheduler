package com.example.mario.techinicianscheduler.Technician;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mario.techinicianscheduler.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class TechnicianRoute extends FragmentActivity implements OnMapReadyCallback,DirectionFinderListener {

    private GoogleMap mMap;
    private ArrayList<LatLng> recordPos;
    private LatLng startEnd=new LatLng(37.331629,-121.8923151);
    private TextView orderedListShow;
    private float minimumDistance=100000000;

    private Button improve;
    private Button route;
    private ArrayList<LatLng> orderedList;
    private ProgressDialog progressDialog;
    private List<Polyline> polylinePaths = new ArrayList<>();
    private int routeCount=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_technician_route);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);//start map service

        Bundle bundle=getIntent().getExtras();
        recordPos=bundle.getParcelableArrayList("recordPos");
        orderedList=new ArrayList<LatLng>();



       findShortestOrder();

        routePlan();

        orderedListShow=(TextView)findViewById(R.id.orderedList);
        improve=(Button)findViewById(R.id.improveSolution);
        minimumDistance=calculateDistance(orderedList);

        improve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                kopt(orderedList);

                String show="";
                for(int i=0;i<orderedList.size();i++){
                    show+="place"+i+":"+orderedList.get(i).latitude+","+orderedList.get(i).longitude+"\n";
                }
                show+="total Distance:"+calculateDistance(orderedList);
                orderedListShow.setText(show);

                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(startEnd).title("Home").icon(BitmapDescriptorFactory.fromResource(R.drawable.start)));
                for(int i=1;i<orderedList.size()-1;i++){
                    LatLng latLng=orderedList.get(i);
                    mMap.addMarker(new MarkerOptions().position(latLng).title("place"+i));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,13.0f));
                }
            }
        });


        String show="";
        for(int i=0;i<orderedList.size();i++){
            show+="place"+i+":"+orderedList.get(i).latitude+","+orderedList.get(i).longitude+"\n";
        }
        show+="total Distance:"+calculateDistance(orderedList);
        orderedListShow.setText(show);


        route=(Button)findViewById(R.id.googleRoute);
        route.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.clear();
                if(routeCount<orderedList.size()-1){
                    try {
                        new DirectionFinder(TechnicianRoute.this,orderedList.get(routeCount).latitude+","+orderedList.get(routeCount).longitude,orderedList.get(routeCount+1).latitude+","+orderedList.get(routeCount+1).longitude).execute();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    routeCount++;
                }else {
                    Toast.makeText(TechnicianRoute.this,"Congratulations! You have finished today's task",Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    /**
     * Use 2-opt heuristic to improve the initial solution made by greedy search.
     */
    private void kopt(ArrayList<LatLng> list) {
        float bestDistance=calculateDistance(list);
        for(int i=1;i<list.size()-1;i++){
            for(int j=i+1;j<list.size()-1;j++){
                ArrayList<LatLng> newList=new ArrayList<LatLng>();
                newList.addAll(list);
                swap(i,j,newList);
                float newDist=calculateDistance(newList);
                if(newDist<bestDistance){
                    list.clear();
                    list.addAll(newList);
                }
            }
        }

        float improveDistance=calculateDistance(list);
        if(improveDistance<bestDistance){
            bestDistance=improveDistance;
            kopt(list);
        }
    }

    private void swap(int i,int j,ArrayList<LatLng> list){
        LatLng latlng1=list.get(i);
        LatLng latlng2=list.get(j);

        list.set(i,latlng2);
        list.set(j,latlng1);
    }

    /**
     * Use google map to plan the route for technician.
     */
    private void routePlan() {
    }

    /**
     * Use Traveling Salesmen problem to find the shortest route order.
     */
    private void findShortestOrder() {
        orderedList.add(startEnd);
        ArrayList<LatLng> waitList=new ArrayList<LatLng>();
        waitList.addAll(recordPos);
        greedySearch(waitList,startEnd);
    }

    private void greedySearch(ArrayList<LatLng> waitList,LatLng base) {

        float minDistance=10000000;
        int minDistId=0;

        //calculate the distance between startpos to each other position
        for(int i=0;i<waitList.size();i++){
            LatLng latlng=waitList.get(i);
            float[] distBetweemTwoNodes=new float[1];
            Location.distanceBetween(base.latitude,base.longitude,latlng.latitude,latlng.longitude,distBetweemTwoNodes);
            if(distBetweemTwoNodes[0]<minDistance){
                minDistance=distBetweemTwoNodes[0];         //find the position with minimum distance
                minDistId=i;
            }
        }
        if(waitList.size()>1){
            LatLng newBase=waitList.get(minDistId);
            orderedList.add(waitList.get(minDistId));
            waitList.remove(minDistId);
            greedySearch(waitList,newBase);
        }else {
            orderedList.add(waitList.get(minDistId));
            waitList.remove(minDistId);
            orderedList.add(startEnd);
        }

    }

    private float calculateDistance(ArrayList<LatLng> list){
        float total=0;
        for(int i=0;i<list.size()-1;i++){
            float[] distBetweemTwoNodes=new float[1];
            Location.distanceBetween(list.get(i).latitude,list.get(i).longitude,list.get(i+1).latitude,list.get(i+1).longitude,distBetweemTwoNodes);
            total+=distBetweemTwoNodes[0];
        }

        return total;
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.addMarker(new MarkerOptions().position(startEnd).title("Home").icon(BitmapDescriptorFactory.fromResource(R.drawable.start)));
        for(int i=1;i<orderedList.size()-1;i++){
            LatLng latLng=orderedList.get(i);
            mMap.addMarker(new MarkerOptions().position(latLng).title("place"+i));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,13.0f));
        }
    }

    @Override
    public void onDirectionFinderStart() {
        progressDialog=ProgressDialog.show(this,"Please wait.","Finding direction..",true);
        if(polylinePaths!=null){
            for(Polyline polyline:polylinePaths){
                polyline.remove();
            }
        }
    }

    @Override
    public void onDirectionFinderSuccess(List<DirectionFinder.Route> routes) {
        progressDialog.dismiss();
        polylinePaths=new ArrayList<>();

        for(DirectionFinder.Route route:routes){
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation,16.0f));
            mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.start)).position(route.startLocation));
            mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.end)).position(route.endLocation));

            PolylineOptions polylineOptions=new PolylineOptions().
                    geodesic(true).color(Color.BLUE).width(15);

            for (int i=0;i<route.points.size();i++){
                polylineOptions.add(route.points.get(i));
            }

            polylinePaths.add(mMap.addPolyline(polylineOptions));

        }
    }
}
