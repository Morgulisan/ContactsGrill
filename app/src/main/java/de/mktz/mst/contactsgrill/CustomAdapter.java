package de.mktz.mst.contactsgrill;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Switch;
import android.widget.TextView;
import java.util.ArrayList;

import de.mktz.mst.contactsgrill.database.DB_Contact;
import de.mktz.mst.contactsgrill.database.DB_Handler;


public class CustomAdapter extends ArrayAdapter<DB_Contact> implements View.OnClickListener{

    //private ArrayList<ContactCard> dataSet;
    private Context activityContext;

    private static class ViewHolder {
        TextView contactNameTextView;
        TextView contactInfoTextView;
        Switch contactFollowView;
    }

    CustomAdapter(ArrayList<DB_Contact> data, Context context) {
        super(context, R.layout.list_contact_card, data);
        //this.dataSet = data;
        activityContext=context;

    }

    @Override
    public void onClick(View v) {
        /*
        int position=(Integer) v.getTag();
        Object object= getItem(position);
        ContactCard dataModel = (ContactCard)object;*/

    }

    //private int lastPosition = -1;

    @Override @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        // Get the data item for this position
        final DB_Contact dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag


        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_contact_card, parent, false);
            viewHolder.contactNameTextView = convertView.findViewById(R.id.contactNameField);
            viewHolder.contactInfoTextView = convertView.findViewById(R.id.NextMeetField);
            viewHolder.contactFollowView   = convertView.findViewById(R.id.followEnabled);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //this.lastPosition = position;

        viewHolder.contactNameTextView.setText(dataModel.getName());
        viewHolder.contactInfoTextView.setText(String.format("%d",dataModel.getId()));
        viewHolder.contactFollowView.setChecked(dataModel.getTracked());
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activityContext,ContactView.class);
                activityContext.startActivity(intent);
            }
        });
        viewHolder.contactFollowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DB_Handler db_handler = new DB_Handler(activityContext);
                dataModel.setTracked(!dataModel.getTracked());
                db_handler.updateContactTrack(dataModel.getId(),dataModel.getTracked());
            }
        });

        // Return the completed view to render on screen
        return convertView;
    }
}
