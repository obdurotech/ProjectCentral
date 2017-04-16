
package com.obdurotech.projectcentral;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Medium implements Serializable
{

    @SerializedName("media_id")
    @Expose
    private String mediaId;
    @SerializedName("media_link")
    @Expose
    private String mediaLink;
    @SerializedName("media_name")
    @Expose
    private String mediaName;
    private final static long serialVersionUID = 767238625450181572L;

    public String getMediaId() {
        return mediaId;
    }

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }

    public String getMediaName() {
        return mediaName;
    }

    public void setMediaName(String mediaName) {
        this.mediaName = mediaName;
    }

    public String getMediaLink() {
        return mediaLink;
    }

    public void setMediaLink(String mediaLink) {
        this.mediaLink = mediaLink;
    }

}
