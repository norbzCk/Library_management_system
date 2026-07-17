package Portal;

import Model.Book;
import Model.BookCopy;
import Model.StudentMember;
import Model.FacultyMember;
import Service.LibrarySystem;
import Staff.Librarian;
import Staff.LibraryManager;
import Staff.LibraryStaff;
import Util.ConsoleUtil;
import Util.InputHelper;
import Util.ReportPrinter;

import java.util.List;

// Librarian / manager operations: catalog, members, loans, reports, announcements.
public class StaffPortal {

    private final LibrarySystem library;

    public StaffPortal(LibrarySystem library) {
        this.library = library;
    }

    public void run() {
        System.out.println("""
                
                ==========================================================
                                STAFF LOGIN
                ==========================================================
                Select staff member:
                1. Sophia (Librarian)
                2. Joel (Library Manager)
                0. Cancel
                ==========================================================
                """);
        int choice = InputHelper.readInt("Enter choice: ");
        if (choice == 0) return;

        LibraryStaff staff;
        String requiredPassword;
        if (choice == 1) {
            staff = new Librarian("ST001", "Sophia");
            requiredPassword = "librarian";
        } else if (choice == 2) {
            staff = new LibraryManager("ST002", "Joel", "Senior");
            requiredPassword = "manager";
        } else {
            System.out.println("Invalid choice. Returning to main menu.");
            return;
        }

        if (!InputHelper.readString("Enter Staff Password: ").equals(requiredPassword)) {
            System.out.println("Access Denied: Invalid password.");
            return;
        }

        System.out.println("\nWelcome, " + staff.getStaffName() + " (" + staff.getStaffRole() + ").");
        ConsoleUtil.clearScreen();

        boolean back = false;
        while (!back) {
            System.out.println("\n==========================================================");
            System.out.println("           LIBRARIAN / STAFF PORTAL - Logged in: " + staff.getStaffName());
            System.out.println("==========================================================");
            System.out.println("1. Add New Book to Catalog");
            System.out.println("2. Add Copy to Existing Book");
            System.out.println("3. Register New Student Member");
            System.out.println("4. Register New Faculty Member");
            System.out.println("5. View All Registered Members");
            System.out.println("6. View All Active Loans");
            System.out.println("7. View Library Report / Statistics");
            System.out.println("8. Send/Broadcast Announcement to All Members");
            if (staff instanceof LibraryManager) {
                System.out.println("9. Approve Member Registration (Manager Only)");
                System.out.println("10. View Manager Statistics (Manager Only)");
            }
            System.out.println("0. Logout");
            System.out.println("==========================================================");

            int option = InputHelper.readInt("Enter choice: ");
            try {
                switch (option) {
                    case 1:
                        staff.manageBooks();
                        addBook();
                        break;
                    case 2:
                        staff.manageBooks();
                        addBookCopy();
                        break;
                    case 3:
                        staff.manageMembers();
                        registerStudent();
                        break;
                    case 4:
                        staff.manageMembers();
                        registerFaculty();
                        break;
                    case 5:
                        ReportPrinter.showMembers(library);
                        break;
                    case 6:
                        ReportPrinter.showLoans(library);
                        break;
                    case 7: {
                        ConsoleUtil.printBox("LIBRARY REPORT & STATUS", library.generateReport());
                        ConsoleUtil.printBox("STAFF ACTIVITY LOG", "Staff Log: " + staff.generateReport());
                        break;
                    }
                    case 8:
                        broadcastAnnouncement(staff);
                        break;
                    case 9:
                        if (staff instanceof LibraryManager) {
                            LibraryManager m = (LibraryManager) staff;
                            m.approveMemberRegistration(InputHelper.readString("Enter Member ID to approve: "));
                        } else {
                            System.out.println("Invalid choice. Try again.");
                        }
                        break;
                    case 10:
                        if (staff instanceof LibraryManager) {
                            LibraryManager m = (LibraryManager) staff;
                            System.out.println(m.viewStatistics());
                        } else {
                            System.out.println("Invalid choice. Try again.");
                        }
                        break;
                    case 0:
                        back = true;
                        break;
                    default:
                        System.out.println("Invalid choice. Try again.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void addBook() {
        String bookId = InputHelper.readString("Enter Book ID (e.g., B006): ");
        if (library.findBook(bookId) != null) {
            System.out.println("Error: A book with this ID already exists.");
            return;
        }
        String title = InputHelper.readString("Enter Title: ");
        String isbn = InputHelper.readString("Enter ISBN: ");
        int year = InputHelper.readInt("Enter Publication Year: ");
        String author = InputHelper.readString("Enter Author Name: ");
        String category = InputHelper.readString("Enter Category Name: ");

        System.out.println("\nSelect Book Format:");
        System.out.println("1. Physical Book (collected at the library)");
        System.out.println("2. E-Book (licensed reading window)");
        BookCopy.MediaType mediaType = InputHelper.readInt("Enter format (1 or 2): ") == 2
                ? BookCopy.MediaType.EBOOK : BookCopy.MediaType.PHYSICAL;

        int copies = InputHelper.readInt("Enter number of copies to add: ");
        while (copies <= 0) {
            System.out.println("Number of copies must be at least 1.");
            copies = InputHelper.readInt("Enter number of copies to add: ");
        }

        library.addBook(bookId, title, isbn, year, author, category, mediaType, copies);
        String label = mediaType == BookCopy.MediaType.EBOOK ? "e-book" : "physical";
        System.out.println("Book '" + title + "' (" + label + ") added successfully with " + copies + " cop(ies).");
    }

    private void addBookCopy() {
        String bookId = InputHelper.readString("Enter Book ID: ");
        Book book = library.findBook(bookId);
        if (book == null) {
            System.out.println("Error: Book not found.");
            return;
        }
        String copyId = InputHelper.readString("Enter unique Copy ID (e.g., BC003): ");
        if (library.findCopy(copyId) != null) {
            System.out.println("Error: A book copy with this ID already exists.");
            return;
        }
        // Keep the new copy in the same format as the book's existing copies.
        BookCopy.MediaType mediaType = book.getCopies().isEmpty()
                ? BookCopy.MediaType.PHYSICAL : book.getCopies().get(0).getMediaType();
        String condition = mediaType == BookCopy.MediaType.EBOOK
                ? "Digital" : InputHelper.readString("Enter Condition (Good/Damaged/New): ");
        book.addCopy(new BookCopy(copyId, condition, mediaType));
        String label = mediaType == BookCopy.MediaType.EBOOK ? "e-book" : "physical";
        System.out.println("Successfully added " + label + " copy " + copyId + " to Book '" + book.getTitle() + "'.");
    }

    private void registerStudent() {
        String id = InputHelper.readString("Student ID: ");
        if (library.findMember(id) != null) {
            System.out.println("Error: Member with ID " + id + " already exists.");
            return;
        }
        String name = InputHelper.readString("Name: ");
        String email = InputHelper.readString("Email: ");
        String phone = InputHelper.readString("Phone: ");
        String institution = InputHelper.readString("Institution: ");
        library.registerMember(new StudentMember(id, name, email, phone, institution));
        System.out.println("Student registered successfully.");
    }

    private void registerFaculty() {
        String id = InputHelper.readString("Faculty ID: ");
        if (library.findMember(id) != null) {
            System.out.println("Error: Member with ID " + id + " already exists.");
            return;
        }
        String name = InputHelper.readString("Name: ");
        String email = InputHelper.readString("Email: ");
        String phone = InputHelper.readString("Phone: ");
        String dept = InputHelper.readString("Department: ");
        library.registerMember(new FacultyMember(id, name, email, phone, dept));
        System.out.println("Faculty registered successfully.");
    }

    private void broadcastAnnouncement(LibraryStaff staff) {
        String message = InputHelper.readString("Enter announcement message: ");
        library.broadcastAnnouncement(staff.getStaffName(), message);
        System.out.println("Announcement successfully sent to all members.");
    }
}
