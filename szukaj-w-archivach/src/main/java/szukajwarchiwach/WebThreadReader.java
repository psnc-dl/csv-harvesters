/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package szukajwarchiwach;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author PCSS
 */
public class WebThreadReader implements Runnable {

    private Document d;
    private ArrayList<String> webPages = new ArrayList<>();

    public WebThreadReader(ArrayList<String> webPages) {
        this.webPages = webPages;
    }

    @Override
    public void run() {
        try {
            addAddressesWithDigital();
        } catch (IOException ex) {
            Logger.getLogger(WebThreadReader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean isAbleToConnect(String url) throws IOException {
        ConnectionChecker cc = new ConnectionChecker();
        return cc.isValidLink(url);
    }

    public void addAddressesWithDigital() throws IOException {
        String link;
        FileHelper rF;

        synchronized (this) {
            rF = new FileHelper();
        }

        for (String url : webPages) {
            if (isAbleToConnect(url)) {
                d = Jsoup.connect(url).timeout(0).get();
                Elements elem = d.select("#resultList");
                for (Element e : elem.select(".search_result_clickable.zespolBg")) {
                    link = e.select("a[href]").toString();
                    link = link.substring(0, link.indexOf("\" class"));
                    link = link.substring(link.indexOf("href") + 6);

                    String digitalCopies = e.select(".search_result_skany").text();
                    if (!digitalCopies.equals("Digital copies: 0")) {
                        synchronized (this) {
                            System.out.print("*");
                            String MAIN_URL = "http://szukajwarchiwach.pl";
                            String writeTo = "addressesWithDigitalData.txt";
                            rF.writeToFile(writeTo, MAIN_URL + link);
                        }
                    }
                }
            }
        }
    }
}
