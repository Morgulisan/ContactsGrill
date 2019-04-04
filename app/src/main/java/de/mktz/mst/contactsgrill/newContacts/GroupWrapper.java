package de.mktz.mst.contactsgrill.newContacts;

import java.util.LinkedList;

class GroupWrapper {

    private int id;
    private String title;
    private LinkedList<ContactWrapper> members;

    GroupWrapper(int id, String title){
        this.id = id;
        this.title = title;
        GroupReader.registerGroup(this);
        members = new LinkedList<>();
    }


    int getGroupId(){
        return  id;
    }

    GroupWrapper addMember(ContactWrapper contact){
        this.members.add(contact);
        return this;
    }


    String getGroupTitle(){
        return title;
    }

    boolean isMemberOf(ContactWrapper contact){
        return members.contains(contact);
    }

    LinkedList<ContactWrapper> getAllMembers(){
        return (LinkedList<ContactWrapper>) members; //TODO data protection, clone?
    }


}
