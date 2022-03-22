package com.example.socialsport.entities;

import java.util.ArrayList;

public class SportActivity {

    private String sport;
    private String description;
    private String date;
    private String hour;
    private String uuidOrganiser;
    private String coords;
    private ArrayList<String> uuids;

    public SportActivity(String sport, String description, String date, String hour, String uuidOrganiser, String coords) {
        ArrayList<String> uuids = new ArrayList<>();
        uuids.add(uuidOrganiser);

        this.sport = sport;
        this.description = description;
        this.date = date;
        this.hour = hour;
        this.uuidOrganiser = uuidOrganiser;
        this.coords = coords;
        this.uuids = uuids;
    }

    public String getSport() {
        return sport;
    }

    public void setSport(String sport) {
        this.sport = sport;
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