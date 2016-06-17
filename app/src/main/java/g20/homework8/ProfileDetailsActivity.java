package g20.homework8;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ProfileDetailsActivity extends AppCompatActivity {

    public static final int IMG = 0x4;

    private EditText mName, mEmail, mPhone, mPassword;
    private TextView mTopName;
    private ImageView mProfile;
    private Button update, cancel;
    private UserObject userObject;
    private Firebase userRef, mFirebaseRef;
    private String name, email, pass, phone;
    private Map<String, Object> user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_details);

        user = new HashMap<>();

        userObject = getIntent().getParcelableExtra(Application.FIRE_USER);
        mFirebaseRef = new Firebase(getResources().getString(R.string.FIREBASE));
        userRef = mFirebaseRef.child("users").child(mFirebaseRef.getAuth().getUid());

        update = (Button) findViewById(R.id.update_btn_Profile);
        cancel = (Button) findViewById(R.id.cancel_btn_Profile);
        mProfile = (ImageView) findViewById(R.id.profile_img_Profile);
        mTopName = (TextView) findViewById(R.id.userName_Profile);
        mName = (EditText) findViewById(R.id.name_Txt_Profile);
        mEmail = (EditText) findViewById(R.id.email_Txt_Profile);
        mPhone = (EditText) findViewById(R.id.phone_Txt_Profile);
        mPassword = (EditText) findViewById(R.id.password_Txt_Profile);

        mName.setText(userObject.getFullName());
        mEmail.setText(userObject.getEmail());
        mPhone.setText(String.valueOf(userObject.getPhoneNumber()));
        mPassword.setText(userObject.getPassword());
        mTopName.setText(userObject.getFullName());


        byte[] decodedString = Base64.decode(userObject.getPicture(), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        // Todo: Add Change Email

        if (bitmap != null) {
            mProfile.setTag(5);
            mProfile.setImageBitmap(bitmap);
        }

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserDetails();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, IMG);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == IMG) {
                if (data != null) {
                    Uri profileImageUri = data.getData();

                    InputStream imageStream = null;
                    try {
                        imageStream = getContentResolver().openInputStream(profileImageUri);

                        Bitmap bitmapFactory = BitmapFactory.decodeStream(imageStream);
                        mProfile.setImageBitmap(bitmapFactory);

                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmapFactory.compress(Bitmap.CompressFormat.PNG, 90, stream);
                        user.put("picture", Base64.encodeToString(stream.toByteArray(), 0));

                    } catch (FileNotFoundException e) {
                        Toast.makeText(ProfileDetailsActivity.this, "Error With Img", Toast.LENGTH_SHORT).show();
                    } finally {
                        if (imageStream != null) {
                            try {
                                imageStream.close();
                            } catch (IOException e) {
                                // Error
                            }
                        }
                    }
                }
            }
        }
    }

    private void updateUserDetails() {

        name = mName.getText().toString();
        email = mEmail.getText().toString();
        pass = mPassword.getText().toString();
        phone = mPhone.getText().toString();

        if (!name.equals("") && !email.equals("") && !pass.equals("") && !phone.equals("")) {
            if (!email.equals(userObject.getEmail())) {
                if (!pass.equals(userObject.getPassword()))
                    updateEmail(false);
                else
                    updateEmail(true);
            } else if (!pass.equals(userObject.getPassword())) {
                updatePassword(email);
            } else {
                updateUserTable();
            }
        } else {
            Toast.makeText(ProfileDetailsActivity.this, "Can't be blank!", Toast.LENGTH_SHORT).show();
        }

    }

    private void updateEmail(final boolean b) {
        mFirebaseRef.changeEmail(userObject.getEmail(), userObject.getPassword(), email, new Firebase.ResultHandler() {
            @Override
            public void onSuccess() {
                Toast.makeText(ProfileDetailsActivity.this, "Updated Email ", Toast.LENGTH_SHORT).show();
                if (b)
                    updateUserTable();
                else
                    updatePassword(email);
            }

            @Override
            public void onError(FirebaseError firebaseError) {
                Toast.makeText(ProfileDetailsActivity.this, "Email Error " + firebaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updatePassword(String em) {
        mFirebaseRef.changePassword(em, userObject.getPassword(), pass, new Firebase.ResultHandler() {
            @Override
            public void onSuccess() {
                Toast.makeText(ProfileDetailsActivity.this, "Updated Password", Toast.LENGTH_SHORT).show();
                updateUserTable();
            }

            @Override
            public void onError(FirebaseError firebaseError) {
                Toast.makeText(ProfileDetailsActivity.this, "Password Error" + firebaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUserTable() {
        user.put("fullName", name);
        user.put("email", email);
        user.put("password", pass);
        user.put("phoneNumber", phone);
        userRef.updateChildren(user);
        Toast.makeText(ProfileDetailsActivity.this, "Updated", Toast.LENGTH_SHORT).show();
        finish();
    }
}
