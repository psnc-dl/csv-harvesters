/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package szukajwarchiwach;

import java.io.IOException;
import java.util.ArrayList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;


public class FindingAid {

    private String controlInentory;
    private String typeOfFindingAid;
    private String additionalInfo;
    private Document doc;

    public FindingAid() {

    }

    public ArrayList<FindingAid> readFindingAids(String url) throws IOException {
        // dolna tabela Finding aids ------------------------------------------------------------
        
        doc = Jsoup.connect(url).get();
        
        boolean firstRow = true;
        Elements lowerTable = doc.select(".underTable").select("table").select("tbody");
        lowerTable.remove(doc.select("table").select("tbody").first());
        ArrayList<FindingAid> findingTable = new ArrayList<>();

        for (Element e : lowerTable.select("tr")) {
            if (firstRow) {
                firstRow = false;
            } else {
                FindingAid findingAids = new FindingAid();
                int i = 0;

                for (Element e2 : e.select("td")) {
                    if (!e2.toString().equals("<td></td>")) {
                        if (!e2.toString().equals("")) {
                            Node node = e2.unwrap();
                            if (node != null) {
                                String output = node.toString();
                                output = output.replaceAll("<label>", "");
                                output = output.replaceAll("</label>", "");
                                switch (i) {
                                    case 0:
                                        findingAids.setTypeOfFindingAid(output);
                                        break;
                                    case 1:
                                        findingAids.setControlInentory(output);
                                        break;
                                    case 2:
                                        findingAids.setAdditionalInfo(output);
                                        break;
                                }
                            }
                        }
                    }
                    i++;
                }
                findingTable.add(findingAids);
            }
        }
        return findingTable;
    }

    public String getControlInentory() {
        return controlInentory;
    }

    public void setControlInentory(String controlInentory) {
        this.controlInentory = controlInentory;
    }

    public String getTypeOfFindingAid() {
        return typeOfFindingAid;
    }

    public void setTypeOfFindingAid(String typeOfFindingAid) {
        this.typeOfFindingAid = typeOfFindingAid;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

}
