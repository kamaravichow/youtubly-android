package com.aravi.youtubely.main;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aravi.youtubely.API;
import com.aravi.youtubely.R;
import com.aravi.youtubely.adapter.SearchResultsAdapter;
import com.aravi.youtubely.model.SearchList;
import com.aravi.youtubely.model.Video;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private final String API_KEY = "API_KEY"; //This is my personal project so No access key is provided //todo app doesn't work without this
    private List<Video> videosList = new ArrayList<>();
    private ProgressBar progressBar;
    private RecyclerView searchRecyclerView;
    private SearchResultsAdapter adapter;
    private MaterialButton searchButton;
    private TextInputEditText searchTermEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        searchRecyclerView = findViewById(R.id.searchResultsRecycler);
        searchButton = findViewById(R.id.searchButton);
        searchTermEditText = findViewById(R.id.searchTermEditText);
        progressBar = findViewById(R.id.progressBar);

        searchRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        searchButton.setOnClickListener(view -> {
            getSearchResultFor(searchTermEditText.getText().toString());
        });
    }


    private void getSearchResultFor(String term) {
        progressBar.setVisibility(View.VISIBLE);
        videosList.clear();
        Call<SearchList> searchListCall = API.getService().getSearchResult(API.url + "search/?term=" + term + "&key=" + API_KEY);
        searchListCall.enqueue(new Callback<SearchList>() {
            @Override
            public void onResponse(Call<SearchList> call, Response<SearchList> response) {
                SearchList list = response.body();
                if (list != null) {
                    List<Video> videos = list.getVideos();
                    videosList.addAll(videos);
                    SearchResultsAdapter adapterNoAds = new SearchResultsAdapter(MainActivity.this, videosList);
                    searchRecyclerView.setAdapter(adapterNoAds);
                    progressBar.setVisibility(View.INVISIBLE);
                }
                else {
                    showSnackbar("No matching videos found !");
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
            @Override
            public void onFailure(Call<SearchList> call, Throwable t) {
                progressBar.setVisibility(View.INVISIBLE);
                showSnackbar("Something went wrong ! : " + new Exception(t).getMessage());
            }
        });

    }

    private void showSnackbar(String message){
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show();
    }
}