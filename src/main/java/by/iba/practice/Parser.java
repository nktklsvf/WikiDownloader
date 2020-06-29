package by.iba.practice;

import org.apache.commons.lang3.StringEscapeUtils;
import org.jsoup.Jsoup;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Parser {
    private StringBuilder result;

    public Parser() {
        result = new StringBuilder();
    }

    private String getHTMLFromPage(String s) {
        final String HTML_START = "<div class=\"mw-parser-output\">";
        final String HTML_END = "</text>";

        s = StringEscapeUtils.unescapeHtml4(s);

        return s.substring(s.indexOf(HTML_START), s.indexOf(HTML_END));
    }

    private void parseTitle(String page) {
        final String REGEX_TITLE = "<parse title=\"(.+?)\"";
        final int MATCH_INDEX = 1;

        Pattern pattern = Pattern.compile(REGEX_TITLE);
        Matcher matcher = pattern.matcher(page);

        if (matcher.find()) {
            result.append(matcher.group(MATCH_INDEX))
                    .append(Constants.LINE_SEPARATOR)
                    .append(Constants.LINE_SEPARATOR);
        }
    }

    private void parseBody(String page) {
        final String PARSER_OUTPUT_CSS_QUERY = "div.mw-parser-output";

        result.append(PlainText.getPlaintTextFromElement(Jsoup.parse(getHTMLFromPage(page)).select(PARSER_OUTPUT_CSS_QUERY).first()));
    }

    public String parse(String page) {
        page = StringEscapeUtils.unescapeHtml4(page);
        parseTitle(page);
        parseBody(page);

        return getResult();
    }

    public String getResult() {
        return result.toString();
    }
}
