package in.com.bookmydoc.activities;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import in.com.bookmydoc.R;
import in.com.bookmydoc.adapter.HospitalAdapter;
import in.com.bookmydoc.model.HospitalModel;

public class HospitalActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private HospitalAdapter hospitalAdapter;
    private List<HospitalModel> hospitalList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_hospital);

        recyclerView = findViewById(R.id.recyclerViewSpecialty);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        hospitalList = new ArrayList<>();
        hospitalAdapter = new HospitalAdapter(this, hospitalList);
        recyclerView.setAdapter(hospitalAdapter);

        db = FirebaseFirestore.getInstance();

        loadHospitals();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void loadHospitals() {
        db.collection("hospitals")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    hospitalList.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        String id = doc.getId();
                        String name = doc.getString("name");
                        String logoUrl = doc.getString("logoUrl");

                        // address null aa sakta hai kyunki Firebase me field nahi hai
                        String address = doc.getString("address");

                        double rating = doc.getDouble("rating") != null ? doc.getDouble("rating") : 0;
                        int ratingCount = doc.getLong("ratingCount") != null ? doc.getLong("ratingCount").intValue() : 0;
                        int distance = doc.getLong("distance") != null ? doc.getLong("distance").intValue() : 0;

                        List<String> imageUrls = (List<String>) doc.get("imageUrls");

                        HospitalModel hospital = new HospitalModel(
                                id, name, address, logoUrl,
                                rating, ratingCount, distance, imageUrls
                        );

                        hospitalList.add(hospital);
                    }
                    hospitalAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Log.e("FirebaseError", "Error: ", e));
    }

}