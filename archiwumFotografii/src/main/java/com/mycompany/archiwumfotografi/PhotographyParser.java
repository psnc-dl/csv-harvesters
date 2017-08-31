/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.archiwumfotografi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author PCSS
 */
public class PhotographyParser {

    private int numberOfPages;
    private ArrayList<String> pagesList = new ArrayList<>();
    private ArrayList<String> photoUrlList = new ArrayList<>();

    public PhotographyParser() {

    }

    /**
     * Setting number of pages with photos
     * @throws IOException 
     */
    public void setNumberOfPages() throws IOException {
        int maxNumber = 0;
        String url = "http://www.bibliotekadzisiaj.pl/mlij/index.php";

        Document d = Jsoup.connect(url).get();
        Elements elem = d.select("#navig");
        for (Element e : elem.select("a")) {
            String str = e.attr("href");
            String n = str.replace("./index.php?count=672&fraza=&cpage=", "");
            int number = Integer.parseInt(n);
            if (number > maxNumber) {
                maxNumber = number;
            }
        }
        this.numberOfPages = maxNumber;
    }

    /**
     * Setting all pages with photos addresses 
     */
    public void setAllPagesAddresses() {
        String url = "http://www.bibliotekadzisiaj.pl/mlij/index.php?count=672&fraza=&cpage=";
        for (int i = 0; i < this.numberOfPages; i++) {
            pagesList.add(url + (i + 1));
        }
    }

    /**
     * Getting all url of photos
     * 
     * @throws IOException 
     */
    public void getAllPhotosUrls() throws IOException {
        String prefix = "http://www.bibliotekadzisiaj.pl/mlij";
        for (String url : pagesList) {
            Document doc = Jsoup.connect(url).get();
            Elements elem = doc.select("#middle");
            for (Element e : elem.select("tr").select("td")) {
                String out = e.select("a").attr("href").replaceFirst(".", "");
                if (!out.equals("")) {
                    photoUrlList.add(prefix + out);
                }
            }
        }
    }

    /**
     * Reading data about photos
     * 
     * @throws IOException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException 
     */
    public void readAllPhotos() throws IOException, IllegalArgumentException, IllegalAccessException {
        CsvHelper csv = new CsvHelper();
        HashSet<String> objectsAlreadyRead = csv.readObjectsAlreadyRead();

        for (String url : this.photoUrlList) {
            if (!objectsAlreadyRead.contains(url)) {
                Photo photo = new Photo();
                photo.readObject(url);
                csv.writeObjectToFile(photo);
            }
        }
    }

}
