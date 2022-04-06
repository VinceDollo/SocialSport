package com.example.socialsport.entities;

import java.util.ArrayList;
import java.util.List;

public class SportActivity {

    private String sport;
    private final String description;
    private final String date;
    private final String hour;
    private final String uuidOrganiser;
    private final String coords;
    private ArrayList<String> uuids;

    public SportActivity() {
        this.sport = "";
        this.description = "";
        this.date = "";
        this.hour = "";
        this.uuidOrganiser = "";
        this.coords = "";
        uuids = new ArrayList<>();
    }

    public SportActivity(String sport, String description, String date, String hour, String uuidOrganiser, String coords) {
        this.sport = sport;
        this.description = description;
        this.date = date;
        this.hour = hour;
        this.uuidOrganiser = uuidOrganiser;
        this.coords = coords;
        uuids = new ArrayList<>();
        uuids.add(uuidOrganiser);

        new SportActivity(); //Code smell
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

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }

    public String getHour() {
        return hour;
    }

    public String getUuidOrganiser() {
        return uuidOrganiser;
    }

    public List<String> getUuids() {
        return uuids;
    }

    public void setUuids(List<String> uuids) {
        this.uuids = (ArrayList<String>) uuids;
    }


}