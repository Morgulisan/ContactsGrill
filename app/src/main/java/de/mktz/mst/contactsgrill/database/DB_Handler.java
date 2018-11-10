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
    private static final int DB_VERSION = 1;



    public DB_Handler(Context context) {
        //SQLiteDatabase.CursorFactory factory = null;
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS contacts (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, tracked INTEGER, photoUri TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS lookups (lookup TEXT PRIMARY KEY, id INTEGER)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

    }

    public DB_Contact insertContact(DB_Contact c){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name",c.getName());
        values.put("tracked",c.getTracked());

        long id = db.insert("contacts",null,values);
        c.setId(id);

        for(String l : c.getLookups() ){
            ContentValues lookups = new ContentValues();
            lookups.put("lookup", l);
            lookups.put("id", id);
            db.insert("lookups",null, lookups);
        }

        this.close();
        return c;
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
        cursor.moveToFirst();
        DB_Contact rc = new DB_Contact(cursor.getInt(indexID),cursor.getString(indexName),cursor.getInt(indexTrack)== 1);
        if(cursor.getString(indexPhoto) != null){
            rc.setPhotoUri(cursor.getString(indexPhoto));
        }
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
        cursor.moveToFirst();
        do {
            DB_Contact rc = new DB_Contact(cursor.getInt(indexID), cursor.getString(indexName), cursor.getInt(indexTrack) == 1);
            if(cursor.getString(indexPhoto) != null){
                rc.setPhotoUri(cursor.getString(indexPhoto));
            }
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
        if(cursor.moveToFirst()) {
            do {
                DB_Contact rc = new DB_Contact(cursor.getInt(indexID), cursor.getString(indexName), cursor.getInt(indexTrack) == 1);
                if(cursor.getString(indexPhoto) != null){
                    rc.setPhotoUri(cursor.getString(indexPhoto));
                }
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
                Log.d("test",cursor.getString(cursor.getColumnIndex("name")));
            }
        }
        cursor.close();
        db.close();
        return i;
    }
}
