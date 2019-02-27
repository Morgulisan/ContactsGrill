package de.mktz.mst.contactsgrill;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;
import java.util.function.BiConsumer;

import de.mktz.mst.contactsgrill.newContacts.ContactReader;
import de.mktz.mst.contactsgrill.newContacts.ContactWrapper;

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
        final ContactReader reader = new ContactReader(this);
        final ContactWrapper contact = reader.getContactByID(id);
        ((TextView) findViewById(R.id.displayNameView)).setText(contact.getDisplayName());
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
        ((TextView) findViewById(R.id.completeInfoField)).setText(String.format(getResources().getString(R.string.contact_view_completeness),(int) (contact.getCompleteness() * 100)));//TODO
        if(contact.getCompleteness() != 1f){
            ((ImageView) findViewById(R.id.completenessIcon)).setImageDrawable(getResources().getDrawable(R.drawable.ic_error_outline_24dp,null));
            //TODO Color
        }
        if(!contact.getPhoneNumbers().isEmpty() )((TextView) findViewById(R.id.infoFeld2)).setText(String.format("%s",contact.getPhoneNumbers().get(0)));
        ((Switch)findViewById(R.id.followed)).setChecked(contact.getTracked());
        findViewById(R.id.followed).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contact.setTracked(((Switch) findViewById(R.id.followed)).isChecked());
                reader.updateContactTrack(contact.getId(),((Switch) findViewById(R.id.followed)).isChecked());
            }
        });
        //DEBUG

        ContactReader cr = new ContactReader(getApplicationContext());
        int count = 0;
        long a = (long)(new Random().nextInt(620));
        while(count-- != 0 && a != 0) {
            cr.getContactByID(a--).DEBUG_Log();
        }

        //DEBUG END
    }

    void getDebugData(long id){

        ContactReader reader = new ContactReader(this);
        ContactWrapper contact = reader.getContactByID(id);

        if (checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, 1160);
            ((TextView) findViewById(R.id.debugText)).setText("...");
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        }
        else {
            //Has permissions
            try {
                final StringBuilder builder = new StringBuilder();
                ContentResolver contentResolver = getContentResolver();
                Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null, null);
                contact.getCompletingTasks().forEach(new BiConsumer<String, Integer>() {
                    @Override
                    public void accept(String s, Integer integer) {
                        if(s.substring(0,1).equals("#"))
                        builder.append(s).append(": ");
                        builder.append(getResources().getString(integer)).append("\n");

                    }
                });
                builder.append("\n");

                if (cursor != null && cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {
                        if(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)).equals(contact.getDisplayName())) {
                            int count = cursor.getColumnCount();
                            while(count-- > 0){
                                // add to debug text
                                builder.append(cursor.getColumnName(count)).append(": ").append(cursor.getString(count)).append("\n");
                            }
                            builder.append("\n\n\n\n");

                            String deviceID = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                            ContactReader cr = new ContactReader(getApplicationContext());
                            cr.getContactByID(Long.parseLong(deviceID)).DEBUG_Log();


                            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                            String lookup = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
                            String lookupURI = ContactsContract.Contacts.getLookupUri((long) cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts._ID)), lookup).toString();
                            builder.append(id).append(": Name : ").append(name).append(" LOOKUP : ").append(lookup).append("\n LOOKUP_URI : ").append(lookupURI).append("\n");
                        }
                    }
                    cursor.close();
                }
                else builder.append("Invalid Contact");

                cursor = contentResolver.query(ContactsContract.Data.CONTENT_URI, null, null, null, null, null);
                if (cursor != null && cursor.getCount() > 0) {
                    LinearLayout linLayout = findViewById(R.id.detailsContainer);
                    //LinearLayout linLayout = findViewById(R.id.detailsContainer);
                    while (cursor.moveToNext()) {
                        if(cursor.getString(cursor.getColumnIndex(ContactsContract.Data.DISPLAY_NAME)).equals(contact.getDisplayName())) {
                            addDataEntry(cursor, linLayout);
                            int count = cursor.getColumnCount();
                            while(count-- > 0){
                                // add as view
                                //if(cursor.getString(count) != null) addDataEntry(cursor.getString(cursor.getColumnIndex("mimetype")),cursor.getColumnName(count), cursor.getString(count), linLayout);
                                // add to debug text
                                try {
                                    if(cursor.getColumnName(count).equals("mimetype"))// Log.d("malte",cursor.getString(count));
                                    if(cursor.getString(count) != null)
                                        builder.append(cursor.getColumnName(count)).append(": ").append(cursor.getString(count)).append("\n");
                                }catch (Exception e) { Log.d("malte",e.getMessage());}

                            }
                        }
                    }
                    cursor.close();
                }

                ((TextView) findViewById(R.id.debugText)).setText(builder.toString());
            } catch (Exception e) {
                ((TextView) findViewById(R.id.debugText)).setText(e.getMessage());
            }
        }
        ((TextView) findViewById(R.id.debugText)).append(contact.getPhoneNumbers().toString());
    }


    private void addEntry(String id, String value, ViewGroup root) {
        View v = getLayoutInflater().inflate(R.layout.contact_detail_item, root, false);
        TextView desc = v.findViewById(R.id.descriptor);
        TextView val = v.findViewById(R.id.value);

        String descString = id;
        int resId = getResources().getIdentifier(id, "string", getPackageName());
        if (resId != 0)
            descString = getString(resId);

        desc.setText(descString);
        val.setText(value);

        if (!descString.equals("-"))
            root.addView(v);
    }


    //TODO
    private void addDataEntry(Cursor cursor, ViewGroup root) {
        String mime = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.MIMETYPE));
        String info;
        String value;

        View v = getLayoutInflater().inflate(R.layout.contact_detail_item, root, false);
        TextView desc = v.findViewById(R.id.descriptor);
        TextView val = v.findViewById(R.id.value);
        ImageView icon = v.findViewById(R.id.informationIcon);

        switch (mime){
            case "vnd.android.cursor.item/phone_v2": //TODO Constants
                icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_phone_black_24dp,null));
                info = "phone"; //TODO type
                value = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                break;
            case "vnd.android.cursor.item/email_v2": //TODO Constants
                icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_mail_outline_black_24dp,null));
                info = "mail"; //TODO Type
                value = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));
                break;
            default:
                //Log.d("malte",mime);
                return;
        }

        String descString = info;
        int resId = getResources().getIdentifier(info, "string", getPackageName());
        if (resId != 0)
            descString = getString(resId);

        desc.setText(descString);
        val.setText(value);

        if (!descString.equals("-"))
            root.addView(v);
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
        int diff = b.get(Calendar.YEAR) - a.get(Calendar.YEAR);
        if (a.get(Calendar.MONTH) > b.get(Calendar.MONTH) ||
                (a.get(Calendar.MONTH) == b.get(Calendar.MONTH) && a.get(Calendar.DATE) > b.get(Calendar.DATE))) {
            diff--;
        }
        return diff;
    }

}
