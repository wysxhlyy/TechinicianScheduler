package com.example.mario.techinicianscheduler;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

/**
 * Task class, A task object will store all the information related to this task.
 * Implements the parcelable in order to make the task object could be transmitted between the activity.
 */

public class Task implements Parcelable {
    private int id;
    private int skillRequirement;
    private int duration;
    private LatLng position;
    private String stationName;
    private String stationId;
    private String finished;
    private String name;
    private String description;
    //All the information need to be set out for a task.


    protected Task(Parcel in) {
        id = in.readInt();
        skillRequirement = in.readInt();
        duration = in.readInt();
        position = in.readParcelable(LatLng.class.getClassLoader());
        stationName = in.readString();
        stationId = in.readString();
        finished = in.readString();
        name = in.readString();
        description = in.readString();
    }

    public Task(){

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSkillRequirement() {
        return skillRequirement;
    }

    public void setSkillRequirement(int skillRequirement) {
        this.skillRequirement = skillRequirement;
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

    public String getFinished() {
        return finished;
    }

    public void setFinished(String finished) {
        this.finished = finished;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
        parcel.writeString(finished);
        parcel.writeString(name);
        parcel.writeString(description);
    }
}
