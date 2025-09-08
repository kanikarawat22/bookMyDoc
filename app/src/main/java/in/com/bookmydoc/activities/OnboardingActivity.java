package in.com.bookmydoc.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import in.com.bookmydoc.R;
import in.com.bookmydoc.adapter.OnboardingAdapter;
import in.com.bookmydoc.model.OnboardingItem;
import com.google.android.material.button.MaterialButton;
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator;

import java.util.ArrayList;
import java.util.List;

public class OnboardingActivity extends AppCompatActivity {

    ViewPager2 viewPager;
    MaterialButton btnGetStarted, btnNext, btnSkip;
    WormDotsIndicator dotsIndicator;
    int currentIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        viewPager = findViewById(R.id.viewPager);
        btnGetStarted = findViewById(R.id.btnGetStarted);
        btnNext = findViewById(R.id.btnNext);
        btnSkip = findViewById(R.id.btnSkip);
        dotsIndicator = findViewById(R.id.dotsIndicator);

        List<OnboardingItem> items = new ArrayList<>();
        items.add(new OnboardingItem(R.raw.appointment, "Book Appointments", "Easily schedule consultations with doctors"));
        items.add(new OnboardingItem(R.raw.consultation, "Virtual Consultation", "Connect with doctors via video call"));
        items.add(new OnboardingItem(R.raw.delivery, "Medicine Delivery", "Get medicines delivered at your door"));

        OnboardingAdapter adapter = new OnboardingAdapter(items);
        viewPager.setAdapter(adapter);
        dotsIndicator.setViewPager2(viewPager);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                currentIndex = position;
                btnGetStarted.setVisibility(position == items.size() - 1 ? View.VISIBLE : View.GONE);
                btnNext.setVisibility(position == items.size() - 1 ? View.GONE : View.VISIBLE);
                btnSkip.setVisibility(position == items.size() - 1 ? View.GONE : View.VISIBLE);

            }
        });
        btnSkip.setOnClickListener(v -> {
            Intent intent = new Intent(OnboardingActivity.this, PhoneActivity.class);
            startActivity(intent);
            finish();
        });



        btnGetStarted.setOnClickListener(v -> {
            Intent intent = new Intent(OnboardingActivity.this, PhoneActivity.class);
            startActivity(intent);
            finish();
        });

        btnNext.setOnClickListener(v -> {
            if (currentIndex < items.size() - 1) {
                viewPager.setCurrentItem(currentIndex + 1);
            }
        });

    }
}
