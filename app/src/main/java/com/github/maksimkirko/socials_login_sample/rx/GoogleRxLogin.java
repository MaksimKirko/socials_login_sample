package com.github.maksimkirko.socials_login_sample.rx;


import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.github.maksimkirko.socials_login_sample.R;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;

import java.util.List;

import io.reactivex.Observable;

public class GoogleRxLogin implements RxSocialLogin {

    public static final int RC_SIGN_IN = 007;

    private GoogleApiClient mGoogleApiClient;

    public GoogleRxLogin(@NonNull Activity activityInstance) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(activityInstance.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(activityInstance)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    @Override
    public Observable<LoginResultData> login(@NonNull Activity activityInstance, @NonNull List<String> permissions) {
        return Observable.create(subscriber -> {
            OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
            if (opr.isDone()) {
                GoogleSignInResult result = opr.get();
                subscriber.onNext(new LoginResultData(result));
            } else {
                opr.setResultCallback(googleSignInResult -> subscriber.onNext(new LoginResultData(googleSignInResult)));
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                activityInstance.startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
    }

    @Override
    public Observable<String> logout() {
        return null;
    }

    @Override
    public Observable<String> getUserData(@NonNull LoginResultData loginResultData) {
        GoogleSignInAccount acct = loginResultData.getGoogleLoginResult().getSignInAccount();

        String info = "";
        if (acct != null) {
            String personName = acct.getDisplayName();
            String email = acct.getEmail();
            String token = acct.getIdToken();

            info = personName + "\n" + email + "\n" + token;
        }
        return Observable.just(info);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }
}
