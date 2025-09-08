package in.com.bookmydoc.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import in.com.bookmydoc.R;

public class PromoBannerAdapter extends RecyclerView.Adapter<PromoBannerAdapter.BannerViewHolder> {

    private final List<Integer> bannerImageResIds; // Using drawable resource IDs for simplicity

    public PromoBannerAdapter(List<Integer> bannerImageResIds) {
        this.bannerImageResIds = bannerImageResIds;
    }

    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_promo_banner, parent, false);
        return new BannerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
        int imageResId = bannerImageResIds.get(position);
        Glide.with(holder.imageViewBanner.getContext())
                .load(imageResId)
                .into(holder.imageViewBanner);
    }

    @Override
    public int getItemCount() {
        return bannerImageResIds.size();
    }

    static class BannerViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewBanner;
        BannerViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewBanner = itemView.findViewById(R.id.imageViewBanner);
        }
    }
}