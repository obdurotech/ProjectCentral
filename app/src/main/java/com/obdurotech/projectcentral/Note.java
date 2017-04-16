
package com.obdurotech.projectcentral;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Note implements Serializable
{

    @SerializedName("note_id")
    @Expose
    private String noteId;
    @SerializedName("note_desc")
    @Expose
    private String noteDesc;
    private final static long serialVersionUID = 1466563817584812890L;

    public String getNoteId() {
        return noteId;
    }

    public void setNoteId(String noteId) {
        this.noteId = noteId;
    }

    public String getNoteDesc() {
        return noteDesc;
    }

    public void setNoteDesc(String noteDesc) {
        this.noteDesc = noteDesc;
    }

}
