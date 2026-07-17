package Menu;

import Service.LibrarySystem;
import Util.ConsoleUtil;
import Util.InputHelper;
import Portal.StudentPortal;
import Portal.StaffPortal;

// The top-level menu: pick a portal and hand control over to it.
public class MainMenu {

    private final LibrarySystem library;

    public MainMenu(LibrarySystem library) {
        this.library = library;
    }

    public void start() {
        boolean running = true;
        while (running) {
            display();
            int choice = InputHelper.readInt("Enter choice: ");
            switch (choice) {
                case 1:
                    new StudentPortal(library).run();
                    break;
                case 2:
                    new StaffPortal(library).run();
                    break;
                case 0:
                    System.out.println("\nThank you for using IFM Library. Goodbye.");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private void display() {
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
}
