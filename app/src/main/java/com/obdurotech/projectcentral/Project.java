
package com.obdurotech.projectcentral;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.HashMap;

public class Project implements Serializable
{

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("tasks")
    @Expose
    private HashMap<String, Task> tasks = null;
    @SerializedName("reminders")
    @Expose
    private HashMap<String, Reminder> reminders = null;
    @SerializedName("media")
    @Expose
    private HashMap<String, Medium> media = null;
    @SerializedName("notes")
    @Expose
    private HashMap<String, Note> notes = null;
    private final static long serialVersionUID = 4323240669997670886L;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public HashMap<String, Task> getTasks() {
        return tasks;
    }

    public void setTasks(HashMap<String, Task> tasks) {
        this.tasks = tasks;
    }

    public HashMap<String, Reminder> getReminders() {
        return reminders;
    }

    public void setReminders(HashMap<String, Reminder> reminders) {
        this.reminders = reminders;
    }

    public HashMap<String, Medium> getMedia() {
        return media;
    }

    public void setMedia(HashMap<String, Medium> media) {
        this.media = media;
    }

    public HashMap<String, Note> getNotes() {
        return notes;
    }

    public void setNotes(HashMap<String, Note> notes) {
        this.notes = notes;
    }

}
