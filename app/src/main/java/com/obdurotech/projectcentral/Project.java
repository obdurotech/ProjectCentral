package com.obdurotech.projectcentral;

import java.io.Serializable;

/**
 * Created by Sandesh Ashok Naik on 4/9/2017.
 */

public class Project implements Serializable {

    String name, description;

    public Project() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
