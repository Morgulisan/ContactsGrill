package de.mktz.mst.contactsgrill;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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
    }

    void getDebugData(long id){
        Log.d("test","Opening Contact for ID " + id);
        DB_Handler handler = new DB_Handler(this);
        DB_Contact contact = handler.getContactByID(id);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, 1160);
            ((TextView) findViewById(R.id.debugText)).setText("...");
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        }
        else {
            Log.d("test", "Has Permissions");
            try {
                StringBuilder builder = new StringBuilder();
                ContentResolver contentResolver = getContentResolver();
                Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null, null);

                int c = 0;
                if (cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {
                        if(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)).equals(contact.getName())) {
                            int count = cursor.getColumnCount();
                            while(count-- > 0){
                                builder.append(cursor.getColumnName(count)).append(": ").append(cursor.getString(count)).append("\n");
                            }
                            builder.append("\n\n\n\n");
                            String Cid = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
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

}
