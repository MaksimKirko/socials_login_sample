package com.github.maksimkirko.socials_login_sample.rx;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONException;

import java.util.List;

import io.reactivex.Observable;


public class FacebookRxSocialLogin implements RxSocialLogin {

    private CallbackManager callbackManager;

    public FacebookRxSocialLogin() {
        callbackManager = CallbackManager.Factory.create();
    }

    @Override
    public Observable<LoginResult> login(@NonNull Activity activityInstance, @NonNull List<String> permissions) {
        LoginManager loginManager = LoginManager.getInstance();
        Observable<LoginResult> observable = Observable.create(subscriber ->
                loginManager.registerCallback(callbackManager,
                        new FacebookCallback<LoginResult>() {
                            @Override
                            public void onSuccess(LoginResult loginResult) {
                                subscriber.onNext(loginResult);
                            }

                            @Override
                            public void onCancel() {
                                subscriber.onError(new NullPointerException());
                            }

                            @Override
                            public void onError(FacebookException exception) {
                                subscriber.onError(exception);
                            }
                        }));


        loginManager.logInWithReadPermissions(
                activityInstance,
                permissions);

        return observable;
    }

    @Override
    public Observable<String> logout() {
        return null;
    }

    @Override
    public Observable<String> getUserData(@NonNull AccessToken accessToken) {
        Observable<String> observable = Observable.create(subscriber -> {
            GraphRequest request = GraphRequest.newMeRequest(
                    accessToken,
                    (object, response) -> {
                        try {
                            String email = object.getString("email");
                            String name = object.getString("name");
                            String birthday = object.getString("birthday");
                            String gender = object.getString("gender");
                            String picture = object.getString("picture");

                            String info = email + "\n" + name + "\n" + birthday +
                                    "\n" + gender;

                            subscriber.onNext(info);
                        } catch (JSONException e) {
                            subscriber.onError(e);
                        }
                    });

            Bundle parameters = new Bundle();
            parameters.putString("fields", "name,email,birthday,gender,picture");
            request.setParameters(parameters);
            request.executeAndWait();
        });

        return observable;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
