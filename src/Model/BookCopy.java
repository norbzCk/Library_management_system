package Model;

import java.time.LocalDate;

/**
 * A single copy of a book. Copies are either physical (collected at the desk)
 * or e-books (borrowed under a licensed reading window).
 */
public class BookCopy {
    public enum MediaType {
        PHYSICAL,
        EBOOK
    }

    private String copyId;
    private String status;
    private String condition;
    private MediaType mediaType;

    private LocalDate licenseUntil; // e-books only: last day the member may read it

    public BookCopy(String copyId, String status, String condition, MediaType mediaType) {
        this.copyId = copyId;
        this.status = status;
        this.condition = condition;
        this.mediaType = mediaType;
    }

    public BookCopy(String copyId, String condition, MediaType mediaType) {
        this(copyId, "Available", condition, mediaType);
    }

    public String getCopyId() { return copyId; }
    public String getStatus() { return status; }
    public String getCondition() { return condition; }
    public MediaType getMediaType() { return mediaType; }
    public LocalDate getLicenseUntil() { return licenseUntil; }

    public boolean isPhysical() { return mediaType == MediaType.PHYSICAL; }
    public boolean isEbook() { return mediaType == MediaType.EBOOK; }

    public void borrowCopy() {
        if (!isAvailable()) {
            throw new IllegalStateException("Copy " + copyId + " is not available.");
        }
        status = "Borrowed";
    }

    // E-books record the license expiry; physical copies just get marked borrowed.
    public void borrowCopy(LocalDate licenseUntil) {
        borrowCopy();
        if (mediaType == MediaType.EBOOK) {
            this.licenseUntil = licenseUntil;
        }
    }

    public void returnCopy() {
        status = "Available";
        licenseUntil = null;
    }

    public boolean isAvailable() {
        return status.equalsIgnoreCase("Available");
    }

    @Override
    public String toString() {
        String typeLabel = mediaType == MediaType.EBOOK ? "E-Book" : "Physical";
        return copyId + " (" + status + ", " + typeLabel + ")";
    }
}
