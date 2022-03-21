package com.example.socialsport.entities;

import java.util.ArrayList;

public class SportActivity {

    private String description;
    private String activityId;
    private String date;
    private String hour;
    private String uuidOrganiser;
    private String coords;
    private ArrayList<String> uuids;

    public SportActivity(String activityId,String description, String date, String hour, String uuidOrganiser, String coords) {
        ArrayList<String> uuids = new ArrayList<>();
        uuids.add(uuidOrganiser);

        this.activityId = activityId;
        this.description = description;
        this.date = date;
        this.hour = hour;
        this.uuidOrganiser = uuidOrganiser;
        this.coords = coords;
        this.uuids = uuids;
    }

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public String getCoords() {
        return coords;
    }

    public void setCoords(String coords) {
        this.coords = coords;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getUuidOrganiser() {
        return uuidOrganiser;
    }

    public void setUuidOrganiser(String uuidOrganiser) {
        this.uuidOrganiser = uuidOrganiser;
    }

    public ArrayList<String> getUuids() {
        return uuids;
    }

    public void setUuids(ArrayList<String> uuids) {
        this.uuids = uuids;
    }


}