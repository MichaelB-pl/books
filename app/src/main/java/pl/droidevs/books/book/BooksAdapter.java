package pl.droidevs.books.book;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

import pl.droidevs.books.R;
import pl.droidevs.books.model.Book;

/**
 * Created by micha on 21.02.2018.
 */

public class BooksAdapter extends FragmentPagerAdapter {
    private final FragmentManager fragmentManager;
    private final Context context;

    private List<Book> books = new ArrayList<>();

    private BookFragment currentFragment;

    /*public BooksAdapter(FragmentManager fragmentManager, Context context) {
        super(fragmentManager);
        this.fragmentManager = fragmentManager;
        this.context = context;
    }*/

    public BooksAdapter(FragmentManager fragmentManager, Context context, int listSize) {
        super(fragmentManager);
        this.fragmentManager = fragmentManager;
        this.context = context;

        List<Book> books = new ArrayList<>();
        for (int i = 0; i < listSize; i++) {
            Book book = new Book("", "", Book.Category.KIDS);
            books.add(book);
        }
        this.books = books;
    }


    @Override
    public Fragment getItem(int position) {
        return BookFragment.newInstance(position);
    }

    @Override
    public int getCount() {
        return books.size();
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        if (getCurrentFragment() != object) {
            currentFragment = ((BookFragment) object);
        }
        Window window = ((AppCompatActivity) context).getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        int statusBarColor;
        if (currentFragment.getStatusBarColor() != 0) {
            statusBarColor = currentFragment.getStatusBarColor();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            statusBarColor = context.getResources().getColor(R.color.colorPrimaryDark, null);
        } else {
            statusBarColor = context.getResources().getColor(R.color.colorPrimaryDark);
        }
        window.setStatusBarColor(statusBarColor);
        super.setPrimaryItem(container, position, object);
    }

    public BookFragment getCurrentFragment() {
        return currentFragment;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
        notifyDataSetChanged();
    }

    @Nullable
    public Book getBook(int position) {
        if (position >= books.size()) {
            return null;
        }
        return books.get(position);
    }
}
