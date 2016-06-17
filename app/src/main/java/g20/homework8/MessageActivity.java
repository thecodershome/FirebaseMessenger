package g20.homework8;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.ui.FirebaseRecyclerAdapter;

import java.util.Calendar;
import java.util.HashMap;

public class MessageActivity extends AppCompatActivity implements View.OnClickListener {

    private Firebase mMessageRef, mUserRef, mOtherUserRef;
    private RecyclerView recyclerView;
    private UserObject mUser, mOtherUser;
    private HashMap<String, Object> status;
    private Button mSendBtn;
    private EditText mTextContent;
    private String mOtherUserID;
    private String msg;
    private AuthData mAuthData;
    private FirebaseRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        status = new HashMap<>();

        // Get ActionBar
        ActionBar actionBar = getSupportActionBar();

        // Passed Variables
        mUser = getIntent().getParcelableExtra(Application.FIRE_USER);
        Bundle bun = getIntent().getBundleExtra(Application.FIRE_OTHER_USER);
        mOtherUser = bun.getParcelable("user");
        mOtherUserID = bun.getString("ref");

        // Update OtherUser Hash To show I have viewed the message
        mOtherUserRef = new Firebase(getResources().getString(R.string.FIREBASE) + "/users/" + mOtherUserID + "/status/");
        mAuthData = mOtherUserRef.getAuth();

        status.put(mAuthData.getUid(), "n");
        mOtherUserRef.updateChildren(status);

        // Firebase Ref
        mUserRef = new Firebase(getResources().getString(R.string.FIREBASE) + "/users/" + mAuthData.getUid() + "/status/");

        // GUI Variables
        mSendBtn = (Button) findViewById(R.id.send_btn_Message);
        mTextContent = (EditText) findViewById(R.id.msgText_txt_Message);
        recyclerView = (RecyclerView) findViewById(R.id.messageRecycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(MessageActivity.this));

        // Set Activity Label
        if (actionBar != null)
            actionBar.setTitle(mOtherUser.getFullName());

        // Create Firebase Message Group Reference
        if (mUser.getaLongUUID() < mOtherUser.getaLongUUID()) {
            mMessageRef = new Firebase(getResources().getString(R.string.FIREBASE) + "/messages/" + mAuthData.getUid() + "--" + mOtherUserID);
        } else {
            mMessageRef = new Firebase(getResources().getString(R.string.FIREBASE) + "/messages/" + mOtherUserID + "--" + mAuthData.getUid());
        }

        // RecyclerAdapter
        adapter = new FirebaseMessageAdapter(MessageObject.class, R.layout.cell_message, FirebaseMessageAdapter.MessageViewHolder.class, mMessageRef, mUser, mOtherUser);
        recyclerView.setAdapter(adapter);

        mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(msg = mTextContent.getText().toString()).equals("")) {
                    addMessage(msg);
                    mTextContent.setText("");
                }else {
                    Toast.makeText(MessageActivity.this, "Message was Blank", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void addMessage(String m) {
        mMessageRef
                .push()
                .setValue(new MessageObject(Calendar.getInstance(), m, mAuthData.getUid(), mOtherUserID));


        // set hashmap for updating user when message is sent
        status.clear();
        status.put(mOtherUserID, "y");
        mUserRef.updateChildren(status);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_message, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_callContact:

                Uri phone = Uri.parse("tel:" + mOtherUser.getPhoneNumber());
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MessageActivity.this, R.string.phonePermissionFailed, Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Intent.ACTION_DIAL, phone));
                } else {
                    startActivity(new Intent(Intent.ACTION_CALL, phone));
                }
                break;
            case R.id.menu_viewContact:
                startActivity(new Intent(MessageActivity.this, DisplayContactActivity.class).putExtra(Application.FIRE_OTHER_USER, mOtherUser));
                break;
        }
        return true;
    }


    @Override
    public void onClick(View v) {
        ((Firebase) v.getTag()).setValue(null);
        adapter.notifyDataSetChanged();
    }
}
