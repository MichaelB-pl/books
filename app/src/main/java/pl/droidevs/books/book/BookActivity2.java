package pl.droidevs.books.book;

import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.BindView;
import pl.droidevs.books.R;
import pl.droidevs.books.model.BookId;

/**
 * Created by micha on 08.03.2018.
 */

public class BookActivity2 extends AppCompatActivity {
    private static final String EXTRAS_BOOK_ID = "EXTRAS_BOOK_ID";
    public static final String EXTRAS_SELECTED_INDEX = "EXTRAS_SELECTED_INDEX";
    private static final String EXTRAS_LIST_SIZE = "EXTRAS_LIST_SIZE";

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
        animationBundle.putFloat(EXTRAS_SHARED_TITLE_TEXT_SIZE, tvTitle.getTextSize());
        animationBundle.putFloat(EXTRAS_SHARED_AUTHOR_TEXT_SIZE, tvAuthor.getTextSize());
        return animationBundle;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);
        postponeEnterTransition();
    }
}
