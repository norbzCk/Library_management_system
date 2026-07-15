package Service;

import Model.Author;
import Model.Book;
import Model.BookCopy;
import Model.Category;
import Model.Loan;
import Model.Member;
import Notification.EmailNotification;
import Notification.Notification;
import Notification.SMSNotification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Central service: manages books, members, and loans.
 * Demonstrates method overloading via searchBook(...) variants.
 */
public class LibrarySystem {

    private String libraryName;
    private List<Book> books;
    private List<Member> members;
    private List<Loan> loans;
    private int loanCounter;
    private int copyCounter;

    public LibrarySystem(String libraryName) {
        this.libraryName = libraryName;
        this.books = new ArrayList<>();
        this.members = new ArrayList<>();
        this.loans = new ArrayList<>();
        this.loanCounter = 1;
        this.copyCounter = 1;
    }

    public String getLibraryName() {
        return libraryName;
    }

    public List<Book> getBooks() {
        return books;
    }

    public List<Member> getMembers() {
        return members;
    }

    public List<Loan> getLoans() {
        return loans;
    }

    public void addBook(Book book) {
        books.add(book);
    }

    public Book addBook(String bookId, String title, String isbn, int year,
                        String authorName, String categoryName) {
        Author author = new Author("A-" + bookId, authorName, "", "");
        Category category = new Category("C-" + bookId, categoryName, "");
        Book book = new Book(bookId, title, isbn, year, author, category);
        String copyId = "BC" + String.format("%03d", copyCounter++);
        book.addCopy(new BookCopy(copyId, "Good"));
        books.add(book);
        return book;
    }

    public void registerMember(Member member) {
        members.add(member);
        notifyMember(member, "Welcome to " + libraryName + "!");
    }

    /** Overloaded search — by title only. */
    public List<Book> searchBook(String title) {
        List<Book> results = new ArrayList<>();
        for (Book book : books) {
            if (book.getTitle().toLowerCase().contains(title.toLowerCase())) {
                results.add(book);
            }
        }
        return results;
    }

    /** Overloaded search — by title and author name. */
    public List<Book> searchBook(String title, String authorName) {
        List<Book> results = new ArrayList<>();
        for (Book book : books) {
            boolean titleMatch = book.getTitle().toLowerCase().contains(title.toLowerCase());
            boolean authorMatch = book.getAuthor().getAuthorName()
                    .toLowerCase().contains(authorName.toLowerCase());
            if (titleMatch && authorMatch) {
                results.add(book);
            }
        }
        return results;
    }

    public Loan borrowBook(String memberId, String copyId) {
        Member member = findMember(memberId);
        if (member == null) {
            throw new IllegalArgumentException("Member not found: " + memberId);
        }
        if (!member.canBorrow()) {
            throw new IllegalStateException("Member cannot borrow more books.");
        }

        BookCopy copy = findCopy(copyId);
        if (copy == null) {
            throw new IllegalArgumentException("Book copy not found: " + copyId);
        }
        if (!copy.isAvailable()) {
            throw new IllegalStateException("Book copy is not available.");
        }

        copy.borrowCopy();
        String loanId = "L" + String.format("%03d", loanCounter++);
        Loan loan = new Loan(loanId, member, copy, LocalDate.now());
        loans.add(loan);
        member.borrowBook(loan);
        notifyMember(member, "Borrowed copy " + copyId + ". Due: " + loan.getDueDate());
        return loan;
    }

    public Loan returnBook(String loanId) {
        Loan loan = findLoan(loanId);
        if (loan == null) {
            throw new IllegalArgumentException("Loan not found: " + loanId);
        }
        loan.returnBook();
        loan.getMember().returnBook(loan);
        notifyMember(loan.getMember(), "Returned loan " + loanId + " successfully.");
        return loan;
    }
//                    case 11 -> demoStaffPolymorphism();
    //                    case 11 -> demoStaffPolymorphism();

    public Member findMember(String memberId) {
        for (Member member : members) {
            if (member.getMemberId().equalsIgnoreCase(memberId)) {
                return member;    //                    case 11 -> demoStaffPolymorphism();

            }
        }
        return null;
    }
//                    case 11 -> demoStaffPolymorphism();

    public Book findBook(String bookId) {
        for (Book book : books) {
            if (book.getBookId().equalsIgnoreCase(bookId)) {
                return book;
            }
        }
        return null;
    }

    public Loan findLoan(String loanId) {
        for (Loan loan : loans) {
            if (loan.getLoanId().equalsIgnoreCase(loanId)) {
                return loan;
            }
        }
        return null;
    }

    public BookCopy findCopy(String copyId) {
        for (Book book : books) {
            for (BookCopy copy : book.getCopies()) {
                if (copy.getCopyId().equalsIgnoreCase(copyId)) {
                    return copy;
                }
            }
        }
        return null;
    }

    public String generateReport() {
        int available = 0;
        int totalCopies = 0;
        for (Book book : books) {
            for (BookCopy copy : book.getCopies()) {
                totalCopies++;
                if (copy.isAvailable()) {
                    available++;
                }
            }
        }

        int activeLoans = 0;
        int overdue = 0;
        for (Loan loan : loans) {
            if (!loan.isReturned()) {
                activeLoans++;
                if (loan.isOverdue()) {
                    overdue++;
                }
            }
        }

        return String.format("""
                ===== %s Report =====
                Books        : %d
                Copies       : %d (available: %d)
                Members      : %d
                Active loans : %d
                Overdue      : %d
                ========================
                """,
                libraryName,
                books.size(),
                totalCopies,
                available,
                members.size(),
                activeLoans,
                overdue);
    }

    private void notifyMember(Member member, String message) {
        Notification email = new EmailNotification(member.getEmail());
        Notification sms = new SMSNotification(member.getPhoneNumber());
        email.sendNotification(message);
        sms.sendNotification(message);
    }
}
