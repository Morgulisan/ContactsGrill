package de.mktz.mst.contactsgrill;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import de.mktz.mst.contactsgrill.database.DB_Contact;
import de.mktz.mst.contactsgrill.database.DB_Handler;
import de.mktz.mst.contactsgrill.newContacts.ContactWrapper;
import de.mktz.mst.contactsgrill.viewModel.GrillMenuViewModel;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link GrillMenuFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GrillMenuFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_GROUP_NAME = "group-name";

    // TODO: Rename and change types of parameters
    private String groupName;

    private GrillMenuViewModel viewModel;
    private static int sortMode = 0;

    ArrayList<ContactWrapper> dataModels;
    CustomAdapter adapter;
    ListView listView;
    FloatingActionButton fab;

    public GrillMenuFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param groupName Group Name.
     * @return A new instance of fragment GrillMenuFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GrillMenuFragment newInstance(String groupName) {
        GrillMenuFragment fragment = new GrillMenuFragment();
        Bundle args = new Bundle();
        args.putString(ARG_GROUP_NAME, groupName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            groupName = getArguments().getString(ARG_GROUP_NAME);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        viewModel = GrillMenuViewModel.getInstance(getActivity().getApplication());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView = getView().findViewById(R.id.list_contacts_dynamic);
        contactsToMenu();
    }

    public void contactsToMenu() {
        CustomAdapter.ViewType vt = CustomAdapter.ViewType.VIEW_MAIN;
        switch (groupName){
            case "1":
                dataModels = viewModel.getDatabaseHandler().getListOfTrackedContacts();
                break;
            case "2":
                dataModels = viewModel.getDatabaseHandler().getListOfAllContacts();
                break;
            case "3":
                dataModels = viewModel.getDatabaseHandler().getListOfAllContacts(DB_Handler.SortParameter.SORT_BIRTHDAY);
                break;
            case "4":
                dataModels = viewModel.getDatabaseHandler().getListOfIncompleteContacts();
                vt = CustomAdapter.ViewType.VIEW_PROGRESS;
                break;
            default:
                dataModels = viewModel.getDatabaseHandler().getListOfAllContacts();
        }
        adapter = new CustomAdapter(dataModels, getActivity().getApplicationContext(), vt);
        listView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged(); //TODO if List changed, update
    }

    @Override
    public View onCreateView(@NonNull  LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View newView = inflater.inflate(R.layout.fragment_grill_menu, container, false);
        fab = newView.findViewById(R.id.changeSort);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sortMode = ++sortMode % 5;
                fab.setImageDrawable(getSortIcon(getResources(),sortMode));
                sortContacts(dataModels,sortMode);
                adapter.notifyDataSetChanged();
            }
        });
        return newView;
    }



    public static void sortContacts(ArrayList<ContactWrapper> list, int sortType){
        switch (sortType) {
            case 0:
                Collections.sort(list, new Comparator<ContactWrapper>() {
                    public int compare(ContactWrapper o1, ContactWrapper o2) {
                        return (int) ((o1.getLastContactTime() - o2.getLastContactTime()));
                    }
                });
                break;
            case 1:
                Collections.sort(list, new Comparator<ContactWrapper>() {
                    public int compare(ContactWrapper o1, ContactWrapper o2) {
                        return (int) ((o1.getId() - o2.getId()));
                    }
                });
                break;
            case 2:
                Collections.sort(list, new Comparator<ContactWrapper>() {
                    public int compare(ContactWrapper o1, ContactWrapper o2) {
                        if (o1.getDisplayName() == null || o2.getDisplayName() == null) return 999;
                        return o1.getDisplayName().compareTo(o2.getDisplayName());
                    }
                });
                break;
            case 3:
                Collections.sort(list, new Comparator<ContactWrapper>() {
                    public int compare(ContactWrapper o1, ContactWrapper o2) {
                        return (int) ((o1.getCompleteness() - o2.getCompleteness()) * 100);
                    }
                });
                break;
            default:
                Collections.sort(list, new Comparator<ContactWrapper>() {
                    public int compare(ContactWrapper o1, ContactWrapper o2) {
                        return (int) ((o1.getId() - o2.getId()));
                    }
                });
        }
    }
    private static Drawable getSortIcon(Resources r, int sortType) {
        switch (sortType) {
            case 0:
                return r.getDrawable(R.drawable.ic_sort_lastseen, null);
            case 1:
                return r.getDrawable(R.drawable.ic_sort_person, null);
            case 2:
                return r.getDrawable(R.drawable.ic_sort_by_name, null);
            case 3:
                return r.getDrawable(R.drawable.ic_sort_completeness, null);
            default:
                return r.getDrawable(R.drawable.ic_sort_24dp, null);
        }
    }

}
