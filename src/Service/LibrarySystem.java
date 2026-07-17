package Service;

import Model.*;
import Notification.EmailNotification;
import Notification.Notification;
import Notification.SMSNotification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * The heart of the app: keeps the books, members, and loans in sync.
 * searchBook(...) has a couple of overloads for the different search screens.
 */
public class LibrarySystem {

    private String libraryName;
    private List<Book> books;
    private List<Member> members;
    private List<Loan> loans;
    private int loanCounter = 1;
    private int copyCounter = 1;

    public LibrarySystem(String libraryName) {
        this.libraryName = libraryName;
        this.books = new ArrayList<>();
        this.members = new ArrayList<>();
        this.loans = new ArrayList<>();
    }

    public String getLibraryName() { return libraryName; }
    public List<Book> getBooks() { return books; }
    public List<Member> getMembers() { return members; }
    public List<Loan> getLoans() { return loans; }

    public void addBook(Book book) {
        books.add(book);
    }

    // Adds a book along with the given number of copies of the chosen format.
    public Book addBook(String bookId, String title, String isbn, int year, String authorName,
                        String categoryName, BookCopy.MediaType mediaType, int numberOfCopies) {
        Author author = new Author("A-" + bookId, authorName, "", "");
        Category category = new Category("C-" + bookId, categoryName, "");
        Book book = new Book(bookId, title, isbn, year, author, category);
        String condition = mediaType == BookCopy.MediaType.EBOOK ? "Digital" : "Good";
        for (int i = 0; i < numberOfCopies; i++) {
            String copyId = "BC" + String.format("%03d", copyCounter++);
            book.addCopy(new BookCopy(copyId, condition, mediaType));
        }
        books.add(book);
        return book;
    }

    public void registerMember(Member member) {
        members.add(member);
        notifyMember(member, "Welcome to " + libraryName + "!");
    }

    public List<Book> searchBook(String title) {
        List<Book> results = new ArrayList<>();
        for (Book book : books) {
            if (book.getTitle().toLowerCase().contains(title.toLowerCase())) {
                results.add(book);
            }
        }
        return results;
    }

    public List<Book> searchBook(String title, String authorName) {
        List<Book> results = new ArrayList<>();
        for (Book book : books) {
            boolean titleMatch = book.getTitle().toLowerCase().contains(title.toLowerCase());
            boolean authorMatch = book.getAuthor().getAuthorName().toLowerCase().contains(authorName.toLowerCase());
            if (titleMatch && authorMatch) {
                results.add(book);
            }
        }
        return results;
    }

    public Loan borrowBook(String memberId, String copyId) {
        return borrowBook(memberId, copyId, LocalDate.now());
    }

    public Loan borrowBook(String memberId, String copyId, LocalDate borrowDate) {
        Member member = findMember(memberId);
        if (member == null) throw new IllegalArgumentException("Member not found: " + memberId);
        if (!member.canBorrow()) throw new IllegalStateException("Member cannot borrow more books.");

        BookCopy copy = findCopy(copyId);
        if (copy == null) throw new IllegalArgumentException("Book copy not found: " + copyId);
        if (!copy.isAvailable()) throw new IllegalStateException("Book copy is not available.");

        Book book = null;
        for (Book b : books) {
            if (b.getCopies().contains(copy)) {
                book = b;
                break;
            }
        }

        if (copy.isEbook()) {
            copy.borrowCopy(borrowDate.plusDays(member.getLoanPeriodDays()));
        } else {
            copy.borrowCopy();
        }

        String loanId = "L" + String.format("%03d", loanCounter++);
        Loan loan = new Loan(loanId, member, book, copy, borrowDate);
        loans.add(loan);
        member.borrowBook(loan);
        notifyMember(member, buildBorrowMessage(book, copy, loan));
        return loan;
    }

