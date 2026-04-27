package com.example.thryve;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

/**
 * Foreground service that collects GPS coordinates while a run is active.
 * Broadcasts each new location to RunActivity via a local broadcast.
 */
public class LocationTrackingService extends Service {

    public static final String ACTION_LOCATION_UPDATE = "com.example.thryve.LOCATION_UPDATE";
    public static final String EXTRA_LAT = "lat";
    public static final String EXTRA_LNG = "lng";
    public static final String EXTRA_ACC = "acc";

    private static final String CHANNEL_ID   = "thryve_run_channel";
    private static final int    NOTIF_ID     = 101;
    private static final long   INTERVAL_MS  = 3000L;  // update every 3 seconds
    private static final float  MIN_DISTANCE = 5f;     // metres

    private FusedLocationProviderClient fusedClient;
    private LocationCallback locationCallback;

    @Override
    public void onCreate() {
        super.onCreate();
        fusedClient = LocationServices.getFusedLocationProviderClient(this);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult result) {
                for (Location loc : result.getLocations()) {
                    broadcastLocation(loc);
                }
            }
        };
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(NOTIF_ID, buildNotification());
        requestLocationUpdates();
        return START_STICKY;   // restart if killed by system
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) { return null; }

    @Override
    public void onDestroy() {
        super.onDestroy();
        fusedClient.removeLocationUpdates(locationCallback);
    }

    // ── Internal ─────────────────────────────────────────────────────────────

    @SuppressWarnings("MissingPermission")
    private void requestLocationUpdates() {
        LocationRequest request = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, INTERVAL_MS)
                .setMinUpdateDistanceMeters(MIN_DISTANCE)
                .build();
        try {
            fusedClient.requestLocationUpdates(request, locationCallback, Looper.getMainLooper());
        } catch (SecurityException e) {
            stopSelf();
        }
    }

    private void broadcastLocation(Location loc) {
        Intent broadcast = new Intent(ACTION_LOCATION_UPDATE);
        broadcast.putExtra(EXTRA_LAT, loc.getLatitude());
        broadcast.putExtra(EXTRA_LNG, loc.getLongitude());
        broadcast.putExtra(EXTRA_ACC, loc.getAccuracy());
        sendBroadcast(broadcast);
    }

    private Notification buildNotification() {
        // Create notification channel (Android 8+)
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel ch = new NotificationChannel(
                    CHANNEL_ID, "Run Tracking", NotificationManager.IMPORTANCE_LOW);
            ch.setDescription("Shows while Thryve is tracking your run");
            nm.createNotificationChannel(ch);
        }

        PendingIntent pi = PendingIntent.getActivity(
                this, 0,
                new Intent(this, RunActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Thryve is tracking your run")
                .setContentText("Tap to return to your run")
                .setSmallIcon(R.drawable.ic_run)
                .setContentIntent(pi)
                .setOngoing(true)           // can't be swiped away
                .setSilent(true)
                .build();
    }
}
