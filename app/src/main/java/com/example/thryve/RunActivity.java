package com.example.thryve;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.*;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.*;
import android.view.View;
import android.widget.*;

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
import androidx.core.util.Consumer;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

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
    private Polyline currentPolyline;

    private TextView tvDuration, tvDistance, tvPace, tvCalories;
    private Button btnStartStop;

    private Handler timerHandler;
    private Runnable timerRunnable;

    private BroadcastReceiver locationReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run);

        tvDuration = findViewById(R.id.tvDuration);
        tvDistance = findViewById(R.id.tvDistance);
        tvPace = findViewById(R.id.tvPace);
        tvCalories = findViewById(R.id.tvCalories);
        btnStartStop = findViewById(R.id.btnStartStop);

        SupportMapFragment mapFrag =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFrag != null) mapFrag.getMapAsync(this);

        timerHandler = new Handler(Looper.getMainLooper());
        timerRunnable = () -> {
            if (isRunning) {
                elapsedMs = System.currentTimeMillis() - startTimeMs;
                tvDuration.setText(formatDuration(elapsedMs));
                updatePace();
                updateCalories();
                timerHandler.postDelayed(timerRunnable, 1000);
            }
        };

        locationReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                double lat = intent.getDoubleExtra(LocationTrackingService.EXTRA_LAT, 0);
                double lng = intent.getDoubleExtra(LocationTrackingService.EXTRA_LNG, 0);
                float acc = intent.getFloatExtra(LocationTrackingService.EXTRA_ACC, 100);

                if (acc <= 200) {
                    onNewLocation(lat, lng);
                }
            }
        };

        btnStartStop.setOnClickListener(v -> {
            if (!isRunning) startRun();
            else stopRun();
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;

        int currentNightMode = getResources().getConfiguration().uiMode & android.content.res.Configuration.UI_MODE_NIGHT_MASK;
        if (currentNightMode == android.content.res.Configuration.UI_MODE_NIGHT_YES) {
            googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style_dark));
        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            try {
                googleMap.setMyLocationEnabled(true);

                // move my location button to bottom right
                SupportMapFragment mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                if (mapFrag != null && mapFrag.getView() != null) {
                    View mapView = mapFrag.getView();
                    View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
                    if (locationButton != null) {
                        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
                        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
                        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
                        rlp.setMargins(0, 0, 30, 30);
                    }
                }
                
                // AUTO ZOOM ON OPEN
                FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
                fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                    if (location != null) {
                        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 17f));
                    }
                });
                
            } catch (SecurityException ignored) {}
        }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private void startRun() {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST_CODE);
            return;
        }

        isRunning = true;
        startTimeMs = System.currentTimeMillis();
        elapsedMs = 0;
        totalDistanceKm = 0;
        routePoints.clear();
        if (currentPolyline != null) {
            currentPolyline.remove();
            currentPolyline = null;
        }
        lastLocation = null;

        btnStartStop.setText("STOP RUN");

        startService(new Intent(this, LocationTrackingService.class));

        IntentFilter filter = new IntentFilter(LocationTrackingService.ACTION_LOCATION_UPDATE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
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

        try {
            unregisterReceiver(locationReceiver);
        } catch (Exception ignored) {}

        if (routePoints.isEmpty()) {
            Toast.makeText(this, "No GPS data", Toast.LENGTH_SHORT).show();
            return;
        }

        List<Double> lats = new ArrayList<>();
        List<Double> lngs = new ArrayList<>();

        for (LatLng p : routePoints) {
            lats.add(p.latitude);
            lngs.add(p.longitude);
        }

        saveRunToFirebase(totalDistanceKm, elapsedMs, lats, lngs, runId -> {
            Intent summary = new Intent(this, RunSummaryActivity.class);
            summary.putExtra("duration_ms", elapsedMs);
            summary.putExtra("distance_km", totalDistanceKm);
            summary.putExtra("calories", (int)(totalDistanceKm * CALORIES_PER_KM));
            summary.putExtra("lats", (ArrayList<Double>) lats);
            summary.putExtra("lngs", (ArrayList<Double>) lngs);
            if (runId != null) {
                summary.putExtra("run_id", runId);
            }

            startActivity(summary);
        });
    }

    private void onNewLocation(double lat, double lng) {

        LatLng newPoint = new LatLng(lat, lng);

        if (lastLocation != null) {
            float[] res = new float[1];
            Location.distanceBetween(
                    lastLocation.getLatitude(),
                    lastLocation.getLongitude(),
                    lat, lng, res
            );

            if (res[0] > 0 && res[0] <= 100) {
                totalDistanceKm += res[0] / 1000.0;
            }
        }

        routePoints.add(newPoint);

        tvDistance.setText(String.format(Locale.US, "%.2f km", totalDistanceKm));

        if (googleMap != null) {
            if (currentPolyline == null) {
                currentPolyline = googleMap.addPolyline(new PolylineOptions()
                        .color(Color.GREEN)
                        .width(8f));
            }
            currentPolyline.setPoints(routePoints);

            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(newPoint, 17f));
        }

        Location loc = new Location("gps");
        loc.setLatitude(lat);
        loc.setLongitude(lng);
        lastLocation = loc;
    }

    private void saveRunToFirebase(double distance, long duration,
                                   List<Double> lats, List<Double> lngs,
                                   Consumer<String> onSuccess) {

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (auth.getCurrentUser() == null) {
            auth.signInAnonymously().addOnSuccessListener(r ->
                    saveRunWithUser(db, r.getUser().getUid(), distance, duration, lats, lngs, onSuccess));
            return;
        }

        saveRunWithUser(db, auth.getCurrentUser().getUid(), distance, duration, lats, lngs, onSuccess);
    }

    private void saveRunWithUser(FirebaseFirestore db, String userId,
                                 double distance, long duration,
                                 List<Double> lats, List<Double> lngs,
                                 Consumer<String> onSuccess) {

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
                .addOnSuccessListener(d -> {
                    if (onSuccess != null) onSuccess.accept(d.getId());
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to save run", Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startRun();
        }
    }

    private void updatePace() {
        if (totalDistanceKm > 0) {
            double paceSec = (elapsedMs / 1000.0) / totalDistanceKm;
            int min = (int) paceSec / 60;
            int sec = (int) paceSec % 60;
            tvPace.setText(min + ":" + String.format(Locale.US, "%02d", sec) + " /km");
        }
    }

    private void updateCalories() {
        int calories = (int) (totalDistanceKm * CALORIES_PER_KM);
        tvCalories.setText(calories + " kcal");
    }

    private String formatDuration(long ms) {
        long s = ms / 1000;
        return String.format(Locale.US, "%02d:%02d:%02d",
                s/3600, (s%3600)/60, s%60);
    }
}