package de.mktz.mst.contactsgrill.newContacts;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.Manifest;
import android.database.ContentObservable;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.*;
import android.support.v4.app.ActivityCompat;
import android.util.Log;


public class ContactReader {

    public Context context;

    public ContactReader(Context c){
        this.context = c;
    }

    public ContactWrapper getContactByID(Long id)
    {
        if(!hasPermission()) return null; //TODO throw exception?


        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, "_ID = '" + id + "'", null, null, null);
        ContactWrapper cw = null;


        if (cursor.moveToFirst()) {
            cw = new ContactWrapper(id, cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));
            loadContactNumbers(cw);
            loadContactPhotos(cw);
            loadContactDates(cw);

            cw.DEBUG_Log();
        }
        cursor.close();
        return cw;
    }


    private boolean hasPermission(){
        if (context.checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(new Activity(),new String[]{Manifest.permission.READ_CONTACTS}, 1160); //TODO WTF new Activity?
            return false;
        }
        return true;
    }


    private void loadContactNumbers(ContactWrapper cw){
        //TODO Projection
        ContentResolver contentResolver = context.getContentResolver();
        Cursor phones = contentResolver.query(Phone.CONTENT_URI, null, Phone.CONTACT_ID + " = " + cw.getDeviceContactId(), null, null);
        while (phones.moveToNext()) {
            String number = phones.getString(phones.getColumnIndex(Phone.NUMBER));
            int type = phones.getInt(phones.getColumnIndex(Phone.TYPE));
            cw.addPhoneNumber(number);
        }
        phones.close();
    }
    private void loadContactPhotos(ContactWrapper cw){
        //TODO Projection
        ContentResolver contentResolver = context.getContentResolver();
        Cursor photos = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null , ContactsContract.Contacts._ID +" = " + cw.getDeviceContactId(),null,null );
        if (photos.moveToFirst()) {
            String photo = photos.getString(photos.getColumnIndex(ContactsContract.Contacts.PHOTO_URI));
            String icon = photos.getString(photos.getColumnIndex(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI));
            cw.setPhotoUris(photo,icon);
        }
        photos.close();
    }
    private void loadContactDates(ContactWrapper cw){
        ContentResolver contentResolver = context.getContentResolver();
        String[] projection = new String[]{
                ContactsContract.Contacts.Data.RAW_CONTACT_ID,
                ContactsContract.CommonDataKinds.Event.START_DATE,
                ContactsContract.CommonDataKinds.Event.TYPE
        };
        String selection = ContactsContract.Data.MIMETYPE + " = '" + ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE + "' AND " + ContactsContract.Contacts.Data.RAW_CONTACT_ID + " = " + cw.getDeviceContactId();
        Cursor dates = contentResolver.query(ContactsContract.Data.CONTENT_URI,projection, selection,null,null);
        if(dates.moveToFirst()){
            int dateType = dates.getInt(dates.getColumnIndex(ContactsContract.CommonDataKinds.Event.TYPE));
            String dateDate = dates.getString(dates.getColumnIndex(ContactsContract.CommonDataKinds.Event.START_DATE));
            if(dateType == ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY){
                cw.setBirthday(dateDate);
            }
            //cw.DEBUG_Log();
            //Log.d("malte","New Date of Type " + dateType + " on the " + dateDate + " on contact ID " + dates.getString(dates.getColumnIndex(ContactsContract.Data.RAW_CONTACT_ID)));
        }
        dates.close();
    }



}
