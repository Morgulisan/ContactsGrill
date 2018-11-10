package de.mktz.mst.contactsgrill;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import de.mktz.mst.contactsgrill.database.DB_Contact;
import de.mktz.mst.contactsgrill.database.DB_Handler;

public class ContactView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        long contactID = -1;
        Bundle b  = getIntent().getExtras();
        if(b != null){
            contactID = b.getLong("contactId");
        }
        if(contactID != -1){
            FillData(contactID);
            getDebugData(contactID);
        }
    }

    private void FillData(long id){
        DB_Handler handler = new DB_Handler(this);
        DB_Contact contact = handler.getContactByID(id);
        ((TextView) findViewById(R.id.displayNameView)).setText(contact.getName());
        if(contact.getPhotoUri() != null) {
            ImageView image = findViewById(R.id.profileImage);
            image.setImageURI(Uri.parse(contact.getPhotoUri()));
        }
        if(contact.getBirthday() != null){
            String birthday = contact.getBirthday();
            if(birthday.length() == 7)((TextView) findViewById(R.id.BirtdayInfo)).setTextColor(Color.rgb(250,125,0));
            ((TextView) findViewById(R.id.BirtdayInfo)).setText(DebugBirthdayMess(birthday));
        }else {
            ((TextView) findViewById(R.id.BirtdayInfo)).setText(R.string.missing_birthday);
            ((TextView) findViewById(R.id.BirtdayInfo)).setTextColor(Color.RED);
        }
        ((TextView) findViewById(R.id.completeInfoField)).setText("Kontakt " + (int) (contact.getCompleteness() * 100)  + "% komplett");
    }

    void getDebugData(long id){
        DB_Handler handler = new DB_Handler(this);
        DB_Contact contact = handler.getContactByID(id);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, 1160);
            ((TextView) findViewById(R.id.debugText)).setText("...");
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        }
        else {
            //Has permissions
            try {
                StringBuilder builder = new StringBuilder();
                ContentResolver contentResolver = getContentResolver();
                Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null, null);

                if (cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {
                        if(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)).equals(contact.getName())) {
                            int count = cursor.getColumnCount();
                            while(count-- > 0){
                                builder.append(cursor.getColumnName(count)).append(": ").append(cursor.getString(count)).append("\n");
                            }
                            builder.append("\n\n\n\n");
                            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                            String lookup = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
                            String lookupURI = ContactsContract.Contacts.getLookupUri((long) cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts._ID)), lookup).toString();
                            builder.append(id).append(": Name : ").append(name).append(" LOOKUP : ").append(lookup).append("\n LOOKUP_URI : ").append(lookupURI).append("\n");
                        }
                    }
                }
                else builder.append("Invalid Contact");
                cursor.close();
                ((TextView) findViewById(R.id.debugText)).setText(builder.toString());
            } catch (Exception e) {
                ((TextView) findViewById(R.id.debugText)).setText(e.getMessage());
            }
        }
    }

    private static String DebugBirthdayMess(String birthday){
        String age = "";
        Calendar c = new GregorianCalendar();
        c.set(Calendar.DAY_OF_MONTH, Integer.parseInt(birthday.substring(birthday.length()-2)));
        c.set(Calendar.MONTH, Integer.parseInt(birthday.substring(birthday.length()-5,birthday.length() - 3)) -1 );
        Date bd = c.getTime();
        Date today = new Date();
        long diff = bd.getTime() - today.getTime();
        diff = diff / (1000L*60L*60L*24L);
        if (diff < 0){
            diff+= 365; //TODO Leap
        }
        if(birthday.length() == 10) {
            c.set(Calendar.YEAR, Integer.parseInt(birthday.substring(0, 4)));
            age = getAge(c) + " ";
        }
        if(diff == 0) return birthday + " today!!";
        return birthday + "  (" + age + "+" + diff + "d)";
    }
    public static int getAge(Calendar a) {
        Calendar b = new GregorianCalendar();
        b.setTime(new Date());
        Log.d("test", b.toString());
        Log.d("test", a.toString());
        int diff = b.get(Calendar.YEAR) - a.get(Calendar.YEAR);
        if (a.get(Calendar.MONTH) > b.get(Calendar.MONTH) ||
                (a.get(Calendar.MONTH) == b.get(Calendar.MONTH) && a.get(Calendar.DATE) > b.get(Calendar.DATE))) {
            diff--;
        }
        return diff;
    }
}
