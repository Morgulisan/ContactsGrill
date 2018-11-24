package de.mktz.mst.contactsgrill;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;

import de.mktz.mst.contactsgrill.database.DB_Contact;
import de.mktz.mst.contactsgrill.database.DB_Handler;

//import static android.content.pm.PackageManager.PERMISSION_DENIED;

public class ContactSearch extends AppCompatActivity {

    //private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 1160;


    ArrayList<DB_Contact> dataModels;
    ListView listView;
    protected CustomAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_search);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        listView = findViewById(R.id.list_contacts_dynamic);
        contactsToMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void contactsToMenu(){
        DB_Handler handler = new DB_Handler(this);
        dataModels = handler.getListOfAllContacts(DB_Handler.SortParameter.SORT_ADDED);
        adapter = new  CustomAdapter(dataModels, getApplicationContext(), CustomAdapter.ViewType.VIEW_TOGGLE_TRACK);
        listView.setAdapter(adapter);
    }

}
