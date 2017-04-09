package com.obdurotech.projectcentral;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Sandesh Ashok Naik on 4/9/2017.
 */

public class User implements Serializable {

    String name, email;
    List<Project> projects;

    public User() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Project> getProjects() {
        return projects;
    }

}
