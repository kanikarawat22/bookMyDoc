package in.com.bookmydoc.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import in.com.bookmydoc.R;
import in.com.bookmydoc.activities.BookingActivity;
import in.com.bookmydoc.activities.DoctorDetailActivity;
import in.com.bookmydoc.model.Doctor;

public class DoctorAdapter extends RecyclerView.Adapter<DoctorAdapter.DoctorViewHolder> {

    private Context context;
    private List<Doctor> doctorList;
    private List<Doctor> fullList;
    private OnItemClickListener listener;
    public interface OnItemClickListener {
        void onItemClick(Doctor doctor);
    }// ✅ For search & filter

    public DoctorAdapter(Context context, List<Doctor> doctorList, OnItemClickListener listener) {
        this.context = context;
        this.doctorList = doctorList;
        this.fullList = new ArrayList<>(doctorList);
        this.listener = listener; // ✅ correctly assigned
    }


    @NonNull
    @Override
    public DoctorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_doctor, parent, false);
        return new DoctorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DoctorViewHolder holder, int position) {
        Doctor doctor = doctorList.get(position);

        holder.textViewName.setText(doctor.getName());
        holder.textViewSpecialty.setText(doctor.getSpecialty());
        holder.textViewHospital.setText(doctor.getHospital());
        holder.textViewExperience.setText(doctor.getExperience());
        holder.textViewRating.setText(String.format("%.1f ★", doctor.getRating()));
        holder.textViewFee.setText("₹ " + doctor.getFee());


        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(doctor);
            }
        });

        // Load profile image using Glide
        Glide.with(context)
                .load(doctor.getProfileImageUrl())
                .placeholder(R.drawable.profile_lu)
                .error(R.drawable.profile_lu)
                .into(holder.imageViewDoctor);

        // ✅ Book button click

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DoctorDetailActivity.class);
            intent.putExtra("doctorId", doctor.getDocid() != null ? doctor.getDocid() : "");
            intent.putExtra("name", doctor.getName() != null ? doctor.getName() : "N/A");
            intent.putExtra("specialty", doctor.getSpecialty() != null ? doctor.getSpecialty() : "N/A");
            intent.putExtra("hospital", doctor.getHospital() != null ? doctor.getHospital() : "N/A");
            intent.putExtra("experience", doctor.getExperience() != null ? doctor.getExperience() : "N/A");
            intent.putExtra("rating", (float) doctor.getRating());
            intent.putExtra("fee", doctor.getFee());
            intent.putExtra("profileImageUrl", doctor.getProfileImageUrl() != null ? doctor.getProfileImageUrl() : "");
            context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        });


        // ✅ Book button ab DoctorDetailActivity ke andar hoga
        holder.btnBook.setVisibility(View.GONE);

        //holder.btnBook.setOnClickListener(v -> {
          //  v.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100).withEndAction(() -> {
            //    v.animate().scaleX(1f).scaleY(1f).setDuration(100).start();

              //  Intent intent = new Intent(context, BookingActivity.class);
                //intent.putExtra("doctorId", doctor.getDocid());
                //intent.putExtra("name", doctor.getName());
                //intent.putExtra("specialty", doctor.getSpecialty());
                //intent.putExtra("fee", doctor.getFee());
                //context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
           // }).start();
        //});


       /* holder.btnBook.setOnClickListener(v ->
                Toast.makeText(context, "Booking " + doctor.getName(), Toast.LENGTH_SHORT).show()
        );*/
    }

    @Override
    public int getItemCount() {
        return doctorList == null ? 0 : doctorList.size();
    }

    // ✅ Filtering function (by name & specialty)
    public void filter(String query, String specialty, String hospital) {
        doctorList.clear();

        for (Doctor doc : fullList) {
            boolean matchesName = doc.getName().toLowerCase().contains(query.toLowerCase());
            boolean matchesSpecialty = specialty.equals("All") || doc.getSpecialty().equalsIgnoreCase(specialty);
            boolean matchesHospital = hospital.equals("All") || doc.getHospital().equalsIgnoreCase(hospital);

            if (matchesName && matchesSpecialty && matchesHospital) {
                doctorList.add(doc);
            }
        }
        notifyDataSetChanged();
    }


    public static class DoctorViewHolder extends RecyclerView.ViewHolder {

        CircleImageView imageViewDoctor;
        TextView textViewName, textViewSpecialty, textViewHospital,
                textViewExperience, textViewRating, textViewFee;
        Button btnBook;

        public DoctorViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewDoctor = itemView.findViewById(R.id.imageViewDoctor);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewSpecialty = itemView.findViewById(R.id.textViewSpecialty);
            textViewHospital = itemView.findViewById(R.id.textViewClinic);
            textViewExperience = itemView.findViewById(R.id.textViewExperience);
            textViewRating = itemView.findViewById(R.id.textViewRating);
            textViewFee = itemView.findViewById(R.id.textViewFee); // ✅ new
            btnBook = itemView.findViewById(R.id.btnBook); // ✅ new
        }
    }

    public void updateList(List<Doctor> newList) {
        this.doctorList = newList;
        notifyDataSetChanged();
    }

}