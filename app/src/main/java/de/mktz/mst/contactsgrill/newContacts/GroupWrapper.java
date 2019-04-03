package de.mktz.mst.contactsgrill.newContacts;

public class GroupWrapper {

    private int id;
    private String title;

    public GroupWrapper(int id, String title){
        this.id = id;
        this.title = title;
        GroupReader.registerGroup(this);
    }


    public int getGroupId(){
        return  id;
    }

    public String getGroupTitle(){
        return title;
    }
}
