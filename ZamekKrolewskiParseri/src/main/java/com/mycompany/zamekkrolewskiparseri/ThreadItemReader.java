/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.zamekkrolewskiparseri;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author PCSS
 */
public class ThreadItemReader implements Runnable {

    CsvHelper csvHelper;
    ArrayList<String> itemsUrls = new ArrayList<>();

    public ThreadItemReader(ArrayList<String> itemsUrls) {
        this.itemsUrls = itemsUrls;
    }

    @Override
    public void run() {
        try {
            synchronized (this) {
                csvHelper = new CsvHelper();
            }
            for (String itemUrl : itemsUrls) {
                Item item = new Item(itemUrl);
                item.getDataAboutTheItem();
                System.out.print(".");
                synchronized (this) {
                    csvHelper.writeItemToFile(item);
                }
            }
        } catch (IOException | IllegalArgumentException | IllegalAccessException ex) {
            Logger.getLogger(ThreadItemReader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
