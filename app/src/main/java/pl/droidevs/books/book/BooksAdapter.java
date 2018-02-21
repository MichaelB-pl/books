package pl.droidevs.books.book;

import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;

/**
 * Created by micha on 21.02.2018.
 */

public class BooksAdapter extends PagerAdapter {
    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return false;
    }
}
