package com.example.thryve;

import android.os.Bundle;
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

        // OSMdroid config (Zaruri hai load hone ke liye)
        Configuration.getInstance().load(this, getPreferences(MODE_PRIVATE));

        setContentView(R.layout.activity_workout_summary);

        map = findViewById(R.id.mapview);
        map.setTileSource(TileSourceFactory.MAPNIK); // Standard Map Look
        map.setMultiTouchControls(true); // Zoom enable karne ke liye

        // Current Location (Blue Dot) setup
        this.mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this), map);
        this.mLocationOverlay.enableMyLocation();
        map.getOverlays().add(this.mLocationOverlay);

        // Map ko default location pe zoom karna (e.g., Delhi)
        map.getController().setZoom(15.0);
        GeoPoint startPoint = new GeoPoint(28.6139, 77.2090);
        map.getController().setCenter(startPoint);
    }

    @Override
    public void onResume() {
        super.onResume();
        map.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        map.onPause();
    }
}
