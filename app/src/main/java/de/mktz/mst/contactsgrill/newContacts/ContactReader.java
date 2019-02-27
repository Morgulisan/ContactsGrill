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

import de.mktz.mst.contactsgrill.database.DB_Handler;

import static android.support.v4.app.ActivityCompat.requestPermissions;
import static android.support.v4.content.ContextCompat.startActivity;


public class ContactReader {

    public Context context;

    public ContactReader(Context c){
        this.context = c;
    }

    public ContactWrapper getContactByID(Long id) {
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
    public ArrayList<ContactWrapper> getListOfAllContacts(DB_Handler.SortParameter sortParameter){ //TODO remove DB_Handler
        //TODO
        Log.e("malte","getListOf* is not implemented");
        ArrayList<ContactWrapper> r = new ArrayList<>();
        r.add(getContactByID(11L));
        r.add(getContactByID(12L));
        r.add(getContactByID(13L));
        r.add(getContactByID(14L));
        r.add(getContactByID(15L));
        r.add(getContactByID(16L));
        r.add(getContactByID(17L));
        r.add(getContactByID(18L));
        r.add(getContactByID(19L));
        return r;
        //TODO Mockup
    }

    public ArrayList<ContactWrapper> getListOfTrackedContacts(DB_Handler.SortParameter sortParameter){
        return getListOfAllContacts(sortParameter);
    }
    public ArrayList<ContactWrapper> getListOfIncompleteContacts(DB_Handler.SortParameter sortParameter){
        return getListOfAllContacts(sortParameter);
    }
    public ArrayList<ContactWrapper> getListOfTrackedContacts(){
        return getListOfAllContacts(null);
    }
    public ArrayList<ContactWrapper> getListOfIncompleteContacts(){
        return getListOfAllContacts(null);
    }
    public ArrayList<ContactWrapper> getListOfAllContacts(){
        return getListOfAllContacts(null);
    }


    private boolean hasPermission(){

        if (context.checkSelfPermission(Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED &&
            context.checkSelfPermission(Manifest.permission.READ_CONTACTS ) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new Activity(),new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS}, 1160); //TODO WTF new Activity?
            return false;
        }
        return true;
    }
    public void updateContactTrack(Long contactId, boolean track){
        //TODO Implement
        Log.d("malte","Switched tracking of contact " + contactId + " to " + track);
        Log.e("malte","updateContactTrack not implemented" );
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
                    if (label.equals("Test Account Label")) DEBUG_edit("lol@example.com", "Aaron Name was updated" + new Random().nextInt(999), "01730000"+ new Random().nextInt(999), String.valueOf(cw.getDeviceContactId()));
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
        String wherePhone = ContactsContract.Data.CONTACT_ID + " = ? AND " +  ContactsContract.Data.MIMETYPE + " = ?";
        String whereName = ContactsContract.Data.RAW_CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";

        String[] emailParams = new String[]{ContactId, Email.CONTENT_ITEM_TYPE};
        String[] nameParams = new String[] {ContactId, StructuredName.CONTENT_ITEM_TYPE};
        String[] numberParams = new String[]{ContactId, Phone.CONTENT_ITEM_TYPE};

        ArrayList<ContentProviderOperation> ops = new ArrayList<>();

        if(!email.equals("") &&!name.equals("")&& !number.equals(""))
        {
            ops.add(android.content.ContentProviderOperation.newInsert(android.provider.ContactsContract.Data.CONTENT_URI)
                    //.withSelection(wherePhone,emailParams)
                    .withValue(Email.RAW_CONTACT_ID,ContactId)
                    .withValue(Email.MIMETYPE, Email.CONTENT_ITEM_TYPE)
                    .withValue(Email.ADDRESS, "testmail"+ new Random().nextInt(999) + "@test.com")
                    .build());

            ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                    .withSelection(whereName, nameParams)
                    .withValue(StructuredName.DISPLAY_NAME, "Aaron Testkontakt")
                    .withValue(StructuredName.GIVEN_NAME, "Aaron")
                    .withValue(StructuredName.MIDDLE_NAME, "")
                    .withValue(StructuredName.FAMILY_NAME, "Testkontakt")
                    .build());

            ops.add(android.content.ContentProviderOperation.newUpdate(android.provider.ContactsContract.Data.CONTENT_URI)
                    .withSelection(wherePhone,numberParams)
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
