package by.iba.practice;

import org.apache.commons.lang3.StringEscapeUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Category {
    private String name;
    private List<String> pageList;

    public Category() {

    }

    public Category(String name) {
        this.name = name;
        pageList = new ArrayList<>();
    }

    private String parseCMContinue(String page) {
        final String START_WITH = "<continue cmcontinue=\"";
        final String END_WITH = "\" continue=\"";

        int startIndex = page.indexOf(START_WITH) + START_WITH.length();
        int endIndex = page.indexOf(END_WITH);

        if (endIndex == -1) {
            return "";
        } else {
            return page.substring(startIndex, endIndex);
        }
    }

    private int addPages(String page) {
        final String PAGE_NAME_REGEXP = "title=\"(.+?)\"";
        final int MATCH_INDEX = 1;

        Pattern p = Pattern.compile(PAGE_NAME_REGEXP);
        Matcher m = p.matcher(page);

        int foundCounter = 0;

        while (m.find()) {
            pageList.add(StringEscapeUtils.unescapeHtml4(m.group(MATCH_INDEX).replace(" ", "_")));
            foundCounter++;
        }

        return foundCounter;
    }

    private Map<String, String> generateRequestParams(int amount, String cmContinue) {
        Map<String, String> params = new HashMap<>();
        params.put("action", "query");
        params.put("list", "categorymembers");
        params.put("cmtitle", "Category:" + name);
        params.put("cmlimit", String.valueOf(amount));
        params.put("cmcontinue", cmContinue);
        params.put("format", "xml");

        return params;
    }

    public void updatePages(int amount) throws IOException {
        pageList.clear();

        final int MAX_AMOUNT = 500;

        String cmContinue = "";

        while (amount > 0) {
            int currentAmount;
            if (amount > MAX_AMOUNT) {
                currentAmount = MAX_AMOUNT;
                amount -= MAX_AMOUNT;
            } else {
                currentAmount = amount;
                amount = 0;
            }

            String page = StringEscapeUtils.unescapeHtml4(Utils.sendGETRequest(Constants.API_URL, generateRequestParams(currentAmount, cmContinue)));

            cmContinue = parseCMContinue(page);

            if (addPages(page) < currentAmount) {
                amount = 0;
            }
        }
    }

    public List<String> getPageList() {
        return pageList;
    }
}
