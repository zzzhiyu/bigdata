package com.abstract_factory_mode.listfactory;

import com.abstract_factory_mode.factory.Item;
import com.abstract_factory_mode.factory.Page;

public class ListPage extends Page {

    public ListPage(String title, String author) {
        super(title, author);
    }

    @Override
    public String makeHTML() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("<html><head><title>").append(title).append("</head></title>\n");
        buffer.append("<body>\n");
        buffer.append("<h1>").append(title).append("</h1>");
        buffer.append("<ul>\n");
        for (Item item: content){
            buffer.append(item.makeHTML());
        }
        buffer.append("</ul>\n");
        buffer.append("<hr><address>").append(author).append("</address>");
        buffer.append("</body></html>\n");
        return buffer.toString();
    }
}
