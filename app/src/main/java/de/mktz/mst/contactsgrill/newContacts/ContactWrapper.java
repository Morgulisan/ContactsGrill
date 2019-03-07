package de.mktz.mst.contactsgrill.newContacts;

import android.util.Log;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.mktz.mst.contactsgrill.R;

public class ContactWrapper {

    private boolean allDataInitialized;


    private float completeness = -1;
    private Map<String,Integer> completeTasks;
    private static final String COMPLETE_BIRTH_DAY = "BirthDay";
    private static final String COMPLETE_BIRTH_YEAR = "BirthDayYear";
    private static final String COMPLETE_PHOTO = "photo";
    private static final String COMPLETE_NAME_SHORT = "namePart";
    private static final String COMPLETE_NAME_CHARS = "nameContent";


    private long deviceContactId;
    private String displayName;
    private ArrayList<String> phoneNumbers;
    private String photoUri;
    private String photoThumbUri;
    private String birthday;
    private String nextContactTime;
    private boolean isTracked;


    public ContactWrapper(long id, String name){
        this.deviceContactId = id;
        this.displayName = name;
        this.phoneNumbers = new ArrayList<>();
        this.allDataInitialized = false;
    }
    public ContactWrapper(long id, String name,String photo, String thumb, String[] phones){
        this.deviceContactId = id;
        this.displayName = name;
        this.photoUri = photo;
        this.photoThumbUri = thumb;
        for(String nr : phones)
        this.phoneNumbers.add(nr);
        allDataInitialized = true;
    }

    public boolean isFullyInitialized(){
        return allDataInitialized;
    }
    public void setFlagAllDataInitialized(){
        this.allDataInitialized = true;
    }

    public void addPhoneNumber(String number){
        if(! phoneNumbers.contains(number)) this.phoneNumbers.add(number);
    }
    public ContactWrapper setPhotoUris(String photo, String thumb){
        this.photoUri = photo;
        this.photoThumbUri = thumb;
        return this;
    }
    public ContactWrapper setBirthday(String date){
        this.birthday = date;
        return this;
    }
    public ContactWrapper setTracked(boolean track){
        this.isTracked = track;
        Log.e("malte","SetTrack() does currently not work");
        return this;
    }
    public ContactWrapper setLastContactTime(long lastContactTime){
        Log.e("malte","setLastContactTime() does currently not work, set time to " + lastContactTime);
        return this;
    }

    public long getDeviceContactId(){
        return deviceContactId;
    }

    public String getDisplayName(){
        if (displayName == null) displayName = "fehler bei Namens Inizialisierung";//getResources().getString(R.string.missing_name);
        return displayName;
    }

    public long getId(){
        Log.d("malte","Deprecated: use getDeviceContactId");
        return  deviceContactId;
    }
    public String getName() {
        Log.d("malte","Deprecated: use getDisplayName instead of getName");
        if (displayName == null) displayName = "fehler bei Namens Inizialisierung";//getResources().getString(R.string.missing_name);
        return displayName;
    }
    public boolean getTracked(){
        return isTracked;
    }
    public List<String> getLookups() {
        Log.d("malte","Deprecated: stop using getLookups");
        return new LinkedList<String>();
    }
    public String getPhotoUri() {
        return photoUri;
    }
    public long getLastContactTime() {
        Log.d("malte","Deprecating: implement new getLastContactTime");
        return 0L;
    }
    public long getFirstContactTime() {
        Log.d("malte","Deprecating: implement new getFirstContactTime");
        return 0L;
    }
    public String getBirthday() {
        return birthday;
    }
    public ArrayList<String> getPhoneNumbers(){
        return phoneNumbers;
    }

    public float getCompleteness(){
        if (completeness <= 0 && allDataInitialized) {
            if (completeTasks == null) completeTasks = new HashMap<>();
            else completeTasks.clear();
            float tasks = 4f;
            float completes = 0f;
            completes += completenessName();
            completes += completenessBirthday();
            completes += completenessPhoto();
            completes += completenessPhoneNumbers();
            completeness = completes / tasks;
        }
        return completeness;
    }

    private float completenessBirthday(){
        float completeness = 0f;
        if(getBirthday() != null) {
            if(getBirthday().length() >= 7 ) completeness += 0.5f; else addCompleteTask(COMPLETE_BIRTH_DAY, R.string.complete_birthday_day);//contains day and Month
            if(getBirthday().length() == 10) completeness += 0.5f; else addCompleteTask(COMPLETE_BIRTH_YEAR, R.string.complete_birthday_year); //contains Year
        }
        else addCompleteTask(COMPLETE_BIRTH_DAY, R.string.complete_birthday);
        return completeness;
    }
    private float completenessPhoto(){
        if(getPhotoUri() != null) return  1f;
        addCompleteTask(COMPLETE_PHOTO,R.string.complete_photo);
        return 0;
    }
    private float completenessPhoneNumbers(){
        float completeness = 0f;
        if(phoneNumbers == null){
            throw new Error("Missing Phone Numbers #98021401");
        }
        float numberPercent = 1f;
        for (String number : phoneNumbers){
            float numberP = 1f;
            if(number.length() <= 6){
                numberP -= 1f;
                addCompleteTask("#: " + number, R.string.complete_number_length);
            }
            else if (!(number.substring(0,1).equals("+") || number.substring(0,2).equals("00"))){
                numberP -= 0.25f;
                addCompleteTask("#: " + number, R.string.complete_number_pre);
            }
            numberPercent *= numberP;
        }
        if(phoneNumbers.isEmpty()) {
            numberPercent = 0f;
            addCompleteTask("NoNumber", R.string.complete_number_exists);
        }
        completeness += numberPercent;
        return completeness;
    }
    private float completenessName(){
        float completeness = 1f;
        if(getDisplayName().indexOf(' ') == -1 || getDisplayName().length() <= 6){
            completeness -= 0.5f;
            addCompleteTask(COMPLETE_NAME_SHORT,R.string.complete_name_length);
        }
        String regexMatchName =  "^(([A-ZÄÖÜ][a-zäöüß]*(-[A-ZÄÖÜ]?[a-zäöüß]*)?|von) ?)*$";
        Pattern pattern = Pattern.compile(regexMatchName);
        Matcher matcher = pattern.matcher(getDisplayName());
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
        if(completeTasks == null) getCompleteness();
        return completeTasks;
    }


    public void DEBUG_Log(){
        Log.d("malte","===================================");
        Log.d("malte","Contact ID   : " + deviceContactId);
        Log.d("malte","Contact Name : " + displayName);
        if(birthday != null)
        Log.d("malte","Birthday date: " + birthday);
        for (String nr: phoneNumbers) {
            Log.d("malte","Number       : " + nr);
        }
        if(photoUri != null && photoThumbUri != null) {
        Log.d("malte","Contact Photo: " + photoUri);
        Log.d("malte","Contact Thumb: " + photoThumbUri); }

    }


}
