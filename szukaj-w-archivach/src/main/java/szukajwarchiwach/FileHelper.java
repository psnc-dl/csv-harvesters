/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package szukajwarchiwach;

import java.io.*;
import java.util.ArrayList;

/**
 *
 * @author PCSS
 */
public class FileHelper {

    private static final String URL = "addressesWithDigitalData.txt";

    public FileHelper() {
    }

    public void writeToFile(String url, String str) throws IOException {
        File f = new File(url);
        f.createNewFile();

        Writer fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(url, true), "UTF-8"));
        fw.append(str);
        fw.append("\n");
        fw.close();
    }

    public int readMode() throws IOException {
        int modeNumber = 3; // default mode == 3 ( read new addresses and parse them ) 
        String fileName = "setMode.txt";
        File f = new File(fileName);
        f.createNewFile();

        FileReader fr = new FileReader(f);
        BufferedReader br = new BufferedReader(fr);
        String line = br.readLine();
        if (line != null) {
            modeNumber = Integer.parseInt(line);
        }

        return modeNumber;
    }

    /**
     * Reading addresses of urls already read ... but not shure parsed archives
     * !
     *
     * @param fileName that you want to read from
     * @return list of addresses already parsed
     * @throws FileNotFoundException
     * @throws UnsupportedEncodingException
     * @throws IOException
     */
    public ArrayList<String> readAddressesFromFile() throws FileNotFoundException, UnsupportedEncodingException, IOException {
        String line;
        ArrayList<String> archivesAlreadyRead = new ArrayList<>();

        File f = new File(URL);
        f.createNewFile();
        FileReader fr = new FileReader(f);

        try (BufferedReader br = new BufferedReader(fr)) {
            while ((line = br.readLine()) != null) {
                archivesAlreadyRead.add(line);
            }
            br.close();
        }
        return archivesAlreadyRead;
    }
}
