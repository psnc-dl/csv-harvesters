/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package szukajwarchiwach;

import java.io.IOException;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

/**
 *
 * @author PCSS
 */
public class ConnectionChecker {

    public ConnectionChecker() {
    }

    /**
     * Checking if Jsoup can connect with url
     *
     * @param url of page that we want to connect to
     * @return if page is valid
     * @throws IOException
     */
    public boolean isValidLink(String url) throws IOException {
        int attempt = 0;
        Connection.Response response = null;
        while (attempt < 3) {
            FileHelper fh = new FileHelper();
            try {
                Connection connection = Jsoup.connect(url).timeout(5000);
                connection.followRedirects(false);
                response = connection.execute();
            } catch (java.net.SocketTimeoutException | org.jsoup.HttpStatusException e) {
                fh.writeToFile("timeOutPages.txt", url);
            }
            if (response != null) {
                switch (response.statusCode()) {
                    case 200:
                        return true;
                    case 404:
                        return false;
                    case 500:
                        return false;
                    default:
                        attempt++;
                        break;
                }
            }
        }
        return false;
    }
}
