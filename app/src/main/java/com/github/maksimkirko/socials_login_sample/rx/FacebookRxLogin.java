package com.github.maksimkirko.socials_login_sample.rx;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONException;

import java.util.List;

import io.reactivex.Observable;


public class FacebookRxLogin implements RxSocialLogin {

    private CallbackManager callbackManager;

    public FacebookRxLogin() {
        callbackManager = CallbackManager.Factory.create();
    }

    @Override
    public Observable<LoginResultData> login(@NonNull Activity activityInstance, @NonNull List<String> permissions) {
        return Observable.create(subscriber -> {
            LoginManager loginManager = LoginManager.getInstance();

            loginManager.registerCallback(callbackManager,
                    new FacebookCallback<LoginResult>() {
                        @Override
                        public void onSuccess(LoginResult loginResult) {
                            subscriber.onNext(new LoginResultData(loginResult));
                        }

                        @Override
                        public void onCancel() {
                            subscriber.onError(new NullPointerException());
                        }

                        @Override
                        public void onError(FacebookException exception) {
                            subscriber.onError(exception);
                        }
                    });


            loginManager.logInWithReadPermissions(
                    activityInstance,
                    permissions);
        });
    }

    @Override
    public Observable<String> logout() {
        return null;
    }

    @Override
    public Observable<String> getUserData(@NonNull LoginResultData loginResultData) {
        return Observable.create(subscriber -> {
            GraphRequest request = GraphRequest.newMeRequest(
                    loginResultData.getFacebookLoginResult().getAccessToken(),
                    (object, response) -> {
                        try {
                            String email = object.getString("email");
                            String name = object.getString("name");
                            String birthday = object.getString("birthday");
                            String gender = object.getString("gender");
                            String picture = object.getString("picture");

                            String info = email + "\n" + name + "\n" + birthday +
                                    "\n" + gender + "\n" + loginResultData.getFacebookLoginResult().getAccessToken().getToken();

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
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
