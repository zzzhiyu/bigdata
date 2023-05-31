package com.abstract_factory_mode.factory;

public class Main {
    public static void main(String[] args) {
        Factory factory = Factory.getFactory("com.abstract_factory_mode.listfactory.ListFactory");
        Link people = factory.createLink("人民日报", "http://www.people.com.cn/");
        Link gmw = factory.createLink("光明日报", "http://www.gmw.cn/");
        Tray trayNews = factory.createTray(" 日报");
        trayNews.add(people);
        trayNews.add(gmw);

        Page page = factory.createPage("Linkpage", "杨文轩");
        page.add(trayNews);

        page.output();
    }
}
