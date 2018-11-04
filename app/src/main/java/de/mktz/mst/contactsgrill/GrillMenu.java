package de.mktz.mst.contactsgrill;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.Manifest;
import android.content.pm.PackageManager;
import android.content.Intent;

import de.mktz.mst.contactsgrill.database.DB_Handler;
import de.mktz.mst.contactsgrill.database.DB_ContactLoader;

public class GrillMenu extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 1160;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grill_menu);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadContactsAndShowDebug();
            }
        });
        loadContactsAndShowDebug();

        DB_ContactLoader update = new DB_ContactLoader(this);
        update.UpdateContactsInDB();
        DB_Handler handler = new DB_Handler(this);
        Log.d("test",handler.debugRead() + "");
        Log.d("test", handler.debugReadTracked() + "");

    }


    private void loadContactsAndShowDebug(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
            ((TextView) findViewById(R.id.textView5)).setText("Require Peesrmissions");
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        }
        else {
            try {
                StringBuilder builder = new StringBuilder();
                ContentResolver contentResolver = getContentResolver();
                Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null, null);

                int c = 0;
                if (cursor.getCount() > 0) {
                    while (cursor.moveToNext() && c++ < 10) {
                        if(cursor.isFirst() || cursor.isLast()) {
                            int count = cursor.getColumnCount();
                            while(count-- > 0){
                                builder.append(cursor.getColumnName(count)).append(": ").append(cursor.getString(count)).append("\n");
                            }
                            builder.append("\n\n\n\n");
                        }
                        String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                        String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        String lookup = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
                        //String lookupURI = ContactsContract.Contacts.getLookupUri((long) cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts._ID)), lookup).toString();
                        builder.append(id).append(": Name : ").append(name).append(" LOOKUP : ").append(lookup)/*.append("\n LOOKUP_URI : ").append(lookupURI)*/.append("\n");
                    }
                }
                else builder.append("No Contacts");
                cursor.close();
                ((TextView) findViewById(R.id.textView5)).setText(builder.toString());
            } catch (Exception e) {
                ((TextView) findViewById(R.id.textView5)).setText(e.getMessage());
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.grill_menu, menu);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull  MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {
            try {
                Intent myIntent = new Intent(this, ContactSearch.class);
                startActivity(myIntent);
            }catch (Exception e) {
                ((TextView) findViewById(R.id.textView5)).setText(e.getMessage());
            }
        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
