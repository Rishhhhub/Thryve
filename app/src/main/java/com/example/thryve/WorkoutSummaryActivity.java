package com.example.thryve;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.views.MapView;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;

public class WorkoutSummaryActivity extends AppCompatActivity {
    private MapView map = null;
    private MyLocationNewOverlay mLocationOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. OSMdroid Config
        Configuration.getInstance().setUserAgentValue(getPackageName());

        setContentView(R.layout.activity_workout_summary);

        // --- MAP SETUP ---
        map = findViewById(R.id.mapview);
        if (map != null) {
            map.setTileSource(TileSourceFactory.MAPNIK);
            map.setMultiTouchControls(true);
            this.mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this), map);
            this.mLocationOverlay.enableMyLocation();
            map.getOverlays().add(this.mLocationOverlay);
            map.getController().setZoom(15.0);
            GeoPoint startPoint = new GeoPoint(28.6139, 77.2090);
            map.getController().setCenter(startPoint);
        }

        // --- NAVIGATION LOGIC (Important Fix) ---

        // Journal Button (Calendar Icon)
        ImageView btnJournal = findViewById(R.id.btnNavJournal);
        if (btnJournal != null) {
            btnJournal.setOnClickListener(v -> {
                // Journal Activity kholne ke liye
                Intent intent = new Intent(WorkoutSummaryActivity.this, DailyJournalActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            });
        }

        // Home/Grid Button
        ImageView btnGrid = findViewById(R.id.btnNavGrid);
        if (btnGrid != null) {
            btnGrid.setOnClickListener(v -> {
                Toast.makeText(this, "Dashboard opening...", Toast.LENGTH_SHORT).show();
            });
        }

        // Settings Icon (Wrench/Manage)
        ImageView btnSettings = findViewById(R.id.btnGoSettings);
        if (btnSettings != null) {
            btnSettings.setOnClickListener(v -> {
                startActivity(new Intent(WorkoutSummaryActivity.this, SettingsDeviceActivity.class));
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (map != null) map.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (map != null) map.onPause();
    }
}