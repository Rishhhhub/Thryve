package com.example.thryve;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RunAdapter adapter;
    private List<RunModel> runList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        runList = new ArrayList<>();

        adapter = new RunAdapter(runList, run -> {
            Intent intent = new Intent(this, RunSummaryActivity.class);
            intent.putExtra("distance_km", run.distance);
            intent.putExtra("duration_ms", run.duration_sec * 1000);

            if (run.lats != null && run.lngs != null) {
                intent.putExtra("lats", new ArrayList<>(run.lats));
                intent.putExtra("lngs", new ArrayList<>(run.lngs));
            }
            if (run.runId != null) {
                intent.putExtra("run_id", run.runId);
            }

            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);

        loadRuns();
    }

    private void loadRuns() {

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(this, "User not ready", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .collection("runs")
                .orderBy("timestamp", Query.Direction.DESCENDING) // ✅ FIXED ORDER
                .get()
                .addOnSuccessListener(query -> {

                    runList.clear();

                    for (var doc : query.getDocuments()) {

                        Map<String, Object> data = doc.getData();
                        if (data == null) continue;

                        RunModel run = new RunModel();
                        run.runId = doc.getId();

                        if (data.get("distance") != null)
                            run.distance = ((Number) data.get("distance")).doubleValue();

                        if (data.get("duration_sec") != null)
                            run.duration_sec = ((Number) data.get("duration_sec")).longValue();

                        if (data.get("timestamp") != null)
                            run.timestamp = (com.google.firebase.Timestamp) data.get("timestamp");

                        // ✅ CRITICAL FIX: manual parsing
                        List<?> rawLats = (List<?>) data.get("lats");
                        List<?> rawLngs = (List<?>) data.get("lngs");

                        if (rawLats != null && rawLngs != null) {

                            List<Double> lats = new ArrayList<>();
                            List<Double> lngs = new ArrayList<>();

                            for (Object o : rawLats)
                                lats.add(((Number) o).doubleValue());

                            for (Object o : rawLngs)
                                lngs.add(((Number) o).doubleValue());

                            run.lats = lats;
                            run.lngs = lngs;
                        }

                        Log.d("HISTORY_DEBUG", "Loaded run: " + run.lats);

                        runList.add(run);
                    }

                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to load runs", Toast.LENGTH_SHORT).show());
    }
}