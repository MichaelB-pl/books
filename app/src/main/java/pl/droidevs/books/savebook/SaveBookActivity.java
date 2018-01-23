package pl.droidevs.books.savebook;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import dagger.android.AndroidInjection;
import pl.droidevs.books.R;
import pl.droidevs.books.model.Book;
import pl.droidevs.books.model.BookId;
import pl.droidevs.books.removebook.RemoveBookDialogFragment;
import pl.droidevs.books.removebook.RemoveBookViewModel;

import static com.bumptech.glide.Priority.HIGH;

public class SaveBookActivity extends AppCompatActivity implements RemoveBookDialogFragment.OnRemoveListener {

    public static final String BOOK_ID_EXTRA = "book id";

    @BindView(R.id.addBookConstraintLayout)
    ConstraintLayout container;

    @BindView(R.id.coverImageUrlEditText)
    EditText coverUrlEditText;

    @BindView(R.id.coverImageView)
    ImageView coverImageView;

    @BindView(R.id.categorySpinner)
    Spinner categorySpinner;

    @BindView(R.id.titleEditText)
    EditText titleEditText;

    @BindView(R.id.authorEditText)
    EditText authorEditText;

    @BindView(R.id.descriptionEditText)
    EditText descriptionEditText;

    @BindView(R.id.saveButton)
    Button saveButton;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    SaveBookViewModel saveBookViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_book);

        AndroidInjection.inject(this);
        ButterKnife.bind(this);

        setupSpinner();
        setupViewModel();
    }

    private void setupSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getCategoryNames());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        categorySpinner.setAdapter(adapter);
    }

    private List<String> getCategoryNames() {
        Book.Category[] categories = Book.Category.values();
        List<String> categoryNames = new ArrayList<>();

        for (int i = 0; i < categories.length; i++) {
            categoryNames.add(categories[i].toString());
        }

        return categoryNames;
    }

    private void setupViewModel() {
        this.saveBookViewModel = ViewModelProviders
                .of(this, viewModelFactory)
                .get(SaveBookViewModel.class);

        saveBookViewModel.wasSavingSuccessful()
                .observe(this, wasSavingSuccessful -> {
                    if (wasSavingSuccessful) {
                        finish();
                    } else {
                        displaySnackBar(R.string.saving_book_error);
                    }
                });

        if (shouldDisplayEdit()) {
            setupForEdit();
        }
    }

    private void displaySnackBar(int messageResource) {
        Snackbar.make(container, messageResource, Snackbar.LENGTH_SHORT).show();
    }

    private boolean shouldDisplayEdit() {
        return getIntent().hasExtra(BOOK_ID_EXTRA);
    }

    private void setupForEdit() {
        saveBookViewModel.setBookId((BookId) getIntent().getSerializableExtra(BOOK_ID_EXTRA));
        saveBookViewModel.getBook().observe(this, bookObserver);

        saveButton.setText(R.string.edit_book);
    }

    private Observer<Book> bookObserver = book -> {

        if (book == null) {
            return;
        }

        this.titleEditText.setText(book.getTitle());
        this.authorEditText.setText(book.getAuthor());
        this.coverUrlEditText.setText(book.getImageUrl());
        this.descriptionEditText.setText(book.getDescription());
        this.categorySpinner.setSelection(getPositionInCategories(book.getCategory().toString()));

        setDataToViewModel();
    };

    private int getPositionInCategories(String category) {
        List<String> categories = getCategoryNames();

        for (int i = 0; i < categories.size(); i++) {

            if (categories.get(i).equals(category)) {
                return i;
            }
        }

        return 0;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if (shouldDisplayEdit()) {
            getMenuInflater().inflate(R.menu.edit_book_menu, menu);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.remove_book_item) {
            RemoveBookDialogFragment removeBookDialogFragment = RemoveBookDialogFragment.newInstance((BookId) getIntent().getSerializableExtra(BOOK_ID_EXTRA));
            removeBookDialogFragment.show(getSupportFragmentManager(), "");
        }

        return super.onOptionsItemSelected(item);
    }

    @OnTextChanged(R.id.coverImageUrlEditText)
    void onCoverUrlChanged() {
        String imageUrl = coverUrlEditText.getText().toString();

        if (this.saveBookViewModel.isCoverUrlValid(imageUrl)) {
            loadCoverImage(imageUrl);
            saveBookViewModel.setImageUrl(imageUrl);
        }
    }

    void loadCoverImage(String imageUrl) {
        Glide.with(this)
                .load(imageUrl)
                .apply(new RequestOptions()
                        .placeholder(R.drawable.ic_book)
                        .priority(HIGH))
                .into(coverImageView);
    }

    @OnClick(R.id.saveButton)
    void onSaveButtonClicked() {
        setDataToViewModel();

        if (this.saveBookViewModel.isDataValid()) {
            this.saveBookViewModel.saveBook();
        }
    }

    void setDataToViewModel() {
        this.saveBookViewModel.setTitle(this.titleEditText.getText().toString());
        this.saveBookViewModel.setAuthor(this.authorEditText.getText().toString());
        this.saveBookViewModel.setDescription(this.descriptionEditText.getText().toString());
        this.saveBookViewModel.setCategory(this.categorySpinner.getSelectedItem().toString());
    }

    @Override
    public void removeChosen() {
        RemoveBookViewModel removeBookViewModel = ViewModelProviders
                .of(this, viewModelFactory)
                .get(RemoveBookViewModel.class);
        removeBookViewModel.removeBook(saveBookViewModel.createBook());

        finish();
    }
}
