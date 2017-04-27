
package com.obdurotech.projectcentral;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Task implements Serializable
{

    @SerializedName("task_id")
    @Expose
    private String taskId;
    @SerializedName("task_name")
    @Expose
    private String taskName;
    @SerializedName("task_desc")
    @Expose
    private String taskDesc;
    @SerializedName("task_type")
    @Expose
    private String taskType;
    @SerializedName("task_status")
    @Expose
    private String taskStatus;
    private final static long serialVersionUID = -3989619669521095119L;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskDesc() {
        return taskDesc;
    }

    public void setTaskDesc(String taskDesc) {
        this.taskDesc = taskDesc;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public String getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }

}
