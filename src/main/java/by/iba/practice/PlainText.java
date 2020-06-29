package by.iba.practice;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import java.util.List;

public class PlainText {
    public enum ElementType {
        TEXT {
            @Override
            public StringBuilder getPlainText(Element element) {
                StringBuilder result = new StringBuilder();
                String text = element.text();
                if (text.length() > 0) {
                    result.append(text)
                            .append(Constants.LINE_SEPARATOR);
                }
                return result;
            }
        },
        CONTENTS {
            @Override
            public StringBuilder getPlainText(Element element) {
                StringBuilder result = new StringBuilder();
                final String CONTENTS_HEADER_CSS_QUERY = "mw-toc-heading";

                result.append(Constants.LINE_SEPARATOR)
                        .append(element.getElementById(CONTENTS_HEADER_CSS_QUERY).text())
                        .append(Constants.LINE_SEPARATOR)
                        .append(Constants.LINE_SEPARATOR)
                        .append(ElementType.LIST.getPlainText(element.select("ul").first()));

                return result;
            }
        },
        SUB_HEADER {
            @Override
            public StringBuilder getPlainText(Element element) {
                final String SUB_HEADER_CSS_QUERY = "span.mw-headline";
                return new StringBuilder(element.select(SUB_HEADER_CSS_QUERY).text()).append(Constants.LINE_SEPARATOR);
            }
        },
        HEADER {
            @Override
            public StringBuilder getPlainText(Element element) {
                return SUB_HEADER.getPlainText(element).insert(0, Constants.LINE_SEPARATOR);
            }
        },
        LIST {
            private StringBuilder parseList(Element element, int depthLevel) {
                StringBuilder result = new StringBuilder();
                Elements elements = element.children();
                for (Element el : elements) {
                    List<Node> children = el.childNodes();

                    for (int i = 0; i < depthLevel; i++) {
                        result.append(Constants.TAB);
                    }

                    for (Node childNode : children) {
                        // probably bad solution
                        if (childNode instanceof Element) {
                            Element childElement = (Element) childNode;
                            if (childElement.tag().normalName().equals("ul")) {
                                result.append(Constants.LINE_SEPARATOR)
                                        .append(parseList(childElement, depthLevel + 1))
                                        .deleteCharAt(result.length() - 1);
                            } else {
                                result.append(childElement.text());
                            }
                        } else {
                            result.append(childNode.toString());
                        }
                    }
                    result.append(Constants.LINE_SEPARATOR);
                }
                return result;
            }

            @Override
            public StringBuilder getPlainText(Element element) {
                final int DEFAULT_DEPTH_LEVEL = 1;
                return parseList(element, DEFAULT_DEPTH_LEVEL);
            }
        },
        REFERENCES {
            @Override
            public StringBuilder getPlainText(Element element) {
                final String REFERENCES_CSS_QUERY = "ol.references";
                final String REFERENCE_TEXT_CSS_QUERY = "span.reference-text";

                StringBuilder result = new StringBuilder();
                Elements els = element.select(REFERENCES_CSS_QUERY).first().children();
                for (Element el : els) {
                    String text = el.select(REFERENCE_TEXT_CSS_QUERY).text();
                    if (text.length() > 0) {
                        result.append(Constants.TAB)
                                .append(text)
                                .append(Constants.LINE_SEPARATOR);
                    }
                }
                return result;
            }
        },
        DIV {
            @Override
            public StringBuilder getPlainText(Element element) {
                StringBuilder result = new StringBuilder();
                for (Element el: element.children()) {
                    result.append(PlainText.getPlaintTextFromElement(el));
                }
                return result;
            }
        },
        UNUSED {
            @Override
            public StringBuilder getPlainText(Element element) {
                return new StringBuilder();
            }
        };
        public abstract StringBuilder getPlainText(Element element);
    }

    public static ElementType getTypeFromElement(Element element) {
        String tag = element.tag().normalName();
        ElementType elementType;

        if (tag.equals("p")) {
            elementType = ElementType.TEXT;
        }
        else if (tag.equals("div") && element.attr("id").equals("toc")) {
            elementType = ElementType.CONTENTS;
        }
        else if (tag.equals("h3") || tag.equals("h4")) {
            elementType = ElementType.SUB_HEADER;
        }
        else if (tag.equals("h2")) {
            elementType = ElementType.HEADER;
        }
        else if (tag.equals("ul")) {
            elementType = ElementType.LIST;
        }
        else if (tag.equals("div") && element.children().hasClass("references")) {
            elementType = ElementType.REFERENCES;
        }
        else if (tag.equals("div")) {
            elementType = ElementType.DIV;
        }
        else elementType = ElementType.UNUSED;

        return elementType;
    }

    public static String getPlaintTextFromElement(Element element) {
        return getTypeFromElement(element).getPlainText(element).toString();
    }


}
