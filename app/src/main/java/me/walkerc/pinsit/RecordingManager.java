package me.walkerc.pinsit;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.VideoView;

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
    private File recordingLoc;
    private CameraView cameraView;
    private SeekBar seekBar;

    private OnRecordingFinishedListener listener;
    private Timer timer;
    private VideoView videoView;

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
                                    seekBar.setMax(MAX_RECORDING_TIME);
                                    seekBar.setProgress((int) (timeDifference));
                                }
                            });
                }
            }
        }, 0, 250); //Timer with a quarter second interval period
    }

    public void stopTimedRecordingPrematurely() {
        timer.cancel();
        cameraView.stopVideo();
    }

    public void startVideoPreview(final VideoView videoView, File videoFile) {
        this.videoView = videoView;

        videoView.setVisibility(View.VISIBLE);
        videoView.setVideoURI(Uri.fromFile(videoFile));
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
                mp.start();

                Log.i(TAG, "Video started, duration=" + videoView.getDuration());
            }
        });

        //videoView.start();
    }

    public void stopVideoPreview() {
        if (videoView != null && videoView.isPlaying()) {
            videoView.pause();
            videoView.setVisibility(View.INVISIBLE);
        } else if (videoView != null) {
            videoView.setVisibility(View.INVISIBLE);
        } else {
            Log.w(TAG, "The associated videoView instance is null. Are you sure the video has been started?");
        }
    }

    public File getVideoLocation() {
        if (recordingLoc == null)
            Log.w(TAG, "The returned video recording location is null");

        return recordingLoc;
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
        recordingFinished();
    }

    private void recordingFinished() {
        if (listener != null)
            listener.finished(recordingLoc);
        else
            Log.w(TAG, "Failed to retrieve recorded video as the passed OnRecordingFinishedListener is null");
    }
}
