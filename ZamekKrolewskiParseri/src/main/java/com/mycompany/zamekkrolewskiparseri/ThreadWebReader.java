/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.zamekkrolewskiparseri;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.Callable;
import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author PCSS
 */
public class ThreadWebReader implements Callable {

    private final int id;
    private final String MAIN_URL = "https://kolekcja.zamek-krolewski.pl";
    private ArrayList<String> itemsUrls = new ArrayList<>();
    private ArrayList<String> sublist = new ArrayList<>();
    private HashSet<String> itemsAlreadyRead = new HashSet<>();
    private HashSet<String> notToRead = new HashSet<>();

    public ThreadWebReader(int id, ArrayList<String> partOfSetForThread, HashSet<String> itemsAlreadyRead,
            HashSet<String> notToRead) throws IOException {

        this.notToRead = notToRead;
        this.itemsAlreadyRead = itemsAlreadyRead;
        this.sublist = partOfSetForThread;
        this.id = id;
    }

    @Override
    public Object call() throws Exception {
        for (String url : sublist) {
            parseItemsFromList(url);
        }
        return itemsUrls;
    }

    /**
     * Checking if Jsoup can connect with url
     *
     * @param url of page that we want to connect to
     * @return if page is valid
     * @throws IOException
     */
    public boolean isValidLink(String url) throws IOException {
        int attempt = 0;
        Response response = null;
        while (attempt < 3) {
            try {
                Connection connection = Jsoup.connect(url).timeout(10000);
                connection.followRedirects(false);
                response = connection.execute();
            } catch (java.net.SocketTimeoutException e) {
                System.out.println("TIME OUT!");
            }
            if (response != null) {
                switch (response.statusCode()) {
                    case 404:
                        return false;
                    case 200:
                        return true;
                    default:
                        attempt++;
                        break;
                }
            }
        }
        return false;
    }

    public String check(String url) throws IOException {
        String newUrl = url;
        if (isValidLink(url)) {
            Document d = Jsoup.connect(url).timeout(5000).get();
            Elements elements = d.select(".content_box.box_wrapper.box_medium_dark.box_pagination").select(".content_wrapper");
            String title = elements.select("a").attr("title");
            if (title.equals("Pokaż wszystkie wyniki na jednej stronie")) {
                newUrl = MAIN_URL + elements.select("a").attr("href").substring(1);
            }
        }
        return newUrl;
    }

    /**
     * Parsing pages to get items urls
     *
     * @param url of subcategory (where are many items )
     * @throws IOException
     */
    public void parseItemsFromList(String url) throws IOException {
        String newUrl = "";

        url = check(url);

        if (isValidLink(url)) {
            Document d = Jsoup.connect(url).timeout(5000).get();
            Elements elem = d.select(".collection_item");

            for (Element e : elem) {
                String itemUrl = MAIN_URL + e.select("a").attr("href");
                d = Jsoup.connect(itemUrl).get();
                for (Element e3 : d.select(".collection_item").select("h3").select("a")) {
                    String postfix = e3.attr("href");
                    if (!postfix.equals("")) {
                        newUrl = MAIN_URL + postfix;
                        if (isValidLink(newUrl)) {
                            if (!this.itemsAlreadyRead.contains(newUrl)) {
                                if (!notToRead.contains(newUrl)) {
                                    System.out.println("*");
                                    itemsUrls.add(newUrl);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
