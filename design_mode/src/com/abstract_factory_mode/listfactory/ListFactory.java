package com.abstract_factory_mode.listfactory;

import com.abstract_factory_mode.factory.Factory;
import com.abstract_factory_mode.factory.Link;
import com.abstract_factory_mode.factory.Page;
import com.abstract_factory_mode.factory.Tray;

public class ListFactory extends Factory {

    @Override
    public Link createLink(String caption, String url) {
        return new ListLink(caption, url);
    }

    @Override
    public Tray createTray(String caption) {
        return new ListTray(caption);
    }

    @Override
    public Page createPage(String title, String author) {
        return new ListPage(title, author);
    }
}
