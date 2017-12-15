package me.walkerc.pinsit;

import android.*;
import android.Manifest;
import android.content.Context;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.yayandroid.locationmanager.LocationManager;
import com.yayandroid.locationmanager.configuration.Configurations;
import com.yayandroid.locationmanager.configuration.LocationConfiguration;
import com.yayandroid.locationmanager.configuration.PermissionConfiguration;
import com.yayandroid.locationmanager.listener.LocationListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by CChristie on 12/12/2017.
 */
public class Pin {
    private static final String TAG = "Pin";

    private String uid;
    private String dateCreated;
    private String videoReference;

    private File videoFile;
    private LocationManager manager;

    public Pin() { }

    public Pin(File videoFile) {
        this.videoFile = videoFile;

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            uid = user.getUid();
            dateCreated = fromDate(new Date());
            videoReference = "videos/" + UUID.randomUUID() + ".mp4";
        }
    }

    public void postPin(final Fragment fragment, final OnPostStatusUpdateListener listener) {
        //First get user's current location
        getCurrentLocation(fragment, new LocationListener() {
            @Override
            public void onProcessTypeChanged(int processType) {

            }

            @Override
            public void onLocationChanged(final Location location) {
                manager.cancel(); //No longer need location updates
                DatabaseReference db = FirebaseDatabase.getInstance().getReference();

                //Write post to database
                final String key = db.child("posts").push().getKey();
                Map<String, Object> children = new HashMap<>();
                children.put("/posts/" + key, toMap());
                db.updateChildren(children);

                //Upload video
                uploadVideo(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        uploadGeofireLocation(key,
                                new GeoLocation(location.getLatitude(), location.getLongitude()));
                        listener.postSucceeded();
                    }
                });
            }

            @Override
            public void onLocationFailed(int type) {

            }

            @Override
            public void onPermissionGranted(boolean alreadyHadPermission) {

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        });


    }

    public void uploadVideo(OnCompleteListener<UploadTask.TaskSnapshot> listener) {
        StorageReference ref = FirebaseStorage.getInstance().getReference(videoReference);
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("video/mp4")
                .build();

        UploadTask upload = ref.putFile(Uri.fromFile(videoFile), metadata);
        upload.addOnCompleteListener(listener);
    }

    public void uploadGeofireLocation(String key, GeoLocation location) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("geofire");
        GeoFire gf = new GeoFire(ref);

        gf.setLocation(key, location);
    }

    public void getCurrentLocation(Fragment fragment, LocationListener listener) {
        final String msg = "Pinsit uses your current location to pin your video on the map.";
        LocationConfiguration config = Configurations.defaultConfiguration(msg, msg);

        if (fragment.getActivity() == null ||
                fragment.getActivity().getApplicationContext() == null) {
            Log.e(TAG, "Application context is null");
            return;
        }

        manager = new LocationManager.Builder(fragment.getActivity().getApplicationContext())
                .fragment(fragment)
                .configuration(config)
                .notify(listener)
                .build();
        manager.get();
    }

    public Map<String, String> toMap() {
        HashMap<String, String> result = new HashMap<>();
        result.put("uid", uid);
        result.put("dateCreated", dateCreated);
        result.put("videoRef", videoReference);

        return result;
    }

    private byte[] readBytes(File f) {
        FileInputStream fin = null;

        try {
            fin = new FileInputStream(f);
            return new byte[(int) f.length()];
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fin != null) {
                    fin.close();
                }
            } catch (IOException ioe) {
                System.out.println("Error while closing stream: " + ioe);
            }
        }

        return null;
    }

    private String fromDate(Date d) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(d);
    }

    private Date toDate(String s) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return sdf.parse(s);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public interface OnPostStatusUpdateListener {
        void postSucceeded();
    }
}
