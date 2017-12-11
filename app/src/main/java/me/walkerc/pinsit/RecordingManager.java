package me.walkerc.pinsit;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.SeekBar;

import com.wonderkiln.camerakit.CameraKitVideo;
import com.wonderkiln.camerakit.CameraView;
import com.wonderkiln.camerakit.OnCameraKitEvent;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by CChristie on 12/10/2017.
 */

public class RecordingManager {
    /**
     * Recording time in milliseconds (10 seconds)
     */
    private static final int MAX_RECORDING_TIME = 10 * 1000;
    private static final String TAG = "RecordingManager";
    private File recordingLoc; //TODO remove
    private CameraView cameraView;
    private SeekBar seekBar;

    private OnRecordingFinishedListener listener;
    private Timer timer;

    public RecordingManager(@NonNull CameraView cameraView, @NonNull SeekBar seekBar) {
        this.cameraView = cameraView;
        this.seekBar = seekBar;
    }

    /**
     * Starts a timed recording maxed out at the default 10 seconds.
     * Upon timeout or calling <i>stopTimedRecordingPrematurely()</i>, the
     * capture will finish and the passed callback will be called.
     * @param listener callback
     * @param recordingLoc File location to save video
     */
    public void startTimedRecording(OnRecordingFinishedListener listener, File recordingLoc) {
        this.listener = listener;
        this.recordingLoc = recordingLoc;

        cameraView.captureVideo(recordingLoc);
        Log.i(TAG, "Video capture started");

        timer = new Timer();
        final long startTime = System.currentTimeMillis();

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                final long timeDifference = System.currentTimeMillis() - startTime;

                if (timeDifference >= MAX_RECORDING_TIME) {
                    //Update on main thread
                    new Handler(Looper.getMainLooper())
                            .post(new Runnable() {
                                @Override
                                public void run() {
                                    Log.i(TAG, "Video capture finished");
                                    cameraView.stopVideo();
                                    recordingFinished();
                                }
                            });

                    this.cancel();
                } else {
                    //Update on main thread
                    new Handler(Looper.getMainLooper())
                            .post(new Runnable() {
                                @Override
                                public void run() {
                                    seekBar.setMax(MAX_RECORDING_TIME / 1000);
                                    seekBar.setProgress((int) (timeDifference / 1000));
                                }
                            });
                }
            }
        }, 0, 500); //Timer with a half second interval period
    }

    public void stopTimedRecordingPrematurely() {
        timer.cancel();
        cameraView.stopVideo();
        recordingFinished();
    }

    public interface OnRecordingFinishedListener {
        /**
         * Called when the recording has finished either via timeout or
         * from user input
         * @param file
         */
        void finished(File file);
    }

    @OnCameraKitEvent(CameraKitVideo.class)
    public void videoCaptured(CameraKitVideo video) {
        Log.i(TAG, "Test");
    }

    private void recordingFinished() {
        if (listener != null)
            listener.finished(recordingLoc);
        else
            Log.w(TAG, "Failed to retrieve recorded video as the passed OnRecordingFinishedListener is null");
    }
}
