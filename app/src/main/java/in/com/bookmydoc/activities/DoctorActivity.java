package in.com.bookmydoc.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import in.com.bookmydoc.R;
import in.com.bookmydoc.adapter.DoctorAdapter;
import in.com.bookmydoc.model.Doctor;

public class DoctorActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DoctorAdapter doctorAdapter;
    private List<Doctor> doctorList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor);

        recyclerView = findViewById(R.id.recyclerViewDoctors);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        doctorList = new ArrayList<>();
        doctorAdapter = new DoctorAdapter(this, doctorList, doctor -> {
            // Doctor pe click → DoctorDetailActivity kholna
            Intent intent = new Intent(DoctorActivity.this, DoctorDetailActivity.class);
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

        recyclerView.setAdapter(doctorAdapter);

        db = FirebaseFirestore.getInstance();

        // Hospital ID jo intent se aayega
        String hospitalId = getIntent().getStringExtra("hospitalId");
        loadDoctors(hospitalId);
    }

    private void loadDoctors(String hospitalId) {
        db.collection("hospitals")
                .document(hospitalId)
                .collection("doctors")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    doctorList.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        String docid = doc.getString("docid");
                        String name = doc.getString("name");
                        String specialty = doc.getString("specialty");
                        String hospital = doc.getString("hospital");
                        String experience = doc.getString("experience");
                        String profileImageUrl = doc.getString("profileImageUrl");
                        float rating = doc.getDouble("rating") != null ? doc.getDouble("rating").floatValue() : 0f;
                        double fee = doc.getDouble("fee") != null ? doc.getDouble("fee") : 0.0;

                        Doctor doctor = new Doctor(docid, name, specialty, hospital, experience, profileImageUrl, rating, fee);
                        doctorList.add(doctor);
                    }
                    doctorAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Log.e("FirebaseError", "Error fetching doctors", e));
    }
}
