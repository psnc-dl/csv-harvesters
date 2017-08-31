/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package szukajwarchiwach;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.ExecutionException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;

public class SzukajWArchiwach {

    private Document d;
    private final String url = "http://szukajwarchiwach.pl/search?q=XTYPEro:zesp%20jedn%20obie&rpp=100&order=&page=";
    private final int numberOfThreads = 5;
    private int webPagesNumber;
    private final ArrayList<String> webPages;
    private HashSet<String> linksToArchivesAlreadyRead = new HashSet<>();
    HashSet<String> toRead = new HashSet<>();

    public static void main(String[] args) throws IOException, IllegalArgumentException, IllegalAccessException, InterruptedException, ExecutionException {
        SzukajWArchiwach a = new SzukajWArchiwach();
        a.parseAllTheArchives();
    }

    public SzukajWArchiwach() {
        this.webPages = new ArrayList<>();
    }

    /**
     * Setting number of pages with archives getting all the data about each
     * archive ( not jet, now about one of them )
     *
     * @throws IOException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public void parseAllTheArchives() throws IOException, IllegalArgumentException, IllegalAccessException, InterruptedException, ExecutionException {
        CsvHelper c = new CsvHelper();
        linksToArchivesAlreadyRead = c.readArchivesAlreadyRead();

        this.webPagesNumber = 5000;//setWebPagesRecursive(url, 1);
        setWebPagesUrl();

        FileHelper rf = new FileHelper();
        int mode = rf.readMode();
        ArrayList<String> addresses = rf.readAddressesFromFile();

        switch (mode) {
            case (1):
                // reading new addresses from webPages list
                getArchivesWithDigital();
                break;

            case (2):
                // parsing archives that have urls already read from file
                readArchives(addresses);
                break;

            case (3):
                // reading new addresses and parsing archives from them                
                getArchivesWithDigital();
                addresses = rf.readAddressesFromFile();
                readArchives(addresses);
                break;

        }
    }

    public boolean readArchives(ArrayList<String> addresses) throws IOException, IllegalArgumentException, IllegalAccessException {
        if (!addresses.isEmpty()) {
            for (String s : addresses) {
                if (!linksToArchivesAlreadyRead.contains(s)) {
                    toRead.add(s);
                } else {
                    System.out.print("-");
                }
            }
            getArchivesRead();
            return true;
        } else {
            return false;
        }
    }

    /**
     * Read all archives from "http://www.szukajwarchiwach.pl/"
     *
     * @throws IOException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public void getArchivesRead() throws IOException, IllegalArgumentException, IllegalAccessException {
        System.out.println("READING ARCHIVES ");
        int i;
        int startNumber = 0;
        int endNumber;
        int numberOfArchivesToRead = toRead.size();
        System.out.println("number of archives to read: " + numberOfArchivesToRead);
        int perOneThread = (numberOfArchivesToRead - startNumber) / numberOfThreads;

        Thread[] threads = new Thread[numberOfThreads];
        for (i = 0; i < numberOfThreads; i++) {
            startNumber = i * perOneThread;
            endNumber = startNumber + perOneThread - 1;
            if (i == numberOfThreads - 1) {
                endNumber = numberOfArchivesToRead;
            }
            ArrayList<String> list = partOfSetForThread(startNumber, endNumber);
            Thread thread = new Thread(new ThreadArchiveReader(linksToArchivesAlreadyRead, list, startNumber, endNumber));
            threads[i] = thread;
        }
        i = 0;
        while (i < numberOfThreads) {
            threads[i].start();
            i++;
        }
    }

    /**
     * Getting part of webPage list for a thread to read new addresses with
     * digital data from
     *
     * @param start where thread should start reading
     * @param end -||- stop
     * @return
     */
    public ArrayList<String> partOfWebPages(int start, int end) {
        ArrayList<String> list = new ArrayList<>();
        for (int j = start; j < end; j++) {
            list.add(this.webPages.get(j));
        }
        return list;
    }

    /**
     * Getting part of list for a thread
     *
     * @param start number where thread should start parsing
     * @param end number where thread should end parsing
     * @return sublist of list of archives
     */
    public ArrayList<String> partOfSetForThread(int start, int end) {
        ArrayList<String> list = new ArrayList<>();
        ArrayList<String> addressesToRead = new ArrayList<>();
        addressesToRead.addAll(toRead);
        for (int j = start; j < end; j++) {
            list.add(addressesToRead.get(j));
        }
        return list;
    }

