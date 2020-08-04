
package com.mshlab.udacityBot.udacityapi.model;

import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class User {

    @SerializedName("accepted_terms")
    private Boolean mAcceptedTerms;
    @SerializedName("agreement_version")
    private String mAgreementVersion;
    @SerializedName("email")
    private String mEmail;
    @SerializedName("gig_approved")
    private Boolean mGigApproved;
    @SerializedName("id")
    private Long mId;
    @SerializedName("mentor_country_code")
    private String mMentorCountryCode;
    @SerializedName("mentor_languages")
    private List<String> mMentorLanguages;
    @SerializedName("name")
    private String mName;
    @SerializedName("payoneer_status")
    private String mPayoneerStatus;
    @SerializedName("role")
    private String mRole;
    @SerializedName("udacity_key")
    private String mUdacityKey;

    public Boolean getAcceptedTerms() {
        return mAcceptedTerms;
    }

    public void setAcceptedTerms(Boolean acceptedTerms) {
        mAcceptedTerms = acceptedTerms;
    }

    public String getAgreementVersion() {
        return mAgreementVersion;
    }

    public void setAgreementVersion(String agreementVersion) {
        mAgreementVersion = agreementVersion;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public Boolean getGigApproved() {
        return mGigApproved;
    }

    public void setGigApproved(Boolean gigApproved) {
        mGigApproved = gigApproved;
    }

    public Long getId() {
        return mId;
    }

    public void setId(Long id) {
        mId = id;
    }

    public String getMentorCountryCode() {
        return mMentorCountryCode;
    }

    public void setMentorCountryCode(String mentorCountryCode) {
        mMentorCountryCode = mentorCountryCode;
    }

    public List<String> getMentorLanguages() {
        return mMentorLanguages;
    }

    public void setMentorLanguages(List<String> mentorLanguages) {
        mMentorLanguages = mentorLanguages;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getPayoneerStatus() {
        return mPayoneerStatus;
    }

    public void setPayoneerStatus(String payoneerStatus) {
        mPayoneerStatus = payoneerStatus;
    }

    public String getRole() {
        return mRole;
    }

    public void setRole(String role) {
        mRole = role;
    }

    public String getUdacityKey() {
        return mUdacityKey;
    }

    public void setUdacityKey(String udacityKey) {
        mUdacityKey = udacityKey;
    }

}
