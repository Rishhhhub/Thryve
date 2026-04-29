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
import com.google.android.gms.maps.model.MapStyleOptions;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Locale;

public class RunSummaryActivity extends AppCompatActivity implements OnMapReadyCallback {

    private long durationMs;
    private double distanceKm;
    private int calories;

    private ArrayList<Double> lats;
    private ArrayList<Double> lngs;

    private GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) getSupportActionBar().hide();
        setContentView(R.layout.activity_run_summary);

        // get intent data safely
        Intent in = getIntent();

        durationMs = in.getLongExtra("duration_ms", 0);
        distanceKm = in.getDoubleExtra("distance_km", 0);
        calories   = in.getIntExtra("calories", 0);

        lats = (ArrayList<Double>) getIntent().getSerializableExtra("lats");
        lngs = (ArrayList<Double>) getIntent().getSerializableExtra("lngs");
        String runId = getIntent().getStringExtra("run_id");

        populateStats();

        // initialize map
        SupportMapFragment mapFrag =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.summaryMap);

        if (mapFrag != null) {
            mapFrag.getMapAsync(this);
        }

        // 🔙 BACK
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // 💾 SAVE
        ((Button) findViewById(R.id.btnSave)).setOnClickListener(v -> {
            Toast.makeText(this, "Activity saved!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, DashboardActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        });

        // ❌ DISCARD
        ((Button) findViewById(R.id.btnDiscard)).setOnClickListener(v -> {
            if (runId != null) {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                if (auth.getCurrentUser() != null) {
                    FirebaseFirestore.getInstance().collection("users")
                            .document(auth.getCurrentUser().getUid())
                            .collection("runs")
                            .document(runId)
                            .delete()
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(this, "Run discarded", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(this, DashboardActivity.class)
                                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                            });
                    return;
                }
            }
            startActivity(new Intent(this, DashboardActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        });
    }

    private void populateStats() {

        // 📏 Distance
        ((TextView) findViewById(R.id.tvSumDistance))
                .setText(String.format(Locale.US, "%.2f km", distanceKm));

        // ⏱ Duration
        long s = durationMs / 1000;
        ((TextView) findViewById(R.id.tvSumDuration))
                .setText(String.format(Locale.US, "%02d:%02d:%02d",
                        s / 3600, (s % 3600) / 60, s % 60));

        // ⚡ Pace
        if (distanceKm > 0) {
            double paceSecPerKm = (durationMs / 1000.0) / distanceKm;
            int pm = (int) paceSecPerKm / 60;
            int ps = (int) paceSecPerKm % 60;

            ((TextView) findViewById(R.id.tvSumPace))
                    .setText(String.format(Locale.US, "%d:%02d /km", pm, ps));
        }

        // Calories
        ((TextView) findViewById(R.id.tvSumCalories))
                .setText(calories + " kcal");
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {

        googleMap = map;

        int currentNightMode = getResources().getConfiguration().uiMode & android.content.res.Configuration.UI_MODE_NIGHT_MASK;
        if (currentNightMode == android.content.res.Configuration.UI_MODE_NIGHT_YES) {
            googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style_dark));
        }

        googleMap.getUiSettings().setScrollGesturesEnabled(false);
        googleMap.getUiSettings().setZoomGesturesEnabled(false);

        // 🚨 SAFETY CHECK
        if (lats == null || lngs == null || lats.isEmpty() || lngs.isEmpty()) {
            return;
        }

        ArrayList<LatLng> points = new ArrayList<>();
        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();

        for (int i = 0; i < lats.size(); i++) {
            LatLng p = new LatLng(lats.get(i), lngs.get(i));
            points.add(p);
            boundsBuilder.include(p);
        }

        // 🟢 DRAW ROUTE
        googleMap.addPolyline(new PolylineOptions()
                .addAll(points)
                .color(Color.parseColor("#4CAF50"))
                .width(8f));

        // 🟢 START
        googleMap.addMarker(new MarkerOptions()
                .position(points.get(0))
                .title("Start")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

        // 🔴 END
        googleMap.addMarker(new MarkerOptions()
                .position(points.get(points.size() - 1))
                .title("Finish")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

        // 🎯 CAMERA FIT
        try {
            LatLngBounds bounds = boundsBuilder.build();
            googleMap.setOnMapLoadedCallback(() -> {
                googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 80));
            });
        } catch (Exception e) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(points.get(0), 15f));
        }
    }
}