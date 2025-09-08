package in.com.bookmydoc.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.hdodenhof.circleimageview.CircleImageView;
import in.com.bookmydoc.R;
import in.com.bookmydoc.activities.CallActivity;
import in.com.bookmydoc.activities.ConsultDoctorActivity;
import in.com.bookmydoc.activities.SelectHospitalActivity;
import in.com.bookmydoc.activities.SpecialtyActivity;
import in.com.bookmydoc.adapter.PromoBannerAdapter;
import in.com.bookmydoc.adapter.SpecialtyAdapter;
import in.com.bookmydoc.manager.EncryptionManager;
import in.com.bookmydoc.model.Specialty;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    // --- MEMBER VARIABLES ---
    private TextView textViewLocation, textViewAll, textViewUserName;
    private CircleImageView profileImageView;
    private ViewPager2 viewPagerPromoBanner;
    private ShimmerFrameLayout shimmerContainer;
    private ConstraintLayout contentContainer;
    private EditText editTextSearch;
    private MaterialButton buttonCall, buttonBook;
    private TabLayout tabLayoutIndicator;
    private RecyclerView recyclerViewSpecialty;
    private SpecialtyAdapter specialtyAdapter;
    private List<Specialty> specialtyList;
    private LottieAnimationView medicineDeliveryAnimation;
    private LottieAnimationView virtualAppointmentAnimation;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private ActivityResultLauncher<String[]> locationPermissionLauncher;
    private FirebaseFirestore firestore;
    private EncryptionManager encryptionManager;
    private final Handler sliderHandler = new Handler(Looper.getMainLooper());


    public HomeFragment() {
        // Required empty public constructor
    }

    // --- FRAGMENT LIFECYCLE ---
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firestore = FirebaseFirestore.getInstance();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        try {
            encryptionManager = new EncryptionManager();
        } catch (Exception e) {
            Log.e(TAG, "EncryptionManager initialization failed", e);
            Toast.makeText(getContext(), "Security module failed to load.", Toast.LENGTH_SHORT).show();
        }

        locationPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                permissions -> {
                    if (Boolean.TRUE.equals(permissions.get(Manifest.permission.ACCESS_FINE_LOCATION)) ||
                            Boolean.TRUE.equals(permissions.get(Manifest.permission.ACCESS_COARSE_LOCATION))) {
                        fetchLastLocation();
                    } else {
                        Toast.makeText(getContext(), "Location permission denied", Toast.LENGTH_SHORT).show();
                        if (textViewLocation != null) {
                            textViewLocation.setText(R.string.default_location);
                        }
                    }
                });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindViews(view);
        setupPromoBanner();
        setupRecyclerView();
        setupClickListeners();
        showShimmer(true);
        loadData();
    }

    // --- SETUP ---
    private void bindViews(View view) {
        shimmerContainer = view.findViewById(R.id.shimmer_container);
        contentContainer = view.findViewById(R.id.main_content_group);
        recyclerViewSpecialty = view.findViewById(R.id.recycler_view_specialty);
        editTextSearch = view.findViewById(R.id.editTextSearch);
        buttonCall = view.findViewById(R.id.btn_call_hospital);
        buttonBook = view.findViewById(R.id.btn_book_appointment);
        textViewLocation = view.findViewById(R.id.tv_location);
        textViewAll = view.findViewById(R.id.textViewViewAll);
        viewPagerPromoBanner = view.findViewById(R.id.viewPagerPromoBanner);
        medicineDeliveryAnimation = view.findViewById(R.id.lottie_medicine_delivery);
        virtualAppointmentAnimation = view.findViewById(R.id.lottie_virtual_appointment);
        tabLayoutIndicator = view.findViewById(R.id.tabLayoutIndicator);
        profileImageView = view.findViewById(R.id.profile_image);
        textViewUserName = view.findViewById(R.id.tv_greeting);
    }

    private void setupRecyclerView() {
        recyclerViewSpecialty.setLayoutManager(new GridLayoutManager(getContext(), 4));
        specialtyList = new ArrayList<>();
        specialtyAdapter = new SpecialtyAdapter(getContext(), specialtyList, specialty -> {
            Intent intent = new Intent(requireContext(), SelectHospitalActivity.class);
            intent.putExtra("specialty_id", specialty.getId());
            startActivity(intent);
        });
        recyclerViewSpecialty.setAdapter(specialtyAdapter);
    }

    private void setupClickListeners() {
        buttonCall.setOnClickListener(v -> startActivity(new Intent(requireContext(), CallActivity.class)));
        buttonBook.setOnClickListener(v -> startActivity(new Intent(requireContext(), SelectHospitalActivity.class)));
        textViewAll.setOnClickListener(v -> startActivity(new Intent(requireContext(), SpecialtyActivity.class)));

        View medicineCard = requireView().findViewById(R.id.cardMedicineDelivery);
        medicineCard.setOnClickListener(v -> Toast.makeText(getContext(), "Medicine Delivery clicked", Toast.LENGTH_SHORT).show());

        View virtualAppointmentCard = requireView().findViewById(R.id.cardVirtualAppointment);
        virtualAppointmentCard.setOnClickListener(v -> startActivity(new Intent(requireContext(), ConsultDoctorActivity.class)));
    }

    private void setupPromoBanner() {
        List<Integer> bannerImages = new ArrayList<>();
        bannerImages.add(R.drawable.b1);
        bannerImages.add(R.drawable.b2);
        bannerImages.add(R.drawable.b3);

        viewPagerPromoBanner.setAdapter(new PromoBannerAdapter(bannerImages));
        new TabLayoutMediator(tabLayoutIndicator, viewPagerPromoBanner, (tab, position) -> {}).attach();

        viewPagerPromoBanner.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                sliderHandler.removeCallbacks(sliderRunnable);
                sliderHandler.postDelayed(sliderRunnable, 3000);
            }
        });
    }

    // --- DATA LOADING ---
    private void loadData() {
        fetchUserProfile();
        setupLottieAnimations();
        checkAndRequestLocationPermission();
        fetchSpecialtiesFromFirestore();
    }

    private void fetchUserProfile() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null && encryptionManager != null) {
            String uid = currentUser.getUid();
            DocumentReference userDocRef = firestore.collection("users").document(uid);

            userDocRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    String encryptedName = documentSnapshot.getString("fullName");
                    String imageUrl = documentSnapshot.getString("profileImage");

                    if (encryptedName != null) {
                        try {
                            String decryptedName = encryptionManager.decrypt(encryptedName);
                            textViewUserName.setText(String.format("Hi, %s", decryptedName.split(" ")[0])); // Show first name
                        } catch (Exception e) {
                            textViewUserName.setText("Hi, User");
                            Log.e(TAG, "Failed to decrypt name", e);
                        }
                    }

                    if (imageUrl != null && !imageUrl.equals("default") && getContext() != null) {
                        Glide.with(getContext())
                                .load(imageUrl)
                                .placeholder(R.drawable.profile_lu)
                                .error(R.drawable.profile_lu)
                                .into(profileImageView);
                    }
                }
            }).addOnFailureListener(e -> Log.e(TAG, "Error fetching user profile", e));
        }
    }

    private void setupLottieAnimations() {
        medicineDeliveryAnimation.setAnimation(R.raw.delivery);
        virtualAppointmentAnimation.setAnimation(R.raw.consultation);
    }

    private void checkAndRequestLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fetchLastLocation();
        } else {
            locationPermissionLauncher.launch(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION});
        }
    }

    private void fetchLastLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) return;
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(requireActivity(), location -> {
            if (location != null) {
                reverseGeocodeLocation(location);
            } else {
                textViewLocation.setText(R.string.default_location);
            }
        });
    }

    private void reverseGeocodeLocation(Location location) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
            String cityName = null;
            try {
                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                if (addresses != null && !addresses.isEmpty()) cityName = addresses.get(0).getLocality();
            } catch (IOException e) {
                Log.e(TAG, "Geocoding failed", e);
            }
            final String finalCityName = (cityName != null) ? cityName : getString(R.string.default_location);
            handler.post(() -> textViewLocation.setText(finalCityName));
        });
    }

    private void fetchSpecialtiesFromFirestore() {
        firestore.collection("Specialties").orderBy("id").limit(12).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                specialtyList.clear();
                for (QueryDocumentSnapshot doc : task.getResult()) {
                    Specialty specialty = doc.toObject(Specialty.class);
                    specialtyList.add(specialty);
                }
                specialtyAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(getContext(), "Failed to load specialties", Toast.LENGTH_SHORT).show();
            }
            showShimmer(false);
        });
    }

    // --- UI AND LIFECYCLE MANAGEMENT ---
    private final Runnable sliderRunnable = new Runnable() {
        @Override
        public void run() {
            if (viewPagerPromoBanner.getAdapter() != null) {
                int currentItem = viewPagerPromoBanner.getCurrentItem();
                int totalItems = viewPagerPromoBanner.getAdapter().getItemCount();
                if (totalItems > 0) {
                    viewPagerPromoBanner.setCurrentItem((currentItem + 1) % totalItems, true);
                }
            }
        }
    };

    private void showShimmer(boolean show) {
        if (show) {
            shimmerContainer.startShimmer();
            shimmerContainer.setVisibility(View.VISIBLE);
            contentContainer.setVisibility(View.GONE);
        } else {
            shimmerContainer.stopShimmer();
            shimmerContainer.setVisibility(View.GONE);
            contentContainer.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (shimmerContainer.isShimmerVisible()) {
            shimmerContainer.startShimmer();
        }
        sliderHandler.postDelayed(sliderRunnable, 3000);
    }

    @Override
    public void onPause() {
        if(shimmerContainer.isShimmerStarted()){
            shimmerContainer.stopShimmer();
        }
        super.onPause();
        sliderHandler.removeCallbacks(sliderRunnable);
    }
}