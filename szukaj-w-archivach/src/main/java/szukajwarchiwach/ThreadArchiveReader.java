/*
 * To updateFieldValue this license header, choose License Headers in Project Properties.
 * To updateFieldValue this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package szukajwarchiwach;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Laura Rakiewicz
 */
public final class ThreadArchiveReader implements Runnable {

    private int start;
    private int end;
    private CsvHelper csvHelper;
    private ArrayList<String> webPages;
    HashSet<String> linksToArchivesAlreadyRead = new HashSet<>();

    ThreadArchiveReader(HashSet<String> linksToArchivesAlreadyRead, ArrayList<String> webPages, int start, int end) throws IOException, IllegalArgumentException, IllegalAccessException {
        this.linksToArchivesAlreadyRead = linksToArchivesAlreadyRead;
        this.start = start;
        this.end = end;
        this.webPages = webPages;
    }

    public Archive getDataAboutArchive(String urlOfArchive) throws IOException, IllegalArgumentException, IllegalAccessException {
        if (!linksToArchivesAlreadyRead.contains(urlOfArchive)) {
            Archive archive = new Archive();
            archive.setUrl(urlOfArchive.toString());
            archive = archive.readTabs(archive);

            //FindingAid findingAid = new FindingAid();
            //archive.setFindingAids(findingAid.readFindingAids(archive.getUrl()));
            archive.readTheDescription();
            archive = archive.readFrontTable();

            // read inside the tabs ------------------------------------------------------------
            ArrayList<Unit> readUnits = new ArrayList<>();
            readUnits = archive.readUnits(archive.getUrl());
            archive.setUnitList(readUnits);
            return archive;
        }
        return null;
    }

    @Override
    public void run() {
        try {
            synchronized (this) {
                csvHelper = new CsvHelper();
            }
            for (String webPage : webPages) {
                long start = System.currentTimeMillis();
                Archive archive = getDataAboutArchive(webPage);
                long end = System.currentTimeMillis();
                if (archive != null) {
                    synchronized (this) {
                        System.out.print(".");
                        csvHelper.writeArchiveToFile(archive);
                    }
                }
            }
        } catch (IOException | IllegalArgumentException | IllegalAccessException ex) {
            Logger.getLogger(ThreadArchiveReader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

}
