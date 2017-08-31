/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.archiwumfotografi;

import java.io.IOException;
import java.util.ArrayList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;

/**
 *
 * @author PCSS
 */
public class Photo {

    private String url;
    private String image;
    private String inventoryNumber;
    private String description;
    private String date;
    private String dating;
    private String place;
    private String otherNames;
    private String material;
    private String technique;
    private String team;

    public Photo() {

    }

    public void setFields(String nameOfField, String value) {
        switch (nameOfField) {
            case ("Nr. inwentarzowy"):
                this.inventoryNumber = value;
                break;
            case ("opis"):
                this.description = value;
                break;
            case ("data"):
                this.date = value;
                break;
            case ("czas powstania - daty graniczne"):
                this.dating = value;
                break;
            case ("miejsce"):
                this.place = value;
                break;
            case ("inne nazwy własne"):
                this.otherNames = value;
                break;
            case ("tworzywo"):
                this.material = value;
                break;
            case ("technika"):
                this.technique = value;
                break;
            case ("zespół"):
                this.team = value;
                break;
        }
    }

    /**
     * Reading all the info about photography
     *
     * @param url
     * @return
     * @throws IOException
     */
    public Photo readObject(String url) throws IOException {
        String imageUrl = "";
        Document d = Jsoup.connect(url).get();
        Element elem = d.select("#middle").select("table").get(2);      //.select("tr").select("td");

        this.setImage(imageUrl);
        this.setUrl(url);

        Element e = elem.select("table").select("table").select("tr").select("td").get(3);
        ArrayList<String> arr = new ArrayList<>();
        String[] line = e.toString().split("<p style=\"font-size:6px\"></p>");
        for (String s : line) {
            String data;
            String category = s.substring(s.indexOf("<b>") + 3, s.indexOf("</b>"));
            category = category.substring(0, category.indexOf(":"));

            if (s.contains("font")) {
                Element tag = Jsoup.parse(s, "", Parser.xmlParser());
                data = tag.select("font").text();
            } else {
                data = s.substring(s.indexOf("</b>") + 4);
                if (data.contains("<a")) {
                    data = data.replaceAll("br", "");
                    Element tag = Jsoup.parse(data, "", Parser.xmlParser());
                    data = tag.select("a").text();
                }

                data = deletePatternFromString(data, "&nbsp;");
                data = deletePatternFromString(data, "<br>");
                data = deletePatternFromString(data, "</td>");
            }
            this.setFields(category, data);
        }
        String image = d.select("center").get(2).select("table").select("table").get(2).select("img").attr("src");
        image = image.replaceFirst(".", "http://www.bibliotekadzisiaj.pl/mlij");
        this.setImage(image);

        return this;
    }

    /**
     * Changing strings by deleting unwanted parts
     * 
     * @param output
     * @param pattern
     * @return 
     */
    public String deletePatternFromString(String output, String pattern) {
        if (output.contains(pattern)) {
            output = output.replaceAll(pattern, "");
        }
        return output;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setImage(String image) {
        this.image = image;
    }

}
