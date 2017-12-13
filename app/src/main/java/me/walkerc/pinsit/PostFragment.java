package me.walkerc.pinsit;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.wonderkiln.camerakit.CameraKit;
import com.wonderkiln.camerakit.CameraView;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PostFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PostFragment extends Fragment {
    private static final String TAG = "PostFragment";

    @BindView(R.id.cameraView) public CameraView cameraView;
    @BindView(R.id.buttonRecord) public AppCompatImageButton buttonRecord;
    @BindView(R.id.buttonSwitchCam) public AppCompatImageButton buttonSwitchCam;
    @BindView(R.id.buttonToggleLight) public AppCompatImageButton buttonToggleLight;
    @BindView(R.id.buttonClose) public AppCompatImageButton buttonClose;
    @BindView(R.id.statusSeekBar) public SeekBar statusSeekBar;
    @BindView(R.id.videoView) public VideoView videoView;

    private CameraStatus cameraStatus = CameraStatus.DEFAULT;
    private RecordingManager manager;

    public enum CameraStatus {
        DEFAULT,
        RECORDING,
        FINISHED
    }

    RecordingManager.OnRecordingFinishedListener onRecordingFinished = new RecordingManager.OnRecordingFinishedListener() {
        @Override
        public void finished(File file) {
            setCameraStatus(CameraStatus.FINISHED);
            manager.startVideoPreview(videoView, file);
        }
    };

    public PostFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment PostFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PostFragment newInstance(String param1, String param2) {
        PostFragment fragment = new PostFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_post, container, false);
        ButterKnife.bind(this, root);

        cameraView.setVideoQuality(CameraKit.Constants.VIDEO_QUALITY_HIGHEST);
        cameraView.setFocus(CameraKit.Constants.FOCUS_TAP);

        manager = new RecordingManager(cameraView, statusSeekBar);
        cameraView.bindCameraKitListener(manager);

        return root;
    }

    @OnClick(R.id.buttonRecord)
    public void onButtonRecordPressed() {
        switch (cameraStatus) {
            case DEFAULT:
                manager.startTimedRecording(onRecordingFinished, new File(getContext().getFilesDir(), "out.mp4"));
                setCameraStatus(CameraStatus.RECORDING);
                break;
            case RECORDING:
                manager.stopTimedRecordingPrematurely();
                setCameraStatus(CameraStatus.FINISHED);
                break;
            case FINISHED:
                displayPostDialog();
                break;
        }
    }

    /**
     * Toggles the camera direction between front and back
     */
    @OnClick(R.id.buttonSwitchCam)
    public void onButtonSwitchCamPressed() {
        if (cameraView.getFacing() == CameraKit.Constants.FACING_BACK)
            cameraView.setFacing(CameraKit.Constants.FACING_FRONT);
        else
            cameraView.setFacing(CameraKit.Constants.FACING_BACK);
    }

    /**
     * Toggles whether the light is on or off
     */
    @OnClick(R.id.buttonToggleLight)
    public void onButtonLightPressed() {
        if (cameraView.getFlash() == CameraKit.Constants.FLASH_OFF)
            cameraView.setFlash(CameraKit.Constants.FLASH_TORCH);
        else
            cameraView.setFlash(CameraKit.Constants.FLASH_OFF);
    }

    /**
     * If confirmed, Cancels the currently playing video and
     * sets the camera status back to default
     */
    @OnClick(R.id.buttonClose)
    public void onButtonClosePressed() {
        AlertDialog alert = new AlertDialog.Builder(this.getContext()).create();
        alert.setTitle("Are you Sure?");
        alert.setMessage("Are you sure you want to delete your masterpiece?");
        alert.setButton(AlertDialog.BUTTON_NEGATIVE, "Delete",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        manager.stopVideoPreview();
                        setCameraStatus(CameraStatus.DEFAULT);
                    }
                });
        alert.setButton(AlertDialog.BUTTON_NEUTRAL, "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alert.show();
    }

    /**
     * Sets this Fragment's camera status and updates associated
     * view visibility accordingly.
     * @param status Camera status to set
     */
    public void setCameraStatus(CameraStatus status) {
        this.cameraStatus = status;

        switch (status) {
            case DEFAULT:
                buttonRecord.setBackgroundResource(R.drawable.button_recording);
                buttonRecord.setImageResource(0);

                buttonSwitchCam.setVisibility(View.VISIBLE);
                buttonToggleLight.setVisibility(View.VISIBLE);
                buttonClose.setVisibility(View.INVISIBLE);
                statusSeekBar.setVisibility(View.INVISIBLE);

                setVisibility(cameraView, View.VISIBLE);
                if (!cameraView.isStarted()) cameraView.start();

                break;
            case RECORDING:
                buttonRecord.setBackgroundResource(R.drawable.button_recording_active);
                buttonRecord.setImageResource(0);

                buttonSwitchCam.setVisibility(View.VISIBLE);
                buttonToggleLight.setVisibility(View.VISIBLE);
                buttonClose.setVisibility(View.INVISIBLE);
                statusSeekBar.setVisibility(View.VISIBLE);
                setVisibility(cameraView, View.VISIBLE);

                break;
            case FINISHED:
                buttonRecord.setBackgroundResource(R.drawable.button_recording);
                buttonRecord.setImageResource(R.drawable.ic_check);

                buttonSwitchCam.setVisibility(View.INVISIBLE);
                buttonToggleLight.setVisibility(View.INVISIBLE);
                buttonClose.setVisibility(View.VISIBLE);
                statusSeekBar.setVisibility(View.INVISIBLE);

                setVisibility(cameraView, View.INVISIBLE);
                if (cameraView.isStarted()) cameraView.stop();

                break;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (cameraStatus != CameraStatus.FINISHED) {
            cameraView.start();
        }
    }

    @Override
    public void onPause() {
        if (cameraView.isStarted()) {
            cameraView.stop();
        }

        super.onPause();
    }

    private void displayPostDialog() {
        AlertDialog alert = new AlertDialog.Builder(this.getContext()).create();
        alert.setTitle("Ready to Post?");
        alert.setMessage("Are you sure you want to post your video?");
        alert.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alert.setButton(AlertDialog.BUTTON_POSITIVE, "Post",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Pin p = new Pin(manager.getVideoLocation());

                        p.postPin(PostFragment.this, new Pin.OnPostStatusUpdateListener() {
                            @Override
                            public void postSucceeded() {
                                Log.i(TAG, "Pin was posted successfully");
                                Toast.makeText(PostFragment.this.getActivity().getApplicationContext(),
                                        "Your pin was posted successfully!", Toast.LENGTH_SHORT)
                                        .show();
                                PostFragment.this.setCameraStatus(CameraStatus.DEFAULT);
                            }
                        });
                    }
                });
        alert.show();
    }

    private static void setVisibility(ViewGroup layout, int visible) {
        layout.setVisibility(visible);

        for (int i = 0; i < layout.getChildCount(); i++) {
            View child = layout.getChildAt(i);
            if (child instanceof ViewGroup) {
                setVisibility((ViewGroup) child, visible);
            } else {
                child.setVisibility(visible);
            }
        }
    }
}
