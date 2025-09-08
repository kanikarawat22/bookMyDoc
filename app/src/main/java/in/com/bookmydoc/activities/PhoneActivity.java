package in.com.bookmydoc.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import in.com.bookmydoc.databinding.ActivityPhoneBinding;

public class PhoneActivity extends AppCompatActivity {

    private ActivityPhoneBinding binding;
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    private static final String KEY_LOADING = "isLoading";
    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPhoneBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        setupFirebaseCallbacks();
        setupPhoneMask();

        if (savedInstanceState != null) {
            isLoading = savedInstanceState.getBoolean(KEY_LOADING, false);
            showLoading(isLoading);
        }

        binding.sendOtpButton.setOnClickListener(v -> {
            String phoneNumber = binding.phoneEditText.getText().toString().replaceAll("\\D", "");

            if (phoneNumber.length() == 10) {
                binding.phoneEditText.setError(null);
                showLoading(true);
                startPhoneNumberVerification("+91" + phoneNumber);
            } else {
                binding.phoneEditText.setError("Enter a valid 10-digit number");
            }
        });
    }


    private void setupPhoneMask() {
        binding.phoneEditText.addTextChangedListener(new TextWatcher() {
            private boolean isFormatting;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (isFormatting) return;

                isFormatting = true;
                String digits = s.toString().replaceAll("\\D", "");

                // Allow only 10 digits
                if (digits.length() > 10) {
                    digits = digits.substring(0, 10);
                }

                StringBuilder formatted = new StringBuilder();
                for (int i = 0; i < digits.length(); i++) {
                    formatted.append(digits.charAt(i));
                    if (i == 2 || i == 5) {
                        formatted.append("-");
                    }
                }

                binding.phoneEditText.setText(formatted.toString());
                binding.phoneEditText.setSelection(formatted.length());
                isFormatting = false;
            }
        });
    }

    private void startPhoneNumberVerification(String fullNumber) {
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(fullNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(mCallbacks)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void setupFirebaseCallbacks() {
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                showLoading(false);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                showLoading(false);
                String message = "Something went wrong, please try again.";
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    message = "The phone number you entered is invalid.";
                } else if (e.getMessage() != null && e.getMessage().contains("TOO_MANY_REQUESTS")) {
                    message = "You've sent too many requests. Please try again later.";
                }
                binding.phoneEditText.setError(message);
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                showLoading(false);
                Intent intent = new Intent(PhoneActivity.this, OtpVerificationActivity.class);
                intent.putExtra("verificationId", verificationId);
                intent.putExtra("phone", "+91" + binding.phoneEditText.getText().toString().replaceAll("\\D", ""));
                startActivity(intent);
            }
        };
    }

    private void showLoading(boolean loading) {
        isLoading = loading;
        binding.progressbar.setVisibility(loading ? View.VISIBLE : View.GONE);
        binding.sendOtpButton.setVisibility(loading ? View.GONE : View.VISIBLE);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_LOADING, isLoading);
    }
}
