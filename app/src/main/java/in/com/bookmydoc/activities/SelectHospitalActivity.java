package in.com.bookmydoc.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import in.com.bookmydoc.R;
import in.com.bookmydoc.adapter.CategoryAdapter;
import in.com.bookmydoc.adapter.HospitalAdapter;
import in.com.bookmydoc.model.CategoryModel;
import in.com.bookmydoc.model.HospitalModel;

public class SelectHospitalActivity extends AppCompatActivity {

    RecyclerView categoryRecyclerView, hospitalRecyclerView;
    CategoryAdapter categoryAdapter;
    HospitalAdapter hospitalAdapter;
    ImageView backButton;

    EditText searchEditText;
    Spinner sortSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_select_hospital);

        backButton = findViewById(R.id.backButton);
        categoryRecyclerView = findViewById(R.id.categoryRecyclerView);
        hospitalRecyclerView = findViewById(R.id.hospitalRecyclerView);
        searchEditText = findViewById(R.id.SearechHos);
        sortSpinner = findViewById(R.id.sortSpinner);

        // Back button click
        backButton.setOnClickListener(view -> finish());

        // Category RecyclerView setup
        ArrayList<CategoryModel> categories = new ArrayList<>();
        categories.add(new CategoryModel("Heart"));
        categories.add(new CategoryModel("Skin"));
        categories.add(new CategoryModel("Eye Care"));
        categories.add(new CategoryModel("Dental"));
        categories.add(new CategoryModel("ENT"));
        categories.add(new CategoryModel("Neuro"));

        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        categoryAdapter = new CategoryAdapter(categories, this);
        categoryRecyclerView.setAdapter(categoryAdapter);

        // Hospital RecyclerView setup
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        ArrayList<HospitalModel> hospitals = new ArrayList<>();
        hospitalAdapter = new HospitalAdapter(this, hospitals);
        hospitalRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        hospitalRecyclerView.setAdapter(hospitalAdapter);

        // Fetch hospitals from Firestore
        db.collection("hospitals")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        hospitals.clear();
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            HospitalModel model = doc.toObject(HospitalModel.class);
                            if (model != null) {
                                model.setId(doc.getId());
                                hospitals.add(model);
                            }
                        }
                        // Important: update the full list in adapter
                        hospitalAdapter.updateFullList(hospitals);
                        hospitalAdapter.notifyDataSetChanged();
                    }
                });

        // Search functionality
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                hospitalAdapter.filter(s.toString());
            }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        // Sort Spinner listener
        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                hospitalAdapter.sortBy(position);
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
    }
}
