package com.example.mario.techinicianscheduler.Technician;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.mario.techinicianscheduler.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class TechnicianRoute extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ArrayList<LatLng> recordPos;
    private LatLng startEnd=new LatLng(37.331629,-121.8923151);
    private TextView orderedListShow;
    private float minimumDistance=100000000;

    private Button improve;
    private ArrayList<LatLng> orderedList;

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
                for(int i=0;i<orderedList.size();i++){
                    LatLng latLng=orderedList.get(i);
                    mMap.addMarker(new MarkerOptions().position(latLng).title("place"+i));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,12.0f));
                }
            }
        });


        String show="";
        for(int i=0;i<orderedList.size();i++){
            show+="place"+i+":"+orderedList.get(i).latitude+","+orderedList.get(i).longitude+"\n";
        }
        show+="total Distance:"+calculateDistance(orderedList);
        orderedListShow.setText(show);



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

        // Add a marker in Sydney and move the camera
        for(int i=0;i<orderedList.size();i++){
            LatLng latLng=orderedList.get(i);
            mMap.addMarker(new MarkerOptions().position(latLng).title("place"+i));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,12.0f));
        }
    }
}
