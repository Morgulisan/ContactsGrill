package de.mktz.mst.contactsgrill.database;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

import java.util.Arrays;
import java.util.List;

public class DB_ContactLoader {


    public Context context;
    private DB_Handler db_handler;

    public DB_ContactLoader(Context context){
        this.context = context;
        this.db_handler = new DB_Handler(context);
    }

    public void UpdateContactsInDB(){
        ContentValues lookupValues = db_handler.getLookups();

        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                List<String> lookups = Arrays.asList(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY)).split("\\."));
                String photo = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI));

                DB_Contact contact = new DB_Contact();
                contact.setName(name);
                contact.setLookups(lookups);

                boolean contains = false;
                long contactDbId = -1;

                for(String l : lookups){
                    if(lookupValues.containsKey(l)){
                        contains = true;
                        contactDbId = lookupValues.getAsLong(l);
                        break;
                    }
                }
                if(contains){
                    contact = db_handler.getContactByID(contactDbId);

                    if(photo != null && (contact.getPhotoUri() == null  || !contact.getPhotoUri().equals(photo))){
                        Log.d("test","Updated photo for " + name + " from "  + contact.getPhotoUri() + " to " + photo );
                        contact.setPhotoUri(photo);
                        db_handler.updateContactPhotoURI(contactDbId,photo);
                    }
                    if(name != null && !contact.getName().equals(name)){
                        Log.d("test", "Updating Name for " + name + " from " + contact.getName());
                        contact.setName(name);
                        db_handler.updateContactName(contactDbId,name);
                    }
                }
                else {
                    long lastCon = cursor.getLong(cursor.getColumnIndex(ContactsContract.Contacts.LAST_TIME_CONTACTED));
                    contact.setTracked(false);
                    contact.setLastContactTime(lastCon);
                    contact.setFirtContactTime(lastCon);
                    db_handler.insertContact(contact);
                }
            }while(cursor.moveToNext());
        }
        cursor.close();

    }
    public void UpdateBirthdaysInDB(){
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = ContactsContract.Data.CONTENT_URI;
        String[] projection = new String[]{
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts._ID,
                ContactsContract.CommonDataKinds.Event.START_DATE,
                ContactsContract.CommonDataKinds.Event.TYPE
        };
        String where = ContactsContract.Data.MIMETYPE + "= ? AND " +
                ContactsContract.CommonDataKinds.Event.TYPE + "=" + ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY;
        String[] selectionArgs = new String[]{ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE};

        Cursor cursor = contentResolver.query(uri, projection, where, selectionArgs, null, null);
        if (cursor.getCount() > 0){
            cursor.moveToFirst();
            do{
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                String birthday = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Event.START_DATE));

                DB_Contact c = db_handler.getContactByName(name);
                if(c.getBirthday() == null || !c.getBirthday().equals(birthday)){
                    db_handler.updateContactBirthday(c.getId(),birthday);
                }
            }while (cursor.moveToNext());
        }
        cursor.close();
    }
}
