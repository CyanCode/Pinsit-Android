package me.walkerc.pinsit;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PinActivity extends AppCompatActivity {
    private static final String TAG = "PinActivity";

    @BindView(R.id.pinVideoView) VideoView videoView;
    private String videoKey = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin);
        ButterKnife.bind(this);

        Bundle b = getIntent().getExtras();
        if (b == null) {
            Log.e(TAG, "The passed key is null, video lookup will fail");
        } else {
            videoKey = b.getString("key");
            startVideoPlayback();
        }
    }

    protected void startVideoPlayback() {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("posts/" + videoKey);
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String storageLoc = (String) dataSnapshot.child("videoRef").getValue();
                StorageReference ref = FirebaseStorage.getInstance().getReference(storageLoc);
                ref.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        videoView.setVisibility(View.VISIBLE);
                        videoView.setVideoURI(task.getResult() );
                        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                mp.setLooping(true);
                                mp.start();

                                Log.i(TAG, "Video started, duration=" + videoView.getDuration());
                            }
                        });
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });
    }
}
