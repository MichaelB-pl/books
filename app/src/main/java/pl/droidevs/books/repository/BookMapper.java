package pl.droidevs.books.repository;

import android.arch.core.util.Function;

import java.util.ArrayList;
import java.util.List;

import pl.droidevs.books.entity.BookEntity;
import pl.droidevs.books.model.Book;
import pl.droidevs.books.model.BookId;

class BookMapper {

    static final Function<List<BookEntity>, List<Book>> entitiesToBooksFunction =
            input -> {
                List<Book> books = new ArrayList<>(input.size());

                for (BookEntity bookEntity : input) {
                    books.add(BookMapper.getBook(bookEntity));
                }

                return books;
            };

    private BookMapper() {
    }

    static Book getBook(BookEntity entity) {
        if (entity == null) {
            return null;
        }

        final Book book = new Book(
                BookId.of(entity.getId()),
                entity.getTitle(),
                entity.getAuthor(),
                Book.Category.valueOf(entity.getCategory()));
        book.setDescription(entity.getDescription());
        book.setImageUrl(entity.getImageUrl());

        return book;
    }

    static BookEntity getBookEntity(Book book) {
        final BookEntity entity = new BookEntity();
        entity.setAuthor(book.getAuthor());
        entity.setCategory(book.getCategory().toString());
        entity.setDescription(book.getDescription());
        entity.setTitle(book.getTitle());
        entity.setImageUrl(book.getImageUrl());

        if (book.getId() != null) {
            entity.setId(Integer.parseInt(book.getId().getValue()));
        }

        return entity;
    }

    static int getBookEntityIdFromBookId(BookId bookId) {
        return Integer.parseInt(bookId.getValue());
    }
}
