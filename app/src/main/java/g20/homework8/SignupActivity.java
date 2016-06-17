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

import java.util.UUID;

public class SignupActivity extends AppCompatActivity {

    private EditText mEmail, mPassword, mPasswordAgain, mUsername, mPhoneNumber;
    private Button mSignup;
    private Firebase mFireBaseRef;
    private Firebase.AuthStateListener mAuthStateListener;
    private ProgressDialog signupProg;
    private ProgressDialog loginProg;
    private String email, password, fullname, phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Firebase App Reference
        mFireBaseRef = new Firebase(getResources().getString(R.string.FIREBASE));

        mSignup = (Button) findViewById(R.id.signupButton_Signup);
        mEmail = (EditText) findViewById(R.id.email_Signup);
        mPassword = (EditText) findViewById(R.id.password_Signup);
        mPasswordAgain = (EditText) findViewById(R.id.passwordAgain_Signup);
        mUsername = (EditText) findViewById(R.id.username_Signup);
        mPhoneNumber = (EditText) findViewById(R.id.phone_Signup);

        // Signup Progress Dialog
        signupProg = new ProgressDialog(this);
        signupProg.setTitle("Signing Up ...");
        signupProg.setCancelable(false);

        // Login Progress Dialog
        loginProg = new ProgressDialog(this);
        loginProg.setTitle("Logging In ...");
        loginProg.setCancelable(false);

        mSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptSignup();
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

    private void attemptSignup() {
        String passwordAgain;

        email = mEmail.getText().toString();
        password = mPassword.getText().toString();
        passwordAgain = mPasswordAgain.getText().toString();
        fullname = mUsername.getText().toString();
        phoneNumber = mPhoneNumber.getText().toString();

        if (!email.equals("") && !password.equals("")) {
            if (password.equals(passwordAgain)) {
                signupProg.show();
                mFireBaseRef.createUser(email, password, new Firebase.ResultHandler() {
                    @Override
                    public void onSuccess() {
                        signupSuccess();
                    }

                    @Override
                    public void onError(FirebaseError firebaseError) {
                        Toast.makeText(SignupActivity.this, "Error" + firebaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        signupFailed();
                    }
                });
            } else {
                Toast.makeText(SignupActivity.this, "Passwords Must Match", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(SignupActivity.this, "Email/Password Can't be blank!", Toast.LENGTH_SHORT).show();
        }
    }

    private void signupFailed() {
        signupProg.dismiss();
    }

    private void signupSuccess() {
        signupProg.dismiss();
        attemptLogin();
    }

    // Check State
    private void setAuthenticatedUser(AuthData authData) {
        String authName = null;

        if (authData != null) {
            if (authData.getProvider().equals("password")) {
                authName = authData.getUid();

                // Setup User Display Name
                Long uuid = UUID.randomUUID().getMostSignificantBits();

                UserObject user = new UserObject(email, fullname, password, phoneNumber, getResources().getString(R.string.userimg), uuid);

                mFireBaseRef.child("users").child(authData.getUid()).setValue(user);
            } else {
                Log.d("invalid", "Invalid provider: " + authData.getProvider());
            }
            if (authName != null) {
                loginSuccess();
            }
        }
    }

    // Start user login
    private void attemptLogin() {
        mFireBaseRef.authWithPassword(email, password, new AuthResultHandler("password"));
        loginProg.show();
    }

    // On Login Failure
    private void loginFailed() {
        loginProg.dismiss();
    }

    // On Login Success
    private void loginSuccess() {
        loginProg.dismiss();
        startActivity(new Intent(SignupActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Disable Authentication Stawe Listener when view is destroyed
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
            setAuthenticatedUser(authData);
        }

        @Override
        public void onAuthenticationError(FirebaseError firebaseError) {
            Log.d("FireBaseError", firebaseError.toString() + " ");
        }
    }
}
