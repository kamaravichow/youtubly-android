package com.aravi.tunecatcher;

import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioPlaybackCaptureConfiguration;
import android.media.AudioRecord;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    public static final int MEDIA_PROJECTION_RESULT = 8001;

    private MediaProjectionManager mediaProjectionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initMediaProjection();
    }

    private void initMediaProjection() {
        mediaProjectionManager = (MediaProjectionManager) getApplicationContext().getSystemService(MEDIA_PROJECTION_SERVICE);
        startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(), MEDIA_PROJECTION_RESULT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == MEDIA_PROJECTION_RESULT) {
            if (resultCode == RESULT_OK) {
                MediaProjection projection = mediaProjectionManager.getMediaProjection(resultCode, data);
                audioBuilder(projection);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void audioBuilder(MediaProjection mediaProjection) {
        // Retrieve a audio capable projection from the MediaProjectionManager
        AudioPlaybackCaptureConfiguration config =
                new AudioPlaybackCaptureConfiguration.Builder(mediaProjection)
                        .addMatchingUsage(AudioAttributes.USAGE_MEDIA)
                        .build();

        AudioFormat format =
                new AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_MP3)
                        .build();

        AudioRecord record =
                new AudioRecord.Builder()
                        .setAudioPlaybackCaptureConfig(config)
                        .setAudioFormat(format)
                        .build();
        record.startRecording();
    }
}