package de.mktz.mst.contactsgrill.newContacts;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.Manifest;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.*;
import android.support.v4.app.ActivityCompat;


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

        if(cursor == null) return null;
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
        Cursor phonesCursor = contentResolver.query(Phone.CONTENT_URI, null, Phone.CONTACT_ID + " = " + cw.getDeviceContactId(), null, null);
        if(phonesCursor == null) return;
        while (phonesCursor.moveToNext()) {
            String number = phonesCursor.getString(phonesCursor.getColumnIndex(Phone.NUMBER));
            //int type = phonesCursor.getInt(phonesCursor.getColumnIndex(Phone.TYPE));
            cw.addPhoneNumber(number);
        }
        phonesCursor.close();
    }
    private void loadContactPhotos(ContactWrapper cw){
        //TODO Projection
        ContentResolver contentResolver = context.getContentResolver();
        Cursor photosCursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null , ContactsContract.Contacts._ID +" = " + cw.getDeviceContactId(),null,null );
        if(photosCursor == null) return;
        if (photosCursor.moveToFirst()) {
            String photo = photosCursor.getString(photosCursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI));
            String icon = photosCursor.getString(photosCursor.getColumnIndex(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI));
            cw.setPhotoUris(photo,icon);
        }
        photosCursor.close();
    }
    private void loadContactDates(ContactWrapper cw){
        ContentResolver contentResolver = context.getContentResolver();
        String[] projection = new String[]{
                ContactsContract.Contacts.Data.RAW_CONTACT_ID,
                ContactsContract.CommonDataKinds.Event.START_DATE,
                ContactsContract.CommonDataKinds.Event.TYPE
        };
        String selection = ContactsContract.Data.MIMETYPE + " = '" + Event.CONTENT_ITEM_TYPE + "' AND " + ContactsContract.Contacts.Data.RAW_CONTACT_ID + " = " + cw.getDeviceContactId();
        Cursor datesCursor = contentResolver.query(ContactsContract.Data.CONTENT_URI,projection, selection,null,null);
        if(datesCursor == null) return;
        if(datesCursor.getColumnCount() > 0 && datesCursor.moveToFirst()){
            do {
                int dateType = datesCursor.getInt(datesCursor.getColumnIndex(Event.TYPE));
                String dateDate = datesCursor.getString(datesCursor.getColumnIndex(Event.START_DATE));
                if (dateType == Event.TYPE_BIRTHDAY) {
                    cw.setBirthday(dateDate);
                }
                if (dateType == Event.TYPE_ANNIVERSARY) {
                    //TODO set event
                }
                if (dateType == Event.TYPE_CUSTOM){
                    
                }
                //cw.DEBUG_Log();
                //Log.d("malte","New Date of Type " + dateType + " on the " + dateDate + " on contact ID " + dates.getString(dates.getColumnIndex(ContactsContract.Data.RAW_CONTACT_ID)));
            } while (datesCursor.moveToNext());
        }
        datesCursor.close();
    }



}
