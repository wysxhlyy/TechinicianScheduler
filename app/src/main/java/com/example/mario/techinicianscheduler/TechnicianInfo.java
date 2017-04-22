package com.example.mario.techinicianscheduler;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Technician class, A technicianInfo object will store all the information related to this technician.
 * Implements the parcelable in order to make the technicianInfo object could be transmitted between different activity.
 */

public class TechnicianInfo implements Parcelable {
    private int id;
    private String username;
    private String email;
    private String phone;
    private String firstName;
    private String surname;
    private int skillLevel;
    private int workHour;

    public TechnicianInfo(){

    }

    protected TechnicianInfo(Parcel in) {
        id = in.readInt();
        username = in.readString();
        email = in.readString();
        phone = in.readString();
        firstName = in.readString();
        surname = in.readString();
        skillLevel = in.readInt();
        workHour = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(username);
        dest.writeString(email);
        dest.writeString(phone);
        dest.writeString(firstName);
        dest.writeString(surname);
        dest.writeInt(skillLevel);
        dest.writeInt(workHour);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TechnicianInfo> CREATOR = new Creator<TechnicianInfo>() {
        @Override
        public TechnicianInfo createFromParcel(Parcel in) {
            return new TechnicianInfo(in);
        }

        @Override
        public TechnicianInfo[] newArray(int size) {
            return new TechnicianInfo[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public int getSkillLevel() {
        return skillLevel;
    }

    public void setSkillLevel(int skillLevel) {
        this.skillLevel = skillLevel;
    }

    public int getWorkHour() {
        return workHour;
    }

    public void setWorkHour(int workHour) {
        this.workHour = workHour;
    }
}
