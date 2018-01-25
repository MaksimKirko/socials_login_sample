package com.github.maksimkirko.socials_login_sample.rx;


import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;

public class LoginResultData {

    private LoginResult facebookLoginResult;
    private GoogleSignInResult googleLoginResult;

    public LoginResult getFacebookLoginResult() {
        return facebookLoginResult;
    }

    public void setFacebookLoginResult(LoginResult facebookLoginResult) {
        this.facebookLoginResult = facebookLoginResult;
    }

    public GoogleSignInResult getGoogleLoginResult() {
        return googleLoginResult;
    }

    public void setGoogleLoginResult(GoogleSignInResult googleLoginResult) {
        this.googleLoginResult = googleLoginResult;
    }

    public LoginResultData(LoginResult facebookLoginResult) {
        this.facebookLoginResult = facebookLoginResult;
    }

    public LoginResultData(GoogleSignInResult googleLoginResult) {
        this.googleLoginResult = googleLoginResult;
    }
}
