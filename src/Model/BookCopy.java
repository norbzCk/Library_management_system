package Model;

public class BookCopy {
    private String copyId;
    private String status;
    private String condition;

    public BookCopy(String copyId, String status, String condition) {
        this.copyId = copyId;
        this.status = status;
        this.condition = condition;
    }

    public BookCopy(String copyId, String condition) {
        this(copyId, "Available", condition);
    }

    public String getCopyId() {
        return copyId;
    }

    public String getStatus() {
        return status;
    }

    public String getCondition() {
        return condition;
    }

    public void borrowCopy() {
        if (!isAvailable()) {
            throw new IllegalStateException("Copy " + copyId + " is not available.");
        }
        status = "Borrowed";
    }

    public void returnCopy() {
        status = "Available";
    }

    public boolean isAvailable() {
        return status.equalsIgnoreCase("Available");
    }

    @Override
    public String toString() {
        return copyId + " (" + status + ")";
    }
}
