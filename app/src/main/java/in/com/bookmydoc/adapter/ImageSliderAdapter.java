package in.com.bookmydoc.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import in.com.bookmydoc.activities.HospitalDetailsActivity;
import in.com.bookmydoc.R;
import in.com.bookmydoc.model.HospitalModel;

public class ImageSliderAdapter extends RecyclerView.Adapter<ImageSliderAdapter.ImageViewHolder> {

    private List<String> imageUrls;
    private Context context;
    private HospitalModel hospital;

    public ImageSliderAdapter(Context context, List<String> imageUrls, HospitalModel hospital) {
        this.context = context;
        this.imageUrls = imageUrls;
        this.hospital = hospital;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ImageView imageView = new ImageView(context);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return new ImageViewHolder(imageView);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Glide.with(context)
                .load(imageUrls.get(position))
                .placeholder(R.drawable.profile_lu)
                .into((ImageView) holder.itemView);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, HospitalDetailsActivity.class);
            intent.putExtra("id", hospital.getId());
            intent.putExtra("name", hospital.getName());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return (imageUrls != null) ? imageUrls.size() : 0;}

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        public ImageViewHolder(@NonNull ImageView itemView) {
            super(itemView);
        }
    }
}
