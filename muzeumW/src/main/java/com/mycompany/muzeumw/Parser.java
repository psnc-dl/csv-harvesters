/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.muzeumw;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author Laura
 */
public class Parser {

    private int numberOfPages;
    private String mainUrl;
    private ArrayList<String> archivesUrl = new ArrayList<>();

    public static void main(String[] args) throws IOException, IllegalArgumentException, IllegalAccessException {
        getAllObjects();
    }

    /**
     * Get art objects with data
     *
     * @throws IOException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public static void getAllObjects() throws IOException, IllegalArgumentException, IllegalAccessException {
        String objectUrl = "http://cyfrowezbiory.muzeum-wilanow.pl:85/katalog/obiekt/924";
        CsvHelper csv = new CsvHelper();

        Parser parser = new Parser();
        parser.numberOfPages = parser.getNumberOfObjects();
        String url = "http://cyfrowezbiory.muzeum-wilanow.pl:85/katalog/search/514c8ca249ec6254f606aa893acebfd0/";
        parser.mainUrl = url + parser.numberOfPages + "/1/1000";

        HashSet<String> objectsAlreadyRead = csv.readObjectsAlreadyRead();
        parser.getLinks(objectsAlreadyRead);

        System.out.println("Reading art objects: ");
        for (String archiveUrl : parser.archivesUrl) {
            ArtObject artObject = new ArtObject();
            artObject = artObject.getObject(archiveUrl);
            csv.writeObjectToFile(artObject);
        }
    }

    /**
     * Get number of objects
     *
     * @return number of objects
     * @throws IOException
     */
    public int getNumberOfObjects() throws IOException {
        int number = 0;
        String url = "http://cyfrowezbiory.muzeum-wilanow.pl:85/katalog/search/514c8ca249ec6254f606aa893acebfd0/80/1/1000";
        Document doc = Jsoup.connect(url).timeout(0).get();
        Elements elementsNumber = doc.select(".search-info-bar").select(".count").select("span");
        try {
            number = Integer.parseInt(elementsNumber.text());
        } catch (NumberFormatException e) {
            System.out.println("error parsing str to int");
        }
        return number;
    }

    /**
     * Getting url of objects that has not been parsed before
     *
     * @param objectsAlreadyRead
     * @throws IOException
     */
    public void getLinks(HashSet<String> objectsAlreadyRead) throws IOException {
        System.out.println("Reading links ");
        String baseUrl = "http://cyfrowezbiory.muzeum-wilanow.pl:85";
        Document doc = Jsoup.connect(this.mainUrl).timeout(0).get();
        Elements elementsNumber = doc.select(".ol-container").select("#objects_list");
        for (Element e : elementsNumber.select("li")) {
            String out = baseUrl + e.select("a").attr("href");
            if (!objectsAlreadyRead.contains(out)) {
                archivesUrl.add(out);
                System.out.print("*");
            }
        }
    }

}
