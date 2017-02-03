package com.example.mario.techinicianscheduler;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by mario on 2017/2/1.
 */

public class Task implements Parcelable {
    private int id;
    private int skillRequirement;
    private int duration;
    private LatLng position;
    private String stationName;
    private String stationId;

    public Task(Parcel in) {
        id = in.readInt();
        skillRequirement = in.readInt();
        duration = in.readInt();
        position = in.readParcelable(LatLng.class.getClassLoader());
        stationName = in.readString();
        stationId = in.readString();
    }

    public static final Creator<Task> CREATOR = new Creator<Task>() {
        @Override
        public Task createFromParcel(Parcel in) {
            return new Task(in);
        }

        @Override
        public Task[] newArray(int size) {
            return new Task[size];
        }
    };

    public Task() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSkillRequirement() {
        return skillRequirement;
    }

    public void setSkillRequirement(int skill_requirement) {
        this.skillRequirement = skill_requirement;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeInt(skillRequirement);
        parcel.writeInt(duration);
        parcel.writeParcelable(position, i);
        parcel.writeString(stationName);
        parcel.writeString(stationId);
    }
}
