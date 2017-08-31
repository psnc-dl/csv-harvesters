/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.zamekkrolewskiparseri;

import java.io.IOException;
import java.util.ArrayList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author PCSS
 */
public class Item {

    private final String URL;
    private String image;
    private String title;
    private String description;
    private String inventoryNumber; 
    private String dateOf; 
    private String placeOfFinding;
    private String material;
    private String size; 
    private String type; 
    private String owner; 
    private String bibliography;
    private ArrayList<String> tags = new ArrayList<>();

    public Item(String url) {
        this.URL = url;
    }

    public void getDataAboutTheItem() throws IOException {   
        String MAIN_URL = "https://kolekcja.zamek-krolewski.pl";
        Document d = Jsoup.connect(URL).get();

        Elements elem = d.select("#content");
        for (Element e : elem.select("#artwork_img_print").select("img")) {
            this.image = MAIN_URL + e.attr("src");
        }

        for (Element e2 : elem.select(".description_list").select("tr")) {
            String fieldName = e2.select("th").text();
            String data = e2.select("td").select("span").text();
            if(data.contains(";")){
                data = data.replaceAll(";", ",");
            }
            setItemFields(fieldName, data);
        }

        for (Element e2 : elem.select(".new_description")) {
            String description = e2.text().replaceFirst("Opis dzieła", "");
            this.description = description;
        }

        this.title = d.select(".description_top").select("h2").text();
        for (Element e2 : elem.select(".description_top").select("ul").select("li").select("a")) {
            String tag = e2.attr("title");
            if (!tag.equals("")) {
                this.tags.add(tag);
            }
        }
    }

    public void setItemFields(String fieldName, String value) {
        switch (fieldName) {
            case ("Numer inwentarzowy"):
                this.setInventoryNumber(value);
                break;
            case ("Czas powstania"):
                this.setDateOf(value);
                break;
            case ("Miejsce znalezienia"):
                this.setPlaceOfFinding(value);
                break;
            case ("Materiał/tworzywo"):
                this.setMaterial(value);
                break;
            case ("Wymiary"):
                this.setSize(value);
                break;
            case ("Właściciel obiektu"):
                this.setOwner(value);
                break;
            case ("Bibliografia"):
                this.setBibliography(value);
                break;
            case ("Rodzaj"):
                this.setType(value);
                break;
        }
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setInventoryNumber(String inventoryNumber) {
        this.inventoryNumber = inventoryNumber;
    }

    public void setDateOf(String dateOf) {
        this.dateOf = dateOf;
    }

    public void setPlaceOfFinding(String placeOfFinding) {
        this.placeOfFinding = placeOfFinding;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setBibliography(String bibliography) {
        this.bibliography = bibliography;
    }

    public ArrayList<String> getTags() {
        return tags;
    }
    

}
