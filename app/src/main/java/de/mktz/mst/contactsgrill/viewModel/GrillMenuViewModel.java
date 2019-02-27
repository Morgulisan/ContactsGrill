package de.mktz.mst.contactsgrill.viewModel;

import android.Manifest;
import android.app.Application;
import android.content.pm.PackageManager;

import de.mktz.mst.contactsgrill.database.DB_ContactLoader;
import de.mktz.mst.contactsgrill.newContacts.ContactReader;

public class GrillMenuViewModel {
    private static GrillMenuViewModel ourInstance = null;

    private Application application;
    private ContactReader contactReader;

    public static GrillMenuViewModel getInstance(Application application) {
        if (ourInstance == null)
            ourInstance = new GrillMenuViewModel(application);

        return ourInstance;
    }

    private GrillMenuViewModel(Application application) {
        this.application = application;

        initDatabase();
        new Thread(new Runnable() {
            @Override
            public void run() {
                updateDatabase();
            }
        }).run();
    }

    private boolean hasPermissionsRequired(){
        return application.checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED;
    }

    private void updateDatabase() {
        if(hasPermissionsRequired()) {
            DB_ContactLoader update = new DB_ContactLoader(application);
            update.UpdateContactsInDB();
            update.UpdateBirthdaysInDB();
            if (Math.random() > 0.95) //TODO Remove
                update.UpdatePhoneNumbers();
        }
    }

    private void initDatabase() {
        contactReader = new ContactReader(application);
    }

    public ContactReader getDatabaseHandler() {
        return contactReader;
    }
}
