package Util;

import Service.LibrarySystem;
import Model.Book;
import Model.BookCopy;
import Model.StudentMember;
import Model.FacultyMember;

import java.time.LocalDate;

// Drops in the demo catalog, members, an overdue loan, and a couple of announcements.
public class SampleData {

    public static void seed(LibrarySystem library) {
        BookCopy.MediaType P = BookCopy.MediaType.PHYSICAL;
        BookCopy.MediaType E = BookCopy.MediaType.EBOOK;

        library.addBook("B001", "Clean Code", "9780132350884", 2008, "Robert Martin", "Programming", P, 2);
        library.addBook("B002", "Effective Java", "9780134685991", 2018, "Joshua Bloch", "Programming", E, 3);
        library.addBook("B003", "Design Patterns", "9780201633610", 1994, "Erich Gamma", "Software Engineering", P, 2);
        library.addBook("B004", "Introduction to Algorithms", "9780262033848", 2009, "Thomas Cormen", "Computer Science", P, 1);
        library.addBook("B005", "The Pragmatic Programmer", "9780135957059", 2019, "Andrew Hunt", "Programming", E, 2);
        library.addBook("B006", "Java: A Beginner's Guide", "9781260463415", 2020, "Herbert Schildt", "Programming", P, 1);
        library.addBook("B007", "Core Java Volume I", "9780135166307", 2019, "Cay Horstmann", "Programming", P, 2);
        library.addBook("B008", "Clean Architecture", "9780134494166", 2017, "Robert Martin", "Programming", P, 1);
        library.addBook("B009", "Clean Agile", "9780135781869", 2019, "Robert Martin", "Programming", E, 2);

        Book cleanCode = library.findBook("B001");
        if (cleanCode != null) {
            cleanCode.addCopy(new BookCopy("BC001_2", "Good", P));
        }
        Book effectiveJava = library.findBook("B002");
        if (effectiveJava != null) {
            effectiveJava.addCopy(new BookCopy("BC002_2", "New", P));
        }

        library.registerMember(new StudentMember("S001", "Norbert Kiyagi", "norbert@ifm.ac.tz", "0700000001", "IFM"));
        library.registerMember(new FacultyMember("F001", "Dr. Said", "said@ifm.ac.tz", "0700000002", "Computer Science"));

        // A loan taken out 20 days ago so the overdue path is demonstrable.
        library.borrowBook("S001", "BC001", LocalDate.now().minusDays(20));

        library.broadcastAnnouncement("Sophia", "Welcome to the new academic year! We have added 9 standard reference books to the catalog.");
        library.broadcastAnnouncement("Joel", "The library will be closed on Friday for inventory check. Please plan returns accordingly.");
    }
}
