package de.mktz.mst.contactsgrill.database;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
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

                DB_Contact contact = new DB_Contact();
                contact.setName(name);
                contact.setLookups(lookups);

                boolean contains = false;
                for(String l : lookups){
                    contains |= lookupValues.containsKey(l);
                }
                if(contains){
                    try {
                        contact.addLookups(lookups);
                    }
                    catch (Exception e) {
                        Log.d("test",e.toString());
                    }
                    //throw new java.lang.UnsupportedOperationException();
                    //Update
                    //add Lookups
                }
                else {
                    contact.setTracked(false);
                    contact = db_handler.insertContact(contact);
                }
            }while(cursor.moveToNext());
        }
        cursor.close();

    }

}
