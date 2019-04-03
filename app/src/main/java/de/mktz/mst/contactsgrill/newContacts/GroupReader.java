package de.mktz.mst.contactsgrill.newContacts;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

import java.util.Dictionary;

class GroupReader {

    private static Dictionary<Integer,GroupWrapper> groups;
    private Context context;

    public GroupReader(Context c){
        this.context = c;
    }

    static void registerGroup(GroupWrapper group){
        groups.put(group.getGroupId(),group);
    }

    public void createGroups(){
        Cursor groupData = context.getContentResolver().query(
                ContactsContract.Groups.CONTENT_URI
                ,null
                ,null
                ,null
                ,null);

        int indexId = groupData.getColumnIndex(ContactsContract.Groups._ID);
        int indexTitle = groupData.getColumnIndex(ContactsContract.Groups.TITLE);


        groupData.moveToFirst();
        do {
           new GroupWrapper(groupData.getInt(indexId),groupData.getString(indexTitle));
        }while(groupData.moveToNext());
        groupData.close();
    }




}
