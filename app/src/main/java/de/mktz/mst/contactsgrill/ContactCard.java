package de.mktz.mst.contactsgrill;

import android.support.annotation.NonNull;
import java.util.Calendar;



public class ContactCard implements Comparable<ContactCard>{


    private String displayName;
    private boolean trackCommunication;



    private long firstCorrespondenceTimestamp;
    private long nextCorrespondenceTimestamp;
    private long lastCorrespondenceTimestamp;
    private int correspondenceIntervall;
    private int correspondenceVariance;
    private int correspondenceCount;





    ContactCard(@NonNull String displayName, long firstContact, long lastContact, String contactCount) {
        trackCommunication = true;

        this.displayName = displayName;
        this.firstCorrespondenceTimestamp = firstContact;
        this.lastCorrespondenceTimestamp = lastContact;
        this.correspondenceIntervall = 7;
        this.correspondenceVariance = 3;
        this.correspondenceCount = Integer.parseInt( contactCount ) ;

        //TODO min (Birthday / next Contact)
        this.nextCorrespondenceTimestamp = (this.lastCorrespondenceTimestamp + this.correspondenceIntervall*86400 *1000 + ( long )(Math.random() * correspondenceVariance * 86400 * 1000) );
    }

    public String getDisplayName() {
        return this.displayName ;
    }

    public String getNextCorrespondence(){
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(nextCorrespondenceTimestamp);
        //DateFormat format = SimpleDateFormat.getDateInstance();
        //TODO date formatter?
        return debugStringReformat(c);
    }

    public boolean isContactTracked(){
        return trackCommunication;
    }

    private String debugStringReformat(Calendar c){
        return String.valueOf((c.get(Calendar.DAY_OF_MONTH))) + "." + String.valueOf(1+(c.get(Calendar.MONTH)))+ "." + String.valueOf((c.get(Calendar.YEAR)));
    }


    @Override
    public int compareTo(@NonNull ContactCard o) {
        return (int) (this.nextCorrespondenceTimestamp - o.nextCorrespondenceTimestamp);
    }
}
