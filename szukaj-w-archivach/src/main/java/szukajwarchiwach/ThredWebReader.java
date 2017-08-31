/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package szukajwarchiwach;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
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
public final class ThredWebReader implements Callable<Set> {

    private Document d;
    private volatile ArrayList<String> webPages = new ArrayList<>();
    private final int numberStart;
    private final int nrOfThread;
    private final int numberEnd;
    private HashSet<String> setOfAddressesWithDigital = new HashSet<>();
    private HashSet<String> linksToArchivesAlreadyRead = new HashSet<>();

    private final String url = "http://szukajwarchiwach.pl/search?q=XTYPEro%3Azesp%20jedn%20obie&order=&rpp=100";

    public ThredWebReader(HashSet<String> linksToArchivesAlreadyRead, ArrayList<String> webPages,
            int numberOfThread, int numberStart, int numberEnd) throws UnsupportedEncodingException, IOException {

        this.linksToArchivesAlreadyRead = linksToArchivesAlreadyRead;
        this.webPages = webPages;
        this.numberStart = numberStart;
        this.numberEnd = numberEnd;
        this.nrOfThread = numberOfThread;
    }

    @Override
    public Set call() throws Exception {
        try {
            setOfAddressesWithDigital = addAddressesWithDigital();
        } catch (IOException ex) {
            Logger.getLogger(ThredWebReader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return setOfAddressesWithDigital;
    }

    public HashSet<String> addAddressesWithDigital() throws IOException {
        String link;
        String MAIN_URL = "szukajwarchiwach.pl";
        HashSet<String> map = new HashSet<>();
        boolean first = true;
        FileHelper rF;

        synchronized (this) {
            rF = new FileHelper();
        }

        for (String urlrrrr : webPages) {
            d = Jsoup.connect(urlrrrr).timeout(0).get();
            Elements elem = d.select("#resultList");
            for (Element e : elem.select(".search_result_clickable.zespolBg")) {
                link = e.select("a[href]").toString();
                link = link.substring(0, link.indexOf("\" class"));
                link = link.substring(link.indexOf("href") + 6);

                String digitalCopies = e.select(".search_result_skany").text();
                if (!digitalCopies.equals("Digital copies: 0")) {
                    synchronized (this) {
                        System.out.print("*");
                        String url = "addressesWithDigitaData.txt";
                        rF.writeToFile(url,link);
                    }
                }
            }
        }
        return map;
    }

    public HashSet<String> getSetOfAddressesWithDigital() {
        return setOfAddressesWithDigital;
    }

}
