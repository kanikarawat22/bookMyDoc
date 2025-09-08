package in.com.bookmydoc.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import in.com.bookmydoc.R;

public class SplashscreenActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 3000; // 3 seconds
    private static final String PREFS_NAME = "onboarding_prefs";
    private static final String IS_SEEN_KEY = "isOnboardingSeen";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SplashScreen.installSplashScreen(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        ImageView logoImage = findViewById(R.id.logoImage);
        TextView quoteText = findViewById(R.id.quoteText);

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.fade_scale);
        logoImage.startAnimation(animation);
        quoteText.startAnimation(animation);

        // Delay to show splash screen
        new Handler(Looper.getMainLooper()).postDelayed(this::checkOnboardingStatus, SPLASH_DURATION);
    }

    private void checkOnboardingStatus() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isSeen = prefs.getBoolean(IS_SEEN_KEY, false);

        if (!isSeen) {
            prefs.edit().putBoolean(IS_SEEN_KEY, true).apply();
            navigateTo(OnboardingActivity.class);
        } else {
            checkUserStatusAndNavigate();
        }
    }

    private void checkUserStatusAndNavigate() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            navigateTo(PhoneActivity.class);
            return;
        }

        DocumentReference userDocRef = FirebaseFirestore.getInstance()
                .collection("users")
                .document(currentUser.getUid());

        userDocRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists() && document.contains("fullName")) {
                    // Profile complete → open MainActivity with HomeFragment
                    navigateToHomeFragment();
                } else {
                    // Profile not complete → go to profile setup
                    navigateTo(ProfileSetupActivity.class);
                }
            } else {
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
                navigateTo(ErrorActivity.class);
            }
        });
    }

    private void navigateToHomeFragment() {
        Intent intent = new Intent(SplashscreenActivity.this, MainActivity.class);
        intent.putExtra("openHomeFragment", true); // Flag to open HomeFragment
        startActivity(intent);
        finish();
    }

    private void navigateTo(Class<?> targetActivity) {
        Intent intent = new Intent(SplashscreenActivity.this, targetActivity);
        startActivity(intent);
        finish();
    }
}
