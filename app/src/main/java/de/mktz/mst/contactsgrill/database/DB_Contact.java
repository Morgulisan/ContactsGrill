package de.mktz.mst.contactsgrill.database;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;


public class DB_Contact {

    private long id;
    private String name;
    private boolean isTracked;
    private List<String> lookups;
    private ArrayList<String> phoneNumbers;
    private List<DB_Contact_Pocket> pockets;
    private String photoUri;
    private String birthday;


    private String jobDesc;
    private String jobCompany;

    private long firstContactTime;
    private long lastContactTime;



    DB_Contact() {id = -1;}
    DB_Contact(long id, String name, boolean isTracked){
        this.id = id;
        this.name = name;
        this.isTracked = isTracked;
        lastContactTime = 0;
        phoneNumbers = new ArrayList<String>();
    }


    public long getId(){
        return  id;
    }
    public String getName() {
        return name;
    }
    public boolean getTracked(){
        return isTracked;
    }
    public List<String> getLookups() {
        return lookups;
    }
    public String getPhotoUri() {
        return photoUri;
    }
    public long getLastContactTime() {
        return lastContactTime;
    }
    public long getFirstContactTime() {
        return firstContactTime;
    }
    public String getBirthday() {
        return birthday;
    }
    public ArrayList<String> getPhoneNumbers(){
        return phoneNumbers;
    }

    public float getCompleteness(){
        int tasks = 4;
        float completes = 0f;
        if(getBirthday() != null) {
            if(getBirthday().length() >= 7 ) completes += 0.5f; //contains day and Month
            if(getBirthday().length() == 10) completes += 0.5f;  //contains Year
        }
        if(getPhotoUri() != null) completes += 1; //contains contact Photo;
        if(getName().indexOf(' ') != -1) completes += 1; //name probably has first and Second name //TODO check Capitalization
        if(phoneNumbers != null){
            float numberPercent = 1f;
            for (String number : phoneNumbers){
                float numberP = 1f;
                if (!(number.substring(0,1).equals("+") || number.substring(0,2).equals("00"))){
                    numberP -= 0.2f;
                }
                if(number.length() <= 8) numberP -= 0.8;
                numberPercent *= numberP;
            }
            completes += numberPercent;
        }
        return completes / tasks;
    }

    public void setId(long id){
        this.id = id;
    }
    public void setName(String name){
        this.name = name;
    }
    public void setTracked(boolean tracked){
        this.isTracked = tracked;
    }
    public void setLookups(List<String> lookups){
        this.lookups = lookups;
    }
    public void addLookups(List<String> lookups) {
        this.lookups.addAll(lookups);
        //TODO Uniques
    }
    public void setPhotoUri(String uri){
        this.photoUri = uri;
    }
    public void setLastContactTime(long lastContactTime){ this.lastContactTime = lastContactTime;}
    public void setFirtContactTime(long firstContactTime){ this.firstContactTime = firstContactTime;}
    public void setBirthday(String birthday){
        this.birthday = birthday;
    }
    public void addPhoneNumber(String number){
        if(phoneNumbers == null) phoneNumbers = new ArrayList<String>();
        phoneNumbers.add(number);
    }
}
