package Model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * THis is Base class for borrowers. Students and faculty extend this and override the
 * borrow rules (how many books, for how long).
 */
public abstract class Member {

    private String memberId;
    private String name;
    private String email;
    private String phoneNumber;
    private LocalDate membershipDate;
    private String membershipStatus;
    private List<Loan> currentLoans;
    private List<Loan> loanHistory;
    private List<String> notifications;

    protected Member(String memberId, String name, String email, String phoneNumber) {
        this.memberId = memberId;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.membershipDate = LocalDate.now();
        this.membershipStatus = "Active";
        this.currentLoans = new ArrayList<>();
        this.loanHistory = new ArrayList<>();
        this.notifications = new ArrayList<>();
    }

    // Subclasses decide the limits.
    public abstract int getBorrowLimit();
    public abstract int getLoanPeriodDays();
    public abstract String getMemberType();

    public String getMemberId() { 
        return memberId;
    }
    public String getName() { 
        return name; }
    public String getEmail() { 
        return email; }
    public String getPhoneNumber() { return phoneNumber; }
    public LocalDate getMembershipDate() { return membershipDate; }
    public String getMembershipStatus() { return membershipStatus; }
    public List<Loan> getCurrentLoans() { return currentLoans; }
    public List<Loan> getLoanHistory() { return loanHistory; }

    public boolean isActive() {
        return membershipStatus.equalsIgnoreCase("Active");
    }

    public boolean canBorrow() {
        return isActive() && currentLoans.size() < getBorrowLimit();
    }

    public void borrowBook(Loan loan) {
        currentLoans.add(loan);
        loanHistory.add(loan);
    }

    public void returnBook(Loan loan) {
        currentLoans.remove(loan);
    }

    public void updateProfile(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getMemberDetails() {
        return String.format("""
                ------------------------------
                Member ID : %s
                Type      : %s
                Name      : %s
                Email     : %s
                Phone     : %s
                Status    : %s
                Borrowed  : %d/%d
                Loan days : %d
                ------------------------------
                """,
                memberId, getMemberType(), name, email, phoneNumber,
                membershipStatus, currentLoans.size(), getBorrowLimit(), getLoanPeriodDays());
    }

    public void addNotification(String message) { notifications.add(message); }
    public List<String> getNotifications() { return notifications; }

    public void clearNotifications() { notifications.clear(); }

    // Drops returned loans from the history view; active loans stay put.
    public void clearLoanHistory() {
        loanHistory.removeIf(Loan::isReturned);
    }

    @Override
    public String toString() {
        return memberId + " - " + name + " (" + getMemberType() + ")";
    }
}
