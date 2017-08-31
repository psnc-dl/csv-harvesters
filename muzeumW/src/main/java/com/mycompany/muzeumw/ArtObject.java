/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.muzeumw;

import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author Laura
 */
public class ArtObject {

    private String image;
    private String objectUrl;
    private String title;
    private String inventoryNumber;
    private String autor;
    private String category;
    private String size;
    private String provenance;
    private String owner;
    private String material;
    private String technique;
    private String dating;

    public ArtObject() {
    }

    /**
     * Parse one object
     *
     * @param objectUrl
     * @return object
     * @throws IOException
     */
    public ArtObject getObject(String objectUrl) throws IOException {
        String mainUrl = "http://cyfrowezbiory.muzeum-wilanow.pl:85";
        this.objectUrl = objectUrl;

        Document doc = Jsoup.connect(objectUrl).timeout(0).get();
        Elements elem = doc.select("#object");
        this.image = mainUrl + elem.select(".object-single-image").select("img").attr("src");

        Elements data = doc.select(".row.object-informations");
        this.title = data.select(".object-title").text();

        for (Element e : data.select(".object-info-element")) {
            String nameOfField = e.select(".etq").text();
            String value = e.select(".object-element").text();
            setFields(nameOfField, value);
        }
        return this;
    }

    /**
     * Setting fields of object
     *
     * @param nameOfField that will be set
     * @param value that will be written
     */
    public void setFields(String nameOfField, String value) {
        switch (nameOfField) {
            case ("Nr inwentarza"):
                this.inventoryNumber = value;
                break;
            case ("Twórca"):
                this.autor = value;
                break;
            case ("Kategoria"):
                this.category = value;
                break;
            case ("Wymiary"):
                this.size = value;
                break;
            case ("Pochodzenie"):
                this.provenance = value;
                break;
            case ("Właściciel"):
                this.owner = value;
                break;
            case ("Tworzywo"):
                this.material = value;
                break;
            case ("Technika"):
                this.technique = value;
                break;
            case ("Datowanie"):
                this.dating = value;
                break;
        }

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
