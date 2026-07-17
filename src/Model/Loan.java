package Model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * One borrowing of a specific copy by a member over a fixed period.
 * A fine is created here if the copy comes back late.
 */
public class Loan {
    public static final String ACTIVE = "Active";
    public static final String RETURNED = "Returned";

    private String loanId;
    private Member member;
    private Book book;
    private BookCopy bookCopy;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private String loanStatus;
    private Fine fine;

    public Loan(String loanId, Member member, Book book, BookCopy bookCopy, LocalDate borrowDate) {
        this(loanId, member, book, bookCopy, borrowDate, member.getLoanPeriodDays());
    }

    public Loan(String loanId, Member member, Book book, BookCopy bookCopy,
                LocalDate borrowDate, int loanPeriodDays) {
        this.loanId = loanId;
        this.member = member;
        this.book = book;
        this.bookCopy = bookCopy;
        this.borrowDate = borrowDate;
        this.dueDate = borrowDate.plusDays(loanPeriodDays);
        this.loanStatus = ACTIVE;
    }

    public String getLoanId() { return loanId; }
    public Member getMember() { return member; }
    public BookCopy getBookCopy() { return bookCopy; }
    public LocalDate getBorrowDate() { return borrowDate; }
    public LocalDate getDueDate() { return dueDate; }
    public LocalDate getReturnDate() { return returnDate; }
    public String getLoanStatus() { return loanStatus; }
    public Fine getFine() { return fine; }
    public Book getBook() { return book; }

    public boolean isReturned() {
        return RETURNED.equals(loanStatus);
    }

    public boolean isOverdue() {
        LocalDate endDate = returnDate != null ? returnDate : LocalDate.now();
        return !isReturned() && endDate.isAfter(dueDate);
    }

    public long getOverdueDays() {
        LocalDate endDate = returnDate != null ? returnDate : LocalDate.now();
        if (!endDate.isAfter(dueDate)) return 0;
        return ChronoUnit.DAYS.between(dueDate, endDate);
    }

    public double calculateFine() {
        if (fine != null) return fine.getFineAmount();
        return getOverdueDays() * Fine.DAILY_FINE_RATE;
    }

    public void extendLoan(int days) {
        dueDate = dueDate.plusDays(days);
    }

    // Frees the copy and charges a fine if it's late.
    public void returnBook() {
        if (isReturned()) {
            throw new IllegalStateException("Loan has already been returned.");
        }
        returnDate = LocalDate.now();
        loanStatus = RETURNED;
        bookCopy.returnCopy();
        if (returnDate.isAfter(dueDate)) {
            fine = new Fine("FINE-" + loanId, this, returnDate);
        }
    }

    public void completeLoan() {
        returnBook();
    }

    @Override
    public String toString() {
        String returnStatusInfo = isReturned()
                ? "Returned on " + returnDate
                : "Not Returned (Still Borrowed)";

        return String.format("""
                ------------------------------------------------------------
                Loan ID      : %s
                Book Title   : %s
                Copy ID      : %s
                Member       : %s
                Borrow Period: %s to %s
                Status       : %s
                Return Info  : %s
                Overdue Days : %d
                Fine         : %s
                ------------------------------------------------------------
                """,
                loanId,
                book != null ? book.getTitle() : "Unknown Book",
                bookCopy.getCopyId(),
                member.getName(),
                borrowDate,
                dueDate,
                loanStatus,
                returnStatusInfo,
                getOverdueDays(),
                fine == null ? "None" : fine.toString());
    }
}
