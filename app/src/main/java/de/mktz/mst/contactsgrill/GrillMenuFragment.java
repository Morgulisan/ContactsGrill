package de.mktz.mst.contactsgrill;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import de.mktz.mst.contactsgrill.database.DB_Contact;
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

    ArrayList<DB_Contact> dataModels;
    CustomAdapter adapter;
    ListView listView;

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
        dataModels = viewModel.getDatabaseHandler().getListOfTrackedContacts();
        adapter = new CustomAdapter(dataModels, getActivity().getApplicationContext(), CustomAdapter.ViewType.VIEW_MAIN);
        listView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        contactsToMenu();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_grill_menu, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
    }
}
