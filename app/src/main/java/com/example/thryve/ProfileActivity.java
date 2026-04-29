package com.example.thryve;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.EditText;
import android.widget.ImageView;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import java.io.File;
import java.util.Locale;

public class ProfileActivity extends AppCompatActivity {
    private EditText etName, etAge, etWeight, etHeight;
    private TextView tvBMI;
    private SharedPreferences prefs;
    private ImageView imgEditAvatar;
    private Uri imageUri;
    private String currentImagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        setContentView(R.layout.activity_profile);

        prefs = getSharedPreferences("thryve_prefs", MODE_PRIVATE);
        etName = findViewById(R.id.etName);
        etAge = findViewById(R.id.etAge);
        etWeight = findViewById(R.id.etWeight);
        imgEditAvatar = findViewById(R.id.imgEditAvatar);
        etHeight = findViewById(R.id.etHeight);
        tvBMI = findViewById(R.id.tvBMI);

        // Load existing data
        etName.setText(prefs.getString("user_name", "Athlete"));
        etAge.setText(String.valueOf(prefs.getInt("user_age", 25)));
        etWeight.setText(String.valueOf(prefs.getInt("user_weight", 70)));
        etHeight.setText(String.valueOf(prefs.getInt("user_height", 175)));

        updateBMI();

        TextWatcher bmiWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                updateBMI();
            }
        };

        etWeight.addTextChangedListener(bmiWatcher);
        etHeight.addTextChangedListener(bmiWatcher);

        currentImagePath = prefs.getString("profile_path", null);
        if (currentImagePath != null && imgEditAvatar != null) {
            try {
                imgEditAvatar.setImageURI(Uri.parse(currentImagePath));
                imgEditAvatar.setColorFilter(null);
                imgEditAvatar.setPadding(0,0,0,0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        imgEditAvatar.setOnClickListener(v -> showImageSourceDialog());

        findViewById(R.id.btnBackProfile).setOnClickListener(v -> finish());
        findViewById(R.id.btnSaveProfile).setOnClickListener(v -> saveProfile());
    }

    private void showImageSourceDialog() {
        String[] options = {"Camera", "Gallery"};
        new AlertDialog.Builder(this).setTitle("Change Photo").setItems(options, (dialog, which) -> {
            if (which == 0) checkCameraPermission(); else checkGalleryPermission();
        }).show();
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) openCamera();
        else requestCameraLauncher.launch(Manifest.permission.CAMERA);
    }

    private void checkGalleryPermission() {
        String perm = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) ? Manifest.permission.READ_MEDIA_IMAGES : Manifest.permission.READ_EXTERNAL_STORAGE;
        if (ContextCompat.checkSelfPermission(this, perm) == PackageManager.PERMISSION_GRANTED) openGallery();
        else requestGalleryLauncher.launch(perm);
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            if (!storageDir.exists()) storageDir.mkdirs();
            File photoFile = File.createTempFile("PROFILE_", ".jpg", storageDir);
            imageUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", photoFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            cameraLauncher.launch(intent);
        } catch (Exception ex) {
            ex.printStackTrace();
            Toast.makeText(this, "Camera error: " + ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        galleryLauncher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == RESULT_OK) {
            imgEditAvatar.setImageURI(imageUri);
            imgEditAvatar.setColorFilter(null);
            currentImagePath = imageUri.toString();
        }
    });

    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
            Uri selectedUri = result.getData().getData();
            getContentResolver().takePersistableUriPermission(selectedUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            imgEditAvatar.setImageURI(selectedUri);
            imgEditAvatar.setColorFilter(null);
            currentImagePath = selectedUri.toString();
        }
    });

    private final ActivityResultLauncher<String> requestCameraLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), is -> { if(is) openCamera(); });
    private final ActivityResultLauncher<String> requestGalleryLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), is -> { if(is) openGallery(); });

    private void saveProfile() {
        try {
            String newName = etName.getText().toString();
            int newAge = Integer.parseInt(etAge.getText().toString());
            int newWeight = Integer.parseInt(etWeight.getText().toString());
            int newHeight = Integer.parseInt(etHeight.getText().toString());

            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("user_name", newName);
            editor.putInt("user_age", newAge);
            editor.putInt("user_weight", newWeight);
            editor.putInt("user_height", newHeight);
            if (currentImagePath != null) {
                editor.putString("profile_path", currentImagePath);
            }
            editor.apply();

            Toast.makeText(this, "Profile Updated!", Toast.LENGTH_SHORT).show();
            finish(); // Back to dashboard
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter valid numbers", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void updateBMI() {
        try {
            int weight = Integer.parseInt(etWeight.getText().toString());
            int heightCm = Integer.parseInt(etHeight.getText().toString());
            
            if (heightCm > 0) {
                double heightM = heightCm / 100.0;
                double bmi = weight / (heightM * heightM);
                tvBMI.setText(String.format(Locale.US, "%.1f", bmi));
            } else {
                tvBMI.setText("--");
            }
        } catch (NumberFormatException e) {
            tvBMI.setText("--");
        }
    }


}