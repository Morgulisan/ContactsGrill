package de.mktz.mst.contactsgrill.newContacts;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Objects;

class GroupReader {

    public static final String TRACK_GROUP_NAME = "grill.groups.tracked";


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
            if(groupData.moveToFirst())do {
                new GroupWrapper(groupData.getInt(indexId), groupData.getString(indexTitle));
            } while (groupData.moveToNext());
            groupData.close();
        }
        if(getGroup(TRACK_GROUP_NAME) == null){
            ArrayList<ContentProviderOperation> ops = new ArrayList<>();
            ops.add(android.content.ContentProviderOperation.newInsert(ContactsContract.Groups.CONTENT_URI)
                    .withValue(ContactsContract.Groups.TITLE,TRACK_GROUP_NAME)
                    .build());
            try {
                context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
                Log.e("malte","Updated contact");
                loadGroups();
            }catch (Exception e){
                Log.e("malte",e.getMessage());
            }

        } else Log.e("malte",getGroup(TRACK_GROUP_NAME).getGroupId() + " is Added Group");
        groupsLoaded = true;
    }

    static GroupWrapper getGroup(int groupId){
        return groups.get(groupId);
    }
    static GroupWrapper getGroup(String title) {
        for (Integer giD : Collections.list(groups.keys())) {
            if (groups.get(giD).getGroupTitle().equals(title)) return groups.get(giD);
        }
        return null;
    }


}
