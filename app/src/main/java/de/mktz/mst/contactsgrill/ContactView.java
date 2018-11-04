package de.mktz.mst.contactsgrill;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
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
        }
    }

    private void FillData(long id){
        DB_Handler handler = new DB_Handler(this);
        DB_Contact c = handler.getContactByID(id);
        ((TextView) findViewById(R.id.displayNameView)).setText(c.getName());
    }

}
