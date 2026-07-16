import Model.Book;
import Model.BookCopy;
import Model.Loan;
import Model.Member;
import Model.StudentMember;
import Model.FacultyMember;
import Service.LibrarySystem;
import Staff.Librarian;
import Staff.LibraryManager;
import Staff.LibraryStaff;
import Util.InputHelper;

import java.time.LocalDate;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        LibrarySystem library = new LibrarySystem("IFM Library");
        seedSampleData(library);

        boolean running = true;
        while (running) {
            displayMainMenu();
            int choice = InputHelper.readInt("Enter choice: ");
            switch (choice) {
                case 1 -> studentLoginPortal(library);
                case 2 -> librarianPortal(library);
                case 0 -> {
                    System.out.println("\nThank you for using IFM Library. Goodbye.");
                    running = false;
                }
                default -> System.out.println("Invalid choice. Try again.");
            }
        }
    }

    /**
     * utility method to draw boxed borders around content implemented by Norbert.we did this to make the console output more visuallly well organized
  21 +
     */
    public static void printBox(String title, String content) {
        String[] lines = content.split("\n");
        int maxLen = title.length();
        for (String line : lines) {
            if (line.length() > maxLen) {
                maxLen = line.length();
            }
        }
        // Add padding
        int width = maxLen + 4;

        // Draw top border
        System.out.println("┌" + "─".repeat(width) + "┐");

        // Draw title centered
        if (!title.isEmpty()) {
            int paddingLeft = (width - title.length()) / 2;
            int paddingRight = width - title.length() - paddingLeft;
            System.out.println("│" + " ".repeat(paddingLeft) + title + " ".repeat(paddingRight) + "│");
            System.out.println("├" + "─".repeat(width) + "┤");
        }

        // Draw content lines
        for (String line : lines) {
            int paddingRight = width - line.length();
            System.out.println("│  " + line + " ".repeat(paddingRight - 2) + "│");
        }

        // Draw bottom border
        System.out.println("└" + "─".repeat(width) + "┘");
    }

    private static void displayMainMenu() {
        System.out.println("""
            
            ==========================================================
                    INSTITUTE OF FINANCE MANAGEMENT (IFM)
                       LIBRARY MANAGEMENT SYSTEM MAIN MENU
            ==========================================================
            1. Student Portal (Login required)
            2. Librarian / Staff Portal (Login required)
            0. Exit
            ==========================================================
            """);
    }

    private static void studentLoginPortal(LibrarySystem library) {
        System.out.println("\n--- STUDENT LOGIN ---");
        String studentId = InputHelper.readString("Enter Student ID: ");
        String password = InputHelper.readString("Enter Password: ");

        if (!password.equals("student")) {
            System.out.println("Access Denied: Invalid password.");
            return;
        }

        Member member = library.findMember(studentId);
        if (member == null) {
            System.out.println("Access Denied: Member ID not registered.");
            return;
        }

        // Print login greeting
        System.out.println("\nWelcome back, " + member.getName() + " (" + member.getMemberType() + ")!");
        System.out.println("Go to the 'Notifications' option in the menu to check active messages and overdue alerts.");

        boolean back = false;
        while (!back) {
            System.out.println("\n==========================================================");
            System.out.println("           STUDENT PORTAL - Logged in as: " + member.getName());
            System.out.println("==========================================================");
            System.out.println("1. Browse All Books");
            System.out.println("2. Search Book by Title");
            System.out.println("3. Search Book by Title & Author");
            System.out.println("4. View My Borrowed Books (Current Loans & History)");
            System.out.println("5. Borrow Book Copy");
            System.out.println("6. Return Book Copy");
            System.out.println("7. View Notifications & Announcements");
            System.out.println("8. Clear Notifications");
            System.out.println("0. Logout");
            System.out.println("==========================================================");

            int choice = InputHelper.readInt("Enter choice: ");
            try {
                switch (choice) {
                    case 1 -> listBooks(library);
                    case 2 -> searchByTitle(library);
                    case 3 -> searchByTitleAndAuthor(library);
                    case 4 -> viewSpecificMemberLoans(library, member);
                    case 5 -> borrowBookFlow(library, member);
                    case 6 -> returnBookFlow(library, member);
                    case 7 -> displayMemberNotifications(library, member);
                    case 8 -> {
                        member.clearNotifications();
                        System.out.println("Notifications cleared.");
                    }
                    case 0 -> back = true;
                    default -> System.out.println("Invalid choice. Try again.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private static void displayMemberNotifications(LibrarySystem library, Member member) {
        StringBuilder sb = new StringBuilder();

        // Check for any overdue loans and generate fines automatically
        boolean hasOverdue = false;
        for (Loan loan : library.getLoans()) {
            if (loan.getMember().getMemberId().equalsIgnoreCase(member.getMemberId()) && !loan.isReturned()) {
                if (loan.isOverdue()) {
                    hasOverdue = true;
                    double fineAmt = loan.calculateFine();
                    sb.append(String.format("⚠️ URGENT WARNING: Loan ID %s for copy %s is OVERDUE by %d days!\n",
                            loan.getLoanId(), loan.getBookCopy().getCopyId(), loan.getOverdueDays()));
                    sb.append(String.format("                  Book Title: %s\n", loan.getBook().getTitle()));
                    sb.append(String.format("                  Accumulated fine: %.2f Tsh.\n\n", fineAmt));
                }
            }
        }

        if (!hasOverdue) {
            sb.append("✓ Status: You have no overdue books. Thank you!\n\n");
        }

        sb.append("--- Notifications & Announcements Inbox ---\n");
        List<String> notifications = member.getNotifications();
        if (notifications.isEmpty()) {
            sb.append("No notifications or announcements.");
        } else {
            for (int i = 0; i < notifications.size(); i++) {
                sb.append(String.format("%d. %s\n", (i + 1), notifications.get(i)));
            }
        }

        printBox("NOTIFICATIONS & ALERTS", sb.toString());
    }

    private static void librarianPortal(LibrarySystem library) {
        System.out.println("""
            
            ==========================================================
                            STAFF LOGIN
            ==========================================================
            Select staff member:
            1. Joel (Librarian)
            2. Sophia (Library Manager)
            0. Cancel
            ==========================================================
            """);
        int staffChoice = InputHelper.readInt("Enter choice: ");
        if (staffChoice == 0) return;

        LibraryStaff staff;
        String requiredPassword;

        if (staffChoice == 1) {
            staff = new Librarian("ST001", "Joel");
            requiredPassword = "librarian";
        } else if (staffChoice == 2) {
            staff = new LibraryManager("ST002", "Sophia", "Senior");
            requiredPassword = "manager";
        } else {
            System.out.println("Invalid choice. Returning to main menu.");
            return;
        }

        String enteredPassword = InputHelper.readString("Enter Staff Password: ");
        if (!enteredPassword.equals(requiredPassword)) {
            System.out.println("Access Denied: Invalid password.");
            return;
        }

        System.out.println("\nWelcome, " + staff.getStaffName() + " (" + staff.getStaffRole() + ").");

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

            int choice = InputHelper.readInt("Enter choice: ");
            try {
                switch (choice) {
                    case 1 -> {
                        staff.manageBooks();
                        addBook(library);
                    }
                    case 2 -> {
                        staff.manageBooks();
                        addBookCopy(library);
                    }
                    case 3 -> {
                        staff.manageMembers();
                        registerStudent(library);
                    }
                    case 4 -> {
                        staff.manageMembers();
                        registerFaculty(library);
                    }
                    case 5 -> listMembers(library);
                    case 6 -> listLoans(library);
                    case 7 -> {
                        printBox("LIBRARY REPORT & STATUS", library.generateReport());
                        printBox("STAFF ACTIVITY LOG", "Staff Log: " + staff.generateReport());
                    }
                    case 8 -> broadcastAnnouncementFlow(library, staff);
                    case 9 -> {
                        if (staff instanceof LibraryManager manager) {
                            String memberId = InputHelper.readString("Enter Member ID to approve: ");
                            manager.approveMemberRegistration(memberId);
                        } else {
                            System.out.println("Invalid choice. Try again.");
                        }
                    }
                    case 10 -> {
                        if (staff instanceof LibraryManager manager) {
                            System.out.println(manager.viewStatistics());
                        } else {
                            System.out.println("Invalid choice. Try again.");
                        }
                    }
                    case 0 -> back = true;
                    default -> System.out.println("Invalid choice. Try again.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private static void broadcastAnnouncementFlow(LibrarySystem library, LibraryStaff staff) {
        System.out.println("\n--- Broadcast Announcement ---");
        String message = InputHelper.readString("Enter announcement message: ");
        library.broadcastAnnouncement(staff.getStaffName(), message);
        System.out.println("Announcement successfully sent to all members.");
    }

    private static void seedSampleData(LibrarySystem library) {
        // Seed 9 sample books (with overlapping keywords for "Java" and "Clean")
        library.addBook("B001", "Clean Code", "9780132350884", 2008,
                "Robert Martin", "Programming");
        library.addBook("B002", "Effective Java", "9780134685991", 2018,
                "Joshua Bloch", "Programming");
        library.addBook("B003", "Design Patterns", "9780201633610", 1994,
                "Erich Gamma", "Software Engineering");
        library.addBook("B004", "Introduction to Algorithms", "9780262033848", 2009,
                "Thomas Cormen", "Computer Science");
        library.addBook("B005", "The Pragmatic Programmer", "9780135957059", 2019,
                "Andrew Hunt", "Programming");
        library.addBook("B006", "Java: A Beginner's Guide", "9781260463415", 2020,
                "Herbert Schildt", "Programming");
        library.addBook("B007", "Core Java Volume I", "9780135166307", 2019,
                "Cay Horstmann", "Programming");
        library.addBook("B008", "Clean Architecture", "9780134494166", 2017,
                "Robert Martin", "Programming");
        library.addBook("B009", "Clean Agile", "9780135781869", 2019,
                "Robert Martin", "Programming");

        // Seed some extra copies
        Book cleanCode = library.findBook("B001");
        if (cleanCode != null) {
            cleanCode.addCopy(new BookCopy("BC001_2", "Good"));
        }
        Book effectiveJava = library.findBook("B002");
        if (effectiveJava != null) {
            effectiveJava.addCopy(new BookCopy("BC002_2", "New"));
        }

        // Register members
        library.registerMember(new StudentMember(
                "S001", "Norbert Kiyagi", "norbert@ifm.ac.tz", "0700000001", "IFM"));
        library.registerMember(new FacultyMember(
                "F001", "Dr. Said", "said@ifm.ac.tz", "0700000002", "Computer Science"));

        // Seed an overdue loan for S001 borrowed 20 days ago
        library.borrowBook("S001", "BC001", LocalDate.now().minusDays(20));

        // Seed some announcements
        library.broadcastAnnouncement("Sophia", "Welcome to the new academic year! We have added 9 standard reference books to the catalog.");
        library.broadcastAnnouncement("Joel", "The library will be closed on Friday for inventory check. Please plan returns accordingly.");
    }

    private static void listBooks(LibrarySystem library) {
        if (library.getBooks().isEmpty()) {
            printBox("CATALOG STATUS", "No books in catalog.");
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (Book book : library.getBooks()) {
            sb.append(String.format("Book ID : %s | Title: %s\n", book.getBookId(), book.getTitle()));
            sb.append(String.format("ISBN    : %s | Year : %d\n", book.getIsbn(), book.getPublicationYear()));
            sb.append(String.format("Author  : %s | Category: %s\n", book.getAuthor(), book.getCategory()));
            sb.append("Copies  :\n");
            if (book.getCopies().isEmpty()) {
                sb.append("  (No copies added yet)\n");
            } else {
                for (BookCopy copy : book.getCopies()) {
                    sb.append(String.format("  - Copy ID: %s | Status: %s | Condition: %s\n",
                            copy.getCopyId(), copy.getStatus(), copy.getCondition()));
                }
            }
            sb.append("------------------------------------------------------------\n");
        }
        printBox("BOOK CATALOG", sb.toString().trim());
    }

    private static void searchByTitle(LibrarySystem library) {
        String title = InputHelper.readString("Enter title keyword: ");
        printSearchResults(library.searchBook(title));
    }

    private static void searchByTitleAndAuthor(LibrarySystem library) {
        String title = InputHelper.readString("Enter title keyword: ");
        String author = InputHelper.readString("Enter author name keyword: ");
        printSearchResults(library.searchBook(title, author));
    }

    private static void printSearchResults(List<Book> results) {
        if (results.isEmpty()) {
            printBox("SEARCH RESULTS", "No matching books found.");
            return;
        }
        StringBuilder sb = new StringBuilder();
        results.forEach(b -> {
            sb.append(String.format("Book ID : %s | Title: %s (by %s)\n", b.getBookId(), b.getTitle(), b.getAuthor()));
            sb.append(String.format("Category: %s | Available Copies: %s\n", b.getCategory(), b.hasAvailableCopy() ? "Yes" : "No"));
            sb.append("------------------------------------------------------------\n");
        });
        printBox("SEARCH RESULTS", sb.toString().trim());
    }

    private static void registerStudent(LibrarySystem library) {
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

    private static void registerFaculty(LibrarySystem library) {
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

    private static void viewSpecificMemberLoans(LibrarySystem library, Member member) {
        StringBuilder sbCurrent = new StringBuilder();
        List<Loan> current = library.getCurrentLoans(member.getMemberId());
        if (current.isEmpty()) {
            sbCurrent.append("No active loans.");
        } else {
            current.forEach(l -> sbCurrent.append(l.toString()).append("\n"));
        }
        printBox("CURRENT ACTIVE LOANS", sbCurrent.toString().trim());

        StringBuilder sbHistory = new StringBuilder();
        List<Loan> history = library.getLoanHistory(member.getMemberId());
        if (history.isEmpty()) {
            sbHistory.append("No loan history.");
        } else {
            history.forEach(l -> sbHistory.append(l.toString()).append("\n"));
        }
        printBox("LOAN HISTORY (with Book Titles, Borrow Periods & Return Dates)", sbHistory.toString().trim());
    }

    private static void borrowBookFlow(LibrarySystem library, Member member) {
        System.out.println("\n--- BORROW BOOK COPY ---");
        String keyword = InputHelper.readString("Enter Book Title / Keyword to search: ");
        List<Book> searchResults = library.searchBook(keyword);

        if (searchResults.isEmpty()) {
            System.out.println("No matching books found.");
            return;
        }

        System.out.println("\nMatching Books found:");
        for (Book b : searchResults) {
            System.out.printf("- ID: %s | Title: %s (by %s)\n", b.getBookId(), b.getTitle(), b.getAuthor());
        }

        Book selectedBook = null;
        while (selectedBook == null) {
            String bookId = InputHelper.readString("\nEnter Book ID of the book you want to borrow (or type 'cancel' to exit): ");
            if (bookId.equalsIgnoreCase("cancel")) {
                System.out.println("Borrow transaction canceled.");
                return;
            }
            selectedBook = library.findBook(bookId);
            if (selectedBook == null) {
                System.out.println("Invalid Book ID. Please try again.");
            }
        }

        List<BookCopy> availableCopies = library.getAvailableCopies(selectedBook.getBookId());
        if (availableCopies.isEmpty()) {
            System.out.println("Sorry, there are no copies of '" + selectedBook.getTitle() + "' currently available.");
            return;
        }

        System.out.println("\nAvailable Copies of '" + selectedBook.getTitle() + "':");
        for (BookCopy copy : availableCopies) {
            System.out.printf("- Copy ID: %s [Condition: %s]\n", copy.getCopyId(), copy.getCondition());
        }

        BookCopy selectedCopy = null;
        while (selectedCopy == null) {
            String copyId = InputHelper.readString("\nEnter the Copy ID you wish to borrow (or type 'cancel' to exit): ");
            if (copyId.equalsIgnoreCase("cancel")) {
                System.out.println("Borrow transaction canceled.");
                return;
            }
            for (BookCopy copy : availableCopies) {
                if (copy.getCopyId().equalsIgnoreCase(copyId)) {
                    selectedCopy = copy;
                    break;
                }
            }
            if (selectedCopy == null) {
                System.out.println("Invalid Copy ID or copy not available. Please try again.");
            }
        }

        boolean confirm = InputHelper.readYesNo("Confirm borrow of copy " + selectedCopy.getCopyId() + "?");
        if (!confirm) {
            System.out.println("Borrow transaction canceled.");
            return;
        }

        Loan loan = library.borrowBook(member.getMemberId(), selectedCopy.getCopyId());
        printBox("BORROW TRANSACTION SUCCESSFUL", "Book borrowed successfully:\n" + loan.toString());
    }

    private static void returnBookFlow(LibrarySystem library, Member member) {
        System.out.println("\n--- RETURN BOOK COPY ---");
        List<Loan> activeLoans = library.getCurrentLoans(member.getMemberId());
        if (activeLoans.isEmpty()) {
            System.out.println("You do not have any active loans to return.");
            return;
        }

        System.out.println("Your Active Loans:");
        for (Loan loan : activeLoans) {
            System.out.printf("- Loan ID: %s | Copy ID: %s | Book: %s | Due: %s\n",
                    loan.getLoanId(), loan.getBookCopy().getCopyId(), loan.getBookCopy(), loan.getDueDate());
        }

        Loan selectedLoan = null;
        while (selectedLoan == null) {
            String loanId = InputHelper.readString("\nEnter the Loan ID you want to return (or type 'cancel' to exit): ");
            if (loanId.equalsIgnoreCase("cancel")) {
                System.out.println("Return transaction canceled.");
                return;
            }
            for (Loan loan : activeLoans) {
                if (loan.getLoanId().equalsIgnoreCase(loanId)) {
                    selectedLoan = loan;
                    break;
                }
            }
            if (selectedLoan == null) {
                System.out.println("Invalid Loan ID. Please try again.");
            }
        }

        boolean confirm = InputHelper.readYesNo("Confirm return of Copy " + selectedLoan.getBookCopy().getCopyId() + "?");
        if (!confirm) {
            System.out.println("Return transaction canceled.");
            return;
        }

        Loan returnedLoan = library.returnBook(selectedLoan.getLoanId());
        printBox("RETURN TRANSACTION SUCCESSFUL", "Book returned successfully:\n" + returnedLoan.toString());
    }

    private static void addBook(LibrarySystem library) {
        String bookId = InputHelper.readString("Enter Book ID (e.g., B006): ");
        if (library.findBook(bookId) != null) {
            System.out.println("Error: A book with this ID already exists.");
            return;
        }
        String title = InputHelper.readString("Enter Title: ");
        String isbn = InputHelper.readString("Enter ISBN: ");
        int year = InputHelper.readInt("Enter Publication Year: ");
        String authorName = InputHelper.readString("Enter Author Name: ");
        String categoryName = InputHelper.readString("Enter Category Name: ");
        library.addBook(bookId, title, isbn, year, authorName, categoryName);
        System.out.println("Book '" + title + "' added successfully with an initial available copy.");
    }

    private static void addBookCopy(LibrarySystem library) {
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
        String condition = InputHelper.readString("Enter Condition (Good/Damaged/New): ");
        book.addCopy(new Model.BookCopy(copyId, condition));
        System.out.println("Successfully added copy " + copyId + " to Book '" + book.getTitle() + "'.");
    }

    private static void listMembers(LibrarySystem library) {
        if (library.getMembers().isEmpty()) {
            printBox("MEMBER MANAGEMENT", "No registered members.");
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (Member member : library.getMembers()) {
            sb.append(member.getMemberDetails()).append("\n");
        }
        printBox("REGISTERED MEMBERS", sb.toString().trim());
    }

    private static void listLoans(LibrarySystem library) {
        if (library.getLoans().isEmpty()) {
            printBox("LOAN MANAGEMENT", "No loans recorded in the system.");
            return;
        }
        StringBuilder sb = new StringBuilder();
        library.getLoans().forEach(l -> sb.append(l.toString()).append("\n"));
        printBox("ALL RECORDED LOANS", sb.toString().trim());
    }
}
