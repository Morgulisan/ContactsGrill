package de.mktz.mst.contactsgrill.newContacts;

import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.Manifest;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.*;
import android.support.annotation.Nullable;
import android.support.v4.util.LongSparseArray;
import android.util.Log;

import java.util.ArrayList;
import java.util.Random;

import de.mktz.mst.contactsgrill.database.DB_Handler;

import static android.support.v4.app.ActivityCompat.finishAffinity;
import static android.support.v4.app.ActivityCompat.requestPermissions;
import static android.support.v4.content.ContextCompat.startActivity;


public class ContactReader {

    private static final String[] projectionPhone = {
            Phone.CONTACT_ID
            ,Phone.NUMBER
            ,Phone.TYPE
            ,Phone.LABEL
    };
    private static final String[] projectionEvent = {
            Event.CONTACT_ID
            ,Event.START_DATE
            ,Event.TYPE
            ,Event.LABEL
    };

    public Context context;
    private static LongSparseArray<ContactWrapper> previouslyLoadedContacts = new LongSparseArray<>();

    public ContactReader(Context c){
        this.context = c;
    }

    public ContactWrapper getContactByID(Long id) {
        if(!hasPermission()) return null; //TODO throw exception?
        ContactWrapper cw = null;
        if(previouslyLoadedContacts.get(id) != null){
            cw = previouslyLoadedContacts.get(id);
            if(!cw.isFullyInitialized()){ //TODO Optimize for different init progress
                loadContactDates(cw);
                loadContactPhotos(cw);
                loadContactNumbers(cw);
            }
            return cw;
        }
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, "_ID = '" + id + "'", null, null, null);

