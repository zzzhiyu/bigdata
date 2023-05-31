package com.abstract_factory_mode.listfactory;

import com.abstract_factory_mode.factory.Link;

public class ListLink extends Link {
    public ListLink(String caption, String url) {
        super(caption, url);
    }

    @Override
    public String makeHTML() {
        return "    <li><a href=\"" + url + "\">>" + caption + "</a></li>\n";
    }
}
