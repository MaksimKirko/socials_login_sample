package com.github.maksimkirko.socials_login_sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.Button;
import android.widget.TextView;

import com.github.maksimkirko.socials_login_sample.rx.FacebookRxLogin;
import com.github.maksimkirko.socials_login_sample.rx.GoogleRxLogin;
import com.github.maksimkirko.socials_login_sample.rx.LoginResultData;
import com.github.maksimkirko.socials_login_sample.rx.RxSocialLogin;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;

import java.util.Arrays;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.github.maksimkirko.socials_login_sample.rx.GoogleRxLogin.RC_SIGN_IN;

public class MainActivity extends Activity {

    private Button facebookRxLoginButton;
    private Button googleRxLoginButton;
    private TextView textView;

    private RxSocialLogin facebookSocialLogin;
    private RxSocialLogin googleSocialLogin;

    private List<String> permissions = Arrays.asList("public_profile", "email", "user_birthday");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

        facebookSocialLogin = new FacebookRxLogin();
        googleSocialLogin = new GoogleRxLogin(this);
    }

    private void initViews() {
        textView = findViewById(R.id.text_view_activity_main);
        facebookRxLoginButton = findViewById(R.id.button_activity_main_facebook_rx_login);

        facebookRxLoginButton.setOnClickListener(v -> facebookSocialLogin.login(this, permissions)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::getDataFromFacebookResults, throwable -> textView.setText(throwable.getClass().getSimpleName())));

        googleRxLoginButton = findViewById(R.id.button_activity_main_google_rx_login);

        googleRxLoginButton.setOnClickListener(v -> googleSocialLogin.login(this, permissions)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::getDataFromGoogleResults, throwable -> textView.setText(throwable.getClass().getSimpleName())));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO remove this to GoogleRxLogin.onActivityResult()
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            getDataFromGoogleResults(new LoginResultData(result));
        }
        facebookSocialLogin.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void getDataFromFacebookResults(@NonNull LoginResultData loginResultData) {
        facebookSocialLogin.getUserData(loginResultData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> textView.setText(s), throwable -> textView.setText(throwable.getClass().getSimpleName()));
    }

    private void getDataFromGoogleResults(@NonNull LoginResultData loginResultData) {
        googleSocialLogin.getUserData(loginResultData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> textView.setText(s), throwable -> textView.setText(throwable.getClass().getSimpleName()));
    }
}
