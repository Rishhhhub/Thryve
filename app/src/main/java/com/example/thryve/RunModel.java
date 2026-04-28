package com.example.thryve;

import com.google.firebase.Timestamp;
import java.util.List;

public class RunModel {

    public double distance;
    public List <Double> lats;
    public List <Double> lngs;
    public long duration_sec;
    public Timestamp timestamp;

    public RunModel() {
        // required for Firestore
    }

    public RunModel(double distance, long duration_sec, Timestamp timestamp) {
        this.distance = distance;
        this.duration_sec = duration_sec;
        this.timestamp = timestamp;
    }
}