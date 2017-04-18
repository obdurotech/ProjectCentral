
package com.obdurotech.projectcentral;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Reminder implements Serializable
{

    @SerializedName("rem_id")
    @Expose
    private String remId;
    @SerializedName("rem_desc")
    @Expose
    private String remDesc;
    @Expose
    private Date reminderDate;
    @SerializedName("rem_date")
    @Expose
    private String reminderLocation;
    @SerializedName("rem_location")
    private final static long serialVersionUID = -583466873147656789L;

    public String getRemId() {
        return remId;
    }

    public void setRemId(String remId) {
        this.remId = remId;
    }

    public String getRemDesc() {
        return remDesc;
    }

    public void setRemDesc(String remDesc) {
        this.remDesc = remDesc;
    }

    public Date getReminderDate() {return reminderDate;}

    public void setReminderDate(Date newDate) {this.reminderDate = newDate;}

    public String getReminderLocation(){ return reminderLocation;}

    public void setReminderLocation(String newLocation){this.reminderLocation = newLocation;}

}
