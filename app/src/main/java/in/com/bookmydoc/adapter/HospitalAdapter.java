package in.com.bookmydoc.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import in.com.bookmydoc.R;
import in.com.bookmydoc.activities.HospitalDetailsActivity;
import in.com.bookmydoc.model.HospitalModel;

public class HospitalAdapter extends RecyclerView.Adapter<HospitalAdapter.ViewHolder> {

    private List<HospitalModel> hospitalList;
    private List<HospitalModel> hospitalListFull;
    private Context context;

    public HospitalAdapter(Context context, List<HospitalModel> hospitalList) {
        this.context = context;
        this.hospitalList = hospitalList;
        this.hospitalListFull = new ArrayList<>(hospitalList);
    }

    public void filter(String text) {
        hospitalList.clear();
        if (text == null || text.trim().isEmpty()) {
            hospitalList.addAll(hospitalListFull);
        } else {
            String query = text.toLowerCase().trim();
            for (HospitalModel hospital : hospitalListFull) {
                if (hospital.getName().toLowerCase().contains(query)) {
                    hospitalList.add(hospital);
                }
            }
        }
        notifyDataSetChanged();
    }


    // Sorting method
    public void sortBy(int option) {
        switch (option) {
            case 0: // Name
                Collections.sort(hospitalList, (a, b) -> a.getName().compareToIgnoreCase(b.getName()));
                break;
            case 1: // Rating
                Collections.sort(hospitalList, (a, b) -> Double.compare(b.getRating(), a.getRating()));
                break;
            case 2: // Distance
                Collections.sort(hospitalList, (a, b) -> Integer.compare(a.getDistance(), b.getDistance()));
                break;
        }
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, address, rating, distance;
        ViewPager2 viewPager;
        Handler autoScrollHandler;
        Runnable autoScrollRunnable;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.textHospitalName);
            address = itemView.findViewById(R.id.textHospitalAddress); // ✅ Address bind
            rating = itemView.findViewById(R.id.textHospitalRating);
            distance = itemView.findViewById(R.id.textHospitalDistance);
            viewPager = itemView.findViewById(R.id.viewPagerImages);
        }

        public void startAutoScroll(List<String> imageUrls) {
            autoScrollHandler = new Handler(Looper.getMainLooper());
            autoScrollRunnable = new Runnable() {
                int currentPage = 0;

                @Override
                public void run() {
                    if (viewPager != null && imageUrls != null && !imageUrls.isEmpty()) {
                        viewPager.setCurrentItem(currentPage % imageUrls.size(), true);
                        currentPage++;
                        autoScrollHandler.postDelayed(this, 3000);
                    }
                }
            };
            autoScrollHandler.postDelayed(autoScrollRunnable, 3000);
        }

        public void stopAutoScroll() {
            if (autoScrollHandler != null && autoScrollRunnable != null) {
                autoScrollHandler.removeCallbacks(autoScrollRunnable);
            }
        }
    }

    @NonNull
    @Override
    public HospitalAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_hospital, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HospitalAdapter.ViewHolder holder, int position) {
        HospitalModel hospital = hospitalList.get(position);

        holder.name.setText(hospital.getName());
        holder.address.setText(hospital.getAddress()); // ✅ Bind address
        holder.rating.setText("★ " + hospital.getRating() + " (" + hospital.getRatingCount() + " reviews)");
        holder.distance.setText(hospital.getDistance() + " km away");

        // ✅ Pass hospital object to ImageSliderAdapter
        List<String> imageUrls = hospital.getImageUrls();
        if (imageUrls == null) {
            imageUrls = new java.util.ArrayList<>(); // 👈 empty list instead of null
        }

        ImageSliderAdapter imageSliderAdapter = new ImageSliderAdapter(context, imageUrls, hospital);
        holder.viewPager.setAdapter(imageSliderAdapter);

        if (!imageUrls.isEmpty()) {
            holder.startAutoScroll(imageUrls);
        }

        // ✅ Card click to open details
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, HospitalDetailsActivity.class);
            intent.putExtra("id", hospital.getId());
            intent.putExtra("name", hospital.getName());
            intent.putExtra("address", hospital.getAddress()); // ✅ Pass address
            intent.putExtra("logoUrl", hospital.getLogoUrl()); // ✅ Pass logoUrl
            intent.putExtra("rating", hospital.getRating());
            intent.putExtra("ratingCount", hospital.getRatingCount());
            intent.putExtra("distance", hospital.getDistance());
            context.startActivity(intent);
        });
    }

    @Override
    public void onViewRecycled(@NonNull HospitalAdapter.ViewHolder holder) {
        super.onViewRecycled(holder);
        holder.stopAutoScroll();
    }

    @Override
    public int getItemCount() {
        return hospitalList.size();
    }

    // In HospitalAdapter.java
    public void updateFullList(List<HospitalModel> newList) {
        hospitalListFull.clear();
        hospitalListFull.addAll(newList);
    }

}
