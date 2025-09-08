package in.com.bookmydoc.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import in.com.bookmydoc.R;

public class DoctorDetailActivity extends AppCompatActivity
{
    private ImageView ivDoctorImage;
    private TextView tvDoctorName, tvSpecialty, tvExperience, tvFee, tvRating, tvAbout;
    private Button btnBookNow;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_detail);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Doctor");
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // back arrow
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Back arrow click listener
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        ivDoctorImage = findViewById(R.id.ivDoctorImage);
        tvDoctorName = findViewById(R.id.tvDoctorName);
        tvSpecialty = findViewById(R.id.tvSpecialty);
        tvExperience = findViewById(R.id.tvExperience);
        tvFee = findViewById(R.id.tvFee);
        tvRating = findViewById(R.id.tvRating);
        tvAbout = findViewById(R.id.tvAbout);
        btnBookNow = findViewById(R.id.btnBookNow);


        String docID = getIntent().getStringExtra("doctorId");
        String name = getIntent().getStringExtra("name");
        String specialty = getIntent().getStringExtra("specialty");
        String experience = getIntent().getStringExtra("experience");
        String profileImage = getIntent().getStringExtra("profileImageUrl");
        float rating = getIntent().getFloatExtra("rating",0f);
        double fee = getIntent().getDoubleExtra("fee", 0.0);

        tvDoctorName.setText(name);
        tvSpecialty.setText("Speciality:" + specialty);
        tvExperience.setText("Experience:" + experience);
        tvFee.setText("Consulation Fee: ₹" + fee);
        tvRating.setText("Rating: "+ rating+ "★");
        tvAbout.setText("Dr. " + name + " is a highly experienced " + specialty +
                " specialist with over " + experience + " of professional practice. " +
                "Dedicated to providing the best care for patients, Dr. " + name +
                "has a proven track record of successful treatments.");


        Glide.with(this)
                .load(profileImage)
                .placeholder(R.drawable.profile_lu)
                .error(R.drawable.profile_lu)
                .into(ivDoctorImage);


        btnBookNow.setOnClickListener(v ->
        {
            Intent intent = new Intent(DoctorDetailActivity.this, BookingActivity.class);
            intent.putExtra("docId", docID);
            intent.putExtra("name", name);
            intent.putExtra("specialty", specialty);
            intent.putExtra("fee",fee);
            startActivity(intent);

        });

    }

}
