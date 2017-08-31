/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package szukajwarchiwach;

/**
 *
 * @author Laura
 */
public class Unit {
    private String referenceCode; // == link to Unit  HAS TABLE
    private String title;
    private String dates;
    private String numberOfScans;
    private String image;
 
    
    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
    
    public String getReferenceCode() {
        return referenceCode;
    }

    public void setReferenceCode(String referenceCode) {
        this.referenceCode = referenceCode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDates() {
        return dates;
    }

    public void setDates(String dates) {
        this.dates = dates;
    }

    public String getNumberOfScans() {
        return numberOfScans;
    }

    public void setNumberOfScans(String numberOfScans) {
        this.numberOfScans = numberOfScans;
    }
   
    
    
    
}
