package Portal;

import Model.Book;
import Model.BookCopy;
import Model.Loan;
import Model.Member;
import Service.LibrarySystem;
import Util.ConsoleUtil;
import Util.InputHelper;
import Util.ReportPrinter;

import java.util.List;

// Everything a logged-in student can do: browse, search, borrow, return, check notifications.
public class StudentPortal {

    private final LibrarySystem library;

    public StudentPortal(LibrarySystem library) {
        this.library = library;
    }

    public void run() {
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

        System.out.println("\nWelcome back, " + member.getName() + " (" + member.getMemberType() + ")!");
        System.out.println("Go to the 'Notifications' option to check messages and overdue alerts.");
        ConsoleUtil.clearScreen();

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
                    case 1:
                        ReportPrinter.showBooks(library);
                        break;
                    case 2:
                        ReportPrinter.showSearchResults(library.searchBook(InputHelper.readString("Enter title keyword: ")));
                        break;
                    case 3: {
                        String title = InputHelper.readString("Enter title keyword: ");
                        String author = InputHelper.readString("Enter author name keyword: ");
                        ReportPrinter.showSearchResults(library.searchBook(title, author));
                        break;
                    }
                    case 4:
                        viewMemberLoansWithClear(member);
                        break;
                    case 5:
                        borrowBookFlow(member);
                        break;
                    case 6:
                        returnBookFlow(member);
                        break;
                    case 7:
                        displayNotifications(member);
                        break;
                    case 8:
                        member.clearNotifications();
                        System.out.println("Notifications cleared.");
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

    private void displayNotifications(Member member) {
        StringBuilder sb = new StringBuilder();

        boolean hasOverdue = false;
        for (Loan loan : library.getLoans()) {
            if (loan.getMember().getMemberId().equalsIgnoreCase(member.getMemberId())
                    && !loan.isReturned() && loan.isOverdue()) {
                hasOverdue = true;
                sb.append(String.format("URGENT WARNING: Loan %s (copy %s) is OVERDUE by %d days!\n",
                        loan.getLoanId(), loan.getBookCopy().getCopyId(), loan.getOverdueDays()));
                sb.append(String.format("                  Book Title: %s\n", loan.getBook().getTitle()));
                sb.append(String.format("                  Accumulated fine: %.2f Tsh.\n\n", loan.calculateFine()));
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
        ConsoleUtil.printBox("NOTIFICATIONS & ALERTS", sb.toString());
    }

    private void viewMemberLoansWithClear(Member member) {
        ReportPrinter.showMemberLoans(member, library);
        if (library.getLoanHistory(member.getMemberId()).isEmpty()) {
            return;
        }
        String cmd = InputHelper.readString("Type 'clear' to clear your loan history, or press Enter to go back: ");
        if (cmd.equalsIgnoreCase("clear")) {
            if (InputHelper.readYesNo("Confirm clearing your loan history?")) {
                member.clearLoanHistory();
                System.out.println("Loan history cleared.");
            } else {
                System.out.println("Loan history was NOT cleared.");
            }
        }
    }

    private void borrowBookFlow(Member member) {
        System.out.println("\n--- BORROW BOOK COPY ---");
        List<Book> results = library.searchBook(InputHelper.readString("Enter Book Title / Keyword to search: "));
        if (results.isEmpty()) {
            System.out.println("No matching books found.");
            return;
        }

        System.out.println("\nMatching Books found:");
        for (Book b : results) {
            System.out.printf("- ID: %s | Title: %s (by %s)\n", b.getBookId(), b.getTitle(), b.getAuthor());
        }

        Book book = null;
        while (book == null) {
            String bookId = InputHelper.readString("\nEnter Book ID (or 'cancel'): ");
            if (bookId.equalsIgnoreCase("cancel")) return;
            book = library.findBook(bookId);
            if (book == null) System.out.println("Invalid Book ID. Please try again.");
        }

        List<BookCopy> available = library.getAvailableCopies(book.getBookId());
        if (available.isEmpty()) {
            System.out.println("Sorry, there are no copies of '" + book.getTitle() + "' available.");
            return;
        }

        System.out.println("\nAvailable Copies of '" + book.getTitle() + "':");
        for (BookCopy copy : available) {
            String typeLabel = copy.isEbook() ? "E-Book" : "Physical";
            System.out.printf("- Copy ID: %s [Type: %s | Condition: %s]\n", copy.getCopyId(), typeLabel, copy.getCondition());
        }

        BookCopy copy = null;
        while (copy == null) {
            String copyId = InputHelper.readString("\nEnter the Copy ID (or 'cancel'): ");
            if (copyId.equalsIgnoreCase("cancel")) return;
            for (BookCopy c : available) {
                if (c.getCopyId().equalsIgnoreCase(copyId)) { copy = c; break; }
            }
            if (copy == null) System.out.println("Invalid Copy ID. Please try again.");
        }

        if (!InputHelper.readYesNo("Confirm borrow of copy " + copy.getCopyId() + "?")) {
            System.out.println("Borrow transaction canceled.");
            return;
        }

        Loan loan = library.borrowBook(member.getMemberId(), copy.getCopyId());

        StringBuilder msg = new StringBuilder(loan + "\n\n");
        if (copy.isEbook()) {
            msg.append("✓ E-Book. Per licensing terms, granted for reading only until ")
               .append(loan.getDueDate()).append(". Please return the license by that date.");
        } else {
            msg.append("✓ Physical copy. Please come to ").append(library.getLibraryName())
               .append(" to collect it. Due back on ").append(loan.getDueDate()).append(".");
        }
        ConsoleUtil.printBox("BORROW TRANSACTION SUCCESSFUL", msg.toString());
    }

    private void returnBookFlow(Member member) {
        System.out.println("\n--- RETURN BOOK COPY ---");
        List<Loan> active = library.getCurrentLoans(member.getMemberId());
        if (active.isEmpty()) {
            System.out.println("You do not have any active loans to return.");
            return;
        }

        System.out.println("Your Active Loans:");
        for (Loan loan : active) {
            System.out.printf("- Loan ID: %s | Copy ID: %s | Book: %s | Due: %s\n",
                    loan.getLoanId(), loan.getBookCopy().getCopyId(), loan.getBookCopy(), loan.getDueDate());
        }

        Loan loan = null;
        while (loan == null) {
            String loanId = InputHelper.readString("\nEnter the Loan ID (or 'cancel'): ");
            if (loanId.equalsIgnoreCase("cancel")) return;
            for (Loan l : active) {
                if (l.getLoanId().equalsIgnoreCase(loanId)) { loan = l; break; }
            }
            if (loan == null) System.out.println("Invalid Loan ID. Please try again.");
        }

        if (!InputHelper.readYesNo("Confirm return of Copy " + loan.getBookCopy().getCopyId() + "?")) {
            System.out.println("Return transaction canceled.");
            return;
        }
        Loan returned = library.returnBook(loan.getLoanId());
        ConsoleUtil.printBox("RETURN TRANSACTION SUCCESSFUL", "Book returned successfully:\n" + returned);
    }
}
