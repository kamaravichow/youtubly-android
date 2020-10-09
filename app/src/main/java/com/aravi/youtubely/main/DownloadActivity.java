package com.aravi.youtubely.main;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.aravi.youtubely.BuildConfig;
import com.aravi.youtubely.R;
import com.aravi.youtubely.model.Video;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.yausername.youtubedl_android.DownloadProgressCallback;
import com.yausername.youtubedl_android.YoutubeDL;
import com.yausername.youtubedl_android.YoutubeDLRequest;

import java.io.File;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class DownloadActivity extends AppCompatActivity {

    private String videoURL = "www.youtube.com";
    private ProgressBar progressBar;
    private TextView tvDownloadStatus;
    private TextView tvCommandOutput;

    private TextView videoTitle, videoDescription;
    private ImageView videoThumbnail;

    private boolean downloading = false;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        initViews();
    }


    private void initViews() {
        MaterialButton btnVideoDownload = findViewById(R.id.downloadVideoButton);
        MaterialButton btnAudioDownload = findViewById(R.id.downloadAudioButton);

        progressBar = findViewById(R.id.progress_bar);

        tvDownloadStatus = findViewById(R.id.tv_status);
        tvCommandOutput = findViewById(R.id.tv_command_output);

        videoTitle = findViewById(R.id.itemTitle);
        videoDescription = findViewById(R.id.itemDescription);
        videoThumbnail = findViewById(R.id.itemThumbnail);

        btnVideoDownload.setOnClickListener(view -> startDownload(false));
        btnAudioDownload.setOnClickListener(view -> startDownload(true));

        getMetadata();
    }


    private void getMetadata() {
        SharedPreferences sharedPreferences = getSharedPreferences("SELECTION.DATA", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        Video video = gson.fromJson(sharedPreferences.getString("ITEM", "{}"), Video.class);
        Glide.with(this)
                .load(video.getThumbnails().get(0))
                .centerCrop()
                .into(videoThumbnail);
        videoTitle.setText(video.getTitle());
        videoDescription.setText(video.getLongDesc());
        videoURL = "https://www.youtube.com" + video.getUrlSuffix();
    }

    private DownloadProgressCallback callback = new DownloadProgressCallback() {
        @Override
        public void onProgressUpdate(float progress, long etaInSeconds) {
            runOnUiThread(() -> {
                        progressBar.setProgress((int) progress);
                        tvDownloadStatus.setText(String.valueOf(progress) + "% (ETA " + String.valueOf(etaInSeconds) + " seconds)");
                    }
            );
        }
    };

    private void startDownload(boolean isAudio) {
        if (downloading) {
            Toast.makeText(DownloadActivity.this, "cannot start download. a download is already in progress", Toast.LENGTH_LONG).show();
            return;
        }

        if (!isStoragePermissionGranted()) {
            Toast.makeText(DownloadActivity.this, "grant storage permission and retry", Toast.LENGTH_LONG).show();
            return;
        }

        String url = videoURL;

        YoutubeDLRequest request = new YoutubeDLRequest(url);
        File youtubeDLDir = getDownloadLocation();
        if (isAudio){
            request.addOption("--extract-audio");
            request.addOption("--audio-format", "mp3");
            request.addOption("--audio-quality", "0");
        }
        request.addOption("-o", youtubeDLDir.getAbsolutePath() + "/%(title)s.%(ext)s");

        showStart();

        downloading = true;
        Disposable disposable = Observable.fromCallable(() -> YoutubeDL.getInstance().execute(request, callback))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(youtubeDLResponse -> {
                    progressBar.setProgress(100);
                    tvDownloadStatus.setText("COMPLETE");
                    tvCommandOutput.setText(youtubeDLResponse.getOut());
                    Toast.makeText(DownloadActivity.this, "Downloaded to Youtubely in Internal Storage", Toast.LENGTH_LONG).show();
                    downloading = false;
                }, e -> {
                    if(BuildConfig.DEBUG) Log.e("TAG",  "failed to download", e);
                    tvDownloadStatus.setText("FAIL");
                    tvCommandOutput.setText(e.getMessage());
                    Toast.makeText(DownloadActivity.this, "failed :" + e.getMessage(), Toast.LENGTH_LONG).show();
                    downloading = false;
                });
        compositeDisposable.add(disposable);
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.dispose();
        super.onDestroy();
    }

    @NonNull
    private File getDownloadLocation() {
        File defaultFolder = new File(Environment.getExternalStorageDirectory() + "/Youtubely");
        File youtubeDLDir = new File(defaultFolder, "Downloads");
        if (!youtubeDLDir.exists()) youtubeDLDir.mkdir();
        return youtubeDLDir;
    }

    private void showStart() {
        tvDownloadStatus.setText("Start");
        progressBar.setProgress(0);
    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else {
            return true;
        }
    }
}