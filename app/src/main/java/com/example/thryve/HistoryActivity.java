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

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RunAdapter adapter;
    private List<RunModel> runList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        recyclerView = findViewById(R.id.recyclerView);

        if (recyclerView == null) {
            Toast.makeText(this, "RecyclerView not found", Toast.LENGTH_LONG).show();
            return;
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        runList = new ArrayList<>();

        // 🔥 MATCHES RunAdapter constructor
        adapter = new RunAdapter(runList, run -> {
            Intent intent = new Intent(HistoryActivity.this, RunSummaryActivity.class);
            intent.putExtra("distance_km", run.distance);
            intent.putExtra("duration_ms", run.duration_sec * 1000);
            if (run.lats != null && run.lngs != null) {
                intent.putExtra("lats", new ArrayList<>(run.lats));
                intent.putExtra("lngs", new ArrayList<>(run.lngs));
            }
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);

        loadRuns();

        // 🔙 Optional back button (only if exists in XML)
        if (findViewById(R.id.btnBack) != null) {
            findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        }
    }

    private void loadRuns() {

        FirebaseAuth auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() == null) {
            Log.e("HISTORY", "User is NULL");
            Toast.makeText(this, "User not ready", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = auth.getCurrentUser().getUid();

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .collection("runs")
                .orderBy("timestamp")
                .get()
                .addOnSuccessListener(query -> {

                    runList.clear();

                    for (var doc : query.getDocuments()) {
                        RunModel run = doc.toObject(RunModel.class);
                        if (run != null) {
                            runList.add(run);
                        }
                    }

                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.e("HISTORY", "Error loading runs", e);
                    Toast.makeText(this, "Failed to load runs", Toast.LENGTH_SHORT).show();
                });
    }
}