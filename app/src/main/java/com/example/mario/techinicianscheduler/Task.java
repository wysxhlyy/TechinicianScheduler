package com.example.mario.techinicianscheduler;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by mario on 2017/2/1.
 */

public class Task {
    private int id;
    private String skillRequirement;
    private String duration;
    private LatLng position;
    private String stationName;
    private String stationId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSkillRequirement() {
        return skillRequirement;
    }

    public void setSkillRequirement(String skill_requirement) {
        this.skillRequirement = skill_requirement;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public LatLng getPosition() {
        return position;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public String getStationId() {
        return stationId;
    }

    public void setStationId(String stationId) {
        this.stationId = stationId;
    }
}
