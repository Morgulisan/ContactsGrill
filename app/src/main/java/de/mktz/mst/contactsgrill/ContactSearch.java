package de.mktz.mst.contactsgrill;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import de.mktz.mst.contactsgrill.database.DB_Contact;
import de.mktz.mst.contactsgrill.database.DB_Handler;

//import static android.content.pm.PackageManager.PERMISSION_DENIED;

public class ContactSearch extends AppCompatActivity {

    //private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 1160;


    ArrayList<DB_Contact> dataModels;
    ListView listView;
    protected CustomAdapter adapter;
    private int sortMode = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_search);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        listView = findViewById(R.id.list_contacts_dynamic);
        contactsToMenu();
        FloatingActionButton fab = findViewById(R.id.changeSort);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sortMode = ++sortMode % 4;
                sortContacts(dataModels,sortMode);
                adapter.notifyDataSetChanged();
            }
        });
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

    public static void sortContacts(ArrayList<DB_Contact> list,int sortType){
        switch (sortType) {
            case 0:
                Collections.sort(list, new Comparator<DB_Contact>() {
                    public int compare(DB_Contact o1, DB_Contact o2) {
                        return (int) ((o1.getLastContactTime() - o2.getLastContactTime()));
                    }
                });
                break;
            case 1:
                Collections.sort(list, new Comparator<DB_Contact>() {
                    public int compare(DB_Contact o1, DB_Contact o2) {
                        return (int) ((o1.getId() - o2.getId()));
                    }
                });
                break;
            case 2:
                Collections.sort(list, new Comparator<DB_Contact>() {
                    public int compare(DB_Contact o1, DB_Contact o2) {
                        if (o1.getDisplayName() == null || o2.getDisplayName() == null) return 999;
                        return o1.getDisplayName().compareTo(o2.getDisplayName());
                    }
                });
                break;
            default:
                Collections.sort(list, new Comparator<DB_Contact>() {
                    public int compare(DB_Contact o1, DB_Contact o2) {
                        return (int) ((o1.getCompleteness() - o2.getCompleteness()) * 100);
                    }
                });
        }
    }

}
