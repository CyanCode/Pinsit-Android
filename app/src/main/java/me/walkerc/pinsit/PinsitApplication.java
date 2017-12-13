package me.walkerc.pinsit;

import android.app.Application;
import com.google.firebase.FirebaseApp;

/**
 * Created by CChristie on 12/12/2017.
 */
public class PinsitApplication extends Application {
    @Override
    public void onCreate() {
        //Initialize Firebase once
        FirebaseApp.initializeApp(this);

        super.onCreate();
    }
}
