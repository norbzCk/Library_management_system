package Util;

// Small bag of console helpers: clearing the screen and drawing boxes.
public class ConsoleUtil {

    // ANSI trick to wipe the terminal and move the cursor home.
    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    // Draws a simple boxed panel around some text, used by most reports.
    public static void printBox(String title, String content) {
        String[] lines = content.split("\n", -1);
        int maxLen = title.length();
        for (String line : lines) {
            if (line.length() > maxLen) {
                maxLen = line.length();
            }
        }
        int width = maxLen + 4; // 2 spaces of padding on each side

        System.out.println("┌" + "─".repeat(width) + "┐");

        if (!title.isEmpty()) {
            int paddingLeft = (width - title.length()) / 2;
            int paddingRight = width - title.length() - paddingLeft;
            System.out.println("│" + " ".repeat(paddingLeft) + title + " ".repeat(paddingRight) + "│");
            System.out.println("├" + "─".repeat(width) + "┤");
        }

        for (String line : lines) {
            int paddingRight = Math.max(0, (width - 2) - line.length());
            System.out.println("│  " + line + " ".repeat(paddingRight) + "│");
        }

        System.out.println("└" + "─".repeat(width) + "┘");
    }
}