        if(cursor == null) return null;
        if (cursor.moveToFirst()) {
            cw = new ContactWrapper(id, cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));
            loadContactDates(cw);
            loadContactPhotos(cw);
            loadContactNumbers(cw);
        }
        cursor.close();
        previouslyLoadedContacts.put(id,cw);
        return cw;
    }
    public ArrayList<ContactWrapper> getListOfAllContacts(DB_Handler.SortParameter sortParameter){
        //TODO WHERE only not in previouslyLoadedContacts, Remove DB_Handler
        String[] projection = {
                ContactsContract.Contacts._ID
                ,ContactsContract.Contacts.DISPLAY_NAME
                ,ContactsContract.Contacts.PHOTO_URI
                ,ContactsContract.Contacts.PHOTO_THUMBNAIL_URI
        };
        String selection = "";//Event.TYPE + " = " + Event.TYPE_BIRTHDAY;
        ArrayList<ContactWrapper> r = new ArrayList<>();
        ContentResolver resolver = context.getContentResolver();
        Cursor contactsCursor = resolver.query(ContactsContract.Contacts.CONTENT_URI, projection,selection,null,null);

        if (contactsCursor == null) return  r;

        int indexId = contactsCursor.getColumnIndex(ContactsContract.Contacts._ID);
        int indexName = contactsCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
        int indexPhotoThumb = contactsCursor.getColumnIndex(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI);
        int indexPhoto = contactsCursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI);


        if(contactsCursor.moveToFirst()) do {
            if(previouslyLoadedContacts.get(contactsCursor.getLong(indexId)) != null){
                r.add(previouslyLoadedContacts.get(contactsCursor.getLong(indexId)));
            }
            else {
                ContactWrapper cw = new ContactWrapper(contactsCursor.getLong(indexId), contactsCursor.getString(indexName)).setPhotoUris(contactsCursor.getString(indexPhoto), contactsCursor.getString(indexPhotoThumb));
                r.add(cw);
                previouslyLoadedContacts.put(contactsCursor.getLong(indexId),cw);
            }
        }while(contactsCursor.moveToNext());
        contactsCursor.close();
        return r;
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

    public void fillContactData(@Nullable String ids){ //TODO refactor ids to accept lists ?
        StringBuilder builder = new StringBuilder().append('(');
        if(ids == null){
            for(int i = 0; i<previouslyLoadedContacts.size();i++) { //TODO off by one?
                builder.append(previouslyLoadedContacts.keyAt(i)).append(',');
            }
        }else builder.append(ids);
        builder.delete(builder.length()-1,builder.length()).append(')');
        String selection = Phone._ID + " IN " + builder.toString();
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(Phone.CONTENT_URI, projectionPhone,selection,null,null);
        if(cursor != null) {
            int indexNumber = cursor.getColumnIndex(Phone.NUMBER);
            int indexType = cursor.getColumnIndex(Phone.TYPE);
            int indexContact = cursor.getColumnIndex(Phone.CONTACT_ID);
            if (cursor.moveToFirst()) do {
                handleCursorPhone(cursor, indexContact, indexType, indexNumber);
            } while (cursor.moveToNext());
            cursor.close();
        }
        selection = ContactsContract.Data.MIMETYPE + " = '" + Event.CONTENT_ITEM_TYPE;
        cursor = resolver.query(ContactsContract.Data.CONTENT_URI, projectionPhone,selection,null,null);
        if(cursor != null) {
            int indexContact = cursor.getColumnIndex(Event.CONTACT_ID);
            int indexDate = cursor.getColumnIndex(Event.START_DATE);
            int indexType = cursor.getColumnIndex(Event.TYPE);
            if (cursor.moveToFirst()) do {
                handleCursorEvents(cursor, indexContact, indexType, indexDate);
            } while (cursor.moveToNext());
            cursor.close();
        }

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
        Cursor phonesCursor = contentResolver.query(Phone.CONTENT_URI, projectionPhone, Phone.CONTACT_ID + " = " + cw.getDeviceContactId(), null, null);
        if(phonesCursor == null) return;
        int indexNumber = phonesCursor.getColumnIndex(Phone.NUMBER);
        int indexType = phonesCursor.getColumnIndex(Phone.TYPE);
        int indexContact = phonesCursor.getColumnIndex(Phone.CONTACT_ID);
        while (phonesCursor.moveToNext()) {
            handleCursorPhone(phonesCursor,indexContact,indexType,indexNumber);
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
        String selection = ContactsContract.Data.MIMETYPE + " = '" + Event.CONTENT_ITEM_TYPE + "' AND " + ContactsContract.Contacts.Data.RAW_CONTACT_ID + " = " + cw.getDeviceContactId();
        Cursor datesCursor = contentResolver.query(ContactsContract.Data.CONTENT_URI,projectionEvent, selection,null,null);
        if(datesCursor == null) return;
        int indexStart = datesCursor.getColumnIndex(Event.START_DATE);
        int indexType = datesCursor.getColumnIndex(Event.TYPE);
        int indexContact = datesCursor.getColumnIndex(Event.CONTACT_ID);
        if(datesCursor.getColumnCount() > 0 && datesCursor.moveToFirst()){
            do {
                handleCursorEvents(datesCursor,indexContact,indexType,indexStart);
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
    private void DEBUG_edit(String email, String name, String number, String ContactId){
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

    private void handleCursorPhone(Cursor cursor, int indexContactId, int indexNumber, int indexTyp){
        previouslyLoadedContacts.get(cursor.getLong(indexContactId)).addPhoneNumber(cursor.getString(indexNumber));
    }
    private void handleCursorEvents(Cursor cursor, int indexContactId, int indexStartDate, int indexType){
        int dateType = cursor.getInt(indexType);

        if (dateType == Event.TYPE_BIRTHDAY) {
            previouslyLoadedContacts.get(cursor.getLong(indexContactId)).setBirthday(cursor.getString(indexStartDate));
        }
        if (dateType == Event.TYPE_ANNIVERSARY) {
            Log.d("malte","Anniversary Event on " + cursor.getString(indexStartDate));
            //TODO set event
        }
        if (dateType == Event.TYPE_CUSTOM){
            String label = cursor.getString(cursor.getColumnIndex(Event.LABEL));
            Log.d("malte", "Custom Event on " + cursor.getString(indexStartDate) + " labeled :" + label );
            //TODO handle custom Event
        }
    }
}
