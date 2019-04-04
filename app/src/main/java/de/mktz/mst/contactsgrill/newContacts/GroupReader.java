package de.mktz.mst.contactsgrill.newContacts;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Objects;

class GroupReader {

    private static Dictionary<Integer,GroupWrapper> groups = new Hashtable<>();
    private Context context;
    private boolean groupsLoaded;

    GroupReader(Context c){
        this.context = Objects.requireNonNull(c,"Null Contect given to Group Reader");
        this.groupsLoaded = false;
    }

    static void registerGroup(GroupWrapper group){
        groups.put(group.getGroupId(),group);
    }

    void loadGroups(){
        if(groupsLoaded) return;
        Cursor groupData = context.getContentResolver().query(
                ContactsContract.Groups.CONTENT_URI
                ,null
                ,null
                ,null
                ,null);
        if(groupData != null) {
            int indexId = groupData.getColumnIndex(ContactsContract.Groups._ID);
            int indexTitle = groupData.getColumnIndex(ContactsContract.Groups.TITLE);
            groupData.moveToFirst();
            do {
                new GroupWrapper(groupData.getInt(indexId), groupData.getString(indexTitle));
            } while (groupData.moveToNext());
            groupData.close();
            groupsLoaded = true;
        }
    }

    GroupWrapper getGroup(int groupId){
        return groups.get(groupId);
    }


}
