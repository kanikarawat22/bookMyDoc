package in.com.bookmydoc.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.*;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import in.com.bookmydoc.databinding.ActivityOtpVerificationBinding;

public class OtpVerificationActivity extends AppCompatActivity {

    private ActivityOtpVerificationBinding binding;
    private EditText[] otpFields;
    private String verificationId, phone;
    private FirebaseAuth mAuth;
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOtpVerificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        verificationId = getIntent().getStringExtra("verificationId");
        phone = getIntent().getStringExtra("phone");

        if (phone != null && phone.length() >= 10) {
            String masked = "+91 XXXX XX" + phone.substring(phone.length() - 2);
            binding.otpSubtitle2.setText(masked);
        }

        otpFields = new EditText[]{
                binding.otpDigit1, binding.otpDigit2, binding.otpDigit3,
                binding.otpDigit4, binding.otpDigit5, binding.otpDigit6
        };

        setupOtpFieldLogic();
        startResendCountdown();

        binding.verifyOtpButton.setOnClickListener(v -> verifyOtp());
        binding.resendOtp.setOnClickListener(v -> resendVerificationCode());
    }

    private void setupOtpFieldLogic() {
        for (int i = 0; i < otpFields.length; i++) {
            final int index = i;

            otpFields[index].addTextChangedListener(new TextWatcher() {
                private boolean isDeleting = false;

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    isDeleting = count > after;
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    if (!isDeleting && s.length() == 1 && index < otpFields.length - 1) {
                        otpFields[index + 1].requestFocus();
                    }
                }
            });

            otpFields[index].setOnKeyListener((v, keyCode, event) -> {
                if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                    EditText currentBox = otpFields[index];
                    if (currentBox.getText().toString().isEmpty() && index > 0) {
                        otpFields[index - 1].requestFocus();
                        otpFields[index - 1].setSelection(otpFields[index - 1].getText().length());
                    }
                }
                return false;
            });
        }
    }

    private void verifyOtp() {
        StringBuilder codeBuilder = new StringBuilder();
        for (EditText field : otpFields) {
            codeBuilder.append(field.getText().toString());
        }
        String code = codeBuilder.toString();

        if (code.length() == 6 && verificationId != null) {
            showLoading(true);
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
            signInWithCredential(credential);
        } else {
            Toast.makeText(this, "Please enter the full 6-digit OTP", Toast.LENGTH_SHORT).show();
        }
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(task -> {
            showLoading(false);
            if (task.isSuccessful()) {
                FirebaseUser user = task.getResult().getUser();
                if (user != null) {
                    String uid = user.getUid();
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    Map<String, Object> userData = new HashMap<>();
                    userData.put("phone", phone);

                    db.collection("users").document(uid)
                            .set(userData)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(this, "Verified Successfully", Toast.LENGTH_SHORT).show();
                                navigateToProfileSetup();
                            })
                            .addOnFailureListener(e -> Toast.makeText(this, "Failed to save data", Toast.LENGTH_SHORT).show());
                }
            } else {
                String message = "The OTP you entered is invalid";
                if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                    message = "Invalid OTP entered";
                }
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToProfileSetup() {
        Intent intent = new Intent(getApplicationContext(), ProfileSetupActivity.class);
        intent.putExtra("phone", phone);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void resendVerificationCode() {
        binding.resendOtp.setEnabled(false);
        startResendCountdown();

        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber("+91" + phone)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onCodeSent(@NonNull String newVerificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                        super.onCodeSent(newVerificationId, token);
                        verificationId = newVerificationId;
                        Toast.makeText(OtpVerificationActivity.this, "New OTP Sent", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                        signInWithCredential(credential);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Toast.makeText(OtpVerificationActivity.this, "Failed to send OTP", Toast.LENGTH_SHORT).show();
                        binding.resendOtp.setEnabled(true);
                    }
                }).build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }
    private void startSmsRetriever() {
//        smsConsentLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
//            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
//                String message = result.getData().getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE);
//                extractOtpFromMessage(message);
//            }
//        });
//
//        SmsRetrieverClient client = SmsRetriever.getClient(this);
//        client.startSmsUserConsent(null);
    }



    private void startResendCountdown() {
        binding.resendOtp.setEnabled(false);
        countDownTimer = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                binding.resendOtp.setText("Resend in " + millisUntilFinished / 1000 + "s");
            }

            @Override
            public void onFinish() {
                binding.resendOtp.setText("Resend OTP");
                binding.resendOtp.setEnabled(true);
            }
        }.start();
    }

    private void showLoading(boolean isLoading) {
        binding.progressbar.setVisibility(isLoading ? android.view.View.VISIBLE : android.view.View.GONE);
        binding.verifyOtpButton.setVisibility(isLoading ? android.view.View.INVISIBLE : android.view.View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}