    // Physical copies tell the member to collect from the library;
    // e-books grant a licensed reading window until the due date.
    private String buildBorrowMessage(Book book, BookCopy copy, Loan loan) {
        String title = book != null ? book.getTitle() : "Unknown Book";
        if (copy.isEbook()) {
            return String.format(
                    "E-book '%s' (%s) borrowed successfully. Per licensing terms, you may read it " +
                            "until %s. Please return the license by that date.",
                    title, copy.getCopyId(), loan.getDueDate());
        }
        return String.format(
                "Physical copy '%s' of '%s' borrowed successfully. Please visit %s to collect " +
                        "your copy. It is due back on %s.",
                copy.getCopyId(), title, libraryName, loan.getDueDate());
    }

    public List<BookCopy> getAvailableCopies(String bookId) {
        Book book = findBook(bookId);
        List<BookCopy> copies = new ArrayList<>();
        if (book == null) return copies;
        for (BookCopy copy : book.getCopies()) {
            if (copy.isAvailable()) copies.add(copy);
        }
        return copies;
    }

    public List<Loan> getCurrentLoans(String memberId) {
        Member member = findMember(memberId);
        return member != null ? member.getCurrentLoans() : new ArrayList<>();
    }

    public List<Loan> getLoanHistory(String memberId) {
        Member member = findMember(memberId);
        return member != null ? member.getLoanHistory() : new ArrayList<>();
    }

    public Loan returnBook(String loanId) {
        Loan loan = findLoan(loanId);
        if (loan == null) throw new IllegalArgumentException("Loan not found: " + loanId);
        loan.returnBook();
        loan.getMember().returnBook(loan);
        notifyMember(loan.getMember(), "Returned loan " + loanId + " successfully.");
        return loan;
    }

    public Member findMember(String memberId) {
        for (Member member : members) {
            if (member.getMemberId().equalsIgnoreCase(memberId)) return member;
        }
        return null;
    }

    public Book findBook(String bookId) {
        for (Book book : books) {
            if (book.getBookId().equalsIgnoreCase(bookId)) return book;
        }
        return null;
    }

    public Loan findLoan(String loanId) {
        for (Loan loan : loans) {
            if (loan.getLoanId().equalsIgnoreCase(loanId)) return loan;
        }
        return null;
    }

    public BookCopy findCopy(String copyId) {
        for (Book book : books) {
            for (BookCopy copy : book.getCopies()) {
                if (copy.getCopyId().equalsIgnoreCase(copyId)) return copy;
            }
        }
        return null;
    }

    public String generateReport() {
        int available = 0, totalCopies = 0, physicalCopies = 0, ebookCopies = 0;
        for (Book book : books) {
            for (BookCopy copy : book.getCopies()) {
                totalCopies++;
                if (copy.isPhysical()) physicalCopies++;
                else ebookCopies++;
                if (copy.isAvailable()) available++;
            }
        }

        int activeLoans = 0, overdue = 0;
        for (Loan loan : loans) {
            if (!loan.isReturned()) {
                activeLoans++;
                if (loan.isOverdue()) overdue++;
            }
        }

        return String.format("""
                ===== %s Report =====
                Books        : %d
                Copies       : %d (available: %d)
                  - Physical : %d
                  - E-Books  : %d
                Members      : %d
                Active loans : %d
                Overdue      : %d
                ========================
                """,
                libraryName, books.size(), totalCopies, available,
                physicalCopies, ebookCopies, members.size(), activeLoans, overdue);
    }

    // Saves the message to the member's inbox and fires the (silent) email/SMS channels.
    private void notifyMember(Member member, String message) {
        member.addNotification(message);
        new EmailNotification(member.getEmail()).sendNotification(message);
        new SMSNotification(member.getPhoneNumber()).sendNotification(message);
    }

    public void broadcastAnnouncement(String author, String message) {
        String note = String.format("[Announcement by %s]: %s", author, message);
        for (Member m : members) {
            m.addNotification(note);
        }
    }
}
