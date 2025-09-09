package in.com.bookmydoc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide; // For loading images from a URL

import java.util.ArrayList;
import java.util.List;
import in.com.bookmydoc.R;
import in.com.bookmydoc.model.Specialty;

public class SpecialtyAdapter extends RecyclerView.Adapter<SpecialtyAdapter.SpecialtyViewHolder> {

    private final Context context;
    private final List<Specialty> specialtyList;
    private final List<Specialty> specialtyListFull;
    private final OnSpecialtyClickListener listener;

    public interface OnSpecialtyClickListener {
        void onSpecialtyClick(Specialty specialty);
    }

    public SpecialtyAdapter(Context context, List<Specialty> specialtyList, OnSpecialtyClickListener listener) {
        this.context = context;
        this.specialtyList = specialtyList;
        this.specialtyListFull = new ArrayList<>(specialtyList);
        this.listener = listener;
    }

    @NonNull
    @Override
    public SpecialtyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_specialty, parent, false);
        return new SpecialtyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SpecialtyViewHolder holder, int position) {
        Specialty specialty = specialtyList.get(position);
        holder.bind(specialty, listener);
    }

    @Override
    public int getItemCount() {
        return specialtyList.size();
    }

    public void filter(String query) {
        specialtyList.clear();
        if (query == null || query.trim().isEmpty()) {
            specialtyList.addAll(specialtyListFull);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (Specialty item : specialtyListFull) {
                if (item.getName() != null && item.getName().toLowerCase().contains(lowerCaseQuery)) {
                    specialtyList.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    public boolean isEmpty() {
        return specialtyList.isEmpty();
    }

    public void updateData(List<Specialty> newList) {
        specialtyList.clear();
        specialtyList.addAll(newList);
        specialtyListFull.clear();
        specialtyListFull.addAll(newList);
        notifyDataSetChanged();
    }
    static class SpecialtyViewHolder extends RecyclerView.ViewHolder {
        ImageView specialtyIcon;
        TextView specialtyName;

        public SpecialtyViewHolder(@NonNull View itemView) {
            super(itemView);
            specialtyIcon = itemView.findViewById(R.id.iv_specialty_icon);
            specialtyName = itemView.findViewById(R.id.tv_specialty_name);
        }

        public void bind(final Specialty specialty, final OnSpecialtyClickListener listener) {
            specialtyName.setText(specialty.getName()); // Assuming getName() exists

            // Assuming getIconUrl() exists. If you use local drawables, you'll need a different approach.
            Glide.with(itemView.getContext())
                    .load(specialty.getImageUrl())
                    .placeholder(R.drawable.ic_plus_sthes) // A default icon
                    .into(specialtyIcon);

            itemView.setOnClickListener(v -> listener.onSpecialtyClick(specialty));
        }
    }
}