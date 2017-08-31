/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package szukajwarchiwach;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 *
 * @author PCSS
 */
public class AmountOfMaterial {

    private String totalN;
    private String dateN;
    private String total;
    private String withoutInventory;
    private String processed;

    public AmountOfMaterial() {
    }

    public AmountOfMaterial getAmountOfMaterialTable(String url) throws IOException, IllegalArgumentException, IllegalAccessException {
        int i = 0;
        AmountOfMaterial amount = new AmountOfMaterial();

        Document doc = Jsoup.connect(url).get();
        Elements rightTable = doc.select(".col4of12.right").select(".whiteBg.sideInfo");
        String[] headers = rightTable.toString().split("<h3>");

        for (String h : headers) {       // archival and no archival 
            String[] pInfo = h.split("<p>");
            for (String p : pInfo) {
                if (p.contains(":")) {
                    if (p.contains("noContentTxt")) {
                        p = "brak danych";
                    } else {
                        p = p.replaceAll("<label>", "");
                        p = p.replaceAll("</label>", "");
                        p = p.replaceAll("</p>", "");
                        p = p.replaceAll("</h3>", "");
                        p = p.replaceAll("\n", "");

                        String field = p.substring(0, p.indexOf("("));
                        String unit = p.substring(p.indexOf("(") + 1, p.indexOf(")"));
                        String value = p.substring(p.indexOf(":") + 2);

                        amount = amount.setField(field, value, unit);
                    }
                }
            }
            i++;
        }
        return amount;
    }

    public AmountOfMaterial setField(String field, String value, String unit) throws IllegalArgumentException, IllegalAccessException {
        ArrayList<Field> fields = new ArrayList();
        fields.addAll(Arrays.asList(this.getClass().getDeclaredFields()));
        AmountOfMaterial amount = new AmountOfMaterial();

        field = field.replaceAll(" ", "").toLowerCase();

        for (Field f : fields) {
            String fieldName = f.getName().toLowerCase();

            if (!unit.equals("archival units")) {
                fieldName = fieldName + "N";
            }
            if (fieldName.equals(field.toLowerCase())) {
                f.setAccessible(true);
                f.set(amount, value);
                break;
            }
        }
        return amount;
    }
}
