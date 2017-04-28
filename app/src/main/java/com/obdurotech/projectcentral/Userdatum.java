
package com.obdurotech.projectcentral;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Userdatum implements Serializable
{

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("projects")
    @Expose
    private HashMap<String, Project> projects = null;

    public void setQuicknotes(HashMap<String, Note> quicknotes) {
        this.quicknotes = quicknotes;
    }

    public HashMap<String, Note> getQuicknotes() {
        return quicknotes;
    }

    @SerializedName("quicknotes")
    @Expose
    private HashMap<String, Note> quicknotes = null;
    private final static long serialVersionUID = -367770429138071270L;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public HashMap<String, Project> getProjects() {
        return projects;
    }

    public void setProjects(HashMap<String, Project> projects) {
        this.projects = projects;
    }

}
