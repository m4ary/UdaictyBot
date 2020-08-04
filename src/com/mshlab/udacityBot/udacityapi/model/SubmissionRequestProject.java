package com.mshlab.udacityBot.udacityapi.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SubmissionRequestProject {

    @SerializedName("project_id")
    @Expose
    private Integer projectId;
    @SerializedName("language")
    @Expose
    private String language;

    /**
     * @return ID of the Project of submissions being requested
     */
    public Integer getProjectId() {
        return projectId;
    }

    /**
     * @param projectId The project_id
     */
    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }


    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    @Override
    public String toString() {
        return "SubmissionRequestProject{" +
                "projectId=" + projectId +
                ", language=" + language +
                '}';
    }
}
