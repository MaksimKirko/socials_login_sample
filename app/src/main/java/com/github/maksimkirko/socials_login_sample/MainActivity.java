package com.github.maksimkirko.socials_login_sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.login.LoginResult;
import com.github.maksimkirko.socials_login_sample.rx.FacebookRxSocialLogin;
import com.github.maksimkirko.socials_login_sample.rx.RxSocialLogin;

import java.util.Arrays;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends Activity {

    private Button facebookRxLoginButton;
    private TextView textView;

    private RxSocialLogin rxSocialLogin;

    private List<String> permissions = Arrays.asList("public_profile", "email", "user_birthday");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

        rxSocialLogin = new FacebookRxSocialLogin();
    }

    private void initViews() {
        textView = findViewById(R.id.text_view_activity_main);
        facebookRxLoginButton = findViewById(R.id.button_activity_main_facebook_rx_login);

        facebookRxLoginButton.setOnClickListener(v -> rxSocialLogin.login(this, permissions)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::getData, throwable -> textView.setText(throwable.getClass().getSimpleName())));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        rxSocialLogin.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void getData(@NonNull LoginResult loginResult) {
        rxSocialLogin.getUserData(loginResult.getAccessToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> textView.setText(s), throwable -> textView.setText(throwable.getClass().getSimpleName()));
    }
}
