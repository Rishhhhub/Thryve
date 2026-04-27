package com.example.thryve;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Locale;

public class RunSummaryActivity extends AppCompatActivity implements OnMapReadyCallback {

    private long durationMs;
    private double distanceKm;
    private int calories;
    private ArrayList<Double> lats, lngs;
    private GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Initialize Maps SDK explicitly
        com.google.android.gms.maps.MapsInitializer.initialize(this, com.google.android.gms.maps.MapsInitializer.Renderer.LATEST, null);

        if (getSupportActionBar() != null) getSupportActionBar().hide();
        setContentView(R.layout.activity_run_summary);

        // Retrieve data from RunActivity
        Intent in = getIntent();
        durationMs  = in.getLongExtra("duration_ms", 0);
        distanceKm  = in.getDoubleExtra("distance_km", 0);
        calories    = in.getIntExtra("calories", 0);
        lats        = (ArrayList<Double>) in.getSerializableExtra("lats");
        lngs        = (ArrayList<Double>) in.getSerializableExtra("lngs");

        populateStats();

        // Init summary map
        SupportMapFragment mapFrag = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.summaryMap);
        if (mapFrag != null) mapFrag.getMapAsync(this);

        findViewById(R.id.btnBack).setOnClickListener(v ->
                startActivity(new Intent(this, DashboardActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));

        ((Button) findViewById(R.id.btnSave)).setOnClickListener(v -> {
            Toast.makeText(this, "Activity saved!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, DashboardActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        });

        ((Button) findViewById(R.id.btnDiscard)).setOnClickListener(v ->
                startActivity(new Intent(this, DashboardActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));
    }

    private void populateStats() {
        // Distance
        ((TextView) findViewById(R.id.tvSumDistance))
                .setText(String.format(Locale.US, "%.2f km", distanceKm));

        // Duration
        long s = durationMs / 1000;
        ((TextView) findViewById(R.id.tvSumDuration))
                .setText(String.format(Locale.US, "%02d:%02d:%02d", s/3600, (s%3600)/60, s%60));

        // Pace
        if (distanceKm > 0) {
            double paceSecPerKm = (durationMs / 1000.0) / distanceKm;
            int pm = (int) paceSecPerKm / 60, ps = (int) paceSecPerKm % 60;
            ((TextView) findViewById(R.id.tvSumPace))
                    .setText(String.format(Locale.US, "%d:%02d /km", pm, ps));
        }

        // Calories
        ((TextView) findViewById(R.id.tvSumCalories)).setText(calories + " kcal");
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;
        googleMap.getUiSettings().setScrollGesturesEnabled(false);
        googleMap.getUiSettings().setZoomGesturesEnabled(false);

        if (lats == null || lats.isEmpty()) return;

        ArrayList<LatLng> points = new ArrayList<>();
        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();

        for (int i = 0; i < lats.size(); i++) {
            LatLng p = new LatLng(lats.get(i), lngs.get(i));
            points.add(p);
            boundsBuilder.include(p);
        }

        // Draw route
        googleMap.addPolyline(new PolylineOptions()
                .addAll(points)
                .color(Color.parseColor("#4CAF50"))
                .width(8f));

        // Start marker (green)
        googleMap.addMarker(new MarkerOptions()
                .position(points.get(0))
                .title("Start")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

        // End marker (red)
        googleMap.addMarker(new MarkerOptions()
                .position(points.get(points.size() - 1))
                .title("Finish")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

        // Fit map to route
        try {
            LatLngBounds bounds = boundsBuilder.build();
            googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 80));
        } catch (Exception e) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(points.get(0), 15f));
        }
    }
}
