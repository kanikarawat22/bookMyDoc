package in.com.bookmydoc.activities;

import android.view.View;
import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;

public class DepthPageTransformer implements ViewPager2.PageTransformer {
    @Override
    public void transformPage(@NonNull View page, float position) {
        if (position <= 0) { // [-1,0]
            page.setAlpha(1);
            page.setTranslationX(0);
            page.setScaleX(1);
            page.setScaleY(1);
        } else if (position <= 1) { // (0,1]
            page.setAlpha(1 - position);
            page.setTranslationX(page.getWidth() * -position);
            float scale = 0.75f + (1 - 0.75f) * (1 - position);
            page.setScaleX(scale);
            page.setScaleY(scale);
        }
    }
}
