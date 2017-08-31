/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.archiwumfotografi;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class MuzeumLowiectwaiJezdziectwa {

    public static void main(String[] args) throws IOException, IllegalArgumentException, IllegalAccessException {
        MuzeumLowiectwaiJezdziectwa s = new MuzeumLowiectwaiJezdziectwa();
    }

    public MuzeumLowiectwaiJezdziectwa() throws IllegalArgumentException, IllegalAccessException {
        try {
            doEverything();
        } catch (IOException ex) {
            Logger.getLogger(MuzeumLowiectwaiJezdziectwa.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void doEverything() throws IOException, IllegalArgumentException, IllegalAccessException {
        PhotographyParser parser = new PhotographyParser();
        parser.setNumberOfPages();
        parser.setAllPagesAddresses();
        parser.getAllPhotosUrls();
        parser.readAllPhotos(); 
    }
    
}
