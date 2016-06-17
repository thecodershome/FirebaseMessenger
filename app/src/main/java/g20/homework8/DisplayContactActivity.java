package g20.homework8;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.TextView;

public class DisplayContactActivity extends AppCompatActivity {

    private TextView mTopName, mName, mPhone, mEmail;
    private ImageView contactPicture;
    private UserObject contact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_contact);

        // GUI Variables
        contactPicture = (ImageView) findViewById(R.id.contact_img_Contact);
        mTopName = (TextView) findViewById(R.id.contactName_Contact);
        mName = (TextView) findViewById(R.id.name_Contact);
        mPhone = (TextView) findViewById(R.id.phone_Contact);
        mEmail = (TextView) findViewById(R.id.email_Contact);

        // Contact Object
        contact = getIntent().getParcelableExtra(Application.FIRE_OTHER_USER);

        displayData();
    }

    private void displayData(){
        mTopName.setText(contact.getFullName());
        mName.setText(contact.getFullName());
        mPhone.setText(contact.getPhoneNumber());
        mEmail.setText(contact.getEmail());

        byte[] decodedString = Base64.decode(contact.getPicture(), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        contactPicture.setImageBitmap(bitmap);
    }
}
