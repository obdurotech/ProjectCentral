
package com.obdurotech.projectcentral;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserData implements Serializable
{

    @SerializedName("userdata")
    @Expose
    private HashMap<String, Userdatum> userdata = null;
    private final static long serialVersionUID = 7610732789655188903L;

    public HashMap<String, Userdatum> getUserdata() {
        return userdata;
    }

    public void setUserdata(HashMap<String, Userdatum> userdata) {
        this.userdata = userdata;
    }

}
