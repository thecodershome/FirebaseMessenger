package g20.homework8;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;

public class DispatchActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Firebase ref = new Firebase(getResources().getString(R.string.FIREBASE));

        AuthData authData = ref.getAuth();

        if (authData != null) {
            startActivity(new Intent(DispatchActivity.this, MainActivity.class));
        } else {
            startActivity(new Intent(DispatchActivity.this, LoginActivity.class));
        }
    }
}
