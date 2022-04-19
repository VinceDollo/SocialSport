package com.example.socialsport.entities;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class SportActivity {

    private String sport;
    private final String description;
    private final String date;
    private final String time;
    private final String uuidOrganiser;
    private final String coords;
    private ArrayList<String> uuids = new ArrayList<>();

    public SportActivity() {
        this.sport = "";
        this.description = "";
        this.date = "";
        this.time = "";
        this.uuidOrganiser = "";
        this.coords = "";
    }

    public SportActivity(String sport, String description, String date, String time, String uuidOrganiser, String coords) {
        this.sport = sport;
        this.description = description;
        this.date = date;
        this.time = time;
        this.uuidOrganiser = uuidOrganiser;
        this.coords = coords;
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

    public String getTime() {
        return time;
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

    @NonNull
    @Override
    public String toString() {
        return "SportActivity{" +
                "sport='" + sport + '\'' +
                ", dateTime=" + date + " " + time + '\'' +
                '}';
    }
}