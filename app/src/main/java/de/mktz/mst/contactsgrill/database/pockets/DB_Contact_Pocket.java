package de.mktz.mst.contactsgrill.database.pockets;

import android.view.View;

import java.util.List;

public abstract class DB_Contact_Pocket {

    public static List<DB_Contact_Pocket> types;

    public abstract String getPocketName();
    public abstract View getInflatedPoket();
    public abstract float getCompleteness();

}
