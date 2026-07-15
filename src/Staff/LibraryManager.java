package Staff;

/** Handles statistics, annual reports, and member approval. */
public class LibraryManager extends LibraryStaff {
    private String managementLevel;

    public LibraryManager(String staffId, String staffName, String managementLevel) {
        super(staffId, staffName, "Library Manager");
        this.managementLevel = managementLevel;
    }

    public String getManagementLevel() {
        return managementLevel;
    }

    @Override
    public void manageBooks() {
        System.out.println("Manager " + staffName + " is reviewing catalog strategy.");
    }

    @Override
    public void manageMembers() {
        System.out.println("Manager " + staffName + " is approving member registrations.");
    }

    @Override
    public String generateReport() {
        return "Annual library report prepared by " + staffName
                + " (level: " + managementLevel + ")";
    }

    public String viewStatistics() {
        return "Manager statistics overview by " + staffName;
    }

    public void approveMemberRegistration(String memberId) {
        System.out.println("Member " + memberId + " approved by " + staffName);
    }
}
