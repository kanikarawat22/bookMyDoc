package in.com.bookmydoc.activities;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import in.com.bookmydoc.R;
import in.com.bookmydoc.fragments.AppTutoFragment;
import in.com.bookmydoc.fragments.AppointmentHistoryFragment;
import in.com.bookmydoc.fragments.HelpFragment;
import in.com.bookmydoc.fragments.HomeFragment;

//public class MainActivity extends AppCompatActivity {
//
//    private RecyclerView recyclerView;
//    private SpecialtyAdapter adapter;
//    private List<Specialty> specialtyList;
//
//    private EditText editTextSearch;
//    private Button buttonCallToBook;
//    private BottomNavigationView bottomNavigationView;
//
//    private LottieAnimationView medicineDeliveryAnimation, virtualAppointmentAnimation;
//
//    private FirebaseFirestore firestore;
//    private CollectionReference specialtyRef;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        // Initialize Views
//        recyclerView = findViewById(R.id.recyclerViewSpecialty);
//        editTextSearch = findViewById(R.id.editTextSearch);
//        buttonCallToBook = findViewById(R.id.buttonCallToBook);
//        bottomNavigationView = findViewById(R.id.bottomNavigationView);
//        medicineDeliveryAnimation = findViewById(R.id.medicine_delivery);
//        virtualAppointmentAnimation = findViewById(R.id.virtual_appointment);
//        firestore = FirebaseFirestore.getInstance();
//
//        // Setup RecyclerView
//        recyclerView.setLayoutManager(new GridLayoutManager(this, 4)); // 4 columns
//        specialtyList = new ArrayList<>();
//        adapter = new SpecialtyAdapter(this, specialtyList, specialty -> {
//            startActivity(new Intent(this, SelectHospitalActivity.class));
//            // You can navigate to another activity here
//        });
//
//        firestore.collection("Specialties").get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        for (QueryDocumentSnapshot doc : task.getResult()) {
//                            Specialty model = doc.toObject(Specialty.class);
//                            specialtyList.add(model);
//                        }
//                        adapter.notifyDataSetChanged();
//                    } else {
//                        // Handle failure
//                    }
//                });
//            recyclerView.setAdapter(adapter);
//
////        loadSpecialtiesFromFirestore();
//
//        // Setup Firestore
////        specialtyRef = firestore.collection("Specialties");
//
//        // Load Specialties
//        medicineDeliveryAnimation.setAnimation(R.raw.delivery);
//        virtualAppointmentAnimation.setAnimation(R.raw.consultation);
//
//        // Call to Book Button Action
//        buttonCallToBook.setOnClickListener(v -> {
//            Toast.makeText(MainActivity.this, "Calling to book appointment...", Toast.LENGTH_SHORT).show();
//            // You can add call intent here
//        });
//
//        // Start Lottie Animations
//        medicineDeliveryAnimation.playAnimation();
//        virtualAppointmentAnimation.playAnimation();
//
//        // Quick Service Animation Click Listeners
//        medicineDeliveryAnimation.setOnClickListener(v -> {
//            Toast.makeText(MainActivity.this, "Medicine Delivery clicked", Toast.LENGTH_SHORT).show();
//            // Navigate or handle action
//        });
//
//        virtualAppointmentAnimation.setOnClickListener(v -> {
//            Toast.makeText(MainActivity.this, "Virtual Appointment clicked", Toast.LENGTH_SHORT).show();
//            // Navigate or handle action
//        });
//
//        // Bottom Navigation Setup
//        setupBottomNavigation();
//    }
//
////    private void loadSpecialtiesFromFirestore() {
////        specialtyRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
////            @Override
////            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
////                if (error != null) {
////                    Toast.makeText(MainActivity.this, "Error loading specialties", Toast.LENGTH_SHORT).show();
////                    return;
////                }
////
////                specialtyList.clear();
////                if (value != null) {
////                    for (QueryDocumentSnapshot doc : value) {
////                        Specialty specialty = doc.toObject(Specialty.class);
////                        specialtyList.add(specialty);
////                    }
////                    adapter.notifyDataSetChanged();
////                }
////            }
////        });
////    }
//
//    private void setupBottomNavigation() {
//        bottomNavigationView.setOnItemSelectedListener(item -> {
//            int itemId = item.getItemId();
//
//            if (itemId == R.id.nav_home) {
//                Toast.makeText(MainActivity.this, "Home selected", Toast.LENGTH_SHORT).show();
//                return true;
//
//            } else if (itemId == R.id.nav_records) {
//                Toast.makeText(MainActivity.this, "Records selected", Toast.LENGTH_SHORT).show();
//                return true;
//
//            } else if (itemId == R.id.nav_demo) {
//                Toast.makeText(MainActivity.this, "Demo Video selected", Toast.LENGTH_SHORT).show();
//                return true;
//
//            } else if (itemId == R.id.nav_contact) {
//                Toast.makeText(MainActivity.this, "Contact Us selected", Toast.LENGTH_SHORT).show();
//                return true;
//
//            }
//
//            return false;
//        });
//    }
//}
public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new HomeFragment()).commit();

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (id == R.id.nav_records) {
                selectedFragment = new AppointmentHistoryFragment();
            } else if (id == R.id.nav_demo) {
                selectedFragment = new AppTutoFragment();
            } else if (id == R.id.nav_contact) {
                selectedFragment = new HelpFragment();
            }

            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                    .replace(R.id.fragment_container, selectedFragment)
                    .commit();

            return true;
        });

    }
}
