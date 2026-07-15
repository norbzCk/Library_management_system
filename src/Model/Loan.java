package Model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Associates a Member with a specific BookCopy for a borrowing period.
 * UML: Loan → Member (1), Loan → BookCopy (1), Loan → Fine (0..1 composition).
 */
public class Loan {
    public static final String ACTIVE = "Active";
    public static final String RETURNED = "Returned";

    private String loanId;
    private Member member;
    private BookCopy bookCopy;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private String loanStatus;
    private Fine fine;

    public Loan(String loanId,
                Member member,
                BookCopy bookCopy,
                LocalDate borrowDate) {
        this(loanId, member, bookCopy, borrowDate, member.getLoanPeriodDays());
    }

    public Loan(String loanId,
                Member member,
                BookCopy bookCopy,
                LocalDate borrowDate,
                int loanPeriodDays) {
        this.loanId = loanId;
        this.member = member;
        this.bookCopy = bookCopy;
        this.borrowDate = borrowDate;
        this.dueDate = borrowDate.plusDays(loanPeriodDays);
        this.loanStatus = ACTIVE;
    }

    public String getLoanId() {
        return loanId;
    }

    public Member getMember() {
        return member;
    }

    public BookCopy getBookCopy() {
        return bookCopy;
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public String getLoanStatus() {
        return loanStatus;
    }

    public Fine getFine() {
        return fine;
    }

    public boolean isReturned() {
        return RETURNED.equals(loanStatus);
    }

    public boolean isOverdue() {
        LocalDate endDate = returnDate != null ? returnDate : LocalDate.now();
        return !isReturned() && endDate.isAfter(dueDate);
    }

    /** Days overdue relative to due date (0 if not overdue). */
    public long getOverdueDays() {
        LocalDate endDate = returnDate != null ? returnDate : LocalDate.now();
        if (!endDate.isAfter(dueDate)) {
            return 0;
        }
        return ChronoUnit.DAYS.between(dueDate, endDate);
    }

    public double calculateFine() {
        if (fine != null) {
            return fine.getFineAmount();
        }
        return getOverdueDays() * Fine.DAILY_FINE_RATE;
    }

    public void extendLoan(int days) {
        dueDate = dueDate.plusDays(days);
    }

    /**
     * Marks the loan returned, frees the BookCopy, and creates a Fine if overdue.
     */
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
        return String.format("""
                ------------------------------
                Loan ID      : %s
                Member       : %s
                Copy ID      : %s
                Borrow Date  : %s
                Due Date     : %s
                Return Date  : %s
                Status       : %s
                Overdue Days : %d
                Fine         : %s
                ------------------------------
                """,
                loanId,
                member.getName(),
                bookCopy.getCopyId(),
                borrowDate,
                dueDate,
                returnDate == null ? "N/A" : returnDate,
                loanStatus,
                getOverdueDays(),
                fine == null ? "None" : fine.toString());
    }
}
