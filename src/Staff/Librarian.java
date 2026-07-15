package Staff;

/** Processes daily loans, returns, and inventory tasks. */
public class Librarian extends LibraryStaff {

    public Librarian(String staffId, String staffName) {
        super(staffId, staffName, "Librarian");
    }

    @Override
    public void manageBooks() {
        System.out.println("Librarian " + staffName + " is managing book inventory.");
    }

    @Override
    public void manageMembers() {
        System.out.println("Librarian " + staffName + " is assisting members with loans/returns.");
    }

    @Override
    public String generateReport() {
        return "Daily librarian report prepared by " + staffName;
    }
}
