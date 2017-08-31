/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.zamekkrolewskiparseri;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;

public class Parser {

    private final int numberOfThreads = 20;
    private final String MAIN_URL = "https://kolekcja.zamek-krolewski.pl";
    private final String URL_COLLECTIONS = "https://kolekcja.zamek-krolewski.pl/kolekcje";
    private ArrayList<String> itemsToRead = new ArrayList<>();
    private HashSet<String> itemsAlreadyRead = new HashSet<>();

    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
        Parser parser = new Parser();
        parser.doAllTheJob();
    }

    public void doAllTheJob() throws IOException, InterruptedException, ExecutionException {
        Parser parser = new Parser();
        CsvHelper csv = new CsvHelper();
        FileHelper fh = new FileHelper();

        HashSet<String> notToRead = new HashSet<>();       // if i want to read new urls only ->  fh.readAddresses();
        ArrayList<String> categories = parser.getLinksToItemLists();
        ArrayList<String> allCattegoriesWithSubCategories = new ArrayList<>();

        for (String itemlist : categories) {
            HashSet<String> toAdd = parser.getAllCattegoriesWithSubCategories(itemlist);
            allCattegoriesWithSubCategories.addAll(toAdd);
        }

        //   not to read contains:
        // * all categories because some erronous links leads to them 
        // * links that has already been read ( from FILE  ->  future from excel ? )
        ///HashSet<String> readAddresses = fh.readAddresses(); // reading from urls already read ( good for now ) 
        parser.itemsAlreadyRead = csv.readItemsAlreadyRead(); // read from csv file
        notToRead.addAll(categories);
        notToRead.addAll(parser.itemsAlreadyRead);

        System.out.println("reading pages ");
        parser.itemsToRead = parser.readWebPages(allCattegoriesWithSubCategories, notToRead);
        System.out.println("reading items ");
        parser.readAllTheItemsByThreads();
    }

    /**
     * Reading items from itemsToRead
     */
    public void readAllTheItemsByThreads() {
        for (int i = 0; i < numberOfThreads; i++) {
            int perOneThread = itemsToRead.size() / numberOfThreads;
            int start = i * perOneThread;
            int end = countEndNumber(i, perOneThread, itemsToRead.size());

            ArrayList<String> partOfSetForThread = partOfSetForThread(start, end, itemsToRead);
            ThreadItemReader tI = new ThreadItemReader(partOfSetForThread);
            tI.run();
        }
    }

    /**
     * Creating threads that will read urls of items
     *
     * @param allCattegoriesWithSubCategories
     * @param categories
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws IOException
     */
    public ArrayList<String> readWebPages(ArrayList<String> allCattegoriesWithSubCategories, HashSet<String> categories) throws InterruptedException, ExecutionException, IOException {
        List<Future<ArrayList<String>>> list = new ArrayList<>();
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        int perOneThread = allCattegoriesWithSubCategories.size() / numberOfThreads;

        for (int i = 0; i < numberOfThreads; i++) {
            int start = i * perOneThread;
            int end = countEndNumber(i, perOneThread, allCattegoriesWithSubCategories.size());
            ArrayList<String> partOfSetForThread = partOfSetForThread(start, end, allCattegoriesWithSubCategories);

            Callable<ArrayList<String>> callable = new ThreadWebReader(i, partOfSetForThread, itemsAlreadyRead, categories);
            Future<ArrayList<String>> future = executor.submit(callable);
            list.add(future);
        }

        FileHelper fw = new FileHelper();
        ArrayList<String> urlOfItems = new ArrayList<>();
        for (Future<ArrayList<String>> fut : list) {
            try {
                ArrayList<String> array = fut.get();
                if (array != null) {
                    for (Object s : array) {
                        fw.writeToFile(s.toString());
                        urlOfItems.add(s.toString());
                    }
                }
            } catch (NullPointerException | ExecutionException e) {
                System.out.println("Excepion");
            }

        }
        return urlOfItems;
    }

    /**
     * Count where should thread end parsing
     *
     * @param threadId id of thread
     * @param perOneThread how many pages should one thread parse
     * @param total
     * @return
     */
    public int countEndNumber(int threadId, int perOneThread, int total) {
        if (threadId == numberOfThreads - 1) {
            return total;
        } else {
            return threadId * perOneThread + perOneThread - 1;
        }
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

    ///////////
    /**
     * Parsing from subCategory to one item url Adding urls to itemsUrls only
     * the ones that hasn't already been read
     *
     * @param url of (sub)category
     * @return
     * @throws IOException
     */
    public ArrayList<String> getLinksToItemLists() throws IOException {
        Document doc = Jsoup.connect(URL_COLLECTIONS).get();
        Elements elem = doc.select(".content_wrapper");

        //list of categories Malarstwo Rzeźba Rysunek
        ArrayList<String> collectionList = new ArrayList<>();

        // tu pierwsza strona 
        for (Element e : elem.select("li").select("h3")) {
            collectionList.add((e.select("a").attr("href")));
        }

        ArrayList<String> listOfAllItemLists = new ArrayList<>();

        for (String collection : collectionList) {
            HashSet<String> allCattegoriesWithSubCategories = getAllCattegoriesWithSubCategories(collection);
            listOfAllItemLists.addAll(allCattegoriesWithSubCategories);
        }
        return collectionList;
    }

    public HashSet<String> getAllCattegoriesWithSubCategories(String artCat) throws IOException {

        HashSet<String> totalH = new HashSet();
        Document doc = Jsoup.connect(MAIN_URL + artCat).get();

        artCat = artCat.replaceFirst("kategorie/", "");
        Elements elemSub = doc.select(".collection_item").select("h3");
        for (Element e2 : elemSub) {
            if (e2.select(".count").size() > 0) {
                if (!e2.select(".count").text().equals("(0)")) {
                    String middle = (e2.select("a").attr("href"));
                    middle = middle.substring(0, middle.indexOf("/kolekcja"));
                    totalH.add(MAIN_URL + middle + artCat);
                }
            }
        }
        return totalH;
    }

    public HashSet<String> getAddresses(Elements elements, String prefix) {
        HashSet<String> addresses = new HashSet();
        for (Element e : elements.select("li").select("h3")) {
            addresses.add(prefix + e.select("a").attr("href"));
        }
        return addresses;
    }
}
