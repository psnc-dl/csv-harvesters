/*
 * To updateFieldValue this license header, choose License Headers in Project Properties.
 * To updateFieldValue this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package szukajwarchiwach;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author PCSS
 */
public class Archive {

    private String url;
    private String dates;
    private String title;
    private String description;
    private String formerTitleOfTheCollection;
    private String foreignLanguageTitleOfTheCollection;
    private String nameOfTheCreator;
    private String category;
    private String accessConditions;
    private String languages;
    private AmountOfMaterial amountOfMaterial;
    private Document d;
    private ArrayList<FindingAid> findingAids = new ArrayList<>();
    private ArrayList<String> webTabs = new ArrayList<>();
    private ArrayList<Unit> unitList = new ArrayList<>();

    public Archive() {

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
        if (isAbleToConnect(url)) {
            d = Jsoup.connect(url).get();
            Elements elem = d.select(".container");
            for (Element e : elem.select(".pagerBox").select("a[href]")) {
                try {
                    if (Integer.parseInt(e.text()) > max) {
                        max = Integer.parseInt(e.text());
                    }
                } catch (NumberFormatException error) {
                    System.out.println("could not parse string to int");
                }
            }
        }
        return max;
    }

    public boolean isAbleToConnect(String url) throws IOException {
        ConnectionChecker cc = new ConnectionChecker();
        return cc.isValidLink(url);
    }

    /**
     * Reading units get info from table info: reference code, title dates,
     * number of scans
     *
     * @param archiveUrl
     * @return list of units
     * @throws IOException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public ArrayList<Unit> readUnits(String archiveUrl) throws IOException, IllegalArgumentException, IllegalAccessException {

        int x = 0;
        int nrOfUnitPages = 0;
        ArrayList<Unit> unitList = new ArrayList<>();
        String intro = archiveUrl.substring(0, archiveUrl.indexOf("?q"));
        String urlUnits = intro + "/str/1/50?ps=True" + "#tabJednostki";

        nrOfUnitPages = setWebPageNumber(urlUnits);

        // no further pages but current need to be read
        if (nrOfUnitPages == 0) {
            nrOfUnitPages = 1;
        }

        for (int n = 0; n < nrOfUnitPages; n++) {
            String urlNew = intro + "str/" + (n + 1) + "/50?ps=True#tabJednostki";
            //if (this.isAbleToConnect(urlNew)) {
            d = Jsoup.connect(urlNew).get();
            Elements elements = d.select(".units").select("table").not(".jednostki_pp").not("th");
            for (Element e : elements.select("tr")) {
                if (!e.toString().contains("th")) {
                    Unit unit = new Unit();
                    for (Element e2 : e.select("td")) {
                        switch (x) {
                            case (0):
                                unit.setReferenceCode("http://szukajwarchiwach.pl" + "/" + e2.text() + "#tabJednostka");
                                break;
                            case (1):
                                unit.setTitle(e2.text());
                                break;
                            case (2):
                                unit.setDates(e2.text());
                                break;
                            case (3):
                                unit.setNumberOfScans(e2.text());
                                break;
                        }
                        x++;
                    }
                    x = 0;
                    if (!unit.getNumberOfScans().equals("0")) {
                        unitList.add(readOneUnit(unit));
                    }
                }
            }
        }
        return unitList;
    }

    /**
     * Page with Unit description table gets image of unit ( if there are any )
     *
     *
     * @param unit
     * @return unit
     * @throws IOException
     */
    public Unit readOneUnit(Unit unit) throws IOException {

        if (this.isAbleToConnect(url)) {
            Document d2 = Jsoup.connect(unit.getReferenceCode()).get();
            // czytanie nav 
            Elements nav = d2.select("header").select(".tabNav").select("li");
            for (Element e : nav) {
                String text = e.text();
                if (text.contains("Items") || text.contains("Digital copies")) {
                    if (text.contains("Digital copies")) {
                        String url1 = unit.getReferenceCode();
                        String url2 = url1.substring(0, url1.indexOf("#")) + "/str/1/1/100#tabSkany";
                        unit.setImage(getImages(url2));
                        break;
                    }
                }

            }
        }
        return unit;
    }

    /**
     * Getting images of archive
     *
     * @param url of the archive
     * @return set of images url's
     * @throws IOException
     */
    public String getImages(String url) throws IOException {
        String image = null;
        Document doc = Jsoup.connect(url).get();
        Elements elements = doc.select(".searchListBg").select(".searchListBg");
        for (Element e : elements.select("li").select("a")) {
            image = "http://szukajwarchiwach.pl" + e.attr("href");
            break;
        }
        return image;
    }

