package de.mktz.mst.contactsgrill.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class DB_Handler extends SQLiteOpenHelper{

    private static final String DB_NAME = "database.db";
    private static final int DB_VERSION = 1;

    public enum SortParameter{
        SORT_NAME,
        SORT_ADDED,
        SORT_BIRTHDAY
    }

    public DB_Handler(Context context) {
        //SQLiteDatabase.CursorFactory factory = null;
        super(context, DB_NAME, null, DB_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS contacts (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, tracked INTEGER, photoUri TEXT, birthDay TEXT, firstContact INTEGER, lastContact INTEGER)");
        db.execSQL("CREATE TABLE IF NOT EXISTS lookups (lookup TEXT PRIMARY KEY, id INTEGER)");
        db.execSQL("CREATE TABLE IF NOT EXISTS phoneNumbers (id INTEGER, number TEXT UNIQUE, type INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        if(oldV <= 1 && newV >=2) {
            //db.execSQL("ALTER TABLE phoneNumbers ADD COLUMN birthDay TEXT;");
            Log.d("malte", "updated database from Version 1 to 2");
        }if(oldV <= 2 && newV >= 3){
            //db.execSQL("ALTER TABLE contacts ADD COLUMN firstContact INTEGER;");
            //db.execSQL("ALTER TABLE contacts ADD COLUMN lastContact INTEGER;");
            Log.d("malte", "updated database from Version 2 to 3");
        }
    }

    public void insertContact(DB_Contact c){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name",c.getName());
        values.put("tracked",c.getTracked());
        values.put("photoUri", c.getPhotoUri());
        values.put("firstContact", c.getFirstContactTime());
        values.put("lastContact", c.getLastContactTime());

        long id = db.insert("contacts",null,values);
        c.setId(id);

        for(String l : c.getLookups() ){
            ContentValues lookups = new ContentValues();
            lookups.put("lookup", l);
            lookups.put("id", id);
            db.insert("lookups",null, lookups);
        }

        db.close();
    }
    public void insertPhoneNumber(long contactID, String phoneNumber,int type){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id",contactID);
        values.put("number",phoneNumber);
        values.put("type", type);
        db.insert("phoneNumbers",null,values);
        db.close();
    }

    public void updateContactTrack(long id, boolean track){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("tracked",track);
        db.update("contacts",cv,"id="+id, null);
        this.close();
    }
    public void updateContactPhotoURI(long id, String uri){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("photoUri",uri);
        db.update("contacts",cv,"id="+id, null);
        this.close();
    }
    public void updateContactBirthday(long id, String birthday){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("birthDay",birthday);
        db.update("contacts",cv,"id="+id, null);
        this.close();
    }
    public void updateContactName(long id, String name){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name",name);
        db.update("contacts",cv,"id="+id, null);
        this.close();
    }
    public void updateContactLastCon(long id, long timestamp){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("lastContact",timestamp);
        db.update("contacts",cv,"id="+id, null);
        this.close();
    }


    public ContentValues getLookups(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor;

        cursor = db.query("lookups",null,null,null,null,null,null);
        ContentValues values = new ContentValues();

        int lookupIndex = cursor.getColumnIndex("lookup");
        int idIndex = cursor.getColumnIndex("id");

        if(cursor.moveToFirst()){
            do{
                values.put(cursor.getString(lookupIndex), cursor.getLong(idIndex));
            }while (cursor.moveToNext());
        }
        cursor.close();
        return values;
    }

    public DB_Contact getContactByID(long contactID){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query("contacts",null,"id=?",new String[]{String.valueOf(contactID)},null,null,null);
        int indexID = cursor.getColumnIndex("id");
        int indexName = cursor.getColumnIndex("name");
        int indexTrack = cursor.getColumnIndex("tracked");
        int indexPhoto = cursor.getColumnIndex("photoUri");
        int indexBirthday = cursor.getColumnIndex("birthDay");
        int indexFirstCon = cursor.getColumnIndex("firstContact");
        int indexLastCon = cursor.getColumnIndex("lastContact");
        cursor.moveToFirst();
        DB_Contact rc = new DB_Contact(cursor.getInt(indexID),cursor.getString(indexName),cursor.getInt(indexTrack)== 1);
        if(cursor.getString(indexPhoto) != null){
            rc.setPhotoUri(cursor.getString(indexPhoto));
        }
        if(cursor.getString(indexBirthday) != null) {
            rc.setBirthday(cursor.getString(indexBirthday));
        }
        rc.setLastContactTime(cursor.getLong(indexLastCon));
        cursor.close();
        Cursor phones = db.query("phoneNumbers",null,"id=?",new String[]{String.valueOf(contactID)},null,null,null);
        if(phones.moveToFirst()) do {
            rc.addPhoneNumber(phones.getString(phones.getColumnIndex("number")));
        }while (phones.moveToNext());
        phones.close();
        return rc;
    }
    public DB_Contact getContactByName(String name){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query("contacts",null,"name=?",new String[]{name},null,null,null);
        int indexID = cursor.getColumnIndex("id");
        int indexName = cursor.getColumnIndex("name");
        int indexTrack = cursor.getColumnIndex("tracked");
        int indexPhoto = cursor.getColumnIndex("photoUri");
        int indexBirthday = cursor.getColumnIndex("birthDay");
        int indexFirstCon = cursor.getColumnIndex("firstContact");
        int indexLastCon = cursor.getColumnIndex("lastContact");
        cursor.moveToFirst();
        DB_Contact rc = new DB_Contact(cursor.getInt(indexID),cursor.getString(indexName),cursor.getInt(indexTrack)== 1);
        if(cursor.getString(indexPhoto) != null){
            rc.setPhotoUri(cursor.getString(indexPhoto));
        }
        if(cursor.getString(indexBirthday) != null) {
            rc.setBirthday(cursor.getString(indexBirthday));
        }
        rc.setLastContactTime(cursor.getLong(indexLastCon));
        cursor.close();
        return rc;
    }


    public ArrayList<DB_Contact> getListOfAllContacts(){
        return getListOfAllContacts(null,null,null,"name");
    }

    public ArrayList<DB_Contact> getListOfAllContacts(SortParameter sortParameter){
        String orderBy;
        switch (sortParameter){
            case SORT_NAME:
                orderBy = "name";
                break;
            case SORT_ADDED:
                orderBy = "id DESC";
                break;
            case SORT_BIRTHDAY:
                orderBy = "birthDay DESC";
                break;
            default:
                orderBy = null;
        }
        return getListOfAllContacts(null,null,null,orderBy);
    }
    public ArrayList<DB_Contact> getListOfTrackedContacts(){
        return getListOfAllContacts(null,"tracked=1",null,null);
    }
    public ArrayList<DB_Contact> getListOfIncompleteContacts(){
        ArrayList<DB_Contact> r = getListOfAllContacts(null,"tracked=1",null,null);
        //r.removeIf(s -> s.getCompleteness() == 1f);
        Collections.sort(r,new Comparator<DB_Contact>() {
            public int compare(DB_Contact o1, DB_Contact o2) {
                //Log.d("malte","Sorted " + o1.getCompleteness() + " vs " + o2.getCompleteness() + " as "  +  (int)((o1.getCompleteness() - o2.getCompleteness()) *100));
                return (int)((o1.getCompleteness() - o2.getCompleteness()) *100);
            }
        });
        return r;
    }

    private ArrayList<DB_Contact> getListOfAllContacts(String[] columns, String selection, String[] selectArgs, String orderBy){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query("contacts",columns,selection,selectArgs,null,null,orderBy);
        ArrayList<DB_Contact> listOfResults = new ArrayList<>();

        int indexID = cursor.getColumnIndex("id");
        int indexName = cursor.getColumnIndex("name");
        int indexTrack = cursor.getColumnIndex("tracked");
        int indexPhoto = cursor.getColumnIndex("photoUri");
        int indexBirthday = cursor.getColumnIndex("birthDay");
        int indexFirstCon = cursor.getColumnIndex("firstContact");
        int indexLastCon = cursor.getColumnIndex("lastContact");
        if(cursor.moveToFirst()) {
            do {
                DB_Contact rc = new DB_Contact(cursor.getInt(indexID), cursor.getString(indexName), cursor.getInt(indexTrack) == 1);
                if(cursor.getString(indexPhoto) != null){
                    rc.setPhotoUri(cursor.getString(indexPhoto));
                }
                if(cursor.getString(indexBirthday) != null) {
                    rc.setBirthday(cursor.getString(indexBirthday));
                }
                rc.setLastContactTime(cursor.getLong(indexLastCon));
                Cursor phones = db.query("phoneNumbers",null,"id=?",new String[]{String.valueOf(cursor.getInt(indexID))},null,null,null);//TODO join Tables
                if(phones.moveToFirst()) do {
                    rc.addPhoneNumber(phones.getString(phones.getColumnIndex("number")));
                }while (phones.moveToNext());
                phones.close();
                listOfResults.add(rc);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return listOfResults;
    }
}
