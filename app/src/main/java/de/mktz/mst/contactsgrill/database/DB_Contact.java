package de.mktz.mst.contactsgrill.database;
import android.util.Log;

import java.util.List;


public class DB_Contact {

    private long id;
    private String name;
    private boolean isTracked;
    private List<String> lookups;
    private String photoUri;
    private String birthday;
    private String jobDesc;
    private String jobCompany;

    private long lastContactTime;


    DB_Contact() {id = -1;}
    DB_Contact(long id, String name, boolean isTracked){
        this.id = id;
        this.name = name;
        this.isTracked = isTracked;
        lastContactTime = 0;
    }
    DB_Contact(long id, String name, boolean isTracked, String photoUri, String birthday, long lastContactTime){
        this.id = id;
        this.name = name;
        this.isTracked = isTracked;
        this.photoUri = photoUri;
        this.birthday = birthday;
        this.lastContactTime = lastContactTime;
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
    public String getBirthday() {
        return birthday;
    }

    public float getCompleteness(){
        int tasks = 3;
        float completes = 0f;
        if(getBirthday() != null) {
            if(getBirthday().length() >= 7 ) completes += 0.5f; //contains day and Month
            if(getBirthday().length() == 10) completes += 0.5f;  //contains Year
        }
        if(getPhotoUri() != null) completes += 1; //contains contact Photo;
        if(getName().indexOf(' ') != -1) completes += 1; //name probably has first and Second name //TODO check Capitalization
        Log.d("test", "Contact " + name + " is " + completes/tasks + " complete");
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
    public void setBirthday(String birthday){
        this.birthday = birthday;
    }
}
