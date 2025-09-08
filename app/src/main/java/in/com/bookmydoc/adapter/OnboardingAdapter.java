package in.com.bookmydoc.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import in.com.bookmydoc.R;
import in.com.bookmydoc.model.OnboardingItem;

import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.airbnb.lottie.LottieAnimationView;
import java.util.List;

public class OnboardingAdapter extends RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder> {

    private final List<OnboardingItem> items;

    public OnboardingAdapter(List<OnboardingItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public OnboardingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.onboarding_item, parent, false);
        return new OnboardingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OnboardingViewHolder holder, int position) {
        OnboardingItem item = items.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class OnboardingViewHolder extends RecyclerView.ViewHolder {
        LottieAnimationView animationView;
        TextView title, desc;

        OnboardingViewHolder(@NonNull View itemView) {
            super(itemView);
            animationView = itemView.findViewById(R.id.lottie);
            title = itemView.findViewById(R.id.title);
            desc = itemView.findViewById(R.id.desc);
        }

        public void bind(OnboardingItem item) {
            animationView.setAnimation(item.animationResId);
            animationView.playAnimation();  // ensure it plays
            title.setText(item.title);
            desc.setText(item.desc);
        }


    }
}
