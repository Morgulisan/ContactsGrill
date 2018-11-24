package de.mktz.mst.contactsgrill.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.mktz.mst.contactsgrill.R;
import de.mktz.mst.contactsgrill.database.pockets.DB_Contact_Pocket;


public class DB_Contact {

    private static final String COMPLETE_BIRTH_DAY = "BirthDay";
    private static final String COMPLETE_BIRTH_YEAR = "BirthDayYear";
    private static final String COMPLETE_PHOTO = "photo";
    private static final String COMPLETE_NAME_SHORT = "namePart";
    private static final String COMPLETE_NAME_CHARS = "nameContent";

    private long id;
    private String name;
    private boolean isTracked;
    private List<String> lookups;
    private ArrayList<String> phoneNumbers;
    private List<DB_Contact_Pocket> pockets;
    private String photoUri;
    private String birthday;

    private long firstContactTime;
    private long lastContactTime;

    private float completeness = -1;
    private Map<String,Integer> completeTasks;


    DB_Contact() {id = -1;}
    DB_Contact(long id, String name, boolean isTracked){
        this.id = id;
        this.name = name;
        this.isTracked = isTracked;
        lastContactTime = 0;
        phoneNumbers = new ArrayList<>();

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
        if(completeness <= 0) {
            if(completeTasks != null)completeTasks.clear();
            int tasks = 4;
            float completes = 0f;
            completes += completenessBirthday();
            completes += completenessPhoto();
            completes += completenessName();
            completes += completenessPhoneNumbers();
            completeness = completes / tasks;
            if (pockets != null)
            for (DB_Contact_Pocket pocket: pockets) {
                tasks++;
                pocket.getCompleteness();
            }
        }
        return completeness;
    }

    private float completenessBirthday(){
        float completeness = 0f;
        if(getBirthday() != null) {
            if(getBirthday().length() >= 7 ) completeness += 0.5f; else addCompleteTask(COMPLETE_BIRTH_DAY, R.string.complete_birthday);//contains day and Month
            if(getBirthday().length() == 10) completeness += 0.5f; else addCompleteTask(COMPLETE_BIRTH_YEAR, R.string.complete_birthday_year); //contains Year
        }
        return completeness;
    }
    private float completenessPhoto(){
        if(getPhotoUri() != null) return  1f;
        addCompleteTask(COMPLETE_PHOTO,R.string.complete_photo);
        return 0;
    }
    private float completenessPhoneNumbers(){
        float completeness = 0f;
        if(phoneNumbers != null){
            float numberPercent = 1f;
            for (String number : phoneNumbers){
                float numberP = 1f;
                if (!(number.substring(0,1).equals("+") || number.substring(0,2).equals("00"))){
                    numberP -= 0.2f;
                    addCompleteTask("#: " + number, R.string.complete_number_pre);
                }
                if(number.length() <= 8){
                    numberP -= 0.8;
                    addCompleteTask("#: " + number, R.string.complete_number_length);
                }
                numberPercent *= numberP;
            }
            completeness += numberPercent;
        }
        return completeness;
    }
    private float completenessName(){
        float completeness = 1f;
        if(getName().indexOf(' ') == -1 || getName().length() <= 6){
            completeness -= 0.5f;
            addCompleteTask(COMPLETE_NAME_SHORT,R.string.complete_name_length);
        }
        String regexMatchName =  "^(([A-ZÄÖÜ][a-zäöü]*(-[A-ZÄÖÜ]?[a-zäöü]*)?|von) ?){2,}$";
        Pattern pattern = Pattern.compile(regexMatchName);
        Matcher matcher = pattern.matcher(getName());
        if(!matcher.find()){
            completeness -= 0.5f;
            addCompleteTask(COMPLETE_NAME_CHARS, R.string.complete_name_chars);
        }
        return completeness;
    }

    private void addCompleteTask(String TaskName, int TaskDescription){
        if(completeTasks == null) completeTasks = new HashMap<>();
        completeTasks.put(TaskName,TaskDescription);
    }
    public Map<String,Integer> getCompletingTasks(){
        if(completeTasks == null) completeTasks = new HashMap<>();
        return completeTasks;
    }

    public void setId(long id){
        this.id = id;
    }
    public void setName(String name){
        completeness = -1;
        this.name = name;
    }
    public void setTracked(boolean tracked){
        this.isTracked = tracked;
    }
    public void setLookups(List<String> lookups){
        this.lookups = lookups;
    }
    public void setPhotoUri(String uri){
        completeness = -1;
        this.photoUri = uri;
    }
    public void setLastContactTime(long lastContactTime){ this.lastContactTime = lastContactTime;}
    public void setFirstContactTime(long firstContactTime){ this.firstContactTime = firstContactTime;}
    public void setBirthday(String birthday){
        completeness = -1;
        this.birthday = birthday;
    }
    public void addPhoneNumber(String number){
        completeness = -1;
        if(phoneNumbers == null) phoneNumbers = new ArrayList<>();
        phoneNumbers.add(number);
    }
}
