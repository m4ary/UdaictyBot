
package com.mshlab.udacityBot.udacityapi.model;

import javax.annotation.Generated;

import com.google.gson.annotations.SerializedName;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class SignInObj {

    @SerializedName("email")
    private String mEmail;
    @SerializedName("next")
    private String mNext;
    @SerializedName("otp")
    private String mOtp;
    @SerializedName("password")
    private String mPassword;

    /**
     * {"email":"mshary@iharbi.com","password":"ma?iZNLQyA","otp":"","next":"https://mentor-dashboard.udacity.com/reviews/overview"}
     *
     * @param mEmail
     * @param mPassword
     */

    public SignInObj(String mEmail, String mPassword) {
        this.mEmail = mEmail;
        this.mPassword = mPassword;
        this.mNext = "https://mentor-dashboard.udacity.com/reviews/overview";
        this.mOtp = "";
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public String getNext() {
        return mNext;
    }

    public void setNext(String next) {
        mNext = next;
    }

    public String getOtp() {
        return mOtp;
    }

    public void setOtp(String otp) {
        mOtp = otp;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String password) {
        mPassword = password;
    }

}
