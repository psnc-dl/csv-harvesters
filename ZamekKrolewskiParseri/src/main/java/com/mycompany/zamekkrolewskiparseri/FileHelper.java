/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.zamekkrolewskiparseri;

import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 *
 * @author PCSS
 */
public class FileHelper {

    private final String URL = "urlsOfObjects.txt";
    private final String NEW_LINE_SEPARATOR = "\n";
    private File file;

    public FileHelper() {
        file = new File(URL);
    }

    public void writeToFile(String str) throws IOException {
        Writer fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(URL, true), "UTF-8"));
        fw.append(str);
        fw.append(NEW_LINE_SEPARATOR);
        fw.close();
    }

    public HashSet<String> readAddresses() throws FileNotFoundException, IOException {
        HashSet<String> urlsRead = new HashSet<>();
        BufferedReader reader = new BufferedReader(new FileReader(URL));
        String line;
        while ((line = reader.readLine()) != null) {
            urlsRead.add(line);
        }
        return urlsRead;
    }

}
