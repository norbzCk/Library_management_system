import Model.Book;
import Model.FacultyMember;
import Model.Loan;
import Model.Member;
import Model.StudentMember;
import Service.LibrarySystem;
import Staff.Librarian;
import Staff.LibraryManager;
import Staff.LibraryStaff;
import Util.InputHelper;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        LibrarySystem library = new LibrarySystem("IFM Library");
        seedSampleData(library);

        boolean running = true;
        while (running) {
            System.out.println("\n===== " + library.getLibraryName() + " =====");
            System.out.println("1. List books");
            System.out.println("2. Search book (title)");
            System.out.println("3. Search book (title + author)");
            System.out.println("4. Register student member");
            System.out.println("5. Register faculty member");
            System.out.println("6. Borrow book");
            System.out.println("7. Return book");
            System.out.println("8. Show members");
            System.out.println("9. Show loans");

            System.out.println("10. Generate report");
            System.out.println("0. Exit");

            int choice = InputHelper.readInt("Enter choice: ");
            try {
                switch (choice) {
                    case 1 -> listBooks(library);
                    case 2 -> searchByTitle(library);
                    case 3 -> searchByTitleAndAuthor(library);
                    case 4 -> registerStudent(library);
                    case 5 -> registerFaculty(library);
                    case 6 -> borrowBook(library);
                    case 7 -> returnBook(library);
                    case 8 -> listMembers(library);
                    case 9 -> listLoans(library);
                    case 10 -> System.out.println(library.generateReport());
    //                    case 11 -> demoStaffPolymorphism();
                    case 0 -> running = false;
                    default -> System.out.println("Invalid choice.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }

        System.out.println("Goodbye.");
    }

    private static void seedSampleData(LibrarySystem library) {
        library.addBook("B001", "Clean Code", "9780132350884", 2008,
                "Robert Martin", "Programming");
        library.addBook("B002", "Effective Java", "9780134685991", 2018,
                "Joshua Bloch", "Programming");
        library.registerMember(new StudentMember(
                "S001", "Norbert Kiyagi", "norbert@ifm.ac.tz", "0700000001", "IFM"));
        library.registerMember(new FacultyMember(
                "F001", "Dr. Said", "said@ifm.ac.tz", "0700000002", "Computer Science"));
    }

    private static void listBooks(LibrarySystem library) {
        if (library.getBooks().isEmpty()) {
            System.out.println("No books.");
            return;
        }
        for (Book book : library.getBooks()) {
            System.out.println(book.getBookDetails());
            book.getCopies().forEach(c -> System.out.println("  Copy: " + c));
        }
    }

    private static void searchByTitle(LibrarySystem library) {
        String title = InputHelper.readString("Enter title: ");
        printSearchResults(library.searchBook(title));
    }

    private static void searchByTitleAndAuthor(LibrarySystem library) {
        String title = InputHelper.readString("Enter title: ");
        String author = InputHelper.readString("Enter author: ");
        printSearchResults(library.searchBook(title, author));
    }

    private static void printSearchResults(List<Book> results) {
        if (results.isEmpty()) {
            System.out.println("No matching books.");
            return;
        }
        results.forEach(b -> System.out.println(b.getBookDetails()));
    }

    private static void registerStudent(LibrarySystem library) {
        String id = InputHelper.readString("Student ID: ");
        String name = InputHelper.readString("Name: ");
        String email = InputHelper.readString("Email: ");
        String phone = InputHelper.readString("Phone: ");
        String institution = InputHelper.readString("Institution: ");
        library.registerMember(new StudentMember(id, name, email, phone, institution));
        System.out.println("Student registered.");
    }

    private static void registerFaculty(LibrarySystem library) {
        String id = InputHelper.readString("Faculty ID: ");
        String name = InputHelper.readString("Name: ");
        String email = InputHelper.readString("Email: ");
        String phone = InputHelper.readString("Phone: ");
        String dept = InputHelper.readString("Department: ");
        library.registerMember(new FacultyMember(id, name, email, phone, dept));
        System.out.println("Faculty registered.");
    }

    private static void borrowBook(LibrarySystem library) {
        String memberId = InputHelper.readString("Member ID: ");
        String copyId = InputHelper.readString("Copy ID: ");
        Loan loan = library.borrowBook(memberId, copyId);
        System.out.println("Borrow successful:\n" + loan);
    }

    private static void returnBook(LibrarySystem library) {
        String loanId = InputHelper.readString("Loan ID: ");
        Loan loan = library.returnBook(loanId);
        System.out.println("Return successful:\n" + loan);
    }

    private static void listMembers(LibrarySystem library) {
        for (Member member : library.getMembers()) {
            // Polymorphism: runtime type decides getMemberDetails / getMemberType
            System.out.println(member.getMemberDetails());
        }
    }

    private static void listLoans(LibrarySystem library) {
        if (library.getLoans().isEmpty()) {
            System.out.println("No loans.");
            return;
        }
        library.getLoans().forEach(System.out::println);
    }

    private static void demoStaffPolymorphism() {
        LibraryStaff librarian = new Librarian("ST001", "Joel");
        LibraryStaff manager = new LibraryManager("ST002", "Sophia", "Senior");

        LibraryStaff[] staff = {librarian, manager};
        for (LibraryStaff s : staff) {
            System.out.println(s);
            s.manageBooks();
            s.manageMembers();
            System.out.println(s.generateReport());
            System.out.println();
        }
    }
}
