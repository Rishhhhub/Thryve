package com.saarthak.thryve;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class SelectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Page 2 ki layout file ka naam yahan likhna hai
        setContentView(R.layout.activity_selection);

        // Grid Items Setup (Matching 2nd Page SS)
        updateCard(R.id.cardRunning, "Running", "Outdoor / Indoor", R.drawable.ic_running);
        updateCard(R.id.cardWalking, "Walking", "Pace tracking", R.drawable.ic_walking);
        updateCard(R.id.cardCycling, "Cycling", "GPS route", R.drawable.ic_cycling);
        updateCard(R.id.cardYoga, "Yoga", "Mindfulness", R.drawable.ic_yoga);
        updateCard(R.id.cardSwimming, "Swimming", "Lap counter", R.drawable.ic_swimming);
        updateCard(R.id.cardHiking, "Hiking", "Elevation tracking", R.drawable.ic_walking);
    }

    private void updateCard(int id, String title, String sub, int iconRes) {
        View card = findViewById(id);
        if (card != null) {
            ((TextView) card.findViewById(R.id.activityTitle)).setText(title);
            ((TextView) card.findViewById(R.id.activitySub)).setText(sub);
            ((ImageView) card.findViewById(R.id.activityIcon)).setImageResource(iconRes);
        }
    }
}