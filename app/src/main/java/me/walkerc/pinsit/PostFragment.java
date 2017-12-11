package me.walkerc.pinsit;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.wonderkiln.camerakit.CameraKit;
import com.wonderkiln.camerakit.CameraView;

import java.io.File;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PostFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PostFragment extends Fragment {
    private CameraView cameraView;
    private AppCompatImageButton buttonRecord;
    private AppCompatImageButton buttonSwitchCam;
    private AppCompatImageButton buttonToggleLight;
    private SeekBar statusSeekBar;

    private CameraStatus cameraStatus = CameraStatus.DEFAULT;
    private RecordingManager manager;

    public enum CameraStatus {
        DEFAULT,
        RECORDING,
        FINISHED
    }

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_post, container, false);
        cameraView = root.findViewById(R.id.cameraView);
        buttonRecord = (AppCompatImageButton) root.findViewById(R.id.buttonRecord);
        buttonSwitchCam = (AppCompatImageButton) root.findViewById(R.id.buttonSwitchCam);
        buttonToggleLight = (AppCompatImageButton) root.findViewById(R.id.buttonToggleLight);
        statusSeekBar = (SeekBar) root.findViewById(R.id.statusSeekBar);

        buttonRecord.setOnClickListener(onButtonRecordPressed);
        buttonSwitchCam.setOnClickListener(onButtonSwitchCamPressed);
        buttonToggleLight.setOnClickListener(onButtonLightPressed);

        cameraView.setVideoQuality(CameraKit.Constants.VIDEO_QUALITY_HIGHEST);
        cameraView.setFocus(CameraKit.Constants.FOCUS_TAP);
        manager = new RecordingManager(cameraView, statusSeekBar);
        cameraView.bindCameraKitListener(manager);

        return root;
    }

    View.OnClickListener onButtonRecordPressed = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (cameraStatus) {
                case DEFAULT:
                    manager.startTimedRecording(onRecordingFinished, new File(getContext().getFilesDir(), "out.mp4"));

                    break;
                case RECORDING:
                    manager.stopTimedRecordingPrematurely();

                    break;
                case FINISHED:
                    break;
            }
        }
    };

    RecordingManager.OnRecordingFinishedListener onRecordingFinished = new RecordingManager.OnRecordingFinishedListener() {
        @Override
        public void finished(File file) {

        }
    };

    /**
     * Toggles the camera direction between front and back
     * @param v pressed view
     */
    View.OnClickListener onButtonSwitchCamPressed = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (cameraView.getFacing() == CameraKit.Constants.FACING_BACK)
                cameraView.setFacing(CameraKit.Constants.FACING_FRONT);
            else
                cameraView.setFacing(CameraKit.Constants.FACING_BACK);
        }
    };

    /**
     * Toggles whether the light is on or off
     * @param v pressed view
     */
    View.OnClickListener onButtonLightPressed = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (cameraView.getFlash() == CameraKit.Constants.FLASH_OFF)
                cameraView.setFlash(CameraKit.Constants.FLASH_TORCH);
            else
                cameraView.setFlash(CameraKit.Constants.FLASH_OFF);
        }
    };

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
                buttonRecord.setEnabled(true);

                buttonSwitchCam.setEnabled(true);
                buttonToggleLight.setEnabled(true);
                statusSeekBar.setVisibility(View.GONE);

                break;
            case RECORDING:
                buttonRecord.setBackgroundResource(R.drawable.button_recording_active);
                buttonRecord.setEnabled(true);

                buttonSwitchCam.setEnabled(true);
                buttonToggleLight.setEnabled(true);
                statusSeekBar.setVisibility(View.VISIBLE);

                break;
            case FINISHED:
                buttonRecord.setBackgroundResource(R.drawable.button_recording);
                buttonRecord.setEnabled(true);

                buttonSwitchCam.setEnabled(false);
                buttonToggleLight.setEnabled(false);
                statusSeekBar.setVisibility(View.VISIBLE);

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
}
