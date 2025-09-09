package in.com.bookmydoc.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

import in.com.bookmydoc.R;
import in.com.bookmydoc.adapter.DoctorAdapter;
import in.com.bookmydoc.model.Doctor;

public class HospitalDetailsActivity extends AppCompatActivity {

    private RecyclerView docRecyclerView;
    private DoctorAdapter doctorAdapter;
    private ArrayList<Doctor> doctors;
    private String id, name;
    private TextView hname, emptyMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_hospital_details);

        // ✅ Views
        docRecyclerView = findViewById(R.id.recyclerViewDoctors);
        hname = findViewById(R.id.textViewHospitalName);
        emptyMessage = findViewById(R.id.textViewEmptyDoctors);

        // ✅ Get intent data
        id = getIntent().getStringExtra("id");
        name = getIntent().getStringExtra("name");

        hname.setText(name);

        // ✅ RecyclerView setup
        doctors = new ArrayList<>();
        doctorAdapter = new DoctorAdapter(this, doctors, doctor -> {
            // Doctor pe click → DoctorDetailActivity kholna
            Intent intent = new Intent(HospitalDetailsActivity.this, DoctorDetailActivity.class);
            intent.putExtra("doctorId", doctor.getDocid());
            intent.putExtra("name", doctor.getName());
            intent.putExtra("specialty", doctor.getSpecialty());
            intent.putExtra("hospital", doctor.getHospital());
            intent.putExtra("experience", doctor.getExperience());
            intent.putExtra("rating", doctor.getRating());
            intent.putExtra("fee", doctor.getFee());
            intent.putExtra("profileImageUrl", doctor.getProfileImageUrl());
            startActivity(intent);
        });

        docRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        docRecyclerView.setAdapter(doctorAdapter);

        // ✅ Fetch data from Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("hospitals").document(id).collection("doctors")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        doctors.clear();
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            Doctor model = doc.toObject(Doctor.class);
                            doctors.add(model);
                        }
                        doctorAdapter.notifyDataSetChanged();

                        // ✅ Empty state check88888
                        if (doctors.isEmpty()) {
                            emptyMessage.setVisibility(TextView.VISIBLE);
                            docRecyclerView.setVisibility(RecyclerView.GONE);
                        } else {
                            emptyMessage.setVisibility(TextView.GONE);
                            docRecyclerView.setVisibility(RecyclerView.VISIBLE);
                        }
                    } else {
                        Toast.makeText(this, "Failed to load doctors", Toast.LENGTH_SHORT).show();
                    }
                });


        // ✅ Insets handling (status bar padding)
        View rootLayout = findViewById(R.id.rootLayout);
        if (rootLayout != null) {
            ViewCompat.setOnApplyWindowInsetsListener(rootLayout, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

    }
}
