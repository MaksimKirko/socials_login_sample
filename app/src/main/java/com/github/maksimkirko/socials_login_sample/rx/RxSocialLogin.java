package com.github.maksimkirko.socials_login_sample.rx;


import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.facebook.AccessToken;
import com.facebook.login.LoginResult;

import java.util.List;

import io.reactivex.Observable;

public interface RxSocialLogin {

    Observable<LoginResult> login(@NonNull Activity activityInstance, @NonNull List<String> permissions);

    Observable<String> logout();

    Observable<String> getUserData(@NonNull AccessToken accessToken);

    void onActivityResult(int requestCode, int resultCode, Intent data);
}
