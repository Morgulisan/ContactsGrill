package de.mktz.mst.contactsgrill.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DB_Handler extends SQLiteOpenHelper{

    private static final String DB_NAME = "database.db";
    private static final int DB_VERSION = 3;



    public DB_Handler(Context context) {
        //SQLiteDatabase.CursorFactory factory = null;
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS contacts (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, tracked INTEGER, photoUri TEXT, birthDay TEXT, firstContact INTEGER, lastContact INTEGER)");
        db.execSQL("CREATE TABLE IF NOT EXISTS lookups (lookup TEXT PRIMARY KEY, id INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        if(oldV <= 1 && newV >=2) {
            db.execSQL("ALTER TABLE contacts ADD COLUMN birthDay TEXT;");
            Log.d("test", "updated database from Version 1 to 2");
        }if(oldV <= 2 && newV >= 3){
            db.execSQL("ALTER TABLE contacts ADD COLUMN firstContact INTEGER;");
            db.execSQL("ALTER TABLE contacts ADD COLUMN lastContact INTEGER;");
            Log.d("test", "updated database from Version 2 to 3");
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

        this.close();
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
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query("contacts",null,null,null,null,null,null);
        ArrayList<DB_Contact> listOfResults = new ArrayList<>();

        int indexID = cursor.getColumnIndex("id");
        int indexName = cursor.getColumnIndex("name");
        int indexTrack = cursor.getColumnIndex("tracked");
        int indexPhoto = cursor.getColumnIndex("photoUri");
        int indexBirthday = cursor.getColumnIndex("birthDay");
        int indexFirstCon = cursor.getColumnIndex("firstContact");
        int indexLastCon = cursor.getColumnIndex("lastContact");
        cursor.moveToFirst();
        do {
            DB_Contact rc = new DB_Contact(cursor.getInt(indexID), cursor.getString(indexName), cursor.getInt(indexTrack) == 1);
            if(cursor.getString(indexPhoto) != null){
                rc.setPhotoUri(cursor.getString(indexPhoto));
            }
            if(cursor.getString(indexBirthday) != null) {
                rc.setBirthday(cursor.getString(indexBirthday));
            }
            rc.setLastContactTime(cursor.getLong(indexLastCon));
            listOfResults.add(rc);
        } while (cursor.moveToNext());
        cursor.close();
        db.close();
        return listOfResults;
    }
    public ArrayList<DB_Contact> getListOfTrackedContacts(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query("contacts",null,"tracked=1",null,null,null,null);
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
                listOfResults.add(rc);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return listOfResults;
    }

    public int debugRead(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query("contacts",null,null,null,null,null,null);
        int i = 0;
        cursor.moveToFirst();
        while(cursor.moveToNext()){
            i++;
        }
        cursor.close();
        db.close();
        return i;
    }

    public int debugReadTracked(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query("contacts",null,null,null,null,null,null);
        int i = 0;
        cursor.moveToFirst();
        while(cursor.moveToNext()){
            if(cursor.getInt(cursor.getColumnIndex("tracked")) != 0) {
                i++;
               // Log.d("test",cursor.getString(cursor.getColumnIndex("name")));
            }
        }
        cursor.close();
        db.close();
        return i;
    }
}
