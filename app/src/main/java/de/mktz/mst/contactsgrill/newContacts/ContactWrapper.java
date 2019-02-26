package de.mktz.mst.contactsgrill.newContacts;

import android.util.Log;

import java.net.URI;
import java.util.ArrayList;

public class ContactWrapper {

    private long deviceContactId;
    private String displayName;
    private ArrayList<String> phoneNumbers;
    private String photoUri;
    private String photoThumbUri;
    private String birthday;

    private boolean isTracked;


    public ContactWrapper(long id, String name){
        this.deviceContactId = id;
        this.displayName = name;
        this.phoneNumbers = new ArrayList<>();
    }

    public void addPhoneNumber(String number){
        if(! phoneNumbers.contains(number)) this.phoneNumbers.add(number);
    }

    public void setPhotoUris(String photo, String thumb){
        this.photoUri = photo;
        this.photoThumbUri = thumb;
    }

    public void setBirthday(String date){
        this.birthday = date;
    }

    public long getDeviceContactId(){
        return deviceContactId;
    }



    public void DEBUG_Log(){
        Log.d("malte","===================================");
        Log.d("malte","Contact ID   : " + deviceContactId);
        Log.d("malte","Contact Name : " + displayName);
        for (String nr: phoneNumbers) {
            Log.d("malte","Number       : " + nr);
        }
        if(photoUri != null && photoThumbUri != null) {
        Log.d("malte","Contact Photo: " + photoUri);
        Log.d("malte","Contact Thumb: " + photoThumbUri); }

    }


}
