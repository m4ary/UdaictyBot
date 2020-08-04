package com.mshlab.udacityBot.udacityapi.model;

import com.google.gson.annotations.SerializedName;

public enum SubmissionRequestStatus {
    @SerializedName("available")
    AVAILABLE,
    @SerializedName("fulfilled")
    FULFILLED,
    @SerializedName("closed")
    CLOSED
}