    /**
     * Count where should thread start parsing
     *
     * @param threadId id of thread
     * @param numberOfThreads
     * @param startNumber
     * @param endNumber
     * @param perOneThread how many pages should one thread parse
     * @return
     */
    public int countStartNumber(int threadId, int numberOfThreads, int startNumber, int endNumber, int perOneThread) {
        return threadId * perOneThread;
    }

    /**
     * Getting part of list for a thread
     *
     * @param start number where thread should start parsing
     * @param end number where thread should end parsing
     * @param pagesToRead
     * @return sublist of list of archives
     */
    public ArrayList<String> partOfSetForThread(int start, int end, ArrayList<String> pagesToRead) {
        ArrayList<String> list = new ArrayList<>();
        end += 1;
        if (pagesToRead.size() == end - 1) {
            end = pagesToRead.size();
        }
        for (int j = start; j < end; j++) {
            list.add(pagesToRead.get(j));
        }
        return list;
    }

    /**
     * Reading addresses of archives with digital data
     */
    public void getArchivesWithDigital() throws InterruptedException {
        System.out.println("READING ADDRESSES OF ARCHIVES WITH DIGITAL DATA");
        int i;
        int startstart = 1500;
        int startNumber = startstart;
        int endExit = this.webPagesNumber ;

        int perOneThread = (endExit - startNumber) / numberOfThreads;
        Thread[] threads = new Thread[numberOfThreads];

        for (i = 0; i < numberOfThreads; i++) {
            startNumber = i * perOneThread + startstart;
            int endNumber = startNumber + perOneThread - 1;
            if (i == numberOfThreads - 1) {
                endNumber = endExit;
            }
            ArrayList<String> partOfSetForThread = partOfWebPages(startNumber, endNumber);
            Thread thread = new Thread(new WebThreadReader(partOfSetForThread));
            threads[i] = thread;
        }
        for (Thread t : threads) {
            t.start();
        }
        for (i = 0; i < threads.length; i++) {
            threads[i].join();
        }
    }

    /**
     * Setting names of all the web pages url
     */
    public void setWebPagesUrl() {
        String basicUrl = "http://szukajwarchiwach.pl/search?q=XTYPEro:zesp%20jedn%20obie&rpp=100&order=&page=";
        webPages.add(url);
        for (int i = 0; i < webPagesNumber; i++) {
            webPages.add(basicUrl + (i + 2));
        }
    }

    /**
     * Checking connection to url
     *
     * @param url
     * @return if can connect
     * @throws IOException
     */
    public boolean isAbleToConnect(String url) throws IOException {
        ConnectionChecker cc = new ConnectionChecker();
        return cc.isValidLink(url);
    }

    /**
     * Set number of pages from "szukajwarchiwach.pl"
     *
     * @param url web page address
     * @return number of pages in navigation of that page
     * @throws IOException
     */
    public int setWebPageNumber(String url) throws IOException {
        int max = 0;
        if (this.isAbleToConnect(url)) {
            d = Jsoup.connect(url).get();
            Elements elem = d.select(".container");
            for (Element e : elem.select(".pagerBox").select("a[href]")) {
                try {
                    if (Integer.parseInt(e.text()) > max) {
                        max = Integer.parseInt(e.text());
                    }
                } catch (NumberFormatException error) {
                    System.out.println("Error parsing string to int");
                }
            }
        }
        return max;
    }

    /**
     * Set total number of pages // recursive because in navigation there isn't
     * total in first place
     *
     * @param url
     * @param lastMax
     * @return total number of pages
     * @throws IOException
     */
    public int setWebPagesRecursive(String url, int lastMax) throws IOException {
        int max = 0;
        if (isAbleToConnect(url)) {
            try {
                d = Jsoup.connect(url + lastMax).timeout(30000).get();

            } catch (java.net.SocketTimeoutException e) {
                System.out.println("Timeout Exception");
            }

            Elements elem = d.select(".container");
            for (Element e : elem.select(".pagerBox").select("a[href]")) {
                try {
                    if (Integer.parseInt(e.text()) > max) {
                        max = Integer.parseInt(e.text());
                    }
                } catch (NumberFormatException error) {
                }
            }

            for (Element e : elem.select(".pagerBox").select("strong")) {
                try {
                    if (Integer.parseInt(e.text()) > max) {
                        max = Integer.parseInt(e.text());
                    }
                } catch (NumberFormatException error) {
                    System.out.println("Error converting String to int ");
                }
            }

            if (lastMax == max) {
                return max;
            } else {
                return setWebPagesRecursive(url, max);
            }
        }
        return this.setWebPageNumber(url);
    }
}
