package com.aravi.tunecatcher;

import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioPlaybackCaptureConfiguration;
import android.media.AudioRecord;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    public static final int MEDIA_PROJECTION_RESULT = 8001;

    private MediaProjectionManager mediaProjectionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        findViewById(R.id.startButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initMediaProjection();
            }
        });


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
                        .setSampleRate(8000)
                        .setChannelMask(AudioFormat.CHANNEL_IN_MONO)
                        .build();

        AudioRecord record =
                new AudioRecord.Builder()
                        .setAudioPlaybackCaptureConfig(config)
                        .setAudioFormat(format)
                        .build();


        record.startRecording();

        File appDirectory = new File(Environment.getExternalStorageDirectory() + "/TuneScraper");
        if (!appDirectory.exists()){
            appDirectory.mkdirs();
        }

        File outputFile = new File(appDirectory.getPath() + File.separator + "29919920.mp3");

        try {
            outputFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }


        short sData[] = new short[1024];
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(appDirectory.getPath() + File.separator + "29919920.mp3");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        record.read(sData, 0, 1024);
        byte bData[] = short2byte(sData);
        try {
            outputStream.write(bData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private byte[] short2byte(short[] sData) {
        int shortArrsize = sData.length;
        byte[] bytes = new byte[shortArrsize * 2];
        for (int i = 0; i < shortArrsize; i++) {
            bytes[i * 2] = (byte) (sData[i] & 0x00FF);
            bytes[(i * 2) + 1] = (byte) (sData[i] >> 8);
            sData[i] = 0;
        }
        return bytes;

    }
}