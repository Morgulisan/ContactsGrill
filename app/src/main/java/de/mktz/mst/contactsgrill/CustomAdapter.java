package de.mktz.mst.contactsgrill;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.mktz.mst.contactsgrill.database.DB_Handler;
import de.mktz.mst.contactsgrill.newContacts.ContactWrapper;


public class CustomAdapter extends ArrayAdapter<ContactWrapper> implements View.OnClickListener{

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
    private static class ViewHolderProgress{
        TextView contactNameTextView;
        ImageView contactImageView;
        ProgressBar contactProgress;
        TextView progressText;
    }

    enum ViewType{
        VIEW_MAIN,
        VIEW_TOGGLE_TRACK,
        VIEW_PROGRESS
    }

    CustomAdapter(List<ContactWrapper> data, Context context, ViewType vt) {
        super(context, R.layout.list_contact_card, data);
        //this.dataSet = data;
        activityContext=context;
        viewType = vt;
    }


    @Override
    public void onClick(View v) {

    }


    @Override @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        switch (viewType){
            case VIEW_MAIN :
                return InflateMain(position,convertView,parent);
            case VIEW_TOGGLE_TRACK:
                return InflateTrack(position,convertView,parent);
            case VIEW_PROGRESS:
                return InflateProgress(position,convertView,parent);
            default:
                throw new Error("Failed Switch #343525871");
        }
    }

    private View InflateTrack(int position, View convertView,@NonNull ViewGroup parent){
        final ContactWrapper dataModel = getItem(position);
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
        assert dataModel != null;
        if(dataModel.getPhotoUri() != null) {viewHolder.contactImageView.setImageURI(Uri.parse(dataModel.getPhotoUri()));}
        else viewHolder.contactImageView.setImageDrawable(null);
        viewHolder.contactNameTextView.setText(dataModel.getDisplayName());
        viewHolder.contactInfoTextView.setText(String.format(Locale.GERMAN,"%d",dataModel.getDeviceContactId()));
        viewHolder.contactFollowView.setChecked(dataModel.getTracked());
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activityContext,ContactView.class);
                Bundle b = new Bundle();
                b.putLong("contactId",dataModel.getDeviceContactId());
                intent.putExtras(b);
                activityContext.startActivity(intent);
            }
        });
        viewHolder.contactFollowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DB_Handler db_handler = new DB_Handler(activityContext);
                dataModel.setTracked(!dataModel.getTracked());
                db_handler.updateContactTrack(dataModel.getDeviceContactId(),dataModel.getTracked());
            }
        });

        // Return the completed view to render on screen
        return convertView;
    }
    private View InflateMain(int position, View convertView,@NonNull ViewGroup parent){
        final ContactWrapper dataModel = getItem(position);
        ViewHolderMain viewHolder;
        if (convertView == null) {

            viewHolder = new ViewHolderMain();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_contact, parent, false);
            viewHolder.contactNameTextView = convertView.findViewById(R.id.contactNameField);
            viewHolder.contactImageView = convertView.findViewById(R.id.profilePicPrev);
            viewHolder.contactedCheck   = convertView.findViewById(R.id.contacted);
            viewHolder.contactInfoText  = convertView.findViewById(R.id.contactInfoField);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolderMain) convertView.getTag();
        }
        viewHolder.contactedCheck.setChecked(false);
        assert dataModel != null;
        viewHolder.contactNameTextView.setText(dataModel.getDisplayName());
        viewHolder.contactInfoText.setText(TimestampToDateText(dataModel.getLastContactTime()));
        if(dataModel.getPhotoUri() != null) viewHolder.contactImageView.setImageURI(Uri.parse(dataModel.getPhotoUri()));
        else viewHolder.contactImageView.setImageDrawable(null);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activityContext,ContactView.class);
                Bundle b = new Bundle();
                b.putLong("contactId",dataModel.getDeviceContactId());
                intent.putExtras(b);
                activityContext.startActivity(intent);
            }
        });
        viewHolder.contactedCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataModel.setLastContactTime(System.currentTimeMillis());
                DB_Handler handler = new DB_Handler(getContext());
                handler.updateContactLastCon(dataModel.getDeviceContactId(),dataModel.getLastContactTime());
            }
        });
        // Return the completed view to render on screen
        return convertView;
    }
    private View InflateProgress(int position, View convertView,@NonNull ViewGroup parent){
        final ContactWrapper dataModel = getItem(position);
        ViewHolderProgress viewHolder;
        if (convertView == null) {

            viewHolder = new ViewHolderProgress();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_contact_complete, parent, false);
            viewHolder.contactNameTextView = convertView.findViewById(R.id.contactNameField);
            viewHolder.contactImageView = convertView.findViewById(R.id.profilePicPrev);
            viewHolder.progressText   = convertView.findViewById(R.id.percentageText);
            viewHolder.contactProgress  = convertView.findViewById(R.id.progressCompleteness);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolderProgress) convertView.getTag();
        }
        assert dataModel != null;
        viewHolder.contactNameTextView.setText(dataModel.getDisplayName());
        viewHolder.contactProgress.setProgress((int) (dataModel.getCompleteness() *100));
        viewHolder.progressText.setText(String.format(Locale.GERMAN,"%d%%",(int) (dataModel.getCompleteness() *100)));

        if(dataModel.getPhotoUri() != null) viewHolder.contactImageView.setImageURI(Uri.parse(dataModel.getPhotoUri()));
        else viewHolder.contactImageView.setImageDrawable(null);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activityContext,ContactView.class);
                Bundle b = new Bundle();
                b.putLong("contactId",dataModel.getDeviceContactId());
                intent.putExtras(b);
                activityContext.startActivity(intent);
            }
        });
        // Return the completed view to render on screen
        return convertView;
    }

    private static String TimestampToDateText(long timestamp){
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy",Locale.GERMAN);
        Date netDate = (new Date(timestamp));
        return sdf.format(netDate);
    }
}
