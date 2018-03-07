package pl.droidevs.books.book;

import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.view.View;

public class BookPageTransformer implements ViewPager.PageTransformer {
    @Override
    public void transformPage(@NonNull View page, float position) {
        if (position == 0) {
            page.setAlpha(1);
            page.setTranslationX(0);
        } else if (position < 0 && position > -1) {
            float alpha = 1 - Math.abs(position);
            float desiredTransitionX = -(page.getWidth() * position * 0.9f);

            page.setAlpha(alpha);
            page.setTranslationX(desiredTransitionX);
        } else if (position > 0 && position < 1) {
            float alpha = 1 - position;
            float desiredTransitionX = -(page.getWidth() * position * 0.9f);

            page.setAlpha(alpha);
            page.setTranslationX(desiredTransitionX);
        } else {
            page.setAlpha(0);
            page.setTranslationX(0);
        }
    }
}
