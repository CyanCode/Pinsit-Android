package me.walkerc.pinsit;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;

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

    public static ProgressDialog showCenteredProgressSpinner(Context context) {
        ProgressDialog progress = new ProgressDialog(context, R.style.CenterGravity);
        progress.setCancelable(false);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setProgress(0);
        progress.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        progress.show();

        return progress;
    }
}
