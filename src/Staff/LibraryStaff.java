package Staff;

/**
 * Base class for library staff. Librarian and LibraryManager fill in the
 * day-to-day behaviour; this is what the menus log in as.
 */
public abstract class LibraryStaff {
    protected String staffId;
    protected String staffName;
    protected String staffRole;

    protected LibraryStaff(String staffId, String staffName, String staffRole) {
        this.staffId = staffId;
        this.staffName = staffName;
        this.staffRole = staffRole;
    }

    public String getStaffId() { return staffId; }
    public String getStaffName() { return staffName; }
    public String getStaffRole() { return staffRole; }

    public abstract void manageBooks();
    public abstract void manageMembers();
    public abstract String generateReport();

    @Override
    public String toString() {
        return staffId + " - " + staffName + " (" + staffRole + ")";
    }
}
