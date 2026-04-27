package com.example.thryve;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.MapStyleOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RunActivity extends AppCompatActivity implements OnMapReadyCallback {

    // ── Constants ──
    private static final int PERMISSION_REQUEST_CODE = 1001;
    private static final int CALORIES_PER_KM = 65; // rough estimate

    // ── State ──
    private boolean isRunning = false;
    private long startTimeMs = 0L;
    private long elapsedMs   = 0L;
    private double totalDistanceKm = 0.0;
    private Location lastLocation = null;
    private final List<LatLng> routePoints = new ArrayList<>();

    // ── UI ──
    private GoogleMap googleMap;
    private TextView tvDuration, tvDistance, tvPace, tvCalories, tvRunStatus;
    private Button btnStartStop;

    // ── Timer ──
    private Handler timerHandler;
    private Runnable timerRunnable;

    // ── Location Receiver ──
    private BroadcastReceiver locationReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Initialize Maps SDK explicitly
        com.google.android.gms.maps.MapsInitializer.initialize(this, com.google.android.gms.maps.MapsInitializer.Renderer.LATEST, null);

        if (getSupportActionBar() != null) getSupportActionBar().hide();
        setContentView(R.layout.activity_run);

        tvDuration  = findViewById(R.id.tvDuration);
        tvDistance  = findViewById(R.id.tvDistance);
        tvPace      = findViewById(R.id.tvPace);
        tvCalories  = findViewById(R.id.tvCalories);
        tvRunStatus = findViewById(R.id.tvRunStatus);
        btnStartStop = findViewById(R.id.btnStartStop);

        // Init map
        SupportMapFragment mapFrag = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFrag != null) mapFrag.getMapAsync(this);

        // Timer setup
        timerHandler = new Handler(Looper.getMainLooper());
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                if (isRunning) {
                    elapsedMs = System.currentTimeMillis() - startTimeMs;
                    tvDuration.setText(formatDuration(elapsedMs));
                    timerHandler.postDelayed(this, 1000);
                }
            }
        };

        // Location broadcast receiver
        locationReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                double lat = intent.getDoubleExtra(LocationTrackingService.EXTRA_LAT, 0);
                double lng = intent.getDoubleExtra(LocationTrackingService.EXTRA_LNG, 0);
                float  acc = intent.getFloatExtra(LocationTrackingService.EXTRA_ACC, 100);
                if (acc <= 30) {   // only use accurate fixes
                    onNewLocation(lat, lng);
                }
            }
        };

        btnStartStop.setOnClickListener(v -> {
            if (!isRunning) startRun(); else stopRun();
        });

        findViewById(R.id.btnBack).setOnClickListener(v -> {
            if (isRunning) {
                Toast.makeText(this, "Stop the run before going back", Toast.LENGTH_SHORT).show();
            } else {
                finish();
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;
        // Dark map style matching app theme
        try {
            googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style_dark));
        } catch (Exception ignored) {}
        googleMap.getUiSettings().setZoomControlsEnabled(false);
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        if (hasLocationPermission()) {
            //noinspection MissingPermission
            googleMap.setMyLocationEnabled(true);
            
            // Immediately zoom to current location
            FusedLocationProviderClient fusedClient = LocationServices.getFusedLocationProviderClient(this);
            fusedClient.getLastLocation().addOnSuccessListener(this, loc -> {
                if (loc != null) {
                    LatLng current = new LatLng(loc.getLatitude(), loc.getLongitude());
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current, 17f));
                }
            });
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    private void startRun() {
        if (!hasLocationPermission()) {
            requestLocationPermission();
            return;
        }
        isRunning    = true;
        startTimeMs  = System.currentTimeMillis();
        elapsedMs    = 0L;
        totalDistanceKm = 0.0;
        routePoints.clear();
        lastLocation = null;

        tvRunStatus.setText("● TRACKING");
        btnStartStop.setText("STOP RUN");
        btnStartStop.setBackgroundResource(R.drawable.bg_btn_red);

        // Start foreground service
        Intent serviceIntent = new Intent(this, LocationTrackingService.class);
        ContextCompat.startForegroundService(this, serviceIntent);

        // Register receiver
        IntentFilter filter = new IntentFilter(LocationTrackingService.ACTION_LOCATION_UPDATE);
        registerReceiver(locationReceiver, filter, RECEIVER_EXPORTED);

        // Start timer
        timerHandler.post(timerRunnable);
    }

    private void stopRun() {
        isRunning = false;
        timerHandler.removeCallbacks(timerRunnable);

        // Stop service
        stopService(new Intent(this, LocationTrackingService.class));

        try { unregisterReceiver(locationReceiver); } catch (Exception ignored) {}

        tvRunStatus.setText("● COMPLETE");
        btnStartStop.setText("START RUN");
        btnStartStop.setBackgroundResource(R.drawable.bg_btn_green);

        // Go to summary
        if (!routePoints.isEmpty()) {
            ArrayList<Double> lats = new ArrayList<>(), lngs = new ArrayList<>();
            for (LatLng p : routePoints) { lats.add(p.latitude); lngs.add(p.longitude); }

            Intent summary = new Intent(this, RunSummaryActivity.class);
            summary.putExtra("duration_ms",  elapsedMs);
            summary.putExtra("distance_km",  totalDistanceKm);
            summary.putExtra("calories",     (int)(totalDistanceKm * CALORIES_PER_KM));
            summary.putExtra("lats",  lats);
            summary.putExtra("lngs",  lngs);
            startActivity(summary);
        } else {
            Toast.makeText(this, "No GPS data recorded", Toast.LENGTH_SHORT).show();
        }
    }

    private void onNewLocation(double lat, double lng) {
        LatLng newPoint = new LatLng(lat, lng);

        if (lastLocation != null) {
            float[] result = new float[1];
            Location.distanceBetween(lastLocation.getLatitude(), lastLocation.getLongitude(),
                    lat, lng, result);
            double segKm = result[0] / 1000.0;
            totalDistanceKm += segKm;
        }

        routePoints.add(newPoint);

        // Update distance label
        tvDistance.setText(String.format(Locale.US, "%.2f", totalDistanceKm));

        // Pace (min/km)
        if (totalDistanceKm > 0.05 && elapsedMs > 0) {
            double paceSecPerKm = (elapsedMs / 1000.0) / totalDistanceKm;
            int paceMin = (int) paceSecPerKm / 60;
            int paceSec = (int) paceSecPerKm % 60;
            tvPace.setText(String.format(Locale.US, "%d:%02d", paceMin, paceSec));
        }

        // Calories
        tvCalories.setText(String.valueOf((int)(totalDistanceKm * CALORIES_PER_KM)));

        // Draw polyline on map
        if (googleMap != null && routePoints.size() >= 2) {
            googleMap.addPolyline(new PolylineOptions()
                    .addAll(routePoints)
                    .color(Color.parseColor("#4CAF50"))
                    .width(8f));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(newPoint, 17f));
        }

        // Store last location
        Location loc = new Location("gps");
        loc.setLatitude(lat); loc.setLongitude(lng);
        lastLocation = loc;
    }

    // ── Permissions ──────────────────────────────────────────────────────────
    private boolean hasLocationPermission() {
        return ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                }, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int req, @NonNull String[] perms,
                                           @NonNull int[] results) {
        super.onRequestPermissionsResult(req, perms, results);
        if (req == PERMISSION_REQUEST_CODE &&
                results.length > 0 &&
                results[0] == PackageManager.PERMISSION_GRANTED) {
            startRun();
        } else {
            Toast.makeText(this, "Location permission required to track run",
                    Toast.LENGTH_LONG).show();
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private String formatDuration(long ms) {
        long s = ms / 1000;
        return String.format(Locale.US, "%02d:%02d:%02d", s/3600, (s%3600)/60, s%60);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isRunning) {
            timerHandler.removeCallbacks(timerRunnable);
            stopService(new Intent(this, LocationTrackingService.class));
            try { unregisterReceiver(locationReceiver); } catch (Exception ignored) {}
        }
    }
}
