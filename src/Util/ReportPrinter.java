package Util;

import Model.Book;
import Model.BookCopy;
import Model.Loan;
import Model.Member;
import Service.LibrarySystem;

import java.util.List;

// Turns library data into the boxed reports shown on screen. No logic, just display.
public class ReportPrinter {

    public static void showBooks(LibrarySystem library) {
        if (library.getBooks().isEmpty()) {
            ConsoleUtil.printBox("CATALOG STATUS", "No books in catalog.");
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
                    String typeLabel = copy.isEbook() ? "E-Book" : "Physical";
                    sb.append(String.format("  - Copy ID: %s | Type: %s | Status: %s | Condition: %s\n",
                            copy.getCopyId(), typeLabel, copy.getStatus(), copy.getCondition()));
                }
            }
            sb.append("------------------------------------------------------------\n");
        }
        ConsoleUtil.printBox("BOOK CATALOG", sb.toString().trim());
    }

    public static void showSearchResults(List<Book> results) {
        if (results.isEmpty()) {
            ConsoleUtil.printBox("SEARCH RESULTS", "No matching books found.");
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (Book b : results) {
            sb.append(String.format("Book ID : %s | Title: %s (by %s)\n", b.getBookId(), b.getTitle(), b.getAuthor()));
            sb.append(String.format("Category: %s | Available Copies: %s\n", b.getCategory(), b.hasAvailableCopy() ? "Yes" : "No"));
            sb.append("------------------------------------------------------------\n");
        }
        ConsoleUtil.printBox("SEARCH RESULTS", sb.toString().trim());
    }

    public static void showMembers(LibrarySystem library) {
        if (library.getMembers().isEmpty()) {
            ConsoleUtil.printBox("MEMBER MANAGEMENT", "No registered members.");
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (Member m : library.getMembers()) {
            sb.append(m.getMemberDetails()).append("\n");
        }
        ConsoleUtil.printBox("REGISTERED MEMBERS", sb.toString().trim());
    }

    public static void showLoans(LibrarySystem library) {
        if (library.getLoans().isEmpty()) {
            ConsoleUtil.printBox("LOAN MANAGEMENT", "No loans recorded in the system.");
            return;
        }
        StringBuilder sb = new StringBuilder();
        library.getLoans().forEach(l -> sb.append(l).append("\n"));
        ConsoleUtil.printBox("ALL RECORDED LOANS", sb.toString().trim());
    }

    public static void showMemberLoans(Member member, LibrarySystem library) {
        List<Loan> active = library.getCurrentLoans(member.getMemberId());
        StringBuilder current = new StringBuilder();
        if (active.isEmpty()) current.append("No active loans.");
        else active.forEach(l -> current.append(l).append("\n"));
        ConsoleUtil.printBox("CURRENT ACTIVE LOANS", current.toString().trim());

        List<Loan> history = library.getLoanHistory(member.getMemberId());
        StringBuilder past = new StringBuilder();
        if (history.isEmpty()) past.append("No loan history.");
        else history.forEach(l -> past.append(l).append("\n"));
        ConsoleUtil.printBox("LOAN HISTORY", past.toString().trim());
    }
}
