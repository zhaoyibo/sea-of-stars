package com.haoyizebo;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws IOException {
        Document doc = Jsoup.connect("http://bbs.8264.com/forum-161-1.html").get();
        Element bbslistbox = doc.select(".bbslistbox").get(2);

        Elements elements = bbslistbox.select("[id^=normalthread]");
        Element first = elements.first();

        Element a = first.select("h2 a").first();
        System.out.println(a.text());
        System.out.println(a.absUrl("href"));

        Elements tds = first.select("td:has(span.d_block)");
        Element td1 = tds.get(0);
        Element td2 = tds.get(1);
        Element td3 = tds.get(2);
        System.out.println("发帖者：" + td1.select("span a").first().text() + "\t发布日期：" + td1.select("em").first().text());
        System.out.println("回复量：" + td2.select("span a").first().text() + "\t查看量：" + td2.select("em").first().text());
        System.out.println("最后回复：" + td3.select("span a").first().text() + "\t回复时间：" + td3.select("span a").last().text());
    }
}
