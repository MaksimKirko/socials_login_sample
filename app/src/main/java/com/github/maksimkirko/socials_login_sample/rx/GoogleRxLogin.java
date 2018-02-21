package com.github.maksimkirko.socials_login_sample.rx;


import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.github.maksimkirko.socials_login_sample.R;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;

import java.util.List;

import io.reactivex.Observable;

public class GoogleRxLogin implements RxSocialLogin {

    public static final int RC_SIGN_IN = 007;

    private GoogleSignInClient googleSignInClient;
    private GoogleSignInOptions gso;

    public GoogleRxLogin(@NonNull Activity activityInstance) {
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(activityInstance.getString(R.string.server_client_id))
                .build();
    }

    @Override
    public Observable<LoginResultData> login(@NonNull Activity activityInstance, @NonNull List<String> permissions) {
        if (googleSignInClient == null) {
            googleSignInClient = GoogleSignIn.getClient(activityInstance, gso);
        }

        return Observable.create(subscriber -> {
            Intent signInIntent = googleSignInClient.getSignInIntent();
            activityInstance.startActivityForResult(signInIntent, RC_SIGN_IN);
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
            String id = acct.getId();
            String token = acct.getIdToken();

            info = personName + "\n" + id + "\n" + email + "\n" + token;
        }
        return Observable.just(info);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }
}
