package de.mktz.mst.contactsgrill.newContacts;

import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.Manifest;
import android.database.Cursor;
import android.os.Debug;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.*;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import static android.support.v4.app.ActivityCompat.requestPermissions;
import static android.support.v4.content.ContextCompat.startActivity;


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
        }
        cursor.close();
        return cw;
    }


    private boolean hasPermission(){
        if (context.checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new Activity(),new String[]{Manifest.permission.READ_CONTACTS}, 1160); //TODO WTF new Activity?
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
                Event.START_DATE,
                Event.TYPE,
                Event.LABEL
        };
        String selection = ContactsContract.Data.MIMETYPE + " = '" + Event.CONTENT_ITEM_TYPE + "' AND " + ContactsContract.Contacts.Data.RAW_CONTACT_ID + " = " + cw.getDeviceContactId();
        Cursor datesCursor = contentResolver.query(ContactsContract.Data.CONTENT_URI,projection, selection,null,null);
        if(datesCursor == null) return;
        Log.d("malte","got Dates");
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
                    String label = datesCursor.getString(datesCursor.getColumnIndex(Event.LABEL));
                    Log.d("malte", "Custom Event on " + dateDate + " labeled :" + label );
                    if (label.equals("Test Account Label")) DEBUG_edit("lol@example.com", "Name was updated" + new Random().nextInt(999), "01730000"+ new Random().nextInt(999), String.valueOf(cw.getDeviceContactId()));
                }
                //cw.DEBUG_Log();
                //Log.d("malte","New Date of Type " + dateType + " on the " + dateDate + " on contact ID " + dates.getString(dates.getColumnIndex(ContactsContract.Data.RAW_CONTACT_ID)));
            } while (datesCursor.moveToNext());
        }
        datesCursor.close();
    }

    private boolean updateDate(){
        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
        intent.putExtra(ContactsContract.Intents.Insert.DATA,"test");
        startActivity(context,intent,null);
        return false;
    }

    private void DEBUG_edit(String email, String name, String number,String ContactId){
        String where = ContactsContract.Data.CONTACT_ID + " = ? AND " +  ContactsContract.Data.MIMETYPE + " = ?";

        String[] emailParams = new String[]{ContactId, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE};
        String[] nameParams = new String[]{ContactId, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE};
        String[] numberParams = new String[]{ContactId, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE};

        ArrayList<ContentProviderOperation> ops = new ArrayList<>();

        if(!email.equals("") &&!name.equals("")&& !number.equals(""))
        {
            ops.add(android.content.ContentProviderOperation.newUpdate(android.provider.ContactsContract.Data.CONTENT_URI)
                    .withSelection(where,emailParams)
                    .withValue(ContactsContract.CommonDataKinds.Email.DATA, email)
                    .build());

            ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                    .withSelection(where, nameParams)
                    .withValue(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, name)
                    .build());

            ops.add(android.content.ContentProviderOperation.newUpdate(android.provider.ContactsContract.Data.CONTENT_URI)
                    .withSelection(where,numberParams)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, number)
                    .build());

            try {
                context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
                Log.d("malte","Updated contact");
            }catch (Exception e){
                Log.d("malte",e.getMessage());
            }
        }
    }
}
