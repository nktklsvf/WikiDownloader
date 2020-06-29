package by.iba.practice;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Downloader {
    private Category category;

    public Downloader() {

    }

    public Downloader(Category category) {
        this.category = category;
    }

    private Map<String, String> generateRequestParams(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("action", "parse");
        params.put("page", name);
        params.put("prop", "text");
        params.put("format", "xml");

        return params;
    }

    public void download(int amount, String path) throws IOException {
        category.updatePages(amount);

        for (String name : category.getPageList()) {
            String filename = String.format("%s\\%s.txt", path, name);
            Parser parser = new Parser();

            String page = Utils.sendGETRequest(Constants.API_URL, generateRequestParams(name));

            Utils.saveStringToFile(filename, parser.parse(page));
            System.out.println("Downloaded " + filename);
        }
    }
}
