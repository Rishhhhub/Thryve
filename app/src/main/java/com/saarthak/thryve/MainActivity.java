package com.saarthak.thryve;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Grid Items Setup
        setupGrid(R.id.cardWalking, "Walking", "Low intensity", R.drawable.ic_walking);
        setupGrid(R.id.cardCycling, "Cycling", "Cardio focus", R.drawable.ic_cycling);
        setupGrid(R.id.cardYoga, "Yoga", "Flexibility", R.drawable.ic_yoga);
        setupGrid(R.id.cardSwimming, "Swimming", "Full body", R.drawable.ic_swimming);

        // List Items Setup
        setupList(R.id.listStrength, "Strength Training", "Weights & Resistance", R.drawable.ic_running);
        setupList(R.id.listRowing, "Rowing", "High intensity", R.drawable.ic_swimming);
        setupList(R.id.listHiking, "Hiking", "Nature & Elevation", R.drawable.ic_walking);
    }

    private void setupGrid(int id, String name, String sub, int icon) {
        View v = findViewById(id);
        ((TextView)v.findViewById(R.id.activityName)).setText(name);
        ((TextView)v.findViewById(R.id.activitySub)).setText(sub);
        ((ImageView)v.findViewById(R.id.activityIcon)).setImageResource(icon);
    }

    private void setupList(int id, String name, String sub, int icon) {
        View v = findViewById(id);
        ((TextView)v.findViewById(R.id.itemTitle)).setText(name);
        ((TextView)v.findViewById(R.id.itemSubtitle)).setText(sub);
        ((ImageView)v.findViewById(R.id.listIcon)).setImageResource(icon);
    }
}