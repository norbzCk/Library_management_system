package Model;

import java.time.LocalDate;

public class Fine {
    public static final double DAILY_FINE_RATE = 500.0;

    private String fineId;
    private Loan loan;
    private double fineAmount;
    private LocalDate fineDate;
    private boolean paidStatus;

    public Fine(String fineId, Loan loan, LocalDate fineDate) {
        this.fineId = fineId;
        this.loan = loan;
        this.fineDate = fineDate;
        this.fineAmount = DAILY_FINE_RATE * loan.getOverdueDays();
        this.paidStatus = false;
    }

    public String getFineId() {
        return fineId;
    }

    public Loan getLoan() {
        return loan;
    }

    public double getFineAmount() {
        return fineAmount;
    }

    public LocalDate getFineDate() {
        return fineDate;
    }

    public boolean isPaid() {
        return paidStatus;
    }

    public void payFine() {
        paidStatus = true;
    }

    public String getPaymentStatus() {
        return paidStatus ? "Paid" : "Pending";
    }

    @Override
    public String toString() {
        return String.format("Fine %s: %.2f Tsh (%s)", fineId, fineAmount, getPaymentStatus());
    }
}
