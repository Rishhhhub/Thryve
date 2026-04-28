package com.example.thryve;

import android.Manifest;
import android.content.*;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.*;
import android.util.Log;
import android.widget.*;

import com.google.android.gms.maps.GoogleMap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.*;

public class RunActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int PERMISSION_REQUEST_CODE = 1001;
    private static final int CALORIES_PER_KM = 65;

    private boolean isRunning = false;
    private long startTimeMs = 0L;
    private long elapsedMs = 0L;
    private double totalDistanceKm = 0.0;
    private Location lastLocation = null;

    private final List<LatLng> routePoints = new ArrayList<>();

    private GoogleMap googleMap;

    private TextView tvDuration, tvDistance, tvPace, tvCalories, tvRunStatus;
    private Button btnStartStop;

    private Handler timerHandler;
    private Runnable timerRunnable;

    private BroadcastReceiver locationReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        setContentView(R.layout.activity_run);

        tvDuration = findViewById(R.id.tvDuration);
        tvDistance = findViewById(R.id.tvDistance);
        tvPace = findViewById(R.id.tvPace);
        tvCalories = findViewById(R.id.tvCalories);
        tvRunStatus = findViewById(R.id.tvRunStatus);
        btnStartStop = findViewById(R.id.btnStartStop);

        SupportMapFragment mapFrag =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFrag != null) mapFrag.getMapAsync(this);

        timerHandler = new Handler(Looper.getMainLooper());
        timerRunnable = () -> {
            if (isRunning) {
                elapsedMs = System.currentTimeMillis() - startTimeMs;
                tvDuration.setText(formatDuration(elapsedMs));
                timerHandler.postDelayed(timerRunnable, 1000);
            }
        };

        locationReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                double lat = intent.getDoubleExtra(LocationTrackingService.EXTRA_LAT, 0);
                double lng = intent.getDoubleExtra(LocationTrackingService.EXTRA_LNG, 0);
                float acc = intent.getFloatExtra(LocationTrackingService.EXTRA_ACC, 100);

                if (acc <= 50) {
                    onNewLocation(lat, lng);
                }
            }
        };

        btnStartStop.setOnClickListener(v -> {
            if (!isRunning) startRun();
            else stopRun();
        });

        findViewById(R.id.btnBack).setOnClickListener(v -> {
            if (isRunning) {
                Toast.makeText(this, "Stop the run first", Toast.LENGTH_SHORT).show();
            } else finish();
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;
        googleMap.getUiSettings().setZoomControlsEnabled(false);

        if (hasLocationPermission()) {
            try {
                googleMap.setMyLocationEnabled(true);
            } catch (SecurityException ignored) {}
        }
    }

    @SuppressWarnings("UnspecifiedRegisterReceiverFlag")
    private void startRun() {
        if (!hasLocationPermission()) {
            requestLocationPermission();
            return;
        }

        isRunning = true;
        startTimeMs = System.currentTimeMillis();
        elapsedMs = 0;
        totalDistanceKm = 0;
        routePoints.clear();
        lastLocation = null;

        btnStartStop.setText("STOP RUN");

        startService(new Intent(this, LocationTrackingService.class));

        IntentFilter filter = new IntentFilter(LocationTrackingService.ACTION_LOCATION_UPDATE);

        if (Build.VERSION.SDK_INT >= 33) {
            registerReceiver(locationReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(locationReceiver, filter);
        }

        timerHandler.post(timerRunnable);
    }

    private void stopRun() {

        isRunning = false;
        timerHandler.removeCallbacks(timerRunnable);

        stopService(new Intent(this, LocationTrackingService.class));

        try { unregisterReceiver(locationReceiver); } catch (Exception ignored) {}

        if (routePoints.isEmpty()) {
            Toast.makeText(this, "No GPS data", Toast.LENGTH_SHORT).show();
            return;
        }

        // 🔥 CONVERT ROUTE → LIST
        List<Double> lats = new ArrayList<>();
        List<Double> lngs = new ArrayList<>();

        for (LatLng p : routePoints) {
            lats.add(p.latitude);
            lngs.add(p.longitude);
        }

        // 🔥 SAVE WITH ROUTE
        saveRunToFirebase(totalDistanceKm, elapsedMs, lats, lngs);

        // 🔥 OPEN SUMMARY
        Intent summary = new Intent(this, RunSummaryActivity.class);
        summary.putExtra("duration_ms", elapsedMs);
        summary.putExtra("distance_km", totalDistanceKm);
        summary.putExtra("calories", (int)(totalDistanceKm * CALORIES_PER_KM));
        summary.putExtra("lats", (ArrayList<Double>) lats);
        summary.putExtra("lngs", (ArrayList<Double>) lngs);

        startActivity(summary);
    }

    private void saveRunToFirebase(double distance, long duration,
                                   List<Double> lats, List<Double> lngs) {

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (auth.getCurrentUser() == null) {
            auth.signInAnonymously()
                    .addOnSuccessListener(r ->
                            saveRunWithUser(db, r.getUser().getUid(), distance, duration, lats, lngs));
            return;
        }

        saveRunWithUser(db, auth.getCurrentUser().getUid(), distance, duration, lats, lngs);
    }

    private void saveRunWithUser(FirebaseFirestore db, String userId,
                                 double distance, long duration,
                                 List<Double> lats, List<Double> lngs) {

        Map<String, Object> run = new HashMap<>();
        run.put("distance", distance);
        run.put("duration_sec", duration / 1000);
        run.put("timestamp", Timestamp.now());
        run.put("lats", lats);
        run.put("lngs", lngs);

        db.collection("users")
                .document(userId)
                .collection("runs")
                .add(run)
                .addOnSuccessListener(d -> Log.d("RUN_SAVE", "Saved"))
                .addOnFailureListener(e -> Log.e("RUN_SAVE", "Error", e));
    }

    private void onNewLocation(double lat, double lng) {

        LatLng newPoint = new LatLng(lat, lng);

        if (lastLocation != null) {
            float[] res = new float[1];
            Location.distanceBetween(lastLocation.getLatitude(), lastLocation.getLongitude(),
                    lat, lng, res);
            totalDistanceKm += res[0] / 1000.0;
        }

        routePoints.add(newPoint);

        tvDistance.setText(String.format(Locale.US, "%.2f km", totalDistanceKm));

        if (googleMap != null) {
            googleMap.addPolyline(new PolylineOptions()
                    .addAll(routePoints)
                    .color(Color.GREEN)
                    .width(8f));

            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(newPoint, 17f));
        }

        Location loc = new Location("gps");
        loc.setLatitude(lat);
        loc.setLongitude(lng);
        lastLocation = loc;
    }

    private boolean hasLocationPermission() {
        return ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int req, @NonNull String[] perms,
                                           @NonNull int[] res) {
        super.onRequestPermissionsResult(req, perms, res);
        if (req == PERMISSION_REQUEST_CODE && res.length > 0 &&
                res[0] == PackageManager.PERMISSION_GRANTED) {
            startRun();
        }
    }

    private String formatDuration(long ms) {
        long s = ms / 1000;
        return String.format(Locale.US, "%02d:%02d:%02d", s/3600, (s%3600)/60, s%60);
    }
}