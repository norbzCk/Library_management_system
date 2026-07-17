import Menu.MainMenu;
import Service.LibrarySystem;
import Util.SampleData;

public class Main {
    public static void main(String[] args) {
        
        LibrarySystem library = new LibrarySystem("IFM Library");

        SampleData.seed(library);

        new MainMenu(library).start();
    }
}
