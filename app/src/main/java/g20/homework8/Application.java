package g20.homework8;

import com.firebase.client.Firebase;

public class Application extends android.app.Application {
    public static final String FIRE_USER = "fireUser";
    public static final String FIRE_OTHER_USER = "fireOtherUser";

    @Override
    public void onCreate() {
        super.onCreate();

        Firebase.setAndroidContext(this);
        Firebase.getDefaultConfig().setPersistenceEnabled(true);
    }
}
