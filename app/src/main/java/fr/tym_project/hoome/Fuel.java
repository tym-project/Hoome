package fr.tym_project.hoome;

import android.support.annotation.NonNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by tym on 15/07/17.
 */

public class Fuel implements Comparable {
    private String id = new String();
    private String date = new String();
    private String cost = new String();

    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy");

    public Fuel(String _id, String _date, String _cost){
        id=_id;
        date=_date;
        cost=_cost;
    }



    public void setDate(String mDate){
        date=mDate;
    }

    public String getDate(){
        return date;
    }

    public String getDisplayDate() {
        return displayFormat.format(this.getDateasDate());
    }

    public String getId(){ return id; }

    public Date getDateasDate(){


        try {
            return format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return new Date();

    }

    @Override
    public String toString() {
        return this.getDisplayDate()+" ("+this.cost+"€)";
    }

    @Override
    public int compareTo(@NonNull Object o) {
        Fuel f = (Fuel) o;
        return f.getDateasDate().compareTo(getDateasDate());
    }
}

