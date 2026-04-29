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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import java.io.File;
import java.io.IOException;

public class OnboardingActivity extends AppCompatActivity {

    private EditText etName;
    private SeekBar seekAge, seekHeight, seekWeight;
    private TextView tvAgeValue, tvHeightValue, tvWeightValue;
    private RadioGroup rgGender;
    private ImageView imgEditAvatar;
    private Uri imageUri;
    private String currentImagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        setContentView(R.layout.activity_onboarding);

        initViews();
        loadExistingData();

        imgEditAvatar.setOnClickListener(v -> showImageSourceDialog());
        findViewById(R.id.btnGetStarted).setOnClickListener(v -> saveAndProceed());
    }

    private void initViews() {
        etName = findViewById(R.id.etName);
        seekAge = findViewById(R.id.seekAge);
        seekHeight = findViewById(R.id.seekHeight);
        seekWeight = findViewById(R.id.seekWeight);
        tvAgeValue = findViewById(R.id.tvAgeValue);
        tvHeightValue = findViewById(R.id.tvHeightValue);
        tvWeightValue = findViewById(R.id.tvWeightValue);
        rgGender = findViewById(R.id.rgGender);
        imgEditAvatar = findViewById(R.id.imgEditAvatar);

        seekAge.setOnSeekBarChangeListener(new SimpleSeekListener() {
            public void onProgressChanged(SeekBar s, int p, boolean u) { tvAgeValue.setText(String.valueOf(p)); }
        });
        seekHeight.setOnSeekBarChangeListener(new SimpleSeekListener() {
            public void onProgressChanged(SeekBar s, int p, boolean u) { tvHeightValue.setText(p + " cm"); }
        });
        seekWeight.setOnSeekBarChangeListener(new SimpleSeekListener() {
            public void onProgressChanged(SeekBar s, int p, boolean u) { tvWeightValue.setText(p + " kg"); }
        });
    }

    private void loadExistingData() {
        SharedPreferences prefs = getSharedPreferences("thryve_prefs", MODE_PRIVATE);
        etName.setText(prefs.getString("user_name", ""));
        seekAge.setProgress(prefs.getInt("user_age", 25));
        seekHeight.setProgress(prefs.getInt("user_height", 175));
        seekWeight.setProgress(prefs.getInt("user_weight", 70));

        currentImagePath = prefs.getString("profile_path", null);
        if (currentImagePath != null && imgEditAvatar != null) {
            imgEditAvatar.setImageURI(Uri.parse(currentImagePath));
            imgEditAvatar.setColorFilter(null);
            imgEditAvatar.setPadding(0,0,0,0);
        }
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
            cameraLauncher.launch(intent);
        } catch (Exception ex) {
            ex.printStackTrace();
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

    private void saveAndProceed() {
        SharedPreferences prefs = getSharedPreferences("thryve_prefs", MODE_PRIVATE);
        String gender = "Male";
        int checkedId = rgGender.getCheckedRadioButtonId();
        if (checkedId == R.id.rbFemale) gender = "Female";
        else if (checkedId == R.id.rbOther) gender = "Other";

        prefs.edit()
                .putString("user_name", etName.getText().toString().trim())
                .putInt("user_age", seekAge.getProgress())
                .putInt("user_height", seekHeight.getProgress())
                .putInt("user_weight", seekWeight.getProgress())
                .putString("user_gender", gender)
                .putString("profile_path", currentImagePath)
                .putBoolean("onboarding_done", true).apply();
        startActivity(new Intent(this, DashboardActivity.class));
        finish();
    }

    abstract static class SimpleSeekListener implements SeekBar.OnSeekBarChangeListener {
        public void onStartTrackingTouch(SeekBar s) {} public void onStopTrackingTouch(SeekBar s) {}
    }
}