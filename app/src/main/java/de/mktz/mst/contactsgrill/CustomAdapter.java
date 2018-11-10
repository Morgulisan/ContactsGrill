package de.mktz.mst.contactsgrill;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import java.security.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.mktz.mst.contactsgrill.database.DB_Contact;
import de.mktz.mst.contactsgrill.database.DB_Handler;


public class CustomAdapter extends ArrayAdapter<DB_Contact> implements View.OnClickListener{

    private Context activityContext;
    private final ViewType viewType;

    private static class ViewHolderToggleTrack {
        TextView contactNameTextView;
        TextView contactInfoTextView;
        Switch contactFollowView;
        ImageView contactImageView;
    }
    private static class ViewHolderMain{
        TextView contactNameTextView;
        ImageView contactImageView;
        CheckBox contactedCheck;
        TextView contactInfoText;

    }
    enum ViewType{
        VIEW_MAIN,
        VIEW_TOGGLE_TRACK
    }

    CustomAdapter(ArrayList<DB_Contact> data, Context context, ViewType vt) {
        super(context, R.layout.list_contact_card, data);
        //this.dataSet = data;
        activityContext=context;
        viewType = vt;

    }


    @Override
    public void onClick(View v) {

    }

    //private int lastPosition = -1;

    @Override @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        switch (viewType){
            case VIEW_MAIN :
                return InflateMain(position,convertView,parent);
            case VIEW_TOGGLE_TRACK:
                return InflateTrack(position,convertView,parent);
            default:
                Log.d("test", "Failed Switch");
        }
        return convertView;
    }

    private View InflateTrack(int position, View convertView,@NonNull ViewGroup parent){
        final DB_Contact dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolderToggleTrack viewHolder; // view lookup cache stored in tag


        if (convertView == null) {

            viewHolder = new ViewHolderToggleTrack();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_contact_card, parent, false);
            viewHolder.contactNameTextView = convertView.findViewById(R.id.contactNameField);
            viewHolder.contactInfoTextView = convertView.findViewById(R.id.NextMeetField);
            viewHolder.contactFollowView   = convertView.findViewById(R.id.followEnabled);
            viewHolder.contactImageView    = convertView.findViewById(R.id.profileImage);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolderToggleTrack) convertView.getTag();
        }

        //this.lastPosition = position;
        if(dataModel.getPhotoUri() != null) {viewHolder.contactImageView.setImageURI(Uri.parse(dataModel.getPhotoUri()));}
        else viewHolder.contactImageView.setImageDrawable(null);
        viewHolder.contactNameTextView.setText(dataModel.getName());
        viewHolder.contactInfoTextView.setText(String.format("%d",dataModel.getId()));
        viewHolder.contactFollowView.setChecked(dataModel.getTracked());
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activityContext,ContactView.class);
                Bundle b = new Bundle();
                b.putLong("contactId",dataModel.getId());
                intent.putExtras(b);
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
    private View InflateMain(int position, View convertView,@NonNull ViewGroup parent){
        final DB_Contact dataModel = getItem(position);
        ViewHolderMain viewHolder;
        if (convertView == null) {

            viewHolder = new ViewHolderMain();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_contact, parent, false);
            viewHolder.contactNameTextView = convertView.findViewById(R.id.contactNameField);
            viewHolder.contactImageView = convertView.findViewById(R.id.profilePicPrev);
            viewHolder.contactedCheck = convertView.findViewById(R.id.contacted);
            viewHolder.contactInfoText = convertView.findViewById(R.id.contactInfoField);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolderMain) convertView.getTag();
        }
        viewHolder.contactedCheck.setChecked(false);
        viewHolder.contactNameTextView.setText(dataModel.getName());
        viewHolder.contactInfoText.setText(TimestampToDateText(dataModel.getLastContactTime()));

        if(dataModel.getPhotoUri() != null) viewHolder.contactImageView.setImageURI(Uri.parse(dataModel.getPhotoUri()));
        else viewHolder.contactImageView.setImageDrawable(null);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activityContext,ContactView.class);
                Bundle b = new Bundle();
                b.putLong("contactId",dataModel.getId());
                intent.putExtras(b);
                activityContext.startActivity(intent);
            }
        });
        viewHolder.contactedCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataModel.setLastContactTime(System.currentTimeMillis());
            }
        });
        // Return the completed view to render on screen
        return convertView;
    }

    private static String TimestampToDateText(long timestamp){
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        Date netDate = (new Date(timestamp));
        return sdf.format(netDate);
    }
}
