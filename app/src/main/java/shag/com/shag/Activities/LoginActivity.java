package shag.com.shag.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import shag.com.shag.R;

public class LoginActivity extends AppCompatActivity {
    LoginButton loginButton;
    CallbackManager callbackManager;
    TextView loginToken;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = this;

        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.bLogin);
        loginToken = (TextView) findViewById(R.id.tvLoginToken);

        if(Profile.getCurrentProfile() != null) {
            onLoginSuccess();
        } else {
            LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    //loginToken.setText("Login Success: " + loginResult.getAccessToken());
                    onLoginSuccess();
                }

                @Override
                public void onCancel() {
                    Log.d("LoginActivity", "Cancel Login");
                }

                @Override
                public void onError(FacebookException error) {
                    Toast.makeText(context, "Error when logging in", Toast.LENGTH_SHORT).show();
                    Log.e("LoginActivity", error.toString());
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void onLoginSuccess() {
        Intent i = new Intent(context, FeedActivity.class);
        context.startActivity(i);
    }
}
