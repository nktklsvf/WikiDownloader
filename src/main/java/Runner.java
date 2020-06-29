import by.iba.practice.*;

import java.io.FileNotFoundException;
import java.io.IOException;

public class Runner {
    public static void main(String[] args) {
        /*
        * params:
        * 0 - Category Name
        * 1 - Amount
        * 2 - Output Folder
        * */
        final int ARGUMENTS_AMOUNT = 3;
        final int CATEGORY_INDEX = 0;
        final int AMOUNT_INDEX = 1;
        final int PATH_INDEX = 2;

        if (args.length == ARGUMENTS_AMOUNT) {
            try {
                final String category = args[CATEGORY_INDEX];
                final int amount = Integer.parseInt(args[AMOUNT_INDEX]);
                final String path = args[PATH_INDEX];

                Downloader downloader = new Downloader(new Category(category));
                downloader.download(amount, path);
            } catch (FileNotFoundException e) {
                System.out.println("Incorrect folder path");
                e.printStackTrace();
            } catch (NumberFormatException e) {
                System.out.println("Incorrect amount");
                e.printStackTrace();
            } catch (IOException e) {
                System.err.println("Error occurred on GET request");
                e.printStackTrace();
            }

        } else {
            System.out.println("Incorrect input");
        }

    }
}