    /**
     * Reading tabs from navigation
     *
     * @param archive one archive
     * @return
     * @throws IOException
     */
    public Archive readTabs(Archive archive) throws IOException {
        if (isAbleToConnect(archive.getUrl())) {
            Document doc = Jsoup.connect(archive.getUrl()).get();
            Elements elem = doc.select(".container").select("header");
            ArrayList<String> webTabsOfArchive = new ArrayList<>();
            archive.setTitle(doc.select(".dokument").select("header").select("h1").text());

            // links to TABS --------------------------------------------------------------------
            for (Element e : elem.select(".tabNav").select("li")) {
                boolean skip = false;
                String a = e.select("a").attr("href");
                if (a.contains("#") && a.contains("\"")) {
                    a = a.substring(a.indexOf("#"));
                    a = a.substring(0, a.indexOf("\""));
                }
                webTabsOfArchive.add(a);
                archive.addWebPage(archive.getUrl() + a);
            }
        }
        return archive;
    }

    /**
     * Reading data from table in the middle
     *
     * @return Archive with fields filled
     * @throws IOException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public Archive readFrontTable() throws IOException, IllegalArgumentException, IllegalAccessException {
        Archive change = null;
        Document doc = Jsoup.connect(this.url).get();
        Element mainTable = doc.select("table").select("tbody").first();

        for (Element e : mainTable.select("tr")) {
            for (Element e2 : e.select("td")) {
                String len = e2.text();
                if (len.contains(":")) {
                    String fieldName = len.substring(0, len.indexOf(":"));
                    fieldName = fieldName.replaceAll(" ", "");
                    boolean success = this.compareFieldName(fieldName);
                    if (success) {
                        change = this.updateFieldValue(this, fieldName.toLowerCase(), len.substring(len.indexOf(":") + 2));
                    }
                }
            }
        }
        return change;
    }

    /**
     * Reading the description of an archive
     *
     * @throws IOException
     */
    public void readTheDescription() throws IOException {
        Document doc = Jsoup.connect(this.url).get();
        Elements elem = doc.select(".documentBg").select(".underTable").select(".longText");
        for (Element e : elem) {
            String s = e.toString().replaceAll("<br>", "\n");
            s = s.replaceAll("<span class=\"longText\">", "");
            if (s.contains(";")) {
                s = s.replaceAll(";", ",");
            }
            if (s.contains("brak danych")) {
                s = "brak danych";
                this.setDescription(s);
                break;
            }
            this.setDescription(s);
        }
    }

    /**
     * Updating value of an archive
     *
     * @param archive that will be changed
     * @param field of an achrive that has to be changed
     * @param value that will be assigned
     * @return updated archive
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public Archive updateFieldValue(Archive archive, String field, String value) throws IllegalArgumentException, IllegalAccessException {
        ArrayList<Field> fields = new ArrayList();
        fields.addAll(Arrays.asList(this.getClass().getDeclaredFields()));

        for (Field f : fields) {
            String fieldName = f.getName().toLowerCase();
            if (fieldName.equals(field)) {
                f.setAccessible(true);
                f.set(archive, value);
                break;
            }
        }
        return archive;
    }

    /**
     * Comparing name of field with parsed string
     *
     * @param name parsed string
     * @return if the field with that has name name exist in
     */
    public boolean compareFieldName(String name) {
        boolean found = false;

        ArrayList<Field> fields = new ArrayList();
        fields.addAll(Arrays.asList(this.getClass().getDeclaredFields()));
        for (Field f : fields) {
            String fieldName = f.getName().toLowerCase();
            if (fieldName.equals(name.toLowerCase())) {
                found = true;
                break;
            }
        }
        return found;
    }

    //****************************************************
    public ArrayList<Unit> getUnitList() {
        return unitList;
    }

    public void setUnitList(ArrayList<Unit> unitList) {
        this.unitList = unitList;
    }

    public ArrayList<String> getWebTabs() {
        return webTabs;
    }

    public void setWebTabs(ArrayList<String> webTabs) {
        this.webTabs = webTabs;
    }

    public ArrayList<FindingAid> getFindingAids() {
        return findingAids;
    }

    public void setFindingAids(ArrayList<FindingAid> findingAids) {
        this.findingAids = findingAids;
    }

    public void addWebPage(String url) {
        this.webTabs.add(url);
    }

    public void addFindingAid(FindingAid findingAid) {
        this.findingAids.add(findingAid);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNameOfTheCreator() {
        return nameOfTheCreator;
    }

    public void setNameOfTheCreator(String nameOfTheCreator) {
        this.nameOfTheCreator = nameOfTheCreator;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAccessConditions() {
        return accessConditions;
    }

    public void setAccessConditions(String accessConditions) {
        this.accessConditions = accessConditions;
    }

    public String getLanguages() {
        return languages;
    }

    public void setLanguages(String languages) {
        this.languages = languages;
    }

    public AmountOfMaterial getAmountOfMaterial() {
        return amountOfMaterial;
    }

    public void setAmountOfMaterial(AmountOfMaterial amountOfMaterial) {
        this.amountOfMaterial = amountOfMaterial;
    }

}
