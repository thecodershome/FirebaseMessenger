package g20.homework8;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

public class LoginActivity extends AppCompatActivity {

    private EditText mEmail, mPassword;
    private Button mLogin, mSignup;
    private Firebase mFireBaseRef;
    private ProgressDialog prog;
    private Firebase.AuthStateListener mAuthStateListener;
    private AuthData mAuthData;
    private String email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Firebase App Reference
        mFireBaseRef = new Firebase(getResources().getString(R.string.FIREBASE));

        // GUI Variables
        mLogin = (Button) findViewById(R.id.loginButton_Login);
        mSignup = (Button) findViewById(R.id.signupButton_Login);
        mEmail = (EditText) findViewById(R.id.email_Login);
        mPassword = (EditText) findViewById(R.id.password_Login);

        // Progress Dialog
        prog = new ProgressDialog(this);
        prog.setTitle("Logging In ...");
        prog.setCancelable(false);

        // Login Button Listener
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });

        // Signup Button Listener
        mSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            }
        });

        // Firebase User Authentication State Listener
        mAuthStateListener = new Firebase.AuthStateListener() {
            @Override
            public void onAuthStateChanged(AuthData authData) {
                setAuthenticatedUser(authData);
            }
        };
        mFireBaseRef.addAuthStateListener(mAuthStateListener);
    }

    // Check State
    private void setAuthenticatedUser(AuthData authData) {
        String authName = null;

        if (authData != null) {
            if (authData.getProvider().equals("password")) {
                authName = authData.getUid();
            } else {
                Log.d("invalid", "Invalid provider: " + authData.getProvider());
            }
            if (authName != null) {
                loginSuccess();
            }
        }
        this.mAuthData = authData;
    }

    // Start user login
    private void attemptLogin() {
        email = mEmail.getText().toString();
        password = mPassword.getText().toString();

        if (!email.equals("") && !password.equals("")) {
            mFireBaseRef.authWithPassword(email, password, new AuthResultHandler("password"));
            prog.show();
        } else {
            Toast.makeText(LoginActivity.this, "Email / Password are Blank!", Toast.LENGTH_SHORT).show();
        }
    }

    // On Login Failure
    private void loginFailed() {
        prog.dismiss();
    }

    // On Login Success
    private void loginSuccess() {
        prog.dismiss();
        startActivity(new Intent(LoginActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Disable Authentication State Listener when view is destroyed
        mFireBaseRef.removeAuthStateListener(mAuthStateListener);
    }

    // Login Attempt Result
    private class AuthResultHandler implements Firebase.AuthResultHandler {
        private final String provider;

        public AuthResultHandler(String provider) {
            this.provider = provider;
        }

        @Override
        public void onAuthenticated(AuthData authData) {
            Log.d("LoginSuccess", provider + " auth successful");
            Toast.makeText(LoginActivity.this, "Login Completed", Toast.LENGTH_SHORT).show();

            setAuthenticatedUser(authData);
        }

        @Override
        public void onAuthenticationError(FirebaseError firebaseError) {
            Toast.makeText(LoginActivity.this, "Error" + firebaseError.getMessage(), Toast.LENGTH_SHORT).show();
            Log.d("FireBaseError", firebaseError.toString() + " ");
            loginFailed();
        }
    }

}