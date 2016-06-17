package g20.homework8;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.ui.FirebaseRecyclerAdapter;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Firebase mFireBaseRef, mFireUsers;
    private AuthData mAuthData;
    private FirebaseRecyclerAdapter adapter;
    private RecyclerView recyclerView;
    private UserObject userObject = null;
    private ChildEventListener myUserListener;
    private Query queryRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFireBaseRef = new Firebase(getResources().getString(R.string.FIREBASE));
        mAuthData = mFireBaseRef.getAuth();

        mFireUsers = new Firebase(getResources().getString(R.string.FIREBASE) + "/users");

        recyclerView = (RecyclerView) findViewById(R.id.recyclerListView);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

        adapter = new FirebaseContactAdapter(UserObject.class, R.layout.cell_user, FirebaseContactAdapter.ProfileViewHolder.class, mFireUsers);
        recyclerView.setAdapter(adapter);

        getMyUserData();
    }

    private void getMyUserData() {
        queryRef = new Firebase(getResources().getString(R.string.FIREBASE) + "/users");
        myUserListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getKey().equals(mAuthData.getUid()))
                    userObject = dataSnapshot.getValue(UserObject.class);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                userObject = dataSnapshot.getValue(UserObject.class);

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        };

        queryRef.addChildEventListener(myUserListener);
    }

    @Override
    protected void onDestroy() {
        queryRef.removeEventListener(myUserListener);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_logout:
                logout();
                break;
            case R.id.editProfile:
                if (userObject != null)
                    startActivity(new Intent(MainActivity.this, ProfileDetailsActivity.class).putExtra(Application.FIRE_USER, userObject));
                break;
        }
        return true;
    }

    private void logout() {
        if (this.mAuthData != null) {
            mFireBaseRef.unauth();
            mAuthData = null;
            startActivity(new Intent(MainActivity.this, DispatchActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        }
    }

    @Override
    public void onClick(View v) {
        Bundle bun = (Bundle) v.getTag();
        UserObject mOTHER = bun.getParcelable("user");

        if (v.getId() == R.id.phone_img_User && mOTHER != null) {
            callContact(mOTHER.getPhoneNumber());
        } else {
            if (userObject != null)
                startActivity(new Intent(MainActivity.this, MessageActivity.class).putExtra(Application.FIRE_USER, userObject).putExtra(Application.FIRE_OTHER_USER, bun));
            else
                Toast.makeText(MainActivity.this, R.string.missingUserData, Toast.LENGTH_SHORT).show();
        }
    }

    private void callContact(String pNumber) {
        Uri phone = Uri.parse("tel:" + pNumber);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(MainActivity.this, R.string.phonePermissionFailed, Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Intent.ACTION_DIAL, phone));
        } else {
            startActivity(new Intent(Intent.ACTION_CALL, phone));
        }
    }
}
