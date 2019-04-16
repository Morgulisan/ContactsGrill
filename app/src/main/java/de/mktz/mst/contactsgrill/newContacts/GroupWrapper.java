package de.mktz.mst.contactsgrill.newContacts;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

class GroupWrapper {

    private int id;
    private String title;
    private LinkedList<ContactWrapper> members;

    GroupWrapper(int id, String title){
        this.id = id;
        this.title = Objects.requireNonNull(title);
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

    @SuppressWarnings("unchecked")
    List<ContactWrapper> getAllMembers() {
        return (List<ContactWrapper>) members.clone();
    }


}
