package pl.droidevs.books.book;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import javax.inject.Inject;

import pl.droidevs.books.model.Book;
import pl.droidevs.books.model.BookId;
import pl.droidevs.books.repository.BookRepository;

public class BookViewModel extends ViewModel {

    private final BookRepository bookRepository;

    @Inject
    BookViewModel(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    LiveData<Book> getBook(BookId bookId) {
        return bookRepository.fetchBy(bookId);
    }

    LiveData<List<Book>> getBooks() {
        return bookRepository.fetchAll();
    }

    public float masterTitleTextSize;
    public float masterAuthorTextSize;
}
