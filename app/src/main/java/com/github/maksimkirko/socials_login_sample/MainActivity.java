package com.github.maksimkirko.socials_login_sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private LoginButton loginButton;
    private TextView textView;
    private ImageView imageView;

    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initCallback();
        initViews();
        initLoginManager();
    }

    private void initCallback() {
        callbackManager = CallbackManager.Factory.create();
    }

    private void initViews() {
        loginButton = findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("public_profile", "email", "user_birthday"));
// If using in a fragment
//        loginButton.setFragment(this);

        textView = findViewById(R.id.text_view_activity_main);
        imageView = findViewById(R.id.image_view_activity_main);
    }

    private void initLoginManager() {
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Toast.makeText(MainActivity.this, "LoginManager callback: " + AccessToken.getCurrentAccessToken().getUserId(), Toast.LENGTH_LONG).show();
                        getData(loginResult);
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(MainActivity.this, "LoginManager callback: error", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Toast.makeText(MainActivity.this, "LoginManager callback: cancel", Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void getData(@NonNull LoginResult loginResult) {
        GraphRequest request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {
                            String email = object.getString("email");
                            String name = object.getString("name");
                            String birthday = object.getString("birthday");
                            String gender = object.getString("gender");
                            String picture = object.getString("picture");

                            String info = email + "\n" + name + "\n" + birthday +
                                    "\n" + gender;

                            textView.setText(info);

                            int startIndex = picture.indexOf("\"url\":\"") + 7;
                            int endIndex = picture.indexOf("\",\"width");
                            String url = picture.substring(startIndex, endIndex);
                            url = url.replaceAll("\\\\", "");
                            int size = getResources().getDimensionPixelSize(R.dimen.size_activity_main_image_view);

                            Picasso.with(MainActivity.this)
                                    .load(url)
                                    .resize(size, size)
                                    .error(R.mipmap.ic_launcher)
                                    .into(imageView);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "name,email,birthday,gender,picture");
        request.setParameters(parameters);
        request.executeAsync();
    }
}
