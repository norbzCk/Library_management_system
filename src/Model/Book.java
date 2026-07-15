package Model;

import java.util.ArrayList;
import java.util.List;

public class Book {

    private String bookId;
    private String title;
    private String isbn;
    private int publicationYear;
    private Author author;
    private Category category;
    private List<BookCopy> copies;

    public Book(String bookId,
                String title,
                String isbn,
                int publicationYear,
                Author author,
                Category category) {
        this.bookId = bookId;
        this.title = title;
        this.isbn = isbn;
        this.publicationYear = publicationYear;
        this.author = author;
        this.category = category;
        this.copies = new ArrayList<>();
    }

    public String getBookId() {
        return bookId;
    }

    public String getTitle() {
        return title;
    }

    public String getIsbn() {
        return isbn;
    }

    public int getPublicationYear() {
        return publicationYear;
    }

    public Author getAuthor() {
        return author;
    }

    public Category getCategory() {
        return category;
    }

    public List<BookCopy> getCopies() {
        return copies;
    }

    public void addCopy(BookCopy copy) {
        copies.add(copy);
    }

    public boolean hasAvailableCopy() {
        for (BookCopy copy : copies) {
            if (copy.isAvailable()) {
                return true;
            }
        }
        return false;
    }

    public BookCopy getAvailableCopy() {
        for (BookCopy copy : copies) {
            if (copy.isAvailable()) {
                return copy;
            }
        }
        return null;
    }

    public String getBookDetails() {
        return String.format("""
                -----------------------------------
                Book ID : %s
                Title   : %s
                ISBN    : %s
                Year    : %d
                Author  : %s
                Category: %s
                Copies  : %d
                Available: %s
                -----------------------------------
                """,
                bookId,
                title,
                isbn,
                publicationYear,
                author,
                category,
                copies.size(),
                hasAvailableCopy() ? "Yes" : "No");
    }

    @Override
    public String toString() {
        return title + " by " + author;
    }
}
