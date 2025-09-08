package in.com.bookmydoc.activities;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import in.com.bookmydoc.R;
import in.com.bookmydoc.generator.IdGenerator;
import in.com.bookmydoc.manager.EncryptionManager;
import in.com.bookmydoc.model.UserProfile;

public class ProfileSetupActivity extends AppCompatActivity {

    private static final String TAG = "ProfileSetupActivity";

    // UI Views
    private CircleImageView profileImageView;
    private TextInputEditText fullNameInput, ageInput, mdrInput, dobInput;
    private TextInputLayout fullNameLayout, dobLayout; // UPDATED: Added TextInputLayouts for validation
    private RadioGroup genderGroup;
    private LinearLayout cameraLayout, galleryLayout, selectImageLayout;
    private ProgressBar progressBar;
    private Button saveBtn;
    private RelativeLayout rootLayout;

    private String phone;
    private Bitmap profileBitmap;

    private ActivityResultLauncher<Void> cameraLauncher;
    private ActivityResultLauncher<String> galleryLauncher;
    private ActivityResultLauncher<String[]> permissionLauncher;

    private EncryptionManager encryptionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setup);

        initializeViews();
        phone = getIntent().getStringExtra("phone");

        try {
            encryptionManager = new EncryptionManager();
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize EncryptionManager", e);
            Toast.makeText(this, "Security setup failed. Cannot save profile.", Toast.LENGTH_LONG).show();
            saveBtn.setEnabled(false);
        }

        registerActivityLaunchers();
        setupClickListeners();
    }

    // UPDATED: IDs now match the new Material Design XML
    private void initializeViews() {
        profileImageView = findViewById(R.id.profileImageView);
        fullNameInput = findViewById(R.id.fullNameInput);
        ageInput = findViewById(R.id.ageInput);
        mdrInput = findViewById(R.id.mdrInput);
        dobInput = findViewById(R.id.dobInput);
        genderGroup = findViewById(R.id.genderGroup);
        selectImageLayout = findViewById(R.id.selectImage);
        cameraLayout = findViewById(R.id.camera);
        galleryLayout = findViewById(R.id.gallery);
        progressBar = findViewById(R.id.progressbar);
        saveBtn = findViewById(R.id.saveProfileBtn);
        rootLayout = findViewById(R.id.rootLayout);
        fullNameLayout = findViewById(R.id.fullNameLayout);
        dobLayout = findViewById(R.id.dobLayout);
    }

    private void registerActivityLaunchers() {
        cameraLauncher = registerForActivityResult(new ActivityResultContracts.TakePicturePreview(), result -> {
            if (result != null) {
                profileBitmap = result;
                profileImageView.setImageBitmap(profileBitmap);
                selectImageLayout.setVisibility(View.GONE);
            }
        });

        galleryLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
            if (result != null) {
                try {
                    profileBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), result);
                    profileImageView.setImageBitmap(profileBitmap);
                    selectImageLayout.setVisibility(View.GONE);
                } catch (Exception e) {
                    Log.e(TAG, "Error getting bitmap from gallery", e);
                    Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
                }
            }
        });

        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
            Boolean cameraGranted = result.getOrDefault(Manifest.permission.CAMERA, false);
            if (Boolean.TRUE.equals(cameraGranted)) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupClickListeners() {
        dobInput.setOnClickListener(v -> showDatePicker());
        profileImageView.setOnClickListener(v -> toggleImageSelection(true));
        rootLayout.setOnClickListener(v -> toggleImageSelection(false));

        cameraLayout.setOnClickListener(v -> {
            if (checkPermissions()) cameraLauncher.launch(null);
            else requestPermissions();
        });

        galleryLayout.setOnClickListener(v -> {
            if (checkPermissions()) galleryLauncher.launch("image/*");
            else requestPermissions();
        });

        saveBtn.setOnClickListener(v -> {
            if (validateInput()) {
                updateUiForLoading(true);
                saveProfile();
            }
        });
    }

    // UPDATED: Validation now uses TextInputLayouts for better error messages
    private boolean validateInput() {
        fullNameLayout.setError(null);
        dobLayout.setError(null);

        if (TextUtils.isEmpty(fullNameInput.getText())) {
            fullNameLayout.setError("Full name cannot be empty");
            return false;
        }
        if (TextUtils.isEmpty(dobInput.getText())) {
            dobLayout.setError("Please select a date of birth");
            return false;
        }
        return true;
    }

    private void saveProfile() {
        String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        if (profileBitmap != null) {
            uploadImageAndSaveData(uid);
        } else {
            saveDataToFirestore(uid, "default");
        }
    }

    private void uploadImageAndSaveData(String uid) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        profileBitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos);
        byte[] data = baos.toByteArray();
        StorageReference ref = FirebaseStorage.getInstance().getReference("profile_images/" + uid + ".jpg");

        ref.putBytes(data)
                .addOnSuccessListener(taskSnapshot -> ref.getDownloadUrl()
                        .addOnSuccessListener(uri -> saveDataToFirestore(uid, uri.toString()))
                        .addOnFailureListener(this::handleSaveError))
                .addOnFailureListener(this::handleSaveError);
    }

    private void saveDataToFirestore(String uid, String imageUrl) {
        int selectedGenderId = genderGroup.getCheckedRadioButtonId();
        RadioButton selectedGender = findViewById(selectedGenderId);

        try {
            UserProfile userProfile = new UserProfile(
                    encryptionManager.encrypt(Objects.requireNonNull(fullNameInput.getText()).toString()),
                    encryptionManager.encrypt(Objects.requireNonNull(ageInput.getText()).toString()),
                    encryptionManager.encrypt(phone),
                    encryptionManager.encrypt(IdGenerator.generateUserId("BMDUSR", phone)),
                    encryptionManager.encrypt(Objects.requireNonNull(mdrInput.getText()).toString().isEmpty() ? "No Record" : mdrInput.getText().toString()),
                    encryptionManager.encrypt(selectedGender.getText().toString()),
                    encryptionManager.encrypt(Objects.requireNonNull(dobInput.getText()).toString()),
                    imageUrl
            );

            // Using SetOptions.merge() will update the existing document without overwriting fields
            FirebaseFirestore.getInstance().collection("users").document(uid).set(userProfile, SetOptions.merge())
                    .addOnSuccessListener(aVoid -> handleSaveSuccess())
                    .addOnFailureListener(this::handleSaveError);

        } catch (Exception e) {
            Log.e(TAG, "Encryption failed during profile save", e);
            handleSaveError(e);
        }
    }

    private void handleSaveSuccess() {
        Toast.makeText(this, "Profile saved!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(FLAG_ACTIVITY_CLEAR_TASK | FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void handleSaveError(Exception e) {
        Log.e(TAG, "Error saving profile", e);
        Toast.makeText(this, "Failed to save profile. Please try again.", Toast.LENGTH_SHORT).show();
        updateUiForLoading(false);
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendar.set(year, month, dayOfMonth);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            dobInput.setText(sdf.format(calendar.getTime()));

            int age = Calendar.getInstance().get(Calendar.YEAR) - year;
            ageInput.setText(String.valueOf(age));
            dobLayout.setError(null); // Clear error after selection
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        dialog.show();
    }

    private void updateUiForLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        saveBtn.setText(isLoading ? "" : "Save Profile");
        saveBtn.setEnabled(!isLoading);
    }

    private void toggleImageSelection(boolean show) {
        selectImageLayout.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private boolean checkPermissions() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        permissionLauncher.launch(new String[]{Manifest.permission.CAMERA});
    }
}