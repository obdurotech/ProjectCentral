
package com.obdurotech.projectcentral;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Task implements Serializable
{

    @SerializedName("task_id")
    @Expose
    private String taskId;
    @SerializedName("task_desc")
    @Expose
    private String taskDesc;
    private final static long serialVersionUID = -3989619669521095119L;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskDesc() {
        return taskDesc;
    }

    public void setTaskDesc(String taskDesc) {
        this.taskDesc = taskDesc;
    }

}
