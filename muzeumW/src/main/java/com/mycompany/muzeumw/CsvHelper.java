/*
 * To updateFieldValue this license header, choose License Headers in Project Properties.
 * To updateFieldValue this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.muzeumw;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

/**
 *
 * @author PCSS
 */
public final class CsvHelper {

    static String COMMA_DELIMITER = ",";
    static String NEW_FIELD_SEPARATOR = ";";
    static String NEW_LINE_SEPARATOR = "\n";
    private final String url = "muzeum-wilanow-output.csv";
    private volatile File file;
    FileWriter writer;

    public CsvHelper() throws IOException {
        createFile();
    }

    /**
     * Writes object to file
     *
     * @param object that has to be written to file
     * @throws IOException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public synchronized void writeObjectToFile(ArtObject object) throws IOException, IllegalArgumentException, IllegalAccessException {
        System.out.println(".");
        writeStringFieldsFromClass(object);
    }

    public synchronized int readFirstLine() throws FileNotFoundException, IOException {
        FileReader aa = new FileReader(file);
        System.out.println(aa.read());
        return aa.read();
    }

    public synchronized void createFile() throws IOException {
        file = new File(url);

        if (!file.exists()) {
            file.createNewFile();
            if (readFirstLine() <= 0) {
                writeTheHeader();
                readFirstLine();
            }
        }
    }

    /**
     * Writes the header to file
     *
     * @throws IOException
     */
    public synchronized void writeTheHeader() throws IOException {
        writer = new FileWriter(file, true);

        for (Field f : ArtObject.class.getDeclaredFields()) {
            if (f.getType().toString().equals("class java.lang.String")) {
                f.setAccessible(true);
                writer.append(f.getName());
                writer.append(NEW_FIELD_SEPARATOR);
            }
        }
        writer.append(NEW_LINE_SEPARATOR);
        writer.close();
    }

    /**
     * Reading fields that are String type
     *
     * @param <T> Class
     * @param item object from class T
     * @throws IOException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public synchronized <T> void writeStringFieldsFromClass(T item) throws IOException, IllegalArgumentException, IllegalAccessException {

        writer = new FileWriter(file, true);

        ArrayList<Field> fields = new ArrayList();
        fields.addAll(Arrays.asList(item.getClass().getDeclaredFields()));

        for (Field f : fields) {
            String typeOfFieldName = f.getType().toString();
            if (typeOfFieldName.equals("class java.lang.String")) {
                f.setAccessible(true);
                if (f.getName().equals("objectUrl") || f.getName().equals("image")) {
                    writer.append("\"" + f.get(item).toString() + "\"");
                    writer.append(NEW_FIELD_SEPARATOR);
                } else {
                    if (f.get(item) != null) {
                        String text = f.get(item).toString();
                        if (text.contains(";")) {
                            text = text.replace(";", ",");
                        }
                        writer.append(text);
                        writer.append(NEW_FIELD_SEPARATOR);
                    }
                }
            }
        }
        writer.append(NEW_LINE_SEPARATOR);
        writer.close();
    }

    /**
     * Reading archives that has already been read (last time the program was
     * run)
     *
     * @return set of links to archives that have been read
     * @throws FileNotFoundException
     * @throws IOException
     */
    public HashSet<String> readObjectsAlreadyRead() throws FileNotFoundException, IOException {
        String line;
        boolean first = true;
        BufferedReader in = new BufferedReader(new FileReader(url));
        HashSet<String> archivesAlreadyRead = new HashSet<>();

        while ((line = in.readLine()) != null) {
            if (first) {
                first = false;
            } else {
                if (line.contains("\"")) {
                    String[] lineSep = line.split("\"");
                    archivesAlreadyRead.add(lineSep[3]);
                }
            }
        }
        return archivesAlreadyRead;
    }
}
