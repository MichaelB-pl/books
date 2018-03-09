package pl.droidevs.books.book;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.ColorUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.AndroidInjection;
import pl.droidevs.books.R;
import pl.droidevs.books.model.Book;
import pl.droidevs.books.model.BookId;

/**
 * Created by micha on 22.02.2018.
 */

public class BookActivity extends AppCompatActivity {
    private static final String EXTRAS_BOOK_ID = "EXTRAS_BOOK_ID";
    public static final String EXTRAS_SELECTED_INDEX = "EXTRAS_SELECTED_INDEX";
    private static final String EXTRAS_LIST_SIZE = "EXTRAS_LIST_SIZE";

    /*public static final String EXTRAS_IMAGE_TRANSITION_NAME = "EXTRAS_IMAGE_TRANSITION_NAME";
    public static final String EXTRAS_TITLE_TRANSITION_NAME = "EXTRAS_TITLE_TRANSITION_NAME";
    public static final String EXTRAS_AUTHOR_TRANSITION_NAME = "EXTRAS_AUTHOR_TRANSITION_NAME";*/
    private static final String EXTRAS_SHARED_TITLE_TEXT_SIZE = "EXTRAS_SHARED_TITLE_TEXT_SIZE";
    private static final String EXTRAS_SHARED_AUTHOR_TEXT_SIZE = "EXTRAS_SHARED_AUTHOR_TEXT_SIZE";
    public static final String BUNDLE_EXTRAS = "BUNDLE_EXTRAS";

    private BookViewModel viewModel;
    private BooksAdapter adapter;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    //region Butter binding
    @BindView(R.id.vp_books)
    ViewPager vpBooks;
    //endregion

    public static Intent createBookIntent(@NonNull Context context, @NonNull BookId bookId,
                                          @NonNull Integer index, @NonNull Integer listSize,
                                          @NonNull View view) {
        Intent intent = new Intent(context, BookActivity.class);

        intent.putExtra(EXTRAS_BOOK_ID, bookId);
        intent.putExtra(EXTRAS_SELECTED_INDEX, index);
        intent.putExtra(EXTRAS_LIST_SIZE, listSize);
        intent.putExtra(BUNDLE_EXTRAS, createAnimationBundle(view));
        return intent;
    }

    private static Bundle createAnimationBundle(View view) {
        TextView tvTitle = view.findViewById(R.id.tv_book_title);
        TextView tvAuthor = view.findViewById(R.id.tv_book_author);

        Bundle animationBundle = new Bundle();
        /*animationBundle.putString(EXTRAS_IMAGE_TRANSITION_NAME, view.findViewById(R.id.iv_book).getTransitionName());
        animationBundle.putString(EXTRAS_TITLE_TRANSITION_NAME, view.findViewById(R.id.tv_book_title).getTransitionName());
        animationBundle.putString(EXTRAS_AUTHOR_TRANSITION_NAME, view.findViewById(R.id.tv_book_author).getTransitionName());*/
        animationBundle.putFloat(EXTRAS_SHARED_TITLE_TEXT_SIZE, tvTitle.getTextSize());
        animationBundle.putFloat(EXTRAS_SHARED_AUTHOR_TEXT_SIZE, tvAuthor.getTextSize());
        return animationBundle;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);
        postponeEnterTransition();

        AndroidInjection.inject(this);
        ButterKnife.bind(this);

        setupViewModel();
        int selectedIndex;
        int listSize;

        if (savedInstanceState != null) {
            selectedIndex = savedInstanceState.getInt(EXTRAS_SELECTED_INDEX);
            listSize = savedInstanceState.getInt(EXTRAS_LIST_SIZE);

            Bundle animationBundle = savedInstanceState.getBundle(BUNDLE_EXTRAS);
            viewModel.masterTitleTextSize = animationBundle.getFloat(EXTRAS_SHARED_TITLE_TEXT_SIZE);
            viewModel.masterAuthorTextSize = animationBundle.getFloat(EXTRAS_SHARED_AUTHOR_TEXT_SIZE);
        } else {
            selectedIndex = getIntent().getIntExtra(EXTRAS_SELECTED_INDEX, 0);
            listSize = getIntent().getIntExtra(EXTRAS_LIST_SIZE, 0);
        }

        adapter = new BooksAdapter(getSupportFragmentManager(), this, listSize);

        vpBooks.setAdapter(adapter);
        vpBooks.setPageTransformer(false, new BookPageTransformer());
        vpBooks.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                if (positionOffset < 1f && position > 0f) {
                    BookFragment leftFragment = getFragmentByPosition(position);
                    BookFragment rightFragment = getFragmentByPosition(position + 1);
                    if (leftFragment != null && rightFragment != null) {
                        int color1 = leftFragment.getStatusBarColor();
                        int color2 = rightFragment.getStatusBarColor();
                        int desiredColor = ColorUtils.blendARGB(color1, color2, positionOffset);
                        getWindow().setStatusBarColor(desiredColor);
                    }
                }
            }
        });
        vpBooks.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                if (vpBooks.getChildCount() > 0) {
                    vpBooks.removeOnLayoutChangeListener(this);
                    startPostponedEnterTransition();
                    adapter.getCurrentFragment().setEnterTransition();
                }
            }
        });
        /*vpBooks.postDelayed(() -> {
            ActivityCompat.startPostponedEnterTransition(this);
            adapter.getCurrentFragment().setEnterTransition();
        }, 10);*/
        vpBooks.setCurrentItem(selectedIndex);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(EXTRAS_SELECTED_INDEX, vpBooks.getCurrentItem());
        outState.putInt(EXTRAS_LIST_SIZE, adapter.getCount());

        Bundle animationBundle = new Bundle();
        animationBundle.putFloat(EXTRAS_SHARED_TITLE_TEXT_SIZE, viewModel.masterTitleTextSize);
        animationBundle.putFloat(EXTRAS_SHARED_AUTHOR_TEXT_SIZE, viewModel.masterAuthorTextSize);
        outState.putBundle(BUNDLE_EXTRAS, animationBundle);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
//        adapter.getCurrentFragment().addAnimationBundle(intent);
        intent.putExtra(EXTRAS_SELECTED_INDEX, vpBooks.getCurrentItem());
        setResult(RESULT_OK, intent);

        adapter.getCurrentFragment().setExitTransition();
        super.onBackPressed();
    }

    private void setupViewModel() {
        viewModel = ViewModelProviders
                .of(this, viewModelFactory)
                .get(BookViewModel.class);
        viewModel.getBooks().observe(this, books -> adapter.setBooks(books));
    }

    public Book getBook(int position) {
        return adapter.getBook(position);
    }

    private BookFragment getFragmentByPosition(int position) {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        String desiredTag = "index_" + position;
        for (Fragment fragment : fragments) {
            if (fragment instanceof BookFragment) {
                BookFragment bookFragment = (BookFragment) fragment;
                String tag = (bookFragment).TAG;
                if (tag.equals(desiredTag)) {
                    return bookFragment;
                }
            }
        }
        return null;
    }
}
