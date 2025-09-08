package in.com.bookmydoc.activities;

import in.com.bookmydoc.R;

import android.os.Bundle;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import in.com.bookmydoc.adapter.DoctorAdapter;
import in.com.bookmydoc.model.Doctor;

public class SelectDocActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DoctorAdapter adapter;
    private List<Doctor> doctorList = new ArrayList<>();
    private SearchView searchView;
    private ChipGroup chipGroup;
    private TextView tvEmpty; // ✅ empty state view

    private String selectedSpecialty = "All"; // default

    // 🔹 Firestore instance
    private FirebaseFirestore db;

    // Hospital ID (ye tum previous screen se intent extra ke through bhejna hoga)
    private String hospitalId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_doc);

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        recyclerView = findViewById(R.id.rvDoctors);
        searchView = findViewById(R.id.searchView);
        chipGroup = findViewById(R.id.chipGroupSpecialty);
        tvEmpty = findViewById(R.id.tvEmpty);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DoctorAdapter(this, doctorList);
        recyclerView.setAdapter(adapter);

        // Firestore init
        db = FirebaseFirestore.getInstance();

        // 🔹 HospitalId from Intent (jo tum SelectHospital screen se bhejoge)
        hospitalId = getIntent().getStringExtra("hospitalId");

        loadDoctorsFromFirestore();
        setupSearch();
        setupChips();
    }

    private void loadDoctorsFromFirestore() {
        if (hospitalId == null || hospitalId.isEmpty()) {
            return;
        }

        db.collection("hospitals")
                .document(hospitalId)
                .collection("doctors")
                .get()
                .addOnSuccessListener((QuerySnapshot snapshots) -> {
                    doctorList.clear();
                    for (DocumentSnapshot doc : snapshots.getDocuments()) {
                        Doctor doctor = doc.toObject(Doctor.class);
                        if (doctor != null) {
                            doctorList.add(doctor);
                        }
                    }
                    adapter.notifyDataSetChanged();
                    toggleEmptyState();
                })
                .addOnFailureListener(e -> {
                    // handle error
                    tvEmpty.setText("Failed to load doctors");
                    tvEmpty.setVisibility(TextView.VISIBLE);
                });
    }

    private void setupSearch() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.filter(query, selectedSpecialty, "All");
                toggleEmptyState();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText, selectedSpecialty, "All");
                toggleEmptyState();
                return true;
            }
        });
    }

    private void setupChips() {
        chipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            Chip chip = group.findViewById(checkedId);
            if (chip != null) {
                selectedSpecialty = chip.getText().toString();
                adapter.filter(searchView.getQuery().toString(), selectedSpecialty, "All");
                toggleEmptyState();
            }
        });
    }

    private void toggleEmptyState() {
        if (adapter.getItemCount() == 0) {
            recyclerView.setVisibility(RecyclerView.GONE);
            tvEmpty.setVisibility(TextView.VISIBLE);
        } else {
            recyclerView.setVisibility(RecyclerView.VISIBLE);
            tvEmpty.setVisibility(TextView.GONE);
        }
    }
}
