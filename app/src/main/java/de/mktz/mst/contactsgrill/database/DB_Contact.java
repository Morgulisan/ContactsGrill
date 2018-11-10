package de.mktz.mst.contactsgrill.database;
import java.util.List;


public class DB_Contact {

    private long id;
    private String name;
    private boolean isTracked;
    private List<String> lookups;
    private String photoUri;
    private String birthday;

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
